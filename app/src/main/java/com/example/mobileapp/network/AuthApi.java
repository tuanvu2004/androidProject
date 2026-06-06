package com.example.mobileapp.network;

import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.LoginData;
import com.example.mobileapp.model.LoginRequest;
import com.example.mobileapp.model.RefreshRequest;
import com.example.mobileapp.model.LogoutRequest;
import com.example.mobileapp.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {

    @POST("api/v1/auth/login")
    Call<ApiResponse<LoginData>> login(@Body LoginRequest request);

    @POST("api/v1/auth/register")
    Call<ApiResponse<String>> register(@Body RegisterRequest request);

    @GET("api/v1/auth/verify-register")
    Call<ApiResponse<String>> verifyRegister(@Query("token") String token);

    @POST("api/v1/auth/refresh")
    Call<ApiResponse<LoginData>> refresh(@Body RefreshRequest request);

    @POST("api/v1/auth/logout")
    Call<ApiResponse<Object>> logout(@Body LogoutRequest request);

}