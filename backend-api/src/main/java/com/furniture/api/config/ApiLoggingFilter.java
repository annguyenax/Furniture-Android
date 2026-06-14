package com.furniture.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestId = resolveRequestId(request);
        long startedAt = System.currentTimeMillis();

        MDC.put("requestId", requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        String method = request.getMethod();
        String path = request.getRequestURI();
        String userId = resolveUserId(request);
        String fullPath = path + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        log.info("{}",
                String.format("REQ -> %-6s %-80s user=%s ip=%s",
                        method, shorten(fullPath), userId, clientIp(request)));

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            long durationMs = System.currentTimeMillis() - startedAt;
            log.error("{}",
                    String.format("REQ !! %-6s %-80s status=%d user=%s time=%dms error=%s",
                            method, shorten(fullPath), response.getStatus(), userId, durationMs, ex.getMessage()), ex);
            throw ex;
        } finally {
            long durationMs = System.currentTimeMillis() - startedAt;
            log.info("{}",
                    String.format("REQ <- %-6s %-80s status=%d user=%s time=%dms",
                            method, shorten(fullPath), response.getStatus(), userId, durationMs));
            MDC.clear();
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String headerValue = request.getHeader(REQUEST_ID_HEADER);
        if (headerValue != null && !headerValue.isBlank()) {
            return headerValue.trim();
        }
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String resolveUserId(HttpServletRequest request) {
        Object requestUserId = request.getAttribute("userId");
        if (requestUserId != null) {
            return String.valueOf(requestUserId);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "anonymous";
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String shorten(String value) {
        if (value == null || value.length() <= 80) {
            return value;
        }
        return value.substring(0, 77) + "...";
    }
}
