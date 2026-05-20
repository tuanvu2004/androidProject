package com.example.mobileapp.Activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.mobileapp.R;
import com.example.mobileapp.Activity.auth.SignInActivity;
import com.example.mobileapp.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnHome, btnLibrary, btnTrophy, btnSettings, btnAdd;
    private FrameLayout container;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        applySavedTheme();

        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (session.getAccessToken() == null) {
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
        btnHome = findViewById(R.id.btn_home);
        btnLibrary = findViewById(R.id.btn_library);
        btnTrophy = findViewById(R.id.btn_trophy);
        btnSettings = findViewById(R.id.btn_settings);
        btnAdd = findViewById(R.id.btn_add);
        container = findViewById(R.id.container);
    }

    private void setupNavigation() {
        btnHome.setOnClickListener(v -> switchTab(0));
        btnLibrary.setOnClickListener(v -> switchTab(1));
        btnTrophy.setOnClickListener(v -> switchTab(2));
        btnAdd.setOnClickListener(v -> switchTab(3));
        btnSettings.setOnClickListener(v -> switchTab(4));
    }

    private void switchTab(int tab) {
        currentTab = tab;

        switch (tab) {
            case 0:
                loadPage(R.layout.layout_home);
                selectTab(btnHome);
                break;
            case 1:
                loadPage(R.layout.layout_library);
                selectTab(btnLibrary);
                break;
            case 2:
                loadPage(R.layout.layout_trophy);
                selectTab(btnTrophy);
                break;
            case 3:
                loadPage(R.layout.activity_create_topic);
                selectTab(btnAdd);
                break;
            case 4:
                loadSettings();
                selectTab(btnSettings);
                break;
        }
    }

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