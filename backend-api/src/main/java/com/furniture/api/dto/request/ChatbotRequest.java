package com.furniture.api.dto.request;

import lombok.Data;

@Data
public class ChatbotRequest {
    private String message;
    private String conversationId;
}
