package com.furniture.api.service.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GeminiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model) {
        this.restClient = RestClient.create("https://generativelanguage.googleapis.com");
        this.apiKey = apiKey;
        this.model = model;
    }

    public GeminiTurnResult generate(String systemPrompt, List<GeminiContent> contents, List<Map<String, Object>> tools) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing GEMINI_API_KEY");
        }

        Map<String, Object> request = Map.of(
                "systemInstruction", GeminiContent.userText(systemPrompt),
                "contents", contents,
                "tools", tools,
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "maxOutputTokens", 700
                )
        );

        GeminiResponse response;
        long startedAt = System.currentTimeMillis();
        log.info("AI  -> model={} messages={} tools={}",
                model, contents != null ? contents.size() : 0, tools != null ? tools.size() : 0);
        try {
            response = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent?key={key}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);
        } catch (RestClientResponseException e) {
            log.warn("AI  !! model={} status={} time={}ms body={}",
                    model, e.getRawStatusCode(), System.currentTimeMillis() - startedAt, compact(e.getResponseBodyAsString()));
            throw new GeminiApiException(e.getRawStatusCode(), e.getResponseBodyAsString(), e);
        }
        log.info("AI  <- model={} candidates={} time={}ms",
                model,
                response != null && response.candidates != null ? response.candidates.size() : 0,
                System.currentTimeMillis() - startedAt);

        if (response == null || response.candidates == null || response.candidates.isEmpty()) {
            return GeminiTurnResult.text("Mình chưa nhận được phản hồi từ AI. Bạn thử hỏi lại giúp mình nhé.");
        }

        Candidate candidate = response.candidates.get(0);
        if (candidate.content == null || candidate.content.parts == null) {
            return GeminiTurnResult.text("Mình chưa nhận được nội dung phù hợp. Bạn thử hỏi lại giúp mình nhé.");
        }

        StringBuilder text = new StringBuilder();
        for (Map<String, Object> part : candidate.content.parts) {
            Object functionCall = part.get("functionCall");
            if (functionCall instanceof Map<?, ?> rawCall) {
                String name = String.valueOf(rawCall.get("name"));
                Object argsObj = rawCall.get("args");
                @SuppressWarnings("unchecked")
                Map<String, Object> args = argsObj instanceof Map<?, ?> ? (Map<String, Object>) argsObj : Map.of();
                return GeminiTurnResult.functionCall(name, args);
            }

            Object textPart = part.get("text");
            if (textPart != null) {
                text.append(textPart);
            }
        }

        String finalText = text.toString().trim();
        return GeminiTurnResult.text(finalText.isEmpty()
                ? "Mình chưa có câu trả lời phù hợp. Bạn hỏi cụ thể hơn giúp mình nhé."
                : finalText);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiContent {
        private String role;
        private List<Map<String, Object>> parts = new ArrayList<>();

        public static GeminiContent userText(String text) {
            return text("user", text);
        }

        public static GeminiContent modelText(String text) {
            return text("model", text);
        }

        public static GeminiContent text(String role, String text) {
            return new GeminiContent(role, List.of(Map.of("text", text)));
        }

        public static GeminiContent functionResponse(String name, Map<String, Object> response) {
            return new GeminiContent("function", List.of(Map.of(
                    "functionResponse", Map.of(
                            "name", name,
                            "response", response
                    )
            )));
        }

        public static GeminiContent modelFunctionCall(String name, Map<String, Object> args) {
            return new GeminiContent("model", List.of(Map.of(
                    "functionCall", Map.of(
                            "name", name,
                            "args", args != null ? args : Map.of()
                    )
            )));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeminiTurnResult {
        private String text;
        private String functionName;
        private Map<String, Object> functionArgs;

        public static GeminiTurnResult text(String text) {
            return new GeminiTurnResult(text, null, null);
        }

        public static GeminiTurnResult functionCall(String functionName, Map<String, Object> functionArgs) {
            return new GeminiTurnResult(null, functionName, functionArgs);
        }

        public boolean hasFunctionCall() {
            return functionName != null && !functionName.isBlank();
        }
    }

    @Data
    @NoArgsConstructor
    private static class GeminiResponse {
        private List<Candidate> candidates;
    }

    @Data
    @NoArgsConstructor
    private static class Candidate {
        private GeminiContent content;
    }

    private String compact(String body) {
        if (body == null) {
            return "";
        }
        String compacted = body.replaceAll("\\s+", " ").trim();
        return compacted.length() > 500 ? compacted.substring(0, 500) + "..." : compacted;
    }

    public static class GeminiApiException extends RuntimeException {
        public GeminiApiException(int statusCode, String body, Throwable cause) {
            super("Gemini API error " + statusCode + ": " + body, cause);
        }
    }
}
