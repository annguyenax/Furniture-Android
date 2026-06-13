package com.furniture.api.service.impl;

import com.furniture.api.dto.request.LoginRequest;
import com.furniture.api.dto.request.RegisterRequest;
import com.furniture.api.dto.response.AuthResponse;
import com.furniture.api.dto.response.UserResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.exception.UnauthorizedException;
import com.furniture.api.model.Role;
import com.furniture.api.model.User;
import com.furniture.api.repository.RoleRepository;
import com.furniture.api.repository.UserRepository;
import com.furniture.api.security.JwtTokenProvider;
import com.furniture.api.service.AuthService;
import com.furniture.api.service.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${google.client-id}")
    private String googleClientId;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;
    private static final int EMAIL_VERIFICATION_EXPIRY_HOURS = 24;
    private static final int PASSWORD_RESET_EXPIRY_MINUTES = 30;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        // Check if phone already exists
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already exists");
        }

        // Get default CUSTOMER role
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
            .orElseGet(() -> {
                Role role = new Role("CUSTOMER");
                return roleRepository.save(role);
            });

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        // Create user
        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .phone(request.getPhone())
            .status(User.UserStatus.ACTIVE)
            .authProvider(User.AuthProvider.LOCAL)
            .isVerified(false)
            .roles(roles)
            .build();

        user = userRepository.save(user);
        createEmailVerificationToken(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Save refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        try {
            emailService.sendVerificationEmail(user, user.getEmailVerificationToken());
        } catch (Exception e) {
            log.warn("Verification email could not be sent for {}. Registration still succeeds.", user.getEmail());
        }

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .user(UserResponse.fromEntity(user))
            .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        ensureAccountCanAuthenticate(user);

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new UnauthorizedException("Invalid email or password");
        }

        // Reset login attempts on successful login
        user.setLoginAttempts(0);
        user.setLockedUntil(null);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Save refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .user(UserResponse.fromEntity(user))
            .build();
    }

    @Override
    @Transactional
    public AuthResponse googleLogin(String googleIdToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleIdToken.trim());
            if (idToken == null) {
                throw new UnauthorizedException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            User user = userRepository.findByGoogleId(googleId)
                    .orElseGet(() -> userRepository.findByEmail(email)
                            .map(existing -> {
                                existing.setGoogleId(googleId);
                                existing.setAuthProvider(User.AuthProvider.GOOGLE);
                                if (pictureUrl != null && existing.getProfilePicture() == null) {
                                    existing.setProfilePicture(pictureUrl);
                                }
                                return userRepository.save(existing);
                            })
                            .orElseGet(() -> createGoogleUser(googleId, email, firstName, lastName, pictureUrl)));
            ensureAccountCanAuthenticate(user);

            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            log.info("Google login successful: {}", email);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .user(UserResponse.fromEntity(user))
                    .build();

        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google login failed", e);
            throw new UnauthorizedException("Google authentication failed");
        }
    }

    private User createGoogleUser(String googleId, String email, String firstName, String lastName, String pictureUrl) {
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role("CUSTOMER")));

        String username = generateUniqueUsername(email);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);

        User newUser = User.builder()
                .googleId(googleId)
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .profilePicture(pictureUrl)
                .authProvider(User.AuthProvider.GOOGLE)
                .status(User.UserStatus.ACTIVE)
                .isVerified(true)
                .roles(roles)
                .build();

        return userRepository.save(newUser);
    }

    private String generateUniqueUsername(String email) {
        String base = email.split("@")[0].replaceAll("[^a-zA-Z0-9]", "");
        if (base.isEmpty()) base = "user";
        String username = base;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = base + suffix++;
        }
        return username;
    }

    private void ensureAccountCanAuthenticate(User user) {
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("Account is locked. Try again later.");
        }
        if (user.isBanned()) {
            throw new UnauthorizedException("Account has been banned");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new UnauthorizedException("Invalid token type");
        }

        Integer userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ensureAccountCanAuthenticate(user);

        // Verify refresh token matches
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        // Update refresh token
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .user(UserResponse.fromEntity(user))
            .build();
    }

    @Override
    @Transactional
    public void logout(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRefreshToken(null);
        userRepository.save(user);

        log.info("User logged out: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email.trim().toLowerCase()).ifPresent(user -> {
            if (user.getAuthProvider() != User.AuthProvider.LOCAL || user.isBanned()) {
                log.info("Ignoring password reset request for non-local or banned account: {}", user.getEmail());
                return;
            }

            String token = generateToken();
            user.setResetPasswordToken(token);
            user.setResetPasswordExpires(LocalDateTime.now().plusMinutes(PASSWORD_RESET_EXPIRY_MINUTES));
            userRepository.save(user);
            emailService.sendPasswordResetEmail(user, token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (user.getResetPasswordExpires() == null || user.getResetPasswordExpires().isBefore(LocalDateTime.now())) {
            clearPasswordResetToken(user);
            userRepository.save(user);
            throw new BadRequestException("Invalid or expired password reset token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLoginAttempts(0);
        user.setLockedUntil(null);
        user.setRefreshToken(null);
        clearPasswordResetToken(user);
        userRepository.save(user);
        log.info("Password reset successfully for user: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid or expired email verification token"));

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            clearEmailVerificationToken(user);
            userRepository.save(user);
            return;
        }

        if (user.getEmailVerificationExpires() == null
                || user.getEmailVerificationExpires().isBefore(LocalDateTime.now())) {
            clearEmailVerificationToken(user);
            userRepository.save(user);
            throw new BadRequestException("Invalid or expired email verification token");
        }

        user.setIsVerified(true);
        clearEmailVerificationToken(user);
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    private void createEmailVerificationToken(User user) {
        user.setEmailVerificationToken(generateToken());
        user.setEmailVerificationExpires(LocalDateTime.now().plusHours(EMAIL_VERIFICATION_EXPIRY_HOURS));
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
    }

    private void clearPasswordResetToken(User user) {
        user.setResetPasswordToken(null);
        user.setResetPasswordExpires(null);
    }

    private void clearEmailVerificationToken(User user) {
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpires(null);
    }

    private void handleFailedLogin(User user) {
        int currentAttempts = user.getLoginAttempts() != null ? user.getLoginAttempts() : 0;
        int attempts = currentAttempts + 1;
        user.setLoginAttempts(attempts);
        user.setLastFailedLogin(LocalDateTime.now());

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
            log.warn("Account locked due to too many failed attempts: {}", user.getEmail());
        }

        userRepository.save(user);
    }
}
