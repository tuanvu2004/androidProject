package com.example.mobileapp.Activity.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import com.example.mobileapp.Custom.CustomBtn;
import com.example.mobileapp.R;

public class RegisterSuccessActivity extends AuthBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater.from(this).inflate(
                R.layout.activity_register_success,
                container,
                true
        );

        CustomBtn btnBackToLogin = findViewById(R.id.btnBackToLogin);
        btnBackToLogin.setText("Quay lại Đăng nhập");
        btnBackToLogin.setOnClickListener(v -> {
            finish(); // Trở về màn hình trước đó (thường là Signup hoặc Signin)
        });
    }
}
