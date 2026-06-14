package com.furniture.api.service.ai;

import com.furniture.api.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatbotRateLimiter {

    private final int maxRequests;
    private final Duration window;
    private final Map<Integer, Deque<Instant>> requestsByUser = new ConcurrentHashMap<>();

    public ChatbotRateLimiter(
            @Value("${chatbot.rate-limit.requests-per-minute:10}") int maxRequests) {
        this.maxRequests = Math.max(1, maxRequests);
        this.window = Duration.ofMinutes(1);
    }

    public void check(Integer userId) {
        Instant now = Instant.now();
        Deque<Instant> requests = requestsByUser.computeIfAbsent(userId, ignored -> new ArrayDeque<>());

        synchronized (requests) {
            while (!requests.isEmpty() && Duration.between(requests.peekFirst(), now).compareTo(window) > 0) {
                requests.removeFirst();
            }
            if (requests.size() >= maxRequests) {
                throw new BadRequestException("Bạn đang gửi hơi nhanh. Thử lại sau khoảng 1 phút nhé.");
            }
            requests.addLast(now);
        }
    }
}
