package com.example.mobileapp.network;

import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.Topic;
import com.example.mobileapp.model.TopicCreateRequest;
import com.example.mobileapp.model.TopicPageResponse;
import com.example.mobileapp.model.TopicSearchRequest;
import com.example.mobileapp.model.Vocabulary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TopicApi {

    @POST("api/v1/topics/search")
    Call<ApiResponse<TopicPageResponse>> searchTopics(
            @Body TopicSearchRequest request
    );

    @POST("api/v1/topics/generate-vocabularies")
    Call<ApiResponse<List<Vocabulary>>> generateVocabularies(
            @Body TopicCreateRequest request
    );

    @POST("api/v1/quiz/generate")
    Call<ApiResponse<List<com.example.mobileapp.model.QuizQuestion>>> generateQuiz(
            @Body com.example.mobileapp.model.QuizRequest request
    );

    @POST("api/v1/quiz/submit")
    Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> submitQuiz(
            @Body com.example.mobileapp.model.QuizSubmissionRequest request
    );

    @POST("api/v1/quiz/history/search")
    Call<ApiResponse<com.example.mobileapp.model.QuizHistoryPageResponse>> searchQuizHistory(
            @Body com.example.mobileapp.model.TopicSearchRequest request
    );

    @GET("api/v1/quiz/history/{resultId}")
    Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> getQuizHistoryDetail(
            @Path("resultId") Long resultId
    );
}