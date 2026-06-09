package com.example.mobileapp.Activity.main;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobileapp.Activity.main.flashCard.FlashcardActivity;
import com.example.mobileapp.Custom.CustomBtn;
import com.example.mobileapp.Custom.CustomInputField;
import com.example.mobileapp.R;
import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.Topic;
import com.example.mobileapp.model.TopicCreateRequest;
import com.example.mobileapp.model.TopicPageResponse;
import com.example.mobileapp.model.TopicSearchRequest;
import com.example.mobileapp.model.Vocabulary;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.TopicApi;
import com.example.mobileapp.session.SessionManager;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTopic {

    public static void init(View root, Activity activity, Runnable onSuccess) {
        CustomInputField viewTopicName = root.findViewById(R.id.viewTopicName);
        CustomBtn btnCreateTopic      = root.findViewById(R.id.btnCreateTopic);
        ProgressBar progressBar       = root.findViewById(R.id.progressBar);

        TopicApi topicApi = ApiClient.getClient(activity).create(TopicApi.class);

        btnCreateTopic.setOnClickListener(v -> {
            String topicName = viewTopicName.getEnteredText().trim();

            if (topicName.isEmpty()) {
                viewTopicName.setError("Vui lòng nhập tên Topic");
                return;
            }

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            viewTopicName.clearError();
            btnCreateTopic.setEnabled(false);
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            SessionManager sessionManager = new SessionManager(activity);
            String userEmail = sessionManager.getEmail();

            if (userEmail == null) {
                Toast.makeText(activity, "Lỗi phiên đăng nhập", Toast.LENGTH_SHORT).show();
                btnCreateTopic.setEnabled(true);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                return;
            }

            TopicCreateRequest request = new TopicCreateRequest(topicName, userEmail);
            
            topicApi.generateVocabularies(request).enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Vocabulary>>> call,
                                       Response<ApiResponse<List<Vocabulary>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Vocabulary> vocabList = response.body().getData();
                        
                        // Đợi server lưu xong (tránh race condition)
                        root.postDelayed(() -> {
                            findAndClaimTopic(topicApi, topicName, userEmail, activity, vocabList, () -> {
                                btnCreateTopic.setEnabled(true);
                                if (progressBar != null) progressBar.setVisibility(View.GONE);
                                if (onSuccess != null) onSuccess.run();
                            });
                        }, 800);

                    } else {
                        btnCreateTopic.setEnabled(true);
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(activity, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Vocabulary>>> call, Throwable t) {
                    btnCreateTopic.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private static void findAndClaimTopic(TopicApi api, String name, String email, Activity activity, List<Vocabulary> vocabList, Runnable onComplete) {
        TopicSearchRequest search = new TopicSearchRequest();
        search.addFilter("name", "LIKE", name);
        
        api.searchTopics(search).enqueue(new Callback<ApiResponse<TopicPageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TopicPageResponse>> call, Response<ApiResponse<TopicPageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Topic> items = response.body().getData().getItems();
                    Topic bestMatch = null;
                    if (items != null) {
                        for (Topic t : items) {
                            if (t.getName().equalsIgnoreCase(name)) {
                                // Nếu server đã trả về đúng email của mình thì gán luôn
                                if (email.equalsIgnoreCase(t.getUserEmail())) {
                                    bestMatch = t; break;
                                } 
                                // Nếu server trả về null email (vừa tạo chưa kịp update), tạm nhận là match tốt nhất
                                else if (t.getUserEmail() == null || t.getUserEmail().isEmpty() || t.getUserEmail().equalsIgnoreCase("null")) {
                                    bestMatch = t;
                                }
                            }
                        }
                    }

                    if (bestMatch != null) {
                        final Topic finalFound = bestMatch;
                        new Thread(() -> {
                            com.example.mobileapp.database.entity.LocalTopic local = new com.example.mobileapp.database.entity.LocalTopic();
                            local.setId(finalFound.getId());
                            local.setName(finalFound.getName());
                            local.setUserEmail(email); // Khóa quyền sở hữu bằng email hiện tại
                            local.setTotalWords(vocabList != null ? vocabList.size() : finalFound.getTotalWords());
                            local.setCreatedAt(finalFound.getCreatedAt());
                            com.example.mobileapp.database.AppDatabase.getDatabase(activity).localTopicDao().insertTopic(local);
                            
                            activity.runOnUiThread(() -> {
                                Toast.makeText(activity, "Tạo Topic thành công!", Toast.LENGTH_SHORT).show();
                                openFlashcard(activity, vocabList, name);
                                onComplete.run();
                            });
                        }).start();
                    } else {
                        openFlashcard(activity, vocabList, name);
                        onComplete.run();
                    }
                } else {
                    openFlashcard(activity, vocabList, name);
                    onComplete.run();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TopicPageResponse>> call, Throwable t) {
                openFlashcard(activity, vocabList, name);
                onComplete.run();
            }
        });
    }

    private static void openFlashcard(Activity activity, List<Vocabulary> vocabList, String topicName) {
        if (vocabList != null && !vocabList.isEmpty()) {
            Intent intent = new Intent(activity, FlashcardActivity.class);
            intent.putExtra("VOCAB_LIST", (Serializable) vocabList);
            intent.putExtra("TOPIC_NAME", topicName);
            activity.startActivity(intent);
        }
    }
}
