package com.example.mobileapp.Activity.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobileapp.Activity.main.SettingManager;

import com.example.mobileapp.Custom.CustomBtn;
import com.example.mobileapp.Custom.CustomInputField;
import com.example.mobileapp.R;
import com.example.mobileapp.Validator.InputValidator;

public class ForgotPassActivity extends AuthBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingManager.applyTheme(this);

        super.onCreate(savedInstanceState);

        LayoutInflater.from(this).inflate(
                R.layout.activity_forgotpass_content,
                container,
                true
        );

        initUI();
    }

    private void initUI() {

        CustomInputField inputEmail = findViewById(R.id.inputEmail);
        CustomInputField inputNewPass = findViewById(R.id.inputNewPass);
        CustomInputField inputConfirmPass = findViewById(R.id.inputConfirmPass);
        CustomBtn btnReset = findViewById(R.id.btnReset);
        TextView tvBack = findViewById(R.id.tvBack);

        inputEmail.setLabelText("Email");
        inputNewPass.setLabelText("Mật khẩu mới");
        inputConfirmPass.setLabelText("Nhập lại mật khẩu");

        ((EditText) inputNewPass.findViewById(R.id.edtInput))
                .setInputType(android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ((EditText) inputConfirmPass.findViewById(R.id.edtInput))
                .setInputType(android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnReset.setText("Đặt lại mật khẩu");

        btnReset.setOnClickListener(v -> {

            String email = inputEmail.getEnteredText();
            String pass = inputNewPass.getEnteredText();
            String confirm = inputConfirmPass.getEnteredText();

            inputEmail.clearError();
            inputNewPass.clearError();
            inputConfirmPass.clearError();

            String emailErr = InputValidator.validateEmail(email);
            if (emailErr != null) {
                inputEmail.setError(emailErr);
                return;
            }

            String passErr = InputValidator.validatePassword(pass);
            if (passErr != null) {
                inputNewPass.setError(passErr);
                return;
            }

            String confirmErr = InputValidator.validateConfirm(pass, confirm);
            if (confirmErr != null) {
                inputConfirmPass.setError(confirmErr);
                return;
            }

            // TODO


            finish();
        });

        tvBack.setOnClickListener(v -> finish());
    }
}