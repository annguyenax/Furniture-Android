package com.furniture.api.dto.response;

import com.furniture.api.model.AiChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotMessageResponse {
    private String conversationId;
    private String role;
    private String content;
    private List<ChatbotProductResponse> products;
    private List<ChatbotSuggestedAction> suggestedActions;
    private LocalDateTime createdAt;

    public static ChatbotMessageResponse fromEntity(AiChatMessage message) {
        return ChatbotMessageResponse.builder()
                .conversationId(message.getConversationId())
                .role(message.getRole().name())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatbotProductResponse {
        private Integer productId;
        private String name;
        private String category;
        private BigDecimal price;
        private BigDecimal discount;
        private BigDecimal finalPrice;
        private Integer stock;
        private BigDecimal rating;
        private String shortDescription;
        private String imageUrl;
        private List<ChatbotVariantResponse> variants;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatbotVariantResponse {
        private Integer variantId;
        private String color;
        private String size;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatbotSuggestedAction {
        private String type;
        private String label;
        private Integer productId;
    }
}
