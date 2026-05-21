package com.furniture.api.dto.response;

import com.furniture.api.model.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Integer messageId;
    private String chatId;
    private Integer senderId;
    private String senderType;
    private String message;
    private String messageType;
    private String mediaUrl;
    private String mediaPublicId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private boolean mine;

    public static ChatMessageResponse fromEntity(ChatMessage msg, boolean adminMode) {
        boolean isMine = adminMode
                ? msg.getSenderType() == ChatMessage.SenderType.SHOP
                : msg.getSenderType() == ChatMessage.SenderType.USER;

        return ChatMessageResponse.builder()
                .messageId(msg.getMessageId())
                .chatId(msg.getChatId())
                .senderId(msg.getSenderId())
                .senderType(msg.getSenderType() != null ? msg.getSenderType().name() : null)
                .message(msg.getMessage())
                .messageType(msg.getMessageType() != null ? msg.getMessageType().name() : ChatMessage.MessageType.TEXT.name())
                .mediaUrl(msg.getMediaUrl())
                .mediaPublicId(msg.getMediaPublicId())
                .isRead(msg.getIsRead())
                .createdAt(msg.getCreatedAt())
                .mine(isMine)
                .build();
    }
}
