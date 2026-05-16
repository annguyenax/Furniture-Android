package com.furniture.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    private String chatId;
    private Integer userId;
    private String userName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private long unreadCount;
}
