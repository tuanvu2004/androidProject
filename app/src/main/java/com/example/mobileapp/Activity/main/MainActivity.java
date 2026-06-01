package com.example.mobileapp.Activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.Activity.auth.SignInActivity;
import com.example.mobileapp.Adapter.TopicAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.TopicPageResponse;
import com.example.mobileapp.model.TopicSearchRequest;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.TopicApi;
import com.example.mobileapp.session.SessionManager;

import android.widget.Toast;
import com.example.mobileapp.model.QuizQuestion;
import com.example.mobileapp.model.QuizRequest;
import java.io.Serializable;
import java.util.List;
import android.widget.Toast;
import com.example.mobileapp.model.QuizQuestion;
import com.example.mobileapp.model.QuizRequest;
import java.io.Serializable;
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnHome, btnLibrary, btnTrophy, btnSettings, btnAdd;
    private FrameLayout container;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applySavedTheme();

        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        String access = session.getAccessToken();
        String refresh = session.getRefreshToken();

        // Chỉ chuyển hướng nếu CẢ HAI token đều không có
        if (access == null && refresh == null) {
            navigateToSignIn();
            return;
        }

        setContentView(R.layout.activity_main);

        initViews();
        setupNavigation();

        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt("tab", 0);
        }

        restoreTab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn tải lại tab hiện tại khi quay lại ứng dụng để cập nhật dữ liệu mới nhất
        refreshCurrentTab();
    }

    private void refreshCurrentTab() {
        switch (currentTab) {
            case 0: loadHomePage(); break;
            case 1: loadLibraryPage(); break;
            case 2: loadTrophyPage(); break;
            case 3: loadCreateTopicPage(); break;
            case 4: loadSettings(); break;
        }
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void applySavedTheme() {
        SharedPreferences pref = getSharedPreferences("settings_pref", MODE_PRIVATE);
        boolean isDarkMode = pref.getBoolean("is_dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ?
                        AppCompatDelegate.MODE_NIGHT_YES :
                        AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void initViews() {
        btnHome     = findViewById(R.id.btn_home);
        btnLibrary  = findViewById(R.id.btn_library);
        btnTrophy   = findViewById(R.id.btn_trophy);
        btnSettings = findViewById(R.id.btn_settings);
        btnAdd      = findViewById(R.id.btn_add);
        container   = findViewById(R.id.container);
    }

    private void setupNavigation() {
        btnHome.setOnClickListener(v     -> switchTab(0));
        btnLibrary.setOnClickListener(v  -> switchTab(1));
        btnTrophy.setOnClickListener(v   -> switchTab(2));
        btnAdd.setOnClickListener(v      -> switchTab(3));
        btnSettings.setOnClickListener(v -> switchTab(4));
    }

    private void switchTab(int tab) {
        if (currentTab == tab && container.getChildCount() > 0) return;
        currentTab = tab;

        switch (tab) {
            case 0:
                loadHomePage();
                selectTab(btnHome);
                break;
            case 1:
                loadLibraryPage();
                selectTab(btnLibrary);
                break;
            case 2:
                loadTrophyPage();
                selectTab(btnTrophy);
                break;
            case 3:
                loadCreateTopicPage();
                selectTab(btnAdd);
                break;
            case 4:
                loadSettings();
                selectTab(btnSettings);
                break;
        }
    }

    // ── HOME ──────────────────────────────────────────────────────────────────

    private void loadHomePage() {
        if (container == null) return;

        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_home, container, false);
        container.addView(view);

        RecyclerView rvTopics = view.findViewById(R.id.rvTopics);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));

        EditText edtSearch = view.findViewById(R.id.edtSearch);

        // Load ban đầu (Mặc định click vào mở Flashcard)
        performSearch("", rvTopics, null);

        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    performSearch(s.toString().trim(), rvTopics, null);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    // ── LIBRARY ───────────────────────────────────────────────────────────────

    private void loadLibraryPage() {
        if (container == null) return;

        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_library, container, false);
        container.addView(view);

        RecyclerView rvTopics = view.findViewById(R.id.rvTopicsLibrary);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));

        EditText edtSearch = view.findViewById(R.id.edtSearch);

        // Load ban đầu với listener xử lý Quiz
        performSearch("", rvTopics, topic -> {
            startQuiz(topic);
        });

        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    performSearch(s.toString().trim(), rvTopics, topic -> startQuiz(topic));
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void startQuiz(com.example.mobileapp.model.Topic topic) {
        final Long topicId = topic.getId();
        final String topicName = topic.getName();
        
        // 1. Kiểm tra database local xem đã có bộ đề cho Topic này chưa
        new Thread(() -> {
            com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
            List<com.example.mobileapp.database.entity.LocalQuizQuestion> localQuestions = 
                    db.localQuizDao().getQuestionsForTopic(topicId);

            if (localQuestions != null && !localQuestions.isEmpty()) {
                // Đã có bộ đề local -> Chuyển sang QuizActivity luôn
                Log.d("QUIZ_LOCAL", "Sử dụng bộ đề local cho: " + topicName);
                List<com.example.mobileapp.model.QuizQuestion> questions = convertToModel(localQuestions);
                launchQuizActivity(topicId, topicName, questions);
            } else {
                // Chưa có -> Gọi API tạo mới
                runOnUiThread(() -> fetchNewQuiz(topic));
            }
        }).start();
    }

    private void fetchNewQuiz(com.example.mobileapp.model.Topic topic) {
        int vocabularyCount = topic.getTotalWords();
        if (vocabularyCount <= 0 && topic.getVocabularies() != null) {
            vocabularyCount = topic.getVocabularies().size();
        }

        if (vocabularyCount == 0) {
            Toast.makeText(this, "Topic này chưa có từ vựng để tạo Quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
        com.example.mobileapp.model.QuizRequest request = new com.example.mobileapp.model.QuizRequest(topic.getId(), vocabularyCount);

        topicApi.generateQuiz(request).enqueue(new Callback<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> call, 
                                 Response<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.mobileapp.model.QuizQuestion> questions = response.body().getData();
                    if (questions != null && !questions.isEmpty()) {
                        // Lưu vào local database để dùng cho lần sau
                        saveQuestionsToLocal(topic.getId(), questions);
                        launchQuizActivity(topic.getId(), topic.getName(), questions);
                    } else {
                        Toast.makeText(MainActivity.this, "Không thể tạo bộ đề", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> call, Throwable t) {
                Log.e("QUIZ_API", "Lỗi: " + t.getMessage());
            }
        });
    }

    private void saveQuestionsToLocal(Long topicId, List<com.example.mobileapp.model.QuizQuestion> questions) {
        new Thread(() -> {
            List<com.example.mobileapp.database.entity.LocalQuizQuestion> localList = new ArrayList<>();
            for (com.example.mobileapp.model.QuizQuestion q : questions) {
                com.example.mobileapp.database.entity.LocalQuizQuestion local = new com.example.mobileapp.database.entity.LocalQuizQuestion();
                local.setTopicId(topicId);
                local.setWordId(q.getWordId());
                local.setEnglish(q.getEnglish());
                local.setExample(q.getExample());
                local.setOptions(q.getOptions());
                localList.add(local);
            }
            com.example.mobileapp.database.AppDatabase.getDatabase(this).localQuizDao().insertQuestions(localList);
        }).start();
    }

    private List<com.example.mobileapp.model.QuizQuestion> convertToModel(List<com.example.mobileapp.database.entity.LocalQuizQuestion> localList) {
        List<com.example.mobileapp.model.QuizQuestion> list = new ArrayList<>();
        for (com.example.mobileapp.database.entity.LocalQuizQuestion local : localList) {
            com.example.mobileapp.model.QuizQuestion q = new com.example.mobileapp.model.QuizQuestion();
            q.setWordId(local.getWordId());
            q.setEnglish(local.getEnglish());
            q.setExample(local.getExample());
            q.setOptions(local.getOptions());
            list.add(q);
        }
        return list;
    }

    private void launchQuizActivity(Long topicId, String topicName, List<com.example.mobileapp.model.QuizQuestion> questions) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, "Bắt đầu ôn tập: " + topicName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("QUESTIONS", (java.io.Serializable) questions);
            intent.putExtra("TOPIC_ID", topicId);
            startActivity(intent);
        });
    }

    private void performSearch(String query, RecyclerView rvTopics, TopicAdapter.OnItemClickListener listener) {
        TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
        TopicSearchRequest request = new TopicSearchRequest();

        if (!query.isEmpty()) {
            request.addFilter("name", "LIKE", query);
        }

        // Fetch topics and history together to show progress
        topicApi.searchTopics(request).enqueue(new Callback<ApiResponse<TopicPageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TopicPageResponse>> call, Response<ApiResponse<TopicPageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<com.example.mobileapp.model.Topic> topics = response.body().getData().getItems();
                    
                    // Now fetch history to map progress
                    topicApi.searchQuizHistory(new TopicSearchRequest()).enqueue(new Callback<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Response<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> hResponse) {
                            Map<Long, com.example.mobileapp.model.QuizResultResponse> historyMap = new HashMap<>();
                            if (hResponse.isSuccessful() && hResponse.body() != null && hResponse.body().getData() != null) {
                                List<com.example.mobileapp.model.QuizResultResponse> historyItems = hResponse.body().getData().getItems();
                                if (historyItems != null) {
                                    // Sắp xếp theo thời gian giảm dần (mới nhất lên đầu)
                                    Collections.sort(historyItems, (o1, o2) -> {
                                        if (o1.getCreatedAt() == null || o2.getCreatedAt() == null) return 0;
                                        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                    });
                                    
                                    for (com.example.mobileapp.model.QuizResultResponse res : historyItems) {
                                        if (!historyMap.containsKey(res.getTopicId())) {
                                            historyMap.put(res.getTopicId(), res);
                                        }
                                    }
                                }
                            }
                            
                            runOnUiThread(() -> {
                                TopicAdapter adapter = new TopicAdapter(topics != null ? topics : new ArrayList<>(), listener);
                                adapter.setLibraryMode(listener != null); 
                                adapter.setHistoryMap(historyMap);
                                rvTopics.setAdapter(adapter);
                            });
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Throwable t) {
                            // Fallback to topics only if history fails
                            runOnUiThread(() -> {
                                TopicAdapter adapter = new TopicAdapter(topics != null ? topics : new ArrayList<>(), listener);
                                rvTopics.setAdapter(adapter);
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TopicPageResponse>> call, Throwable t) {
                Log.e("SEARCH_API", "Error: " + t.getMessage());
            }
        });
    }

    // ── CREATE TOPIC ─────────────────────────────────────────────────────────

    private void loadCreateTopicPage() {
        if (container == null) return;

        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.activity_create_topic, container, false);
        container.addView(view);

        CreateTopic.init(view, this, () -> {
            // Quay lại trang chủ sau khi tạo xong
            switchTab(0);
        });
    }

    // ── TROPHY (HISTORY) ─────────────────────────────────────────────────────

    private void loadTrophyPage() {
        if (container == null) return;

        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_trophy, container, false);
        container.addView(view);

        RecyclerView rvHistory = view.findViewById(R.id.rvQuizHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
        TopicSearchRequest request = new TopicSearchRequest();
        // Bạn có thể thêm filter ở đây nếu cần

        topicApi.searchQuizHistory(request).enqueue(new Callback<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, 
                                 Response<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<com.example.mobileapp.model.QuizResultResponse> allItems = response.body().getData().getItems();
                    
                    // Lọc: Chỉ lấy kết quả MỚI NHẤT cho mỗi Topic
                    Map<Long, com.example.mobileapp.model.QuizResultResponse> latestResults = new LinkedHashMap<>();
                    if (allItems != null) {
                        // Sắp xếp mới nhất lên đầu
                        Collections.sort(allItems, (o1, o2) -> {
                            if (o1.getCreatedAt() == null || o2.getCreatedAt() == null) return 0;
                            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                        });

                        for (com.example.mobileapp.model.QuizResultResponse item : allItems) {
                            if (!latestResults.containsKey(item.getTopicId())) {
                                latestResults.put(item.getTopicId(), item);
                            }
                        }
                    }
                    
                    List<com.example.mobileapp.model.QuizResultResponse> displayItems = new ArrayList<>(latestResults.values());

                    runOnUiThread(() -> {
                        com.example.mobileapp.Adapter.QuizHistoryAdapter adapter =
                                new com.example.mobileapp.Adapter.QuizHistoryAdapter(displayItems, item -> {
                                    fetchHistoryDetailAndReview(item.getResultId());
                                });
                        rvHistory.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Throwable t) {
                Log.e("HISTORY_API", "Error: " + t.getMessage());
            }
        });
    }

    private void fetchHistoryDetailAndReview(Long resultId) {
        if (resultId == null) return;

        TopicApi api = ApiClient.getClient(this).create(TopicApi.class);
        api.getQuizHistoryDetail(resultId).enqueue(new Callback<ApiResponse<com.example.mobileapp.model.QuizResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> call,
                                 Response<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(MainActivity.this, ReviewQuizActivity.class);
                    intent.putExtra("QUIZ_RESULT", response.body().getData());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải chi tiết kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── GENERIC ───────────────────────────────────────────────────────────────

    private void loadPage(int layoutId) {
        if (container == null) return;
        container.removeAllViews();
        View view = getLayoutInflater().inflate(layoutId, container, false);
        container.addView(view);
    }

    private void loadSettings() {
        if (container == null) return;

        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_setting, container, false);
        container.addView(view);

        SettingManager.init(view, this);
    }

    private void restoreTab() {
        switchTab(currentTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", currentTab);
        super.onSaveInstanceState(outState);
    }

    private void selectTab(ImageButton selected) {
        ImageButton[] buttons = {btnHome, btnLibrary, btnTrophy, btnSettings, btnAdd};

        for (ImageButton btn : buttons) {
            if (btn != null) btn.setSelected(false);
        }

        if (selected != null) selected.setSelected(true);
    }
}