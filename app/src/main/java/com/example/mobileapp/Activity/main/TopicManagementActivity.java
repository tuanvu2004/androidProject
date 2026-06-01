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
        
        findViewById(R.id.btnAddVocab).setOnClickListener(v -> showVocabDialog(null, -1));

        findViewById(R.id.btnRenameTopic).setOnClickListener(v -> showRenameTopicDialog());
        viewTopicName.setText(currentTopic.getName());
        
        rvVocabList.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new VocabEditAdapter(vocabList, new VocabEditAdapter.OnVocabActionListener() {
            @Override
            public void onEdit(Vocabulary vocabulary, int position) {
                showVocabDialog(vocabulary, position);
            }

            @Override
            public void onDelete(Vocabulary vocabulary, int position) {
                confirmDeleteVocab(vocabulary, position);
            }
        });
        rvVocabList.setAdapter(adapter);
    }

    private void showLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
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
            } else {
                vocab.setEnglish(eng);
                vocab.setVietnamese(vie);
                vocab.setExample(ex);
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
                    currentTopic.setName(oldName); // Rollback
                    Toast.makeText(TopicManagementActivity.this, "Lỗi khi đổi tên topic (500)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topic>> call, Throwable t) {
                showLoading(false);
                currentTopic.setName(oldName); // Rollback
                Toast.makeText(TopicManagementActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncVocabulariesWithServer() {
        showLoading(true);
        List<Vocabulary> listToSend = new ArrayList<>(vocabList);
        
        Log.d("SYNC_VOCAB", "Sending updateVocabularies request. List size: " + listToSend.size());

        topicApi.updateVocabularies(currentTopic.getId(), listToSend).enqueue(new Callback<ApiResponse<Topic>>() {
            @Override
            public void onResponse(Call<ApiResponse<Topic>> call, Response<ApiResponse<Topic>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentTopic = response.body().getData();
                    updateUI(); 
                    deleteLocalQuizCache();
                    isDataChanged = true;
                    Toast.makeText(TopicManagementActivity.this, "Đã cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("SYNC_ERR", "Sync failed. Code: " + response.code());
                    Toast.makeText(TopicManagementActivity.this, "Lỗi server khi đồng bộ (500)", Toast.LENGTH_SHORT).show();
                    fetchFullTopicData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Topic>> call, Throwable t) {
                showLoading(false);
                Log.e("SYNC_FAIL", "Network error: " + t.getMessage());
                Toast.makeText(TopicManagementActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteVocab(Vocabulary vocabulary, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa từ")
                .setMessage("Bạn có chắc chắn muốn xóa từ '" + vocabulary.getEnglish() + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (position >= 0 && position < vocabList.size()) {
                        vocabList.remove(position);
                        syncVocabulariesWithServer();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteLocalQuizCache() {
        new Thread(() -> {
            com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
            db.localQuizDao().deleteQuestionsForTopic(currentTopic.getId());
        }).start();
    }
}
