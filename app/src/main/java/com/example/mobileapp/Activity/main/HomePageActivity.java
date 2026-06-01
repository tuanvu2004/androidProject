package com.example.mobileapp.Activity.main;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.Adapter.TopicAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.TopicPageResponse;
import com.example.mobileapp.model.TopicSearchRequest;
import com.example.mobileapp.network.ApiClient;
import com.example.mobileapp.network.TopicApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView rvTopics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        rvTopics = findViewById(R.id.rvTopics);

        rvTopics.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadTopics();
    }

    private void loadTopics() {

        TopicApi topicApi =
                ApiClient.getClient(this)
                        .create(TopicApi.class);

        topicApi.searchTopics(new TopicSearchRequest())
                .enqueue(
                        new Callback<ApiResponse<TopicPageResponse>>() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<TopicPageResponse>> call,
                                    Response<ApiResponse<TopicPageResponse>> response
                            ) {
                                Log.d("TOPIC_API", "HTTP Code: " + response.code());

                                if (response.isSuccessful() && response.body() != null) {
                                    ApiResponse<TopicPageResponse> apiResponse = response.body();
                                    Log.d("TOPIC_API", "API Message: " + apiResponse.getMessage());

                                    if (apiResponse.getData() != null) {
                                        TopicPageResponse page = apiResponse.getData();
                                        if (page.getItems() != null && !page.getItems().isEmpty()) {
                                            TopicAdapter adapter = new TopicAdapter(page.getItems());
                                            rvTopics.setAdapter(adapter);
                                            Log.d("TOPIC_API", "Success: Found " + page.getItems().size() + " items");
                                        } else {
                                            Log.w("TOPIC_API", "Warning: List is empty");
                                        }
                                    } else {
                                        Log.e("TOPIC_API", "Error: Data field is null");
                                    }
                                } else {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                        Log.e("TOPIC_API", "Request Failed: " + errorBody);
                                    } catch (Exception e) {
                                        Log.e("TOPIC_API", "Error parsing error body", e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(
                                    Call<ApiResponse<TopicPageResponse>> call,
                                    Throwable t
                            ) {

                                Log.e(
                                        "TOPIC_API",
                                        "Error",
                                        t
                                );
                            }
                        }
                );
    }
}