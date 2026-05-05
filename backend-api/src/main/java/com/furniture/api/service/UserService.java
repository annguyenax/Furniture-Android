package com.furniture.api.service;

import com.furniture.api.dto.request.UpdateUserRequest;
import com.furniture.api.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse getCurrentUser(Integer userId);

    UserResponse updateUser(Integer userId, UpdateUserRequest request);

    String uploadAvatar(Integer userId, MultipartFile file);

    void changePassword(Integer userId, String oldPassword, String newPassword);
}
