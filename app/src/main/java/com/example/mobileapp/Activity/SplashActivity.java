package com.example.mobileapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobileapp.Activity.auth.SignInActivity;
import com.example.mobileapp.Activity.main.MainActivity;
import com.example.mobileapp.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        String token = session.getAccessToken();

        if (token != null && !token.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }

        finish();
    }
}