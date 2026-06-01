package com.example.mobileapp.Activity.main;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.Adapter.ReviewAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.model.QuizResultResponse;

import java.util.List;

public class ReviewQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_quiz);

        QuizResultResponse result = (QuizResultResponse) getIntent().getSerializableExtra("QUIZ_RESULT");
        if (result == null || result.getAnswers() == null) {
            finish();
            return;
        }

        ImageButton btnBack = findViewById(R.id.btnBackReview);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rvReview = findViewById(R.id.rvReviewDetails);
        rvReview.setLayoutManager(new LinearLayoutManager(this));
        rvReview.setAdapter(new ReviewAdapter(result.getAnswers()));
    }
}