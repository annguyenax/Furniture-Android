package com.furniture.api.service;

import com.furniture.api.dto.request.LoginRequest;
import com.furniture.api.dto.request.RegisterRequest;
import com.furniture.api.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse googleLogin(String googleIdToken);

    AuthResponse refreshToken(String refreshToken);

    void logout(Integer userId);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    void verifyEmail(String token);
}
