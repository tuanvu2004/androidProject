package com.example.mobileapp.Validator;
public class InputValidator {

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Vui lòng nhập email";
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Email không hợp lệ";
        }
        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "Vui lòng nhập mật khẩu";
        }
        if (password.length() < 8) {
            return "Mật khẩu tối thiểu 8 ký tự";
        }
        return null;
    }

    public static String validateConfirm(String pass, String confirm) {
        if (confirm == null || confirm.isEmpty()) {
            return "Vui lòng nhập lại mật khẩu";
        }
        if (!pass.equals(confirm)) {
            return "Mật khẩu không khớp";
        }
        return null;
    }
}