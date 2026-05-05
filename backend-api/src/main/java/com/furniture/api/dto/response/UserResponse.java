package com.furniture.api.dto.response;

import com.furniture.api.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String gender;
    private String status;
    private LocalDate dateOfBirth;
    private String profilePicture;
    private Boolean isVerified;
    private String authProvider;
    private List<String> roles;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
            .userId(user.getUserId())
            .username(user.getUsername())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .gender(user.getGender() != null ? user.getGender().getValue() : null)
            .status(user.getStatus() != null ? user.getStatus().getValue() : null)
            .dateOfBirth(user.getDateOfBirth())
            .profilePicture(user.getProfilePicture())
            .isVerified(user.getIsVerified())
            .authProvider(user.getAuthProvider() != null ? user.getAuthProvider().getValue() : null)
            .roles(user.getRoles().stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList()))
            .createdAt(user.getCreatedAt())
            .build();
    }
}
