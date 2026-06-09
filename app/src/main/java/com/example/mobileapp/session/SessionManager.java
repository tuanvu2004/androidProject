package com.example.mobileapp.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "app_session";

    private static final String KEY_ACCESS = "access_token";
    private static final String KEY_REFRESH = "refresh_token";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String access, String refresh) {
        prefs.edit()
                .putString(KEY_ACCESS, access)
                .putString(KEY_REFRESH, refresh)
                .apply();
    }

    public void saveEmail(String email) {
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public String getEmail() {
        String email = prefs.getString(KEY_EMAIL, null);
        return (email == null || email.isEmpty()) ? null : email;
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH, null);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}