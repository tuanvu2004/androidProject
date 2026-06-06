package com.example.mobileapp.network;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.LoginData;
import com.example.mobileapp.model.RefreshRequest;
import com.example.mobileapp.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class ApiInterceptor implements Interceptor {

    private final Context context;
    private final SessionManager sessionManager;

    public ApiInterceptor(Context context) {
        this.context = context.getApplicationContext();
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String url = original.url().toString();
        
        String deviceId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        Request.Builder builder = original.newBuilder()
                .addHeader("x-device-id", deviceId);

        String token = sessionManager.getAccessToken();
        if (token != null && !url.contains("/auth/login") && !url.contains("/auth/refresh")) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(builder.build());

        if (response.code() == 401 && !url.contains("/auth/refresh")) {
            synchronized (this) {
                String refreshToken = sessionManager.getRefreshToken();
                if (refreshToken != null) {
                    try {
                        AuthApi authApi = ApiClient.getClient(context).create(AuthApi.class);
                        Call<ApiResponse<LoginData>> call = authApi.refresh(new RefreshRequest(refreshToken));
                        
                        retrofit2.Response<ApiResponse<LoginData>> refreshRes = call.execute();

                        if (refreshRes.isSuccessful() && refreshRes.body() != null && "0".equals(refreshRes.body().getCode())) {
                            LoginData data = refreshRes.body().getData();
                            sessionManager.saveToken(data.getAccessToken(), data.getRefreshToken());

                            response.close();
                            
                            Request retryRequest = original.newBuilder()
                                    .header("Authorization", "Bearer " + data.getAccessToken())
                                    .header("x-device-id", deviceId)
                                    .build();
                            return chain.proceed(retryRequest);
                        }
                    } catch (Exception e) {
                        Log.e("ApiInterceptor", "Refresh failed", e);
                    }
                }
            }
        }

        return response;
    }
}