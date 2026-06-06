package com.example.mobileapp.network;

import com.example.mobileapp.model.ApiResponse;
import com.example.mobileapp.model.QuizHistoryPageResponse;
import com.example.mobileapp.model.QuizQuestion;
import com.example.mobileapp.model.QuizRequest;
import com.example.mobileapp.model.QuizResultResponse;
import com.example.mobileapp.model.QuizSubmissionRequest;
import com.example.mobileapp.model.Topic;
import com.example.mobileapp.model.TopicCreateRequest;
import com.example.mobileapp.model.TopicPageResponse;
import com.example.mobileapp.model.TopicSearchRequest;
import com.example.mobileapp.model.Vocabulary;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.HTTP;

public interface TopicApi {

    @Headers("Cache-Control: no-cache")
    @POST("api/v1/topics/search")
    Call<ApiResponse<TopicPageResponse>> searchTopics(
            @Body TopicSearchRequest request
    );

    @POST("api/v1/topics/generate-vocabularies")
    Call<ApiResponse<List<Vocabulary>>> generateVocabularies(
            @Body TopicCreateRequest request
    );

    @POST("api/v1/quiz/generate")
    Call<ApiResponse<List<QuizQuestion>>> generateQuiz(
            @Body QuizRequest request
    );

    @POST("api/v1/quiz/submit")
    Call<ApiResponse<QuizResultResponse>> submitQuiz(
            @Body QuizSubmissionRequest request
    );

    @Headers("Cache-Control: no-cache")
    @POST("api/v1/quiz/history/search")
    Call<ApiResponse<QuizHistoryPageResponse>> searchQuizHistory(
            @Body TopicSearchRequest request
    );

    @GET("api/v1/quiz/history/{resultId}")
    Call<ApiResponse<com.example.mobileapp.model.QuizResultResponse>> getQuizHistoryDetail(
            @Path("resultId") Long resultId
    );

    @Headers("Cache-Control: no-cache")
    @GET("api/v1/topics/{id}")
    Call<ApiResponse<Topic>> getTopicById(
            @Path("id") Long id
    );

    @DELETE("api/v1/topics/{id}")
    Call<okhttp3.ResponseBody> deleteTopic(
            @Path("id") Long id
    );

    @PUT("api/v1/topics/{id}")
    Call<ApiResponse<Topic>> updateTopic(
            @Path("id") Long id,
            @Body Topic topic
    );

    @PUT("api/v1/topics/{id}/vocabularies")
    Call<ApiResponse<Topic>> updateVocabularies(
            @Path("id") Long id,
            @Body List<Vocabulary> vocabularies
    );

    @HTTP(method = "DELETE", path = "api/v1/topics/{id}/vocabularies", hasBody = true)
    Call<ApiResponse<Topic>> deleteVocabularies(
            @Path("id") Long id,
            @Body List<String> words
    );
}