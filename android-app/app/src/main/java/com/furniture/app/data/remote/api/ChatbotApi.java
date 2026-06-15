package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatbotMessage;
import com.furniture.app.data.model.request.ChatbotRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatbotApi {

    @POST("chatbot/message")
    Call<ApiResponse<ChatbotMessage>> sendMessage(@Body ChatbotRequest request);

    @GET("chatbot/history")
    Call<ApiResponse<List<ChatbotMessage>>> getHistory(@Query("conversationId") String conversationId);

    @DELETE("chatbot/history")
    Call<ApiResponse<Void>> deleteHistory(@Query("conversationId") String conversationId);
}
