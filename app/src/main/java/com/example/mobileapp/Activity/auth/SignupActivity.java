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
import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.RegisterRequest;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.AuthApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AuthBaseActivity {

    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingManager.applyTheme(this);

        super.onCreate(savedInstanceState);

        authApi = ApiClient.getClient(this).create(AuthApi.class);

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

            RegisterRequest request = new RegisterRequest(email, pass);
            
            btnSignUp.setEnabled(false); // Ngăn nhấn nhiều lần
            
            authApi.register(request).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    btnSignUp.setEnabled(true);
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(SignupActivity.this, RegisterSuccessActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = "Đăng ký thất bại";
                        try {
                            if (response.errorBody() != null) {
                                errorMsg = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(SignupActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    btnSignUp.setEnabled(true);
                    Toast.makeText(SignupActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}