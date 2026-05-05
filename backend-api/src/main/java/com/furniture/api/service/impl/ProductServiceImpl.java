package com.furniture.api.service.impl;

import com.furniture.api.dto.response.ProductResponse;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.model.Product;
import com.furniture.api.repository.ProductRepository;
import com.furniture.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByStatusFetch(Product.ProductStatus.ACTIVE, pageable)
            .map(ProductResponse::fromEntity);
    }

    @Override
    public ProductResponse getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return ProductResponse.fromEntity(product);
    }

    @Override
    public Page<ProductResponse> getProductsByCategory(Integer categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
            .map(ProductResponse::fromEntity);
    }

    @Override
    public Page<ProductResponse> getProductsByShop(Integer shopId, Pageable pageable) {
        return productRepository.findByShopId(shopId, pageable)
            .map(ProductResponse::fromEntity);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeywordFetch(keyword, pageable)
            .map(ProductResponse::fromEntity);
    }

    @Override
    public List<ProductResponse> getFeaturedProducts(int limit) {
        return productRepository.findFeaturedProducts(PageRequest.of(0, limit))
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getNewArrivals(int limit) {
        return productRepository.findNewArrivals(PageRequest.of(0, limit))
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getBestDeals(int limit) {
        return productRepository.findBestDeals(PageRequest.of(0, limit))
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getRelatedProducts(Integer productId, int limit) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getCategoryId() == null) {
            return List.of();
        }

        return productRepository.findByCategoryIdAndStatusAndProductIdNot(
                product.getCategoryId(),
                Product.ProductStatus.ACTIVE,
                productId,
                PageRequest.of(0, limit))
            .stream()
            .map(ProductResponse::fromEntity)
            .collect(Collectors.toList());
    }
}
