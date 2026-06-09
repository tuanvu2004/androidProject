package com.example.mobileapp.Activity.main;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.R;
import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.QuizQuestion;
import com.example.mobileapp.model.QuizResultResponse;
import com.example.mobileapp.model.QuizSubmissionRequest;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.TopicApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private TextView txtTopicTitle, txtProgress, txtQuestionWord, txtQuestionExample;
    private ProgressBar quizProgressBar;
    private LinearLayout optionsContainer;
    private ImageButton btnBackQuiz;
    private Button btnSubmitQuiz;

    private List<QuizQuestion> questions;
    private int currentIndex = 0;
    private Long topicId;
    private List<QuizSubmissionRequest.AnswerRequest> userAnswers = new ArrayList<>();
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questions = (List<QuizQuestion>) getIntent().getSerializableExtra("QUESTIONS");
        topicId = getIntent().getLongExtra("TOPIC_ID", -1);
        String topicName = getIntent().getStringExtra("TOPIC_NAME");
        startTime = System.currentTimeMillis();

        if (questions == null || questions.isEmpty() || topicId == -1) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        if (topicName != null) {
            txtTopicTitle.setText(topicName);
        }
        displayQuestion();
    }

    private void initViews() {
        txtTopicTitle = findViewById(R.id.txtTopicTitle);
        txtProgress = findViewById(R.id.txtProgress);
        txtQuestionWord = findViewById(R.id.txtQuestionWord);
        txtQuestionExample = findViewById(R.id.txtQuestionExample);
        quizProgressBar = findViewById(R.id.quizProgressBar);
        optionsContainer = findViewById(R.id.optionsContainer);
        btnBackQuiz = findViewById(R.id.btnBackQuiz);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);

        btnBackQuiz.setOnClickListener(v -> finish());
        btnSubmitQuiz.setOnClickListener(v -> submitQuiz());
        quizProgressBar.setMax(questions.size());
    }

    private void displayQuestion() {
        optionsContainer.removeAllViews();
        QuizQuestion q = questions.get(currentIndex);

        txtProgress.setText(String.format(Locale.getDefault(), "%d/%d", currentIndex + 1, questions.size()));
        quizProgressBar.setProgress(currentIndex + 1);
        txtQuestionWord.setText(q.getEnglish());
        txtQuestionExample.setText(q.getExample());

        for (String option : q.getOptions()) {
            Button btn = new Button(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 15, 0, 15);
            btn.setLayoutParams(params);
            btn.setText(option);
            btn.setAllCaps(false);
            btn.setBackgroundResource(R.drawable.bg_text);
            btn.setPadding(20, 30, 20, 30);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            btn.setOnClickListener(v -> {
                recordAnswer(q.getWordId(), option);
                nextQuestion();
            });
            optionsContainer.addView(btn);
        }
    }

    private void recordAnswer(Long wordId, String answer) {
        userAnswers.add(new QuizSubmissionRequest.AnswerRequest(wordId, answer));
    }

    private void nextQuestion() {
        currentIndex++;
        if (currentIndex < questions.size()) {
            displayQuestion();
        } else {
            optionsContainer.removeAllViews();
            txtQuestionWord.setText("Hoàn thành!");
            txtQuestionExample.setText("Vui lòng nhấn nút Nộp bài để xem kết quả.");
            btnSubmitQuiz.setVisibility(View.VISIBLE);
        }
    }

    private void submitQuiz() {
        int timeTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);
        QuizSubmissionRequest request = new QuizSubmissionRequest(topicId, userAnswers, timeTaken);
        TopicApi api = ApiClient.getClient(this).create(TopicApi.class);

        btnSubmitQuiz.setEnabled(false);
        btnSubmitQuiz.setText("Đang nộp...");

        api.submitQuiz(request).enqueue(new Callback<ApiResponse<QuizResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<QuizResultResponse>> call, Response<ApiResponse<QuizResultResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showResult(response.body().getData());
                } else {
                    btnSubmitQuiz.setEnabled(true);
                    btnSubmitQuiz.setText("Nộp bài");
                    Toast.makeText(QuizActivity.this, "Lỗi nộp bài", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<QuizResultResponse>> call, Throwable t) {
                btnSubmitQuiz.setEnabled(true);
                btnSubmitQuiz.setText("Nộp bài");
                Toast.makeText(QuizActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResult(QuizResultResponse result) {
        // Cập nhật email cho topic này trong database local sau khi thi xong (để claim sở hữu)
        new Thread(() -> {
            com.example.mobileapp.session.SessionManager session = new com.example.mobileapp.session.SessionManager(this);
            String userEmail = session.getEmail();
            if (userEmail != null && topicId != null) {
                com.example.mobileapp.database.AppDatabase db = com.example.mobileapp.database.AppDatabase.getDatabase(this);
                // Kiểm tra xem topic này đã có trong local chưa, nếu chưa hoặc email null thì update
                // (Thực tế searchTopics ở MainActivity sẽ lưu vào local, ở đây ta củng cố lại)
                List<com.example.mobileapp.database.entity.LocalTopic> userTopics = db.localTopicDao().getTopicsForUser(userEmail);
                boolean found = false;
                for (com.example.mobileapp.database.entity.LocalTopic lt : userTopics) {
                    if (lt.getId().equals(topicId)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // Nếu chưa thấy topic này gán cho user, ta có thể tạo mới bản ghi local hoặc 
                    // tốt nhất là đợi lần search tiếp theo. Nhưng để chắc chắn hiện ngay:
                    // Ta không có tên Topic ở đây trừ khi lấy từ Intent
                    String topicName = getIntent().getStringExtra("TOPIC_NAME");
                    com.example.mobileapp.database.entity.LocalTopic local = new com.example.mobileapp.database.entity.LocalTopic();
                    local.setId(topicId);
                    local.setName(topicName != null ? topicName : "Topic " + topicId);
                    local.setUserEmail(userEmail);
                    db.localTopicDao().insertTopic(local);
                }
            }
        }).start();

        setContentView(R.layout.layout_quiz_result);

        TextView txtTopicName = findViewById(R.id.txtResultTopicName);
        TextView txtScore = findViewById(R.id.txtResultScore);
        TextView txtStats = findViewById(R.id.txtResultStats);
        Button btnBack = findViewById(R.id.btnFinishQuiz);
        Button btnReview = findViewById(R.id.btnReviewQuiz);

        String topicName = getIntent().getStringExtra("TOPIC_NAME");
        if (topicName != null) {
            txtTopicName.setText(topicName);
        }

        int correctCount = 0;
        if (result.getAnswers() != null) {
            for (QuizResultResponse.AnswerDetail detail : result.getAnswers()) {
                if (detail.isCorrect()) {
                    correctCount++;
                }
            }
        }

        txtScore.setText(String.valueOf(result.getScore()));
        txtStats.setText(String.format(Locale.getDefault(), "Đúng %d/%d câu", correctCount, result.getTotalQuestion()));

        btnBack.setOnClickListener(v -> finish());
        btnReview.setOnClickListener(v -> {
            if (result.getResultId() != null) {
                fetchQuizHistoryAndReview(result.getResultId());
            } else {
                Toast.makeText(this, "Không tìm thấy ID kết quả", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchQuizHistoryAndReview(Long resultId) {
        TopicApi api = ApiClient.getClient(this).create(TopicApi.class);
        api.getQuizHistoryDetail(resultId).enqueue(new Callback<ApiResponse<QuizResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<QuizResultResponse>> call, Response<ApiResponse<QuizResultResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    android.content.Intent intent = new android.content.Intent(QuizActivity.this, ReviewQuizActivity.class);
                    intent.putExtra("QUIZ_RESULT", response.body().getData());
                    startActivity(intent);
                } else {
                    Toast.makeText(QuizActivity.this, "Không thể tải chi tiết kết quả", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<QuizResultResponse>> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "Lỗi kết nối khi tải kết quả", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
