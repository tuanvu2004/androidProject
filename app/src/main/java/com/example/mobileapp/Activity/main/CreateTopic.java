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
import com.example.mobileapp.model.TopicCreateRequest;
import com.example.mobileapp.model.Vocabulary;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.TopicApi;

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

            // Ẩn bàn phím
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            viewTopicName.clearError();
            btnCreateTopic.setEnabled(false);
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

            TopicCreateRequest request = new TopicCreateRequest(topicName);
            Log.d("CREATE_TOPIC", "Bắt đầu tạo topic: " + topicName);

            // Server trả về List<Vocabulary>, topic name lấy từ input của user
            topicApi.generateVocabularies(request).enqueue(new Callback<ApiResponse<List<Vocabulary>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Vocabulary>>> call,
                                       Response<ApiResponse<List<Vocabulary>>> response) {
                    btnCreateTopic.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Vocabulary> vocabList = response.body().getData();
                        Log.d("CREATE_TOPIC", "Tạo thành công, số từ: " + (vocabList != null ? vocabList.size() : 0));
                        Toast.makeText(activity, "Tạo Topic thành công!", Toast.LENGTH_SHORT).show();

                        // Mở màn hình Flashcard với danh sách từ vừa generate
                        if (vocabList != null && !vocabList.isEmpty()) {
                            Intent intent = new Intent(activity, FlashcardActivity.class);
                            intent.putExtra("VOCAB_LIST", (Serializable) vocabList);
                            intent.putExtra("TOPIC_NAME", topicName); // dùng tên user đã nhập
                            activity.startActivity(intent);
                        }

                        // Refresh trang chủ
                        if (onSuccess != null) onSuccess.run();

                    } else {
                        Log.e("CREATE_TOPIC", "Lỗi Server: " + response.code());
                        Toast.makeText(activity, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Vocabulary>>> call, Throwable t) {
                    btnCreateTopic.setEnabled(true);
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    String msg = t.getMessage() != null ? t.getMessage() : "Unknown error";
                    Log.e("CREATE_TOPIC", "onFailure: " + msg, t);
                    Toast.makeText(activity, "Lỗi kết nối: " + msg, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}