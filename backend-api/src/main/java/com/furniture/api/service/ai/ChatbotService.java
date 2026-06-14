package com.furniture.api.service.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.api.dto.request.ChatbotRequest;
import com.furniture.api.dto.response.ChatbotMessageResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.model.AiChatMessage;
import com.furniture.api.repository.AiChatMessageRepository;
import com.furniture.api.service.ai.GeminiClient.GeminiContent;
import com.furniture.api.service.ai.GeminiClient.GeminiTurnResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService {

    private static final int MAX_MESSAGE_LENGTH = 1500;
    private static final int MAX_TOOL_ITERATIONS = 5;
    private static final int HISTORY_LIMIT = 10;

    private final AiChatMessageRepository aiChatMessageRepository;
    private final GeminiClient geminiClient;
    private final ChatbotTools chatbotTools;
    private final ChatbotToolExecutor toolExecutor;
    private final ChatbotRateLimiter rateLimiter;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatbotMessageResponse chat(Integer userId, ChatbotRequest request) {
        String message = validateMessage(request != null ? request.getMessage() : null);
        rateLimiter.check(userId);
        String conversationId = resolveConversationId(userId, request != null ? request.getConversationId() : null);

        List<GeminiContent> contents = buildHistory(userId, conversationId);
        contents.add(GeminiContent.userText(message));

        List<ChatbotMessageResponse.ChatbotProductResponse> collectedProducts = new ArrayList<>();
        List<ChatbotMessageResponse.ChatbotSuggestedAction> collectedActions = new ArrayList<>();
        List<Map<String, Object>> toolCalls = new ArrayList<>();
        String assistantText;

        try {
            assistantText = runGeminiLoop(userId, contents, collectedProducts, collectedActions, toolCalls);
        } catch (Exception e) {
            log.warn("Gemini chatbot failed for user {}: {}", userId, e.getMessage());
            assistantText = fallbackMessage(e);
        }

        saveMessage(userId, conversationId, AiChatMessage.Role.USER, message, null);
        AiChatMessage assistantMessage = saveMessage(
                userId,
                conversationId,
                AiChatMessage.Role.ASSISTANT,
                assistantText,
                metadata(collectedProducts, collectedActions, toolCalls)
        );

        return ChatbotMessageResponse.builder()
                .conversationId(conversationId)
                .role(assistantMessage.getRole().name())
                .content(assistantMessage.getContent())
                .products(deduplicateProducts(collectedProducts))
                .suggestedActions(collectedActions)
                .createdAt(assistantMessage.getCreatedAt() != null ? assistantMessage.getCreatedAt() : LocalDateTime.now())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatbotMessageResponse> history(Integer userId, String conversationId) {
        String resolvedConversationId = resolveConversationId(userId, conversationId);
        return aiChatMessageRepository.findByUserIdAndConversationIdOrderByCreatedAtAsc(userId, resolvedConversationId)
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    @Transactional
    public void deleteHistory(Integer userId, String conversationId) {
        String resolvedConversationId = resolveConversationId(userId, conversationId);
        aiChatMessageRepository.deleteByUserIdAndConversationId(userId, resolvedConversationId);
    }

    private String runGeminiLoop(Integer userId,
                                 List<GeminiContent> contents,
                                 List<ChatbotMessageResponse.ChatbotProductResponse> collectedProducts,
                                 List<ChatbotMessageResponse.ChatbotSuggestedAction> collectedActions,
                                 List<Map<String, Object>> toolCalls) {
        for (int i = 0; i < MAX_TOOL_ITERATIONS; i++) {
            GeminiTurnResult result = geminiClient.generate(systemPrompt(), contents, chatbotTools.geminiTools());
            if (!result.hasFunctionCall()) {
                return result.getText();
            }

            ChatbotToolExecutor.ToolExecutionResult toolResult =
                    toolExecutor.execute(result.getFunctionName(), result.getFunctionArgs(), userId);
            log.info("TOOL -> user={} name={} args={} responseKeys={}",
                    userId,
                    result.getFunctionName(),
                    result.getFunctionArgs(),
                    toolResult.getResponse() != null ? toolResult.getResponse().keySet() : List.of());

            collectedProducts.addAll(toolResult.getProducts());
            collectedActions.addAll(toolResult.getSuggestedActions());
            toolCalls.add(Map.of(
                    "name", result.getFunctionName(),
                    "args", result.getFunctionArgs() != null ? result.getFunctionArgs() : Map.of(),
                    "response", toolResult.getResponse()
            ));

            contents.add(GeminiContent.modelFunctionCall(result.getFunctionName(), result.getFunctionArgs()));
            contents.add(GeminiContent.functionResponse(result.getFunctionName(), toolResult.getResponse()));
        }
        return "Mình đã xử lý khá nhiều bước. Bạn hỏi cụ thể hơn một chút để mình hỗ trợ tiếp nhé.";
    }

    private List<GeminiContent> buildHistory(Integer userId, String conversationId) {
        List<AiChatMessage> recent = new ArrayList<>(
                aiChatMessageRepository.findTop20ByUserIdAndConversationIdOrderByCreatedAtDesc(userId, conversationId)
        );
        Collections.reverse(recent);
        if (recent.size() > HISTORY_LIMIT) {
            recent = recent.subList(recent.size() - HISTORY_LIMIT, recent.size());
        }
        return recent.stream()
                .map(m -> m.getRole() == AiChatMessage.Role.USER
                        ? GeminiContent.userText(m.getContent())
                        : GeminiContent.modelText(m.getContent()))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    private AiChatMessage saveMessage(Integer userId,
                                      String conversationId,
                                      AiChatMessage.Role role,
                                      String content,
                                      String metadataJson) {
        return aiChatMessageRepository.save(AiChatMessage.builder()
                .userId(userId)
                .conversationId(conversationId)
                .role(role)
                .content(content)
                .metadataJson(metadataJson)
                .build());
    }

    private String validateMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.isBlank()) {
            throw new BadRequestException("Tin nhắn không được trống");
        }
        String message = rawMessage.trim();
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new BadRequestException("Tin nhắn quá dài, vui lòng rút gọn dưới " + MAX_MESSAGE_LENGTH + " ký tự");
        }
        return message;
    }

    private String fallbackMessage(Exception e) {
        String detail = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        if (detail.contains("429") || detail.contains("quota") || detail.contains("resource_exhausted")) {
            return "Mình đang hết quota AI tạm thời. Bạn thử lại sau nhé, hoặc tìm sản phẩm trực tiếp trong app giúp mình.";
        }
        if (detail.contains("503") || detail.contains("unavailable") || detail.contains("high demand")) {
            return "Trợ lý AI đang quá tải tạm thời. Bạn thử lại sau ít phút nhé.";
        }
        if (detail.contains("api key") || detail.contains("403") || detail.contains("401")) {
            return "Trợ lý AI chưa được cấu hình key hợp lệ. Bạn kiểm tra lại GEMINI_API_KEY giúp mình nhé.";
        }
        return "Mình chưa kết nối được trợ lý AI lúc này. Bạn thử lại sau ít phút nhé.";
    }

    private String resolveConversationId(Integer userId, String requestedConversationId) {
        if (requestedConversationId != null && !requestedConversationId.isBlank()) {
            return requestedConversationId.trim();
        }
        return aiChatMessageRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(AiChatMessage::getConversationId)
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    private ChatbotMessageResponse toHistoryResponse(AiChatMessage message) {
        ChatbotMessageResponse.ChatbotMessageResponseBuilder builder = ChatbotMessageResponse.builder()
                .conversationId(message.getConversationId())
                .role(message.getRole().name())
                .content(message.getContent())
                .createdAt(message.getCreatedAt());

        if (message.getMetadataJson() != null && !message.getMetadataJson().isBlank()) {
            try {
                Map<String, Object> metadata = objectMapper.readValue(
                        message.getMetadataJson(),
                        new TypeReference<Map<String, Object>>() {}
                );
                Object products = metadata.get("products");
                if (products != null) {
                    builder.products(objectMapper.convertValue(
                            products,
                            new TypeReference<List<ChatbotMessageResponse.ChatbotProductResponse>>() {}
                    ));
                }
                Object actions = metadata.get("suggestedActions");
                if (actions != null) {
                    builder.suggestedActions(objectMapper.convertValue(
                            actions,
                            new TypeReference<List<ChatbotMessageResponse.ChatbotSuggestedAction>>() {}
                    ));
                }
            } catch (IllegalArgumentException | JsonProcessingException e) {
                log.warn("Failed to parse chatbot metadata for message {}: {}", message.getMessageId(), e.getMessage());
            }
        }

        return builder.build();
    }

    private String metadata(List<ChatbotMessageResponse.ChatbotProductResponse> products,
                            List<ChatbotMessageResponse.ChatbotSuggestedAction> actions,
                            List<Map<String, Object>> toolCalls) {
        List<ChatbotMessageResponse.ChatbotProductResponse> dedupedProducts = deduplicateProducts(products);
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "productRefs", dedupedProducts.stream()
                            .map(ChatbotMessageResponse.ChatbotProductResponse::getProductId)
                            .toList(),
                    "products", dedupedProducts,
                    "suggestedActions", actions,
                    "toolCalls", toolCalls
            ));
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<ChatbotMessageResponse.ChatbotProductResponse> deduplicateProducts(
            List<ChatbotMessageResponse.ChatbotProductResponse> products) {
        LinkedHashSet<Integer> seen = new LinkedHashSet<>();
        List<ChatbotMessageResponse.ChatbotProductResponse> result = new ArrayList<>();
        for (ChatbotMessageResponse.ChatbotProductResponse product : products) {
            if (product.getProductId() != null && seen.add(product.getProductId())) {
                result.add(product);
            }
        }
        return result;
    }

    private String systemPrompt() {
        return """
                Bạn là trợ lý mua sắm nội thất cho app Furniture.
                Trả lời bằng tiếng Việt, ngắn gọn, tự nhiên.
                Chỉ tư vấn sản phẩm, giá, tồn kho, danh mục, giỏ hàng, đơn hàng, wishlist và địa chỉ dựa trên dữ liệu từ tools.
                Không tự bịa sản phẩm, giá, khuyến mãi, tồn kho, trạng thái đơn hoặc địa chỉ.

                Flow sản phẩm và giỏ hàng:
                1. Khi khách hỏi/gợi ý sản phẩm, dùng search_products/list_categories/get_product_detail.
                2. Khi có danh sách phù hợp, tóm tắt 2-4 lựa chọn nổi bật và nhắc khách bấm card để xem chi tiết.
                3. Khi khách xác nhận muốn mua/thêm giỏ một sản phẩm cụ thể, nếu chưa có productId hãy gọi search_products theo tên sản phẩm để tìm lại productId.
                4. Gọi add_to_cart chỉ khi khách xác nhận rõ muốn mua/thêm giỏ. Không tự thêm giỏ khi khách chỉ hỏi thông tin.
                5. Nếu add_to_cart trả needs_variant_selection, hãy hỏi khách chọn phân loại màu/kích thước trong danh sách tool trả về. Không tự chọn bừa.
                6. Nếu thêm giỏ thành công, báo đã thêm vào giỏ và gợi ý khách bấm Xem giỏ hàng hoặc Đi thanh toán.
                7. Dùng get_cart khi khách hỏi giỏ hàng hiện tại. Dùng remove_cart_item chỉ khi khách yêu cầu xóa rõ ràng.

                Flow đơn hàng:
                - Khi khách hỏi đơn gần đây, đơn đang giao, đơn đã giao hoặc đơn bị hủy, dùng get_my_orders.
                - Khi khách hỏi chi tiết một đơn cụ thể, dùng get_order_detail.
                - Không tự hủy đơn, xác nhận đã nhận hàng, đổi trạng thái đơn hoặc tạo đơn hàng.

                Flow wishlist:
                - Khi khách hỏi danh sách yêu thích, dùng get_wishlist.
                - Chỉ dùng add_to_wishlist khi khách nói rõ muốn thêm sản phẩm vào yêu thích.

                Flow địa chỉ giao hàng:
                - Khi khách hỏi địa chỉ giao hàng hoặc địa chỉ mặc định, dùng get_shipping_addresses hoặc get_default_shipping_address.
                - Chỉ đọc và tóm tắt địa chỉ. Không tự thêm, sửa, xóa hay đổi địa chỉ mặc định.

                Bước đặt hàng, chọn địa chỉ, thanh toán và xác nhận cuối cùng luôn do khách thực hiện trên màn hình giỏ hàng/thanh toán của app.
                """;
    }
}
