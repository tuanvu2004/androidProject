package com.example.mobileapp.Activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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
import java.util.List;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import okhttp3.ResponseBody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.TextView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnHome, btnLibrary, btnTrophy, btnSettings, btnAdd;
    private FrameLayout container;
    private ProgressBar mainProgressBar;
    private int currentTab = 0;
    private boolean skipNextResumeRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        String access = session.getAccessToken();
        String refresh = session.getRefreshToken();

        if (access == null && refresh == null) {
            navigateToSignIn();
            return;
        }

        setContentView(R.layout.activity_main);

        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setNavigationBarColor(android.graphics.Color.BLACK);
        getWindow().setStatusBarColor(android.graphics.Color.BLACK);
        
        androidx.core.view.WindowInsetsControllerCompat controller = 
            androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightNavigationBars(false);
            controller.setAppearanceLightStatusBars(false);
        }

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
        if (skipNextResumeRefresh) {
            skipNextResumeRefresh = false;
            return;
        }
        refreshCurrentTab();
    }

    private static final int REQUEST_TOPIC_MANAGEMENT = 1001;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TOPIC_MANAGEMENT && resultCode == RESULT_OK) {
            skipNextResumeRefresh = true; 
            if (data != null && data.hasExtra("UPDATED_TOPIC")) {
                com.example.mobileapp.model.Topic updated = (com.example.mobileapp.model.Topic) data.getSerializableExtra("UPDATED_TOPIC");
                updateTopicInUI(updated);
            } else {
                refreshCurrentTab();
            }
        }
    }

    private void updateTopicInUI(com.example.mobileapp.model.Topic updatedTopic) {
        runOnUiThread(() -> {
            RecyclerView rv = null;
            if (currentTab == 0) rv = findViewById(R.id.rvTopics);
            else if (currentTab == 1) rv = findViewById(R.id.rvTopicsLibrary);

            if (rv != null && rv.getAdapter() instanceof TopicAdapter) {
                ((TopicAdapter) rv.getAdapter()).updateTopic(updatedTopic);
            } else {
                refreshCurrentTab();
            }
        });
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
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private void initViews() {
        btnHome     = findViewById(R.id.btn_home);
        btnLibrary  = findViewById(R.id.btn_library);
        btnTrophy   = findViewById(R.id.btn_trophy);
        btnSettings = findViewById(R.id.btn_settings);
        btnAdd      = findViewById(R.id.btn_add);
        container   = findViewById(R.id.container);
        mainProgressBar = findViewById(R.id.mainProgressBar);
    }

    private void showMainLoading(boolean loading) {
        if (mainProgressBar != null) mainProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (container != null) container.setAlpha(loading ? 0.5f : 1.0f);
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
            case 0: loadHomePage(); selectTab(btnHome); break;
            case 1: loadLibraryPage(); selectTab(btnLibrary); break;
            case 2: loadTrophyPage(); selectTab(btnTrophy); break;
            case 3: loadCreateTopicPage(); selectTab(btnAdd); break;
            case 4: loadSettings(); selectTab(btnSettings); break;
        }
    }

    private void loadHomePage() {
        if (container == null) return;
        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_home, container, false);
        container.addView(view);
        RecyclerView rvTopics = view.findViewById(R.id.rvTopics);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        EditText edtSearch = view.findViewById(R.id.edtSearch);
        performSearch("", rvTopics, false, topic -> showTopicOptions(topic));
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    performSearch(s.toString().trim(), rvTopics, false, topic -> showTopicOptions(topic));
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void loadLibraryPage() {
        if (container == null) return;
        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_library, container, false);
        container.addView(view);
        RecyclerView rvTopics = view.findViewById(R.id.rvTopicsLibrary);
        rvTopics.setLayoutManager(new LinearLayoutManager(this));
        EditText edtSearch = view.findViewById(R.id.edtSearch);
        performSearch("", rvTopics, true, this::startQuiz);
        if (edtSearch != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    performSearch(s.toString().trim(), rvTopics, true, MainActivity.this::startQuiz);
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void startQuiz(com.example.mobileapp.model.Topic topic) {
        final Long topicId = topic.getId();
        final String topicName = topic.getName();
        new Thread(() -> {
            SessionManager session = new SessionManager(this);
            String userEmail = session.getEmail();
            com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
            List<com.example.mobileapp.database.entity.LocalQuizQuestion> localQuestions = 
                    db.localQuizDao().getQuestionsForTopic(topicId, userEmail);

            if (localQuestions != null && !localQuestions.isEmpty()) {
                List<com.example.mobileapp.model.QuizQuestion> questions = convertToModel(localQuestions);
                launchQuizActivity(topicId, topicName, questions);
            } else {
                runOnUiThread(() -> fetchNewQuiz(topic));
            }
        }).start();
    }

    private void fetchNewQuiz(com.example.mobileapp.model.Topic topic) {
        int vocabularyCount = topic.getTotalWords();
        if (vocabularyCount <= 0 && topic.getVocabularies() != null) vocabularyCount = topic.getVocabularies().size();
        if (vocabularyCount == 0) {
            Toast.makeText(this, "Topic này chưa có từ vựng", Toast.LENGTH_SHORT).show();
            return;
        }
        showMainLoading(true);
        TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
        com.example.mobileapp.model.QuizRequest request = new com.example.mobileapp.model.QuizRequest(topic.getId(), vocabularyCount);
        topicApi.generateQuiz(request).enqueue(new Callback<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>>() {
            @Override public void onResponse(Call<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> call, Response<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> response) {
                showMainLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.mobileapp.model.QuizQuestion> questions = response.body().getData();
                    if (questions != null && !questions.isEmpty()) {
                        saveQuestionsToLocal(topic.getId(), questions);
                        launchQuizActivity(topic.getId(), topic.getName(), questions);
                    }
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> call, Throwable t) { showMainLoading(false); }
        });
    }

    private void saveQuestionsToLocal(Long topicId, List<com.example.mobileapp.model.QuizQuestion> questions) {
        new Thread(() -> {
            SessionManager session = new SessionManager(this);
            String userEmail = session.getEmail();
            if (userEmail == null) return;
            
            List<com.example.mobileapp.database.entity.LocalQuizQuestion> localList = new ArrayList<>();
            for (com.example.mobileapp.model.QuizQuestion q : questions) {
                com.example.mobileapp.database.entity.LocalQuizQuestion local = new com.example.mobileapp.database.entity.LocalQuizQuestion();
                local.setTopicId(topicId); local.setUserEmail(userEmail); local.setWordId(q.getWordId());
                local.setEnglish(q.getEnglish()); local.setExample(q.getExample()); local.setOptions(q.getOptions());
                localList.add(local);
            }
            com.example.mobileapp.database.AppDatabase.getDatabase(this).localQuizDao().insertQuestions(localList);
        }).start();
    }

    private List<com.example.mobileapp.model.QuizQuestion> convertToModel(List<com.example.mobileapp.database.entity.LocalQuizQuestion> localList) {
        List<com.example.mobileapp.model.QuizQuestion> list = new ArrayList<>();
        for (com.example.mobileapp.database.entity.LocalQuizQuestion local : localList) {
            com.example.mobileapp.model.QuizQuestion q = new com.example.mobileapp.model.QuizQuestion();
            q.setWordId(local.getWordId()); q.setEnglish(local.getEnglish()); q.setExample(local.getExample()); q.setOptions(local.getOptions());
            list.add(q);
        }
        return list;
    }

    private void launchQuizActivity(Long topicId, String topicName, List<com.example.mobileapp.model.QuizQuestion> questions) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("QUESTIONS", (java.io.Serializable) questions);
            intent.putExtra("TOPIC_ID", topicId); intent.putExtra("TOPIC_NAME", topicName);
            startActivity(intent);
        });
    }

    private void performSearch(String query, RecyclerView rvTopics, boolean isLibraryMode, TopicAdapter.OnItemClickListener listener) {
        SessionManager session = new SessionManager(this);
        String currentUserEmail = session.getEmail();
        if (currentUserEmail == null) return;

        showMainLoading(true);

        new Thread(() -> {
            com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
            List<com.example.mobileapp.database.entity.LocalTopic> localList = db.localTopicDao().getTopicsForUser(currentUserEmail);
            
            final Set<Long> localTopicIds = new HashSet<>();
            List<com.example.mobileapp.model.Topic> initialList = new ArrayList<>();
            for (com.example.mobileapp.database.entity.LocalTopic lt : localList) {
                localTopicIds.add(lt.getId());
                if (query.isEmpty() || lt.getName().toLowerCase().contains(query.toLowerCase())) {
                    com.example.mobileapp.model.Topic t = new com.example.mobileapp.model.Topic();
                    t.setId(lt.getId()); t.setName(lt.getName()); t.setUserEmail(lt.getUserEmail());
                    t.setTotalWords(lt.getTotalWords()); t.setCreatedAt(lt.getCreatedAt());
                    initialList.add(t);
                }
            }

            runOnUiThread(() -> {
                TopicAdapter adapter = new TopicAdapter(initialList, listener);
                adapter.setLibraryMode(isLibraryMode);
                adapter.setOnItemLongClickListener(topic -> showDeleteConfirmDialog(topic));
                rvTopics.setAdapter(adapter);
            });

            TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
            TopicSearchRequest request = new TopicSearchRequest();
            if (!query.isEmpty()) request.addFilter("name", "LIKE", query);

            topicApi.searchTopics(request).enqueue(new Callback<ApiResponse<TopicPageResponse>>() {
                @Override public void onResponse(Call<ApiResponse<TopicPageResponse>> call, Response<ApiResponse<TopicPageResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<com.example.mobileapp.model.Topic> allApiTopics = response.body().getData().getItems();
                        if (allApiTopics != null) {
                            new Thread(() -> {
                                List<com.example.mobileapp.model.Topic> filtered = new ArrayList<>();
                                List<com.example.mobileapp.database.entity.LocalTopic> toUpdateLocal = new ArrayList<>();
                                for (com.example.mobileapp.model.Topic t : allApiTopics) {
                                    if (t.isDeleted()) continue;
                                    
                                    boolean isMineInServer = (t.getUserEmail() != null && t.getUserEmail().equalsIgnoreCase(currentUserEmail));
                                    boolean alreadyInLocal = localTopicIds.contains(t.getId());
                                    
                                    if (isMineInServer || alreadyInLocal) {
                                        if (query.isEmpty() || t.getName().toLowerCase().contains(query.toLowerCase())) filtered.add(t);
                                        
                                        com.example.mobileapp.database.entity.LocalTopic local = new com.example.mobileapp.database.entity.LocalTopic();
                                        local.setId(t.getId()); local.setName(t.getName()); local.setUserEmail(currentUserEmail);
                                        local.setTotalWords(t.getTotalWords()); local.setCreatedAt(t.getCreatedAt());
                                        toUpdateLocal.add(local);
                                    }
                                }
                                db.localTopicDao().insertTopics(toUpdateLocal);
                                
                                runOnUiThread(() -> {
                                    if (!isLibraryMode) {
                                        showMainLoading(false);
                                        TopicAdapter adapter = new TopicAdapter(filtered, listener);
                                        adapter.setLibraryMode(false);
                                        adapter.setOnItemLongClickListener(topic -> showDeleteConfirmDialog(topic));
                                        rvTopics.setAdapter(adapter);
                                    } else {
                                        fetchHistoryAndShow(filtered, rvTopics, isLibraryMode, listener);
                                    }
                                });
                            }).start();
                        } else showMainLoading(false);
                    } else showMainLoading(false);
                }
                @Override public void onFailure(Call<ApiResponse<TopicPageResponse>> call, Throwable t) { showMainLoading(false); }
            });
        }).start();
    }

    private void fetchHistoryAndShow(List<com.example.mobileapp.model.Topic> topics, RecyclerView rvTopics, boolean isLibraryMode, TopicAdapter.OnItemClickListener listener) {
        TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
        topicApi.searchQuizHistory(new TopicSearchRequest()).enqueue(new Callback<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>>() {
            @Override public void onResponse(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Response<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> hResponse) {
                showMainLoading(false);
                Map<Long, com.example.mobileapp.model.QuizResultResponse> historyMap = new HashMap<>();
                if (hResponse.isSuccessful() && hResponse.body() != null && hResponse.body().getData() != null) {
                    List<com.example.mobileapp.model.QuizResultResponse> historyItems = hResponse.body().getData().getItems();
                    if (historyItems != null) {
                        Collections.sort(historyItems, (o1, o2) -> {
                            if (o1.getCreatedAt() == null || o2.getCreatedAt() == null) return 0;
                            return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                        });
                        for (com.example.mobileapp.model.QuizResultResponse res : historyItems) {
                            if (!historyMap.containsKey(res.getTopicId())) historyMap.put(res.getTopicId(), res);
                        }
                    }
                }
                runOnUiThread(() -> {
                    TopicAdapter adapter = new TopicAdapter(topics, listener);
                    adapter.setLibraryMode(isLibraryMode); adapter.setHistoryMap(historyMap);
                    adapter.setOnItemLongClickListener(topic -> showDeleteConfirmDialog(topic));
                    rvTopics.setAdapter(adapter);
                });
            }
            @Override public void onFailure(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Throwable t) {
                showMainLoading(false);
                runOnUiThread(() -> {
                    TopicAdapter adapter = new TopicAdapter(topics, listener);
                    adapter.setLibraryMode(isLibraryMode);
                    adapter.setOnItemLongClickListener(topic -> showDeleteConfirmDialog(topic));
                    rvTopics.setAdapter(adapter);
                });
            }
        });
    }

    private void loadCreateTopicPage() {
        if (container == null) return;
        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.activity_create_topic, container, false);
        container.addView(view);
        CreateTopic.init(view, this, () -> switchTab(0));
    }

    private void loadTrophyPage() {
        if (container == null) return;
        showMainLoading(true);
        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_trophy, container, false);
        container.addView(view);
        RecyclerView rvHistory = view.findViewById(R.id.rvQuizHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        SessionManager session = new SessionManager(this);
        String currentUserEmail = session.getEmail();
        TopicApi topicApi = ApiClient.getClient(this).create(TopicApi.class);
        TopicSearchRequest request = new TopicSearchRequest();
        topicApi.searchQuizHistory(request).enqueue(new Callback<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>>() {
            @Override public void onResponse(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Response<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<com.example.mobileapp.model.QuizResultResponse> allItems = response.body().getData().getItems();
                    
                    new Thread(() -> {
                        com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(MainActivity.this);
                        List<Long> deletedIds = db.deletedHistoryDao().getDeletedHistoryIds(currentUserEmail);

                        topicApi.searchTopics(new TopicSearchRequest()).enqueue(new Callback<ApiResponse<TopicPageResponse>>() {
                            @Override public void onResponse(Call<ApiResponse<TopicPageResponse>> tCall, Response<ApiResponse<TopicPageResponse>> tResponse) {
                                showMainLoading(false);
                                List<Long> deletedTopicIds = new ArrayList<>();
                                if (tResponse.isSuccessful() && tResponse.body() != null && tResponse.body().getData() != null) {
                                    for (com.example.mobileapp.model.Topic t : tResponse.body().getData().getItems()) if (t.isDeleted()) deletedTopicIds.add(t.getId());
                                }
                                Map<Long, com.example.mobileapp.model.QuizResultResponse> latestResults = new LinkedHashMap<>();
                                if (allItems != null) {
                                    Collections.sort(allItems, (o1, o2) -> {
                                        if (o1.getCreatedAt() == null || o2.getCreatedAt() == null) return 0;
                                        return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                                    });
                                    for (com.example.mobileapp.model.QuizResultResponse item : allItems) {
                                        if (!deletedTopicIds.contains(item.getTopicId()) && 
                                            !deletedIds.contains(item.getResultId()) &&
                                            !latestResults.containsKey(item.getTopicId())) {
                                            latestResults.put(item.getTopicId(), item);
                                        }
                                    }
                                }
                                List<com.example.mobileapp.model.QuizResultResponse> displayItems = new ArrayList<>(latestResults.values());
                                runOnUiThread(() -> {
                                    com.example.mobileapp.Adapter.QuizHistoryAdapter adapter = new com.example.mobileapp.Adapter.QuizHistoryAdapter(displayItems, item -> fetchHistoryDetailAndReview(item.getResultId()));
                                    adapter.setOnItemLongClickListener(item -> showDeleteHistoryDialog(item));
                                    rvHistory.setAdapter(adapter);
                                });
                            }
                            @Override public void onFailure(Call<ApiResponse<TopicPageResponse>> tCall, Throwable t) { showMainLoading(false); loadTrophyPageSimple(allItems, rvHistory); }
                        });
                    }).start();
                } else showMainLoading(false);
            }
            @Override public void onFailure(Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> call, Throwable t) { showMainLoading(false); }
        });
    }

    private void loadTrophyPageSimple(List<com.example.mobileapp.model.QuizResultResponse> allItems, RecyclerView rvHistory) {
        SessionManager session = new SessionManager(this);
        String currentUserEmail = session.getEmail();

        new Thread(() -> {
            com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
            List<Long> deletedIds = db.deletedHistoryDao().getDeletedHistoryIds(currentUserEmail);

            Map<Long, com.example.mobileapp.model.QuizResultResponse> latestResults = new LinkedHashMap<>();
            if (allItems != null) {
                Collections.sort(allItems, (o1, o2) -> {
                    if (o1.getCreatedAt() == null || o2.getCreatedAt() == null) return 0;
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                });
                for (com.example.mobileapp.model.QuizResultResponse item : allItems) {
                    if (!deletedIds.contains(item.getResultId()) && !latestResults.containsKey(item.getTopicId())) {
                        latestResults.put(item.getTopicId(), item);
                    }
                }
            }
            List<com.example.mobileapp.model.QuizResultResponse> displayItems = new ArrayList<>(latestResults.values());
            runOnUiThread(() -> {
                com.example.mobileapp.Adapter.QuizHistoryAdapter adapter = new com.example.mobileapp.Adapter.QuizHistoryAdapter(displayItems, item -> fetchHistoryDetailAndReview(item.getResultId()));
                adapter.setOnItemLongClickListener(item -> showDeleteHistoryDialog(item));
                rvHistory.setAdapter(adapter);
            });
        }).start();
    }

    private void showDeleteHistoryDialog(com.example.mobileapp.model.QuizResultResponse item) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa lịch sử")
                .setMessage("Bạn có muốn ẩn kết quả ôn tập của topic '" + item.getTopicName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteHistoryLocally(item))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteHistoryLocally(com.example.mobileapp.model.QuizResultResponse item) {
        new Thread(() -> {
            SessionManager session = new SessionManager(this);
            String userEmail = session.getEmail();
            if (userEmail == null) return;
            
            com.example.mobileapp.database.entity.DeletedHistory deleted = new com.example.mobileapp.database.entity.DeletedHistory();
            deleted.setResultId(item.getResultId());
            deleted.setUserEmail(userEmail);
            
            com.example.mobileapp.database.AppDatabase.getDatabase(this).deletedHistoryDao().insertDeletedHistory(deleted);
            
            runOnUiThread(() -> {
                RecyclerView rv = findViewById(R.id.rvQuizHistory);
                if (rv != null && rv.getAdapter() instanceof com.example.mobileapp.Adapter.QuizHistoryAdapter) {
                    ((com.example.mobileapp.Adapter.QuizHistoryAdapter) rv.getAdapter()).removeHistoryItem(item.getResultId());
                }
                Toast.makeText(this, "Đã xóa lịch sử thành công", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void fetchHistoryDetailAndReview(Long resultId) {
        if (resultId == null) return;
        showMainLoading(true);
        TopicApi api = ApiClient.getClient(this).create(TopicApi.class);
        api.getQuizHistoryDetail(resultId).enqueue(new Callback<ApiResponse<com.example.mobileapp.model.QuizResultResponse>>() {
            @Override public void onResponse(Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> call, Response<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> response) {
                showMainLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(MainActivity.this, ReviewQuizActivity.class);
                    intent.putExtra("QUIZ_RESULT", response.body().getData());
                    startActivity(intent);
                }
            }
            @Override public void onFailure(Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> call, Throwable t) { showMainLoading(false); }
        });
    }

    private void loadSettings() {
        if (container == null) return;
        container.removeAllViews();
        View view = getLayoutInflater().inflate(R.layout.layout_setting, container, false);
        container.addView(view);
        SettingManager.init(view, this);
    }

    private void restoreTab() { switchTab(currentTab); }

    @Override protected void onSaveInstanceState(Bundle outState) { outState.putInt("tab", currentTab); super.onSaveInstanceState(outState); }

    private void selectTab(ImageButton selected) {
        ImageButton[] buttons = {btnHome, btnLibrary, btnTrophy, btnSettings, btnAdd};
        for (ImageButton btn : buttons) if (btn != null) btn.setSelected(false);
        if (selected != null) selected.setSelected(true);
    }

    private void showTopicOptions(com.example.mobileapp.model.Topic topic) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_topic_options, null);
        bottomSheetDialog.setContentView(view);
        TextView tvTopicName = view.findViewById(R.id.tvTopicName);
        LinearLayout btnLearn = view.findViewById(R.id.btnLearnFlashcard);
        LinearLayout btnManage = view.findViewById(R.id.btnManageVocab);
        tvTopicName.setText(topic.getName());
        btnLearn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, com.example.mobileapp.Activity.main.flashCard.FlashcardActivity.class);
            intent.putExtra("TOPIC_NAME", topic.getName());
            if (topic.getVocabularies() != null) intent.putExtra("VOCAB_LIST", new java.util.ArrayList<>(topic.getVocabularies()));
            startActivity(intent);
        });
        btnManage.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(this, TopicManagementActivity.class);
            intent.putExtra("TOPIC", topic);
            startActivityForResult(intent, REQUEST_TOPIC_MANAGEMENT);
        });
        bottomSheetDialog.show();
    }

    private void showDeleteConfirmDialog(com.example.mobileapp.model.Topic topic) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xóa Topic").setMessage("Bạn có chắc chắn muốn xóa topic '" + topic.getName() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteTopic(topic.getId())).setNegativeButton("Hủy", null).show();
    }

    private void deleteTopic(Long topicId) {
        TopicApi api = ApiClient.getClient(this).create(TopicApi.class);
        api.deleteTopic(topicId).enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    deleteLocalQuizData(topicId);
                    runOnUiThread(() -> {
                        RecyclerView rv = null;
                        if (currentTab == 0) rv = findViewById(R.id.rvTopics); else if (currentTab == 1) rv = findViewById(R.id.rvTopicsLibrary);
                        if (rv != null && rv.getAdapter() instanceof TopicAdapter) ((TopicAdapter) rv.getAdapter()).removeTopic(topicId);
                        else refreshCurrentTab();
                    });
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    private void deleteLocalQuizData(Long topicId) {
        new Thread(() -> {
            SessionManager session = new SessionManager(this);
            String userEmail = session.getEmail();
            if (userEmail == null) return;
            com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
            db.localQuizDao().deleteQuestionsForTopic(topicId, userEmail);
            db.localTopicDao().deleteTopic(topicId, userEmail);
        }).start();
    }
}
