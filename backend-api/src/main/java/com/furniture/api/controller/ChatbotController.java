package com.furniture.api.controller;

import com.furniture.api.dto.request.ChatbotRequest;
import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.ChatbotMessageResponse;
import com.furniture.api.service.ai.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ApiResponse<ChatbotMessageResponse>> sendMessage(
            @RequestBody ChatbotRequest request,
            Authentication authentication) {
        Integer userId = currentUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(chatbotService.chat(userId, request)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatbotMessageResponse>>> getHistory(
            @RequestParam(required = false) String conversationId,
            Authentication authentication) {
        Integer userId = currentUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success(chatbotService.history(userId, conversationId)));
    }

    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<Void>> deleteHistory(
            @RequestParam(required = false) String conversationId,
            Authentication authentication) {
        Integer userId = currentUserId(authentication);
        chatbotService.deleteHistory(userId, conversationId);
        return ResponseEntity.ok(ApiResponse.successMessage("Đã xóa lịch sử chatbot"));
    }

    private Integer currentUserId(Authentication authentication) {
        return Integer.parseInt(authentication.getName());
    }
}
