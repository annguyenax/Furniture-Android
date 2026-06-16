package com.furniture.app.data.model.request;

public class ChatbotRequest {
    private String message;
    private String conversationId;

    public ChatbotRequest(String message, String conversationId) {
        this.message = message;
        this.conversationId = conversationId;
    }

    public String getMessage() {
        return message;
    }

    public String getConversationId() {
        return conversationId;
    }
}
