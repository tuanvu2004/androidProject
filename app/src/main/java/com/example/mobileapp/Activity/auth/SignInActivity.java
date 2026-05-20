package com.example.mobileapp.Activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.mobileapp.model.LoginData;
import com.example.mobileapp.model.LoginRequest;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.AuthApi;
import com.example.mobileapp.session.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AuthBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingManager.applyTheme(this);

        super.onCreate(savedInstanceState);

        LayoutInflater.from(this).inflate(
                R.layout.activity_signin_content,
                container,
                true
        );

        initUI();
    }

    private void initUI() {

        CustomInputField inputUser = findViewById(R.id.inputUser);
        CustomInputField inputPass = findViewById(R.id.inputPass);
        CustomBtn btnSignIn = findViewById(R.id.btnSignIn);
        TextView tvSignup = findViewById(R.id.tvSignup);
        TextView tvForgot = findViewById(R.id.tvForgot);

        // Loading overlay
        View loadingOverlay = findViewById(R.id.loadingOverlay);

        inputUser.setLabelText("Email");
        inputPass.setLabelText("Mật khẩu");

        ((EditText) inputPass.findViewById(R.id.edtInput))
                .setInputType(android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btnSignIn.setText("Đăng nhập");

        AuthApi api = ApiClient.getClient(this).create(AuthApi.class);

        btnSignIn.setOnClickListener(v -> {

            String email = inputUser.getEnteredText();
            String password = inputPass.getEnteredText();

            inputUser.clearError();
            inputPass.clearError();

            // Validate email
            String emailError = InputValidator.validateEmail(email);
            if (emailError != null) {
                inputUser.setError(emailError);
                return;
            }

            // Validate password
            String passError = InputValidator.validatePassword(password);
            if (passError != null) {
                inputPass.setError(passError);
                return;
            }

            // Show loading
            loadingOverlay.setVisibility(View.VISIBLE);
            btnSignIn.setEnabled(false);

            LoginRequest request = new LoginRequest(email, password);

            api.login(request).enqueue(new Callback<ApiResponse<LoginData>>() {

                @Override
                public void onResponse(Call<ApiResponse<LoginData>> call,
                                       Response<ApiResponse<LoginData>> response) {

                    // Hide loading
                    loadingOverlay.setVisibility(View.GONE);
                    btnSignIn.setEnabled(true);

                    if (response.isSuccessful()
                            && response.body() != null
                            && "0".equals(response.body().getCode())) {

                        LoginData data = response.body().getData();

                        SessionManager session =
                                new SessionManager(SignInActivity.this);

                        session.saveToken(
                                data.getAccessToken(),
                                data.getRefreshToken()
                        );

                        session.saveEmail(email);

                        Toast.makeText(
                                SignInActivity.this,
                                "Login thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                SignInActivity.this,
                                MainActivity.class
                        );

                        startActivity(intent);
                        finish();

                    } else {
                        inputPass.setError("Sai email hoặc mật khẩu");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<LoginData>> call,
                                      Throwable t) {

                    // Hide loading
                    loadingOverlay.setVisibility(View.GONE);
                    btnSignIn.setEnabled(true);

                    inputPass.setError("Lỗi kết nối server");
                }
            });
        });

        // SIGNUP
        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        // FORGOT PASSWORD
        tvForgot.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPassActivity.class));
        });
    }
}