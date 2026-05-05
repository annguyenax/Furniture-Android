package com.furniture.api.service.impl;

import com.furniture.api.dto.response.CategoryResponse;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.model.Category;
import com.furniture.api.repository.CategoryRepository;
import com.furniture.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(CategoryResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findByParentIdIsNull().stream()
            .map(CategoryResponse::fromEntityWithChildren)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return CategoryResponse.fromEntityWithChildren(category);
    }

    @Override
    public List<CategoryResponse> getSubCategories(Integer parentId) {
        return categoryRepository.findByParentId(parentId).stream()
            .map(CategoryResponse::fromEntity)
            .collect(Collectors.toList());
    }
}
