package com.furniture.api.service;

import com.furniture.api.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getRootCategories();

    CategoryResponse getCategoryById(Integer categoryId);

    List<CategoryResponse> getSubCategories(Integer parentId);
}
