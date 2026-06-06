package com.example.mobileapp.Activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.Adapter.VocabEditAdapter;
import com.example.mobileapp.Custom.CustomInputField;
import com.example.mobileapp.R;
import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.Topic;
import com.example.mobileapp.model.TopicPageResponse;
import com.example.mobileapp.model.TopicSearchRequest;
import com.example.mobileapp.model.Vocabulary;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.TopicApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicManagementActivity extends AppCompatActivity {

    private TextView viewTopicName;
    private RecyclerView rvVocabList;
    private View progressBar;
    private VocabEditAdapter adapter;
    private Topic currentTopic;
    private List<Vocabulary> vocabList = new ArrayList<>();
    private TopicApi topicApi;
    private boolean isSyncing = false;
    private boolean isDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_management);

        topicApi = ApiClient.getClient(this).create(TopicApi.class);
        currentTopic = (Topic) getIntent().getSerializableExtra("Topic");
        if (currentTopic == null) {
            currentTopic = (Topic) getIntent().getSerializableExtra("TOPIC");
        }

        if (currentTopic == null) {
            Toast.makeText(this, "Không có dữ liệu Topic", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        fetchFullTopicData();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isSyncing) {
                    Toast.makeText(TopicManagementActivity.this, "Đang lưu thay đổi, vui lòng đợi...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isDataChanged) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_TOPIC", currentTopic);
                    setResult(RESULT_OK, resultIntent);
                }
                finish();
            }
        });
    }

    private void initViews() {
        viewTopicName = findViewById(R.id.viewTopicName);
        rvVocabList = findViewById(R.id.rvVocabList);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        findViewById(R.id.btnAddVocab).setOnClickListener(v -> {
            if (isSyncing) return;
            showVocabDialog(null, -1);
        });

        findViewById(R.id.btnRenameTopic).setOnClickListener(v -> {
            if (isSyncing) return;
            showRenameTopicDialog();
        });
        viewTopicName.setText(currentTopic.getName());

        rvVocabList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new VocabEditAdapter(vocabList, new VocabEditAdapter.OnVocabActionListener() {
            @Override
            public void onEdit(Vocabulary vocabulary, int position) {
                if (isSyncing) return;
                showVocabDialog(vocabulary, position);
            }

            @Override
            public void onDelete(Vocabulary vocabulary, int position) {
                if (isSyncing) return;
                confirmDeleteVocab(vocabulary, position);
            }
        });
        rvVocabList.setAdapter(adapter);
    }

    private void showLoading(boolean loading) {
        this.isSyncing = loading;
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        
        // Disable interactions while syncing
        View btnAdd = findViewById(R.id.btnAddVocab);
        View btnRename = findViewById(R.id.btnRenameTopic);
        if (btnAdd != null) btnAdd.setEnabled(!loading);
        if (btnRename != null) btnRename.setEnabled(!loading);

        if (rvVocabList != null) {
            rvVocabList.setEnabled(!loading);
            rvVocabList.setAlpha(loading ? 0.5f : 1.0f);
        }
    }

    private void fetchFullTopicData() {
        showLoading(true);
        topicApi.getTopicById(currentTopic.getId()).enqueue(new Callback<ApiResponse<Topic>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topic>> call, Response<ApiResponse<Topic>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    currentTopic = response.body().getData();
                    updateUI();
                } else {
                    Toast.makeText(TopicManagementActivity.this, "Không thể lấy dữ liệu chi tiết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topic>> call, Throwable t) {
                showLoading(false);
                Log.e("TOPIC_MGMT", "Fetch failed", t);
                Toast.makeText(TopicManagementActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentTopic == null) return;
        viewTopicName.setText(currentTopic.getName());
        
        vocabList.clear();
        if (currentTopic.getVocabularies() != null) {
            vocabList.addAll(currentTopic.getVocabularies());
        }

        currentTopic.setTotalWords(vocabList.size());
        
        Log.d("UI_UPDATE", "Topic: " + currentTopic.getName() + ", Vocabs: " + vocabList.size() + ", totalWords: " + currentTopic.getTotalWords());

        adapter.notifyDataSetChanged();
    }

    private void showVocabDialog(Vocabulary vocab, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_vocab, null);

        CustomInputField inputEnglish = dialogView.findViewById(R.id.dialogInputEnglish);
        CustomInputField inputVietnamese = dialogView.findViewById(R.id.dialogInputVietnamese);
        CustomInputField inputExample = dialogView.findViewById(R.id.dialogInputExample);

        if (vocab != null) {
            inputEnglish.setText(vocab.getEnglish());
            inputVietnamese.setText(vocab.getVietnamese());
            inputExample.setText(vocab.getExample());
        }

        builder.setView(dialogView);
        builder.setTitle(vocab == null ? "Thêm từ mới" : "Sửa từ vựng");
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String eng = inputEnglish.getEnteredText().trim();
            String vie = inputVietnamese.getEnteredText().trim();
            String ex = inputExample.getEnteredText().trim();

            if (eng.isEmpty() || vie.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Anh - Việt", Toast.LENGTH_SHORT).show();
                return;
            }

            if (vocab == null) {
                Vocabulary newVocab = new Vocabulary();
                newVocab.setEnglish(eng);
                newVocab.setVietnamese(vie);
                newVocab.setExample(ex);
                vocabList.add(newVocab);
                adapter.notifyItemInserted(vocabList.size() - 1);
            } else {
                vocab.setEnglish(eng);
                vocab.setVietnamese(vie);
                vocab.setExample(ex);
                adapter.notifyItemChanged(position);
            }
            syncVocabulariesWithServer();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showRenameTopicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi tên Topic");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setText(currentTopic.getName());
        input.setPadding(50, 20, 50, 20);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty() && !newName.equals(currentTopic.getName())) {
                renameTopicOnServer(newName);
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void renameTopicOnServer(String newName) {
        showLoading(true);
        String oldName = currentTopic.getName();
        currentTopic.setName(newName);

        topicApi.updateTopic(currentTopic.getId(), currentTopic).enqueue(new Callback<ApiResponse<Topic>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topic>> call, Response<ApiResponse<Topic>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentTopic = response.body().getData();
                    viewTopicName.setText(currentTopic.getName());
                    isDataChanged = true;
                    Toast.makeText(TopicManagementActivity.this, "Đã đổi tên topic", Toast.LENGTH_SHORT).show();
                } else {
                    currentTopic.setName(oldName);
                    Toast.makeText(TopicManagementActivity.this, "Lỗi khi đổi tên topic (500)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topic>> call, Throwable t) {
                showLoading(false);
                currentTopic.setName(oldName);
                Toast.makeText(TopicManagementActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncVocabulariesWithServer() {
        showLoading(true);

        currentTopic.setVocabularies(new ArrayList<>(vocabList));
        List<Vocabulary> listToSend = currentTopic.getVocabularies();

        Log.d("SYNC_VOCAB", "Sending updateVocabularies request. List size: " + listToSend.size());

        topicApi.updateVocabularies(currentTopic.getId(), listToSend).enqueue(new Callback<ApiResponse<Topic>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topic>> call, Response<ApiResponse<Topic>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Topic serverTopic = response.body().getData();

                    if (serverTopic.getVocabularies() != null) {
                        List<Vocabulary> serverList = serverTopic.getVocabularies();
                        List<Vocabulary> filteredList = new ArrayList<>();
                        
                        for (Vocabulary sv : serverList) {
                            boolean existsInLocal = false;
                            for (Vocabulary lv : vocabList) {
                                if (sv.getId() != null && lv.getId() != null) {
                                    if (sv.getId().equals(lv.getId())) {
                                        existsInLocal = true;
                                        break;
                                    }
                                } else if (sv.getEnglish().equals(lv.getEnglish()) && 
                                           sv.getVietnamese().equals(lv.getVietnamese())) {
                                    existsInLocal = true;
                                    break;
                                }
                            }
                            if (existsInLocal) {
                                filteredList.add(sv);
                            }
                        }
                        
                        currentTopic = serverTopic;
                        currentTopic.setVocabularies(filteredList);
                        currentTopic.setTotalWords(filteredList.size());
                        vocabList.clear();
                        vocabList.addAll(filteredList);
                        adapter.notifyDataSetChanged();
                    }
                    
                    updateUI();
                    isDataChanged = true;
                } else {
                    Log.e("SYNC_ERR", "Sync failed. Code: " + response.code());
                    Toast.makeText(TopicManagementActivity.this, "Lỗi server (" + response.code() + "). Đang tải lại...", Toast.LENGTH_SHORT).show();
                    fetchFullTopicData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topic>> call, Throwable t) {
                showLoading(false);
                Log.e("SYNC_FAIL", "Network error: " + t.getMessage());
                Toast.makeText(TopicManagementActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng", Toast.LENGTH_SHORT).show();
                fetchFullTopicData();
            }
        });
    }

    private void confirmDeleteVocab(Vocabulary vocabulary, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa từ")
                .setMessage("Bạn có chắc chắn muốn xóa từ '" + vocabulary.getEnglish() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int currentIdx = -1;
                    for (int i = 0; i < vocabList.size(); i++) {
                        Vocabulary v = vocabList.get(i);
                        if (vocabulary.getId() != null && v.getId() != null) {
                            if (v.getId().equals(vocabulary.getId())) {
                                currentIdx = i;
                                break;
                            }
                        } else if (v.getEnglish().equals(vocabulary.getEnglish()) && 
                                   v.getVietnamese().equals(vocabulary.getVietnamese())) {
                            currentIdx = i;
                            break;
                        }
                    }

                    if (currentIdx != -1) {
                        Vocabulary toDelete = vocabList.get(currentIdx);
                        vocabList.remove(currentIdx);

                        List<Vocabulary> updatedList = new ArrayList<>(vocabList);
                        currentTopic.setVocabularies(updatedList);
                        currentTopic.setTotalWords(updatedList.size()); 

                        adapter.notifyItemRemoved(currentIdx);
                        isDataChanged = true;

                        deleteLocalQuizCache();

                        deleteVocabFromServer(toDelete);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteVocabFromServer(Vocabulary vocabulary) {
        showLoading(true);

        String englishToDelete = vocabulary.getEnglish();
        List<String> englishWords = new ArrayList<>();
        englishWords.add(englishToDelete);

        Log.d("DELETE_VOCAB", "Request: DELETE api/v1/topics/" + currentTopic.getId() + "/vocabularies Body: [" + englishToDelete + "]");

        topicApi.deleteVocabularies(currentTopic.getId(), englishWords).enqueue(new Callback<ApiResponse<Topic>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topic>> call, Response<ApiResponse<Topic>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DELETE_SUCCESS", "Xóa thành công: " + englishToDelete);
                    Topic updatedTopic = response.body().getData();
                    
                    if (updatedTopic != null) {
                        currentTopic = updatedTopic;
                    } else {
                        if (currentTopic.getVocabularies() != null) {
                            List<Vocabulary> newList = new ArrayList<>(currentTopic.getVocabularies());
                            newList.remove(vocabulary);
                            currentTopic.setVocabularies(newList);
                            currentTopic.setTotalWords(newList.size());
                        }
                    }
                    
                    isDataChanged = true;
                    deleteLocalQuizCache();
                    
                    runOnUiThread(() -> {
                        updateUI();
                        Toast.makeText(TopicManagementActivity.this, "Đã xóa từ vựng thành công", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    String errorMsg = "Lỗi server";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("ERR_BODY", "Error reading error body", e);
                    }
                    Log.e("DELETE_ERR", "Code: " + response.code() + " Message: " + errorMsg);
                    Toast.makeText(TopicManagementActivity.this, "Server từ chối xóa (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    fetchFullTopicData(); 
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topic>> call, Throwable t) {
                showLoading(false);
                Log.e("DELETE_FAIL", "Lỗi kết nối", t);
                Toast.makeText(TopicManagementActivity.this, "Lỗi kết nối. Vui lòng kiểm tra mạng", Toast.LENGTH_SHORT).show();
                fetchFullTopicData();
            }
        });
    }

    private void deleteLocalQuizCache() {
        new Thread(() -> {
            try {
                com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
                db.localQuizDao().deleteQuestionsForTopic(currentTopic.getId());
            } catch (Exception e) {
                Log.e("DB_ERR", "Error clearing cache", e);
            }
        }).start();
    }
}