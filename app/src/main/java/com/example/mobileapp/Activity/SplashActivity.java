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

        String accessToken = session.getAccessToken();
        String refreshToken = session.getRefreshToken();

        // Nếu còn ít nhất một trong hai token thì cho vào Main
        if ((accessToken != null && !accessToken.isEmpty()) || 
            (refreshToken != null && !refreshToken.isEmpty())) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, SignInActivity.class));
        }

        finish();
    }
}