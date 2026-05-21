package com.furniture.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.furniture.api.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    public UploadResult uploadChatImage(MultipartFile file) {
        validateConfig();
        validateImage(file);
        return upload(file, "furniture/chat", "image", "Khong the upload anh chat");
    }

    public UploadResult uploadImage(MultipartFile file, String folder) {
        validateConfig();
        validateImage(file);
        return upload(file, folder, "image", "Khong the upload anh");
    }

    public UploadResult uploadReturnEvidence(MultipartFile file) {
        validateConfig();
        validateImageOrVideo(file);
        return upload(file, "furniture/returns", "auto", "Khong the upload minh chung hoan tra");
    }

    private UploadResult upload(MultipartFile file, String folder, String resourceType, String errorMessage) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", resourceType
            ));
            String url = result.get("secure_url") != null ? String.valueOf(result.get("secure_url")) : null;
            String publicId = result.get("public_id") != null ? String.valueOf(result.get("public_id")) : null;
            if (url == null) throw new BadRequestException(errorMessage + ": Cloudinary khong tra ve URL");
            return new UploadResult(url, publicId);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(errorMessage + ": " + e.getMessage());
        }
    }

    private void validateConfig() {
        if (isBlankOrPlaceholder(cloudName) || isBlankOrPlaceholder(apiKey) || isBlankOrPlaceholder(apiSecret)) {
            throw new BadRequestException("Cloudinary chua duoc cau hinh. Can CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET.");
        }
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui long chon anh");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("File phai la hinh anh");
        }
    }

    private void validateImageOrVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui long chon anh hoac video minh chung");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
            throw new BadRequestException("File minh chung phai la hinh anh hoac video");
        }
    }

    private boolean isBlankOrPlaceholder(String value) {
        return value == null || value.isBlank() || value.startsWith("your_");
    }

    public record UploadResult(String url, String publicId) {}
}
