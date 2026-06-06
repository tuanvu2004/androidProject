package com.example.mobileapp.network;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                    message -> Log.d("OkHttp", message)
            );
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new ApiInterceptor(context))
                    .addInterceptor(logging)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .callTimeout(360, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}