package com.example.mobileapp.Activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.Activity.main.MainActivity;
import com.example.mobileapp.Activity.main.SettingManager;
import com.example.mobileapp.Custom.CustomBtn;
import com.example.mobileapp.Custom.CustomInputField;
import com.example.mobileapp.R;
import com.example.mobileapp.Validator.InputValidator;

public class SignupActivity extends AuthBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingManager.applyTheme(this);

        super.onCreate(savedInstanceState);

        LayoutInflater.from(this).inflate(
                R.layout.activity_signup_content,
                container,
                true
        );

        initUI();
    }

    private void initUI() {

        CustomInputField inputEmail = findViewById(R.id.inputEmail);
        CustomInputField inputPass = findViewById(R.id.inputPass);
        CustomInputField inputConfirmPass = findViewById(R.id.inputConfirmPass);
        CustomBtn btnSignUp = findViewById(R.id.btnSignUp);
        TextView tvLogin = findViewById(R.id.tvLogin);

        inputEmail.setLabelText("Email");
        inputPass.setLabelText("Mật khẩu");
        inputConfirmPass.setLabelText("Nhập lại mật khẩu");

        ((EditText) inputPass.findViewById(R.id.edtInput))
                .setInputType(android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ((EditText) inputConfirmPass.findViewById(R.id.edtInput))
                .setInputType(android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnSignUp.setText("Đăng ký");

        btnSignUp.setOnClickListener(v -> {

            String email = inputEmail.getEnteredText();
            String pass = inputPass.getEnteredText();
            String confirmPass = inputConfirmPass.getEnteredText();

            inputEmail.clearError();
            inputPass.clearError();
            inputConfirmPass.clearError();

            String emailError = InputValidator.validateEmail(email);
            if (emailError != null) {
                inputEmail.setError(emailError);
                return;
            }
            String passError = InputValidator.validatePassword(pass);
            if (passError != null) {
                inputPass.setError(passError);
                return;
            }
            String confirmError = InputValidator.validateConfirm(pass, confirmPass);
            if (confirmError != null) {
                inputConfirmPass.setError(confirmError);
                return;
            }

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SignupActivity.this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            finish();
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}