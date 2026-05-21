package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload-image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String folder) {

        String safeFolder = folder == null || folder.isBlank()
                ? "general"
                : folder.replaceAll("[^a-zA-Z0-9_-]", "");
        CloudinaryService.UploadResult result = cloudinaryService.uploadImage(
                file,
                "furniture/" + safeFolder);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded", result.url()));
    }
}
