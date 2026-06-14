package com.furniture.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatbotMessage;
import com.furniture.app.data.model.request.ChatbotRequest;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ChatbotApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotRepository {

    private final ChatbotApi chatbotApi;

    public ChatbotRepository(String token) {
        chatbotApi = RetrofitClient.getInstance(token).create(ChatbotApi.class);
    }

    public LiveData<ApiResponse<ChatbotMessage>> sendMessage(String message, String conversationId) {
        MutableLiveData<ApiResponse<ChatbotMessage>> liveData = new MutableLiveData<>();
        chatbotApi.sendMessage(new ChatbotRequest(message, conversationId)).enqueue(new Callback<ApiResponse<ChatbotMessage>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChatbotMessage>> call, Response<ApiResponse<ChatbotMessage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(error("Không thể gửi tin nhắn"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ChatbotMessage>> call, Throwable t) {
                liveData.setValue(error("Lỗi kết nối mạng"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<List<ChatbotMessage>>> getHistory(String conversationId) {
        MutableLiveData<ApiResponse<List<ChatbotMessage>>> liveData = new MutableLiveData<>();
        chatbotApi.getHistory(conversationId).enqueue(new Callback<ApiResponse<List<ChatbotMessage>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChatbotMessage>>> call, Response<ApiResponse<List<ChatbotMessage>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(error("Không thể tải lịch sử chatbot"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChatbotMessage>>> call, Throwable t) {
                liveData.setValue(error("Lỗi kết nối mạng"));
            }
        });
        return liveData;
    }

    public LiveData<ApiResponse<Void>> deleteHistory(String conversationId) {
        MutableLiveData<ApiResponse<Void>> liveData = new MutableLiveData<>();
        chatbotApi.deleteHistory(conversationId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(error("Không thể xóa lịch sử chatbot"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.setValue(error("Lỗi kết nối mạng"));
            }
        });
        return liveData;
    }

    private <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}
