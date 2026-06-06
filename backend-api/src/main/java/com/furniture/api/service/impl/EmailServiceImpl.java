package com.furniture.api.service.impl;

import com.furniture.api.exception.BadRequestException;
import com.furniture.api.model.User;
import com.furniture.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.email.send-enabled:true}")
    private boolean sendEnabled;

    @Override
    public void sendVerificationEmail(User user, String token) {
        String link = buildLink("/verify-email", token);
        send(user.getEmail(), "Verify your Furniture account",
                "Hello " + user.getFullName() + ",\n\n"
                        + "Please verify your email using this link:\n" + link + "\n\n"
                        + "This link expires in 24 hours.");
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) {
        String appLink = buildLink("/reset-password", token);
        String localHttpLink = buildHttpLocalLink("/reset-password", token);
        send(user.getEmail(), "Reset your Furniture password",
                "Hello " + user.getFullName() + ",\n\n"
                        + "Reset your password using this link:\n" + localHttpLink + "\n\n"
                        + "If the link does not open the app, copy this app link:\n" + appLink + "\n\n"
                        + "This link expires in 30 minutes. Ignore this email if you did not request it.");
    }

    private String buildLink(String path, String token) {
        String encodedToken = UriUtils.encodeQueryParam(token, StandardCharsets.UTF_8);
        return frontendUrl + path + "?token=" + encodedToken;
    }

    private String buildHttpLocalLink(String path, String token) {
        String encodedToken = UriUtils.encodeQueryParam(token, StandardCharsets.UTF_8);
        return "http://localhost:3000" + path + "?token=" + encodedToken;
    }

    private void send(String to, String subject, String body) {
        if (!sendEnabled) {
            log.info("Email sending disabled. To: {}, Subject: {}, Body: {}", to, subject, body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            log.error("Email authentication failed for sender {}", fromEmail, e);
            throw new BadRequestException("Email service authentication failed. Check MAIL_USERNAME and Gmail App Password.", e);
        } catch (MailException e) {
            log.error("Failed to send email to {}", to, e);
            throw new BadRequestException("Failed to send email. Check SMTP configuration.", e);
        }
    }
}
