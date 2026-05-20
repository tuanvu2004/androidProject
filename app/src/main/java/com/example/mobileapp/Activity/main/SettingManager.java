package com.example.mobileapp.Activity.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.example.mobileapp.Activity.auth.SignInActivity;
import com.example.mobileapp.Custom.CustomBtn;
import com.example.mobileapp.R;
import com.example.mobileapp.session.SessionManager;

public class SettingManager {

    private static final String PREF_NAME = "settings_pref";
    private static final String KEY_DARK_MODE = "is_dark_mode";

    public static void applyTheme(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = pref.getBoolean(KEY_DARK_MODE, false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void init(View view, AppCompatActivity activity) {

        CustomBtn btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setText("Đăng xuất");

        SwitchCompat switchTheme = view.findViewById(R.id.switch_theme);

        TextView avtName = view.findViewById(R.id.avtname);

        SessionManager sessionManager = new SessionManager(activity);

        String email = sessionManager.getEmail();

        if (email != null && !email.isEmpty()) {
            avtName.setText(email);
        } else {
            avtName.setText("Unknown User");
        }

        SharedPreferences pref = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        switchTheme.setChecked(pref.getBoolean(KEY_DARK_MODE, false));

        btnLogout.setOnClickListener(v -> logout(activity));

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pref.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            applyTheme(activity);
            activity.recreate();
        });
    }

    public static void logout(AppCompatActivity activity) {
        new SessionManager(activity).clear();

        Intent intent = new Intent(activity, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(intent);
        activity.finish();
    }
}