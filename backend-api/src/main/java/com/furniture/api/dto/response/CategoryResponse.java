package com.furniture.api.dto.response;

import com.furniture.api.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Integer categoryId;
    private String categoryName;
    private Integer parentId;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private List<CategoryResponse> children;

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
            .categoryId(category.getCategoryId())
            .categoryName(category.getCategoryName())
            .parentId(category.getParentId())
            .description(category.getDescription())
            .image(category.getImage())
            .createdAt(category.getCreatedAt())
            .build();
    }

    public static CategoryResponse fromEntityWithChildren(Category category) {
        CategoryResponse response = fromEntity(category);
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            response.setChildren(category.getChildren().stream()
                .map(CategoryResponse::fromEntityWithChildren)
                .collect(Collectors.toList()));
        }
        return response;
    }
}
