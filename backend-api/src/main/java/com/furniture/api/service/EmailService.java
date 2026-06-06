package com.furniture.api.service;

import com.furniture.api.model.User;

public interface EmailService {

    void sendVerificationEmail(User user, String token);

    void sendPasswordResetEmail(User user, String token);
}
