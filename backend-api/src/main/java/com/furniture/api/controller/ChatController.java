package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.ChatMessageResponse;
import com.furniture.api.dto.response.ChatRoomResponse;
import com.furniture.api.model.ChatMessage;
import com.furniture.api.model.User;
import com.furniture.api.repository.ChatMessageRepository;
import com.furniture.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private static final int SHOP_ID = 1;

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // ─── Send message ─────────────────────────────────────────────────────────

    @PostMapping("/send")
    @Transactional
    public ResponseEntity<ApiResponse<ChatMessageResponse>> sendMessage(
            @RequestBody SendMessageRequest request,
            Authentication authentication) {

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Tin nhắn không được trống"));
        }

        User me = getCurrentUser(authentication);
        boolean adminMode = isAdmin(authentication);

        ChatMessage msg;
        if (adminMode) {
            if (request.getRecipientUserId() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Thiếu recipientUserId"));
            }
            String chatId = ChatMessage.createChatId(request.getRecipientUserId(), SHOP_ID);
            msg = ChatMessage.builder()
                    .chatId(chatId)
                    .senderId(SHOP_ID)
                    .senderType(ChatMessage.SenderType.SHOP)
                    .receiverId(request.getRecipientUserId())
                    .receiverType(ChatMessage.SenderType.USER)
                    .message(request.getMessage().trim())
                    .isRead(false)
                    .build();
        } else {
            String chatId = ChatMessage.createChatId(me.getUserId(), SHOP_ID);
            msg = ChatMessage.builder()
                    .chatId(chatId)
                    .senderId(me.getUserId())
                    .senderType(ChatMessage.SenderType.USER)
                    .receiverId(SHOP_ID)
                    .receiverType(ChatMessage.SenderType.SHOP)
                    .message(request.getMessage().trim())
                    .isRead(false)
                    .build();
        }

        msg = chatMessageRepository.save(msg);
        return ResponseEntity.ok(ApiResponse.success("Đã gửi", ChatMessageResponse.fromEntity(msg, adminMode)));
    }

    // ─── Get messages ─────────────────────────────────────────────────────────

    @GetMapping("/messages/{chatId}")
    @Transactional
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(
            @PathVariable String chatId,
            Authentication authentication) {

        boolean adminMode = isAdmin(authentication);
        User me = adminMode ? null : getCurrentUser(authentication);

        // Mark as read
        int receiverId = adminMode ? SHOP_ID : me.getUserId();
        chatMessageRepository.markAsRead(chatId, receiverId);

        List<ChatMessage> messages = chatMessageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        List<ChatMessageResponse> responses = messages.stream()
                .map(m -> ChatMessageResponse.fromEntity(m, adminMode))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ─── List chat rooms ──────────────────────────────────────────────────────

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> getChatRooms(
            Authentication authentication) {

        boolean adminMode = isAdmin(authentication);
        User me = adminMode ? null : getCurrentUser(authentication);

        List<String> chatIds = adminMode
                ? chatMessageRepository.findChatIdsByShopId(SHOP_ID)
                : chatMessageRepository.findChatIdsByUserId(me.getUserId());

        List<ChatRoomResponse> rooms = chatIds.stream()
                .map(chatId -> buildRoom(chatId, adminMode))
                .filter(r -> r != null)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    private ChatRoomResponse buildRoom(String chatId, boolean adminMode) {
        List<ChatMessage> msgs = chatMessageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
        if (msgs.isEmpty()) return null;

        ChatMessage last = msgs.get(msgs.size() - 1);

        // chatId format: "userId-shopId"
        String[] parts = chatId.split("-");
        Integer userId;
        try {
            userId = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return null;
        }

        String userName = "Khách hàng #" + userId;
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            String first = user.getFirstName() != null ? user.getFirstName() : "";
            String last2 = user.getLastName() != null ? user.getLastName() : "";
            String fullName = (first + " " + last2).trim();
            userName = !fullName.isEmpty() ? fullName : user.getUsername();
        }

        long unread;
        if (adminMode) {
            unread = chatMessageRepository.countByChatIdAndIsReadFalseAndReceiverIdAndReceiverType(
                    chatId, SHOP_ID, ChatMessage.SenderType.SHOP);
        } else {
            unread = chatMessageRepository.countByChatIdAndIsReadFalseAndReceiverIdAndReceiverType(
                    chatId, userId, ChatMessage.SenderType.USER);
        }

        return ChatRoomResponse.builder()
                .chatId(chatId)
                .userId(userId)
                .userName(userName)
                .lastMessage(last.getMessage())
                .lastMessageTime(last.getCreatedAt())
                .unreadCount(unread)
                .build();
    }

    // ─── Inner request class ──────────────────────────────────────────────────

    public static class SendMessageRequest {
        private String message;
        private Integer recipientUserId;

        public String getMessage() { return message; }
        public Integer getRecipientUserId() { return recipientUserId; }
    }
}
