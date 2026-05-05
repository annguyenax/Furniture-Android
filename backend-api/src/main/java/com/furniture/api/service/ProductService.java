package com.furniture.api.service;

import com.furniture.api.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Page<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getProductById(Integer productId);

    Page<ProductResponse> getProductsByCategory(Integer categoryId, Pageable pageable);

    Page<ProductResponse> getProductsByShop(Integer shopId, Pageable pageable);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    List<ProductResponse> getFeaturedProducts(int limit);

    List<ProductResponse> getNewArrivals(int limit);

    List<ProductResponse> getBestDeals(int limit);

    List<ProductResponse> getRelatedProducts(Integer productId, int limit);
}
