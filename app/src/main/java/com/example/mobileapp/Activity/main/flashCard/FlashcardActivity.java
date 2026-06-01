package com.example.mobileapp.Activity.main.flashCard;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mobileapp.R;
import com.example.mobileapp.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView tvProgress;
    private ImageButton btnBack;

    private FlashcardAdapter adapter;
    private List<Vocabulary> flashcards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        initViews();
        setupBackButton();

        loadFromIntent(); // 🔥 lấy data thật

        setupViewPager();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPagerFlashcard);
        tvProgress = findViewById(R.id.tvProgress);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            Log.d("FLASHCARD", "Back clicked");
            finish();
        });
    }

    private void loadFromIntent() {

        // 🔥 lấy list từ Intent
        List<Vocabulary> incomingList =
                (List<Vocabulary>) getIntent().getSerializableExtra("VOCAB_LIST");

        flashcards.clear();

        if (incomingList != null && !incomingList.isEmpty()) {
            flashcards.addAll(incomingList);
        } else {
            Log.w("FLASHCARD", "No data received from Intent");
        }

        String topicName = getIntent().getStringExtra("TOPIC_NAME");
        if (topicName != null) {
            setTitle(topicName);
        }
    }

    private void setupViewPager() {

        adapter = new FlashcardAdapter(flashcards);
        viewPager.setAdapter(adapter);

        if (!flashcards.isEmpty()) {
            updateProgress(1, flashcards.size());
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress(position + 1, flashcards.size());
            }
        });
    }

    private void updateProgress(int current, int total) {
        tvProgress.setText(current + " / " + total);
    }
}