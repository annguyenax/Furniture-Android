package com.furniture.api.controller;

import com.furniture.api.dto.request.LoginRequest;
import com.furniture.api.dto.request.RegisterRequest;
import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.AuthResponse;
import com.furniture.api.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.register(request);
        addTokenCookies(response, authResponse, false);

        return ResponseEntity.ok(ApiResponse.success("Registration successful", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.login(request);
        addTokenCookies(response, authResponse, request.isRememberMe());

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(
            @RequestBody String googleIdToken,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.googleLogin(googleIdToken);
        addTokenCookies(response, authResponse, true);

        return ResponseEntity.ok(ApiResponse.success("Google login successful", authResponse));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            @RequestBody(required = false) String refreshTokenBody,
            HttpServletResponse response) {

        String refreshToken = refreshTokenCookie != null ? refreshTokenCookie : refreshTokenBody;
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        addTokenCookies(response, authResponse, true);

        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestAttribute(value = "userId", required = false) Integer userId,
            HttpServletResponse response) {

        if (userId != null) {
            authService.logout(userId);
        }

        // Clear cookies
        clearTokenCookies(response);

        return ResponseEntity.ok(ApiResponse.successMessage("Logout successful"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.successMessage("Password reset email sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestBody String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok(ApiResponse.successMessage("Password reset successful"));
    }

    private void addTokenCookies(HttpServletResponse response, AuthResponse auth, boolean rememberMe) {
        int maxAge = rememberMe ? 7 * 24 * 60 * 60 : 2 * 60 * 60; // 7 days or 2 hours

        Cookie accessTokenCookie = new Cookie("accessToken", auth.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(maxAge);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", auth.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // Always 7 days for refresh
        response.addCookie(refreshTokenCookie);
    }

    private void clearTokenCookies(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);
        response.addCookie(refreshTokenCookie);
    }
}
