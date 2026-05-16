package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatMessage;
import com.furniture.app.data.model.ChatRoomItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApi {

    @POST("chat/send")
    Call<ApiResponse<ChatMessage>> sendMessage(@Body SendMessageRequest request);

    @GET("chat/messages/{chatId}")
    Call<ApiResponse<List<ChatMessage>>> getMessages(@Path("chatId") String chatId);

    @GET("chat/rooms")
    Call<ApiResponse<List<ChatRoomItem>>> getChatRooms();

    class SendMessageRequest {
        private String message;
        private Integer recipientUserId;

        public SendMessageRequest(String message, Integer recipientUserId) {
            this.message = message;
            this.recipientUserId = recipientUserId;
        }

        public String getMessage() { return message; }
        public Integer getRecipientUserId() { return recipientUserId; }
    }
}
