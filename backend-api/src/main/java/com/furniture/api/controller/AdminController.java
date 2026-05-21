package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.CategoryResponse;
import com.furniture.api.dto.response.OrderResponse;
import com.furniture.api.dto.response.ProductResponse;
import com.furniture.api.dto.response.UserResponse;
import com.furniture.api.model.*;
import com.furniture.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OrderRepository orderRepository;
    private final SubOrderRepository subOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductReviewRepository reviewRepository;

    // ─── Orders ───────────────────────────────────────────────────────────────

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        Pageable bigPage = PageRequest.of(0, 10000, Sort.by("createdAt").descending());

        List<Order> source;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            try {
                Order.OrderStatus s = Order.OrderStatus.valueOf(status.toUpperCase());
                source = orderRepository.findByStatus(s, bigPage).getContent();
            } catch (IllegalArgumentException e) {
                source = orderRepository.findAll(bigPage).getContent();
            }
        } else {
            source = orderRepository.findAll(bigPage).getContent();
        }

        List<OrderResponse> responses = source.stream()
                .map(this::mapOrder)
                .collect(Collectors.toList());

        if (search != null && !search.isBlank()) {
            String q = normalize(search.trim());
            responses = responses.stream()
                    .filter(r -> normalize(r.getOrderCode()).contains(q)
                            || normalize(r.getRecipientName()).contains(q))
                    .collect(Collectors.toList());
        }

        int total = responses.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<OrderResponse> pageContent = from < total ? responses.subList(from, to) : java.util.Collections.emptyList();

        Page<OrderResponse> result = new PageImpl<>(pageContent, PageRequest.of(page, size), total);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus newStatus;
        try {
            newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid status: " + status));
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        return ResponseEntity.ok(ApiResponse.success("Status updated", mapOrder(order)));
    }

    // ─── Products ─────────────────────────────────────────────────────────────

    @PostMapping("/products")
    @Transactional
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestBody CreateProductRequest request) {

        if (request.getProductName() == null || request.getProductName().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Tên sản phẩm không được trống"));
        }
        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Giá sản phẩm phải lớn hơn 0"));
        }

        Product product = Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .discount(request.getDiscount() != null ? request.getDiscount() : java.math.BigDecimal.ZERO)
                .status(Product.ProductStatus.ACTIVE)
                .shopId(request.getShopId() != null ? request.getShopId() : 1)
                .categoryId(request.getCategoryId())
                .sold(0)
                .build();
        product = productRepository.save(product);

        ProductVariant variant = ProductVariant.builder()
                .productId(product.getProductId())
                .price(request.getPrice())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .imageUrl(request.getImageUrl())
                .build();
        productVariantRepository.save(variant);

        Product saved = productRepository.findById(product.getProductId()).orElse(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đã tạo sản phẩm", ProductResponse.fromEntity(saved)));
    }

    @DeleteMapping("/products/{productId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer productId) {
        if (!productRepository.existsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy sản phẩm"));
        }
        productRepository.deleteById(productId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa sản phẩm", null));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Integer productId,
            @RequestBody UpdateProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getProductName() != null && !request.getProductName().isBlank()) {
            product.setProductName(request.getProductName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getDiscount() != null) {
            product.setDiscount(request.getDiscount());
        }
        if (request.getStatus() != null) {
            try {
                product.setStatus(Product.ProductStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        product = productRepository.save(product);
        if (request.getImageUrl() != null) {
            java.util.List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
            ProductVariant variant = variants.isEmpty() ? ProductVariant.builder()
                    .productId(productId)
                    .price(java.math.BigDecimal.ZERO)
                    .stock(product.getStock() != null ? product.getStock() : 0)
                    .build() : variants.get(0);
            variant.setImageUrl(request.getImageUrl().isBlank() ? null : request.getImageUrl());
            productVariantRepository.save(variant);
        }
        return ResponseEntity.ok(ApiResponse.success("Product updated", ProductResponse.fromEntity(product)));
    }

    @PostMapping("/products/{productId}/variants")
    @Transactional
    public ResponseEntity<ApiResponse<ProductResponse.VariantResponse>> createVariant(
            @PathVariable Integer productId,
            @RequestBody VariantMutationRequest request) {

        if (!productRepository.existsById(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Khong tim thay san pham"));
        }
        ProductVariant variant = ProductVariant.builder()
                .productId(productId)
                .size(request.getSize())
                .color(request.getColor())
                .material(request.getMaterial())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .stock(request.getStock() != null ? request.getStock() : 0)
                .imageUrl(request.getImageUrl())
                .build();
        variant = productVariantRepository.save(variant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Da them phan loai", variantResponse(variant)));
    }

    @PutMapping("/variants/{variantId}")
    @Transactional
    public ResponseEntity<ApiResponse<ProductResponse.VariantResponse>> updateVariant(
            @PathVariable Integer variantId,
            @RequestBody VariantMutationRequest request) {

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setMaterial(request.getMaterial());
        if (request.getPrice() != null) variant.setPrice(request.getPrice());
        if (request.getStock() != null) variant.setStock(request.getStock());
        if (request.getImageUrl() != null) variant.setImageUrl(request.getImageUrl().isBlank() ? null : request.getImageUrl());
        variant = productVariantRepository.save(variant);
        return ResponseEntity.ok(ApiResponse.success("Da cap nhat phan loai", variantResponse(variant)));
    }

    @DeleteMapping("/variants/{variantId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteVariant(@PathVariable Integer variantId) {
        if (!productVariantRepository.existsById(variantId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Khong tim thay phan loai"));
        }
        productVariantRepository.deleteById(variantId);
        return ResponseEntity.ok(ApiResponse.success("Da xoa phan loai", null));
    }

    private ProductResponse.VariantResponse variantResponse(ProductVariant variant) {
        return ProductResponse.VariantResponse.builder()
                .variantId(variant.getVariantId())
                .size(variant.getSize())
                .color(variant.getColor())
                .material(variant.getMaterial())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .imageUrl(variant.getImageUrl())
                .build();
    }

    // ─── Categories ───────────────────────────────────────────────────────────

    @PostMapping("/categories")
    @Transactional
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody CategoryMutationRequest request) {

        if (request.getCategoryName() == null || request.getCategoryName().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Tên danh mục không được trống"));
        }
        if (categoryRepository.existsByCategoryName(request.getCategoryName().trim())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Danh mục đã tồn tại"));
        }

        Category category = Category.builder()
                .categoryName(request.getCategoryName().trim())
                .description(request.getDescription())
                .image(request.getImage())
                .parentId(request.getParentId())
                .build();
        category = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đã tạo danh mục", CategoryResponse.fromEntity(category)));
    }

    @PutMapping("/categories/{categoryId}")
    @Transactional
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody CategoryMutationRequest request) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            category.setCategoryName(request.getCategoryName().trim());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getImage() != null) {
            category.setImage(request.getImage());
        }
        category = categoryRepository.save(category);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật danh mục", CategoryResponse.fromEntity(category)));
    }

    @DeleteMapping("/categories/{categoryId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy danh mục"));
        }
        if (productRepository.existsByCategoryId(categoryId)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không thể xóa danh mục đang có sản phẩm. Hãy chuyển hoặc xóa sản phẩm trước."));
        }
        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa danh mục", null));
    }

    // ─── Reviews ──────────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<AdminReviewDto>>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductReview> reviews = reviewRepository.findAll(pageable);
        Page<AdminReviewDto> result = reviews.map(r -> {
            String productName = productRepository.findById(r.getProductId())
                    .map(Product::getProductName).orElse("?");
            User user = userRepository.findById(r.getUserId()).orElse(null);
            String userName = "?";
            if (user != null) {
                String full = ((user.getFirstName() != null ? user.getFirstName() : "") + " "
                        + (user.getLastName() != null ? user.getLastName() : "")).trim();
                userName = full.isEmpty() ? user.getUsername() : full;
            }
            return new AdminReviewDto(r.getReviewId(), r.getProductId(), productName,
                    r.getUserId(), userName, user != null ? user.getEmail() : null,
                    r.getRating(), r.getComment(), r.getImages(), r.getIsVerified(), r.getCreatedAt());
        });
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/reviews/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Integer id) {
        if (!reviewRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Không tìm thấy đánh giá"));
        }
        reviewRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa đánh giá", null));
    }

    // ─── Statistics ───────────────────────────────────────────────────────────

    @GetMapping("/stats")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<StatsResponse>> getStats(
            @RequestParam(defaultValue = "day") String period) {

        List<Object[]> rawRevenue;
        if ("year".equals(period)) {
            rawRevenue = orderRepository.getYearlyRevenue();
        } else if ("month".equals(period)) {
            rawRevenue = orderRepository.getMonthlyRevenue(LocalDateTime.now().minusMonths(12));
        } else {
            rawRevenue = orderRepository.getDailyRevenue(LocalDateTime.now().minusDays(30));
        }

        List<DataPoint> revenueData = rawRevenue.stream()
                .map(r -> new DataPoint(r[0].toString(), new BigDecimal(r[1].toString())))
                .collect(Collectors.toList());

        List<DataPoint> topProducts = orderItemRepository.getTopProductsByRevenue().stream()
                .map(r -> new DataPoint(r[0].toString(), new BigDecimal(r[1].toString())))
                .collect(Collectors.toList());

        List<DataPoint> byCategory = orderItemRepository.getRevenueByCategory().stream()
                .map(r -> new DataPoint(r[0].toString(), new BigDecimal(r[1].toString())))
                .collect(Collectors.toList());

        long totalOrders = orderRepository.countByStatus(Order.OrderStatus.DELIVERED);
        BigDecimal totalRevenue = revenueData.stream().map(DataPoint::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ResponseEntity.ok(ApiResponse.success(
                new StatsResponse(revenueData, topProducts, byCategory, totalRevenue, totalOrders)));
    }

    // ─── Inner DTOs ───────────────────────────────────────────────────────────

    public static class AdminReviewDto {
        private final Integer reviewId, productId, userId, rating;
        private final String productName, userName, userEmail, comment, images;
        private final Boolean isVerified;
        private final LocalDateTime createdAt;

        public AdminReviewDto(Integer reviewId, Integer productId, String productName,
                              Integer userId, String userName, Integer rating,
                              String comment, LocalDateTime createdAt) {
            this(reviewId, productId, productName, userId, userName, null, rating, comment, null, null, createdAt);
        }

        public AdminReviewDto(Integer reviewId, Integer productId, String productName,
                              Integer userId, String userName, String userEmail, Integer rating,
                              String comment, String images, Boolean isVerified, LocalDateTime createdAt) {
            this.reviewId = reviewId; this.productId = productId;
            this.productName = productName; this.userId = userId;
            this.userName = userName; this.userEmail = userEmail; this.rating = rating;
            this.comment = comment; this.images = images; this.isVerified = isVerified;
            this.createdAt = createdAt;
        }
        public Integer getReviewId() { return reviewId; }
        public Integer getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Integer getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserEmail() { return userEmail; }
        public Integer getRating() { return rating; }
        public String getComment() { return comment; }
        public String getImages() { return images; }
        public Boolean getIsVerified() { return isVerified; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class DataPoint {
        private final String label;
        private final BigDecimal value;
        public DataPoint(String label, BigDecimal value) { this.label = label; this.value = value; }
        public String getLabel() { return label; }
        public BigDecimal getValue() { return value; }
    }

    public static class StatsResponse {
        private final List<DataPoint> revenueData, topProducts, byCategory;
        private final BigDecimal totalRevenue;
        private final long totalOrders;
        public StatsResponse(List<DataPoint> revenueData, List<DataPoint> topProducts,
                             List<DataPoint> byCategory, BigDecimal totalRevenue, long totalOrders) {
            this.revenueData = revenueData; this.topProducts = topProducts;
            this.byCategory = byCategory; this.totalRevenue = totalRevenue;
            this.totalOrders = totalOrders;
        }
        public List<DataPoint> getRevenueData() { return revenueData; }
        public List<DataPoint> getTopProducts() { return topProducts; }
        public List<DataPoint> getByCategory() { return byCategory; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public long getTotalOrders() { return totalOrders; }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static String normalize(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase().replace("đ", "d");
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    private OrderResponse mapOrder(Order order) {
        Address address = order.getShippingAddressId() != null
                ? addressRepository.findById(order.getShippingAddressId()).orElse(null)
                : null;

        List<OrderItem> items = subOrderRepository.findByOrderId(order.getOrderId()).stream()
                .flatMap(sub -> orderItemRepository.findBySubOrderId(sub.getSubOrderId()).stream())
                .collect(Collectors.toList());

        List<OrderResponse.OrderItemResponse> itemResponses = items.stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .orderItemId(item.getOrderItemId())
                        .productId(item.getProductId())
                        .variantId(item.getVariantId())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getTotal())
                        .variantName(item.getVariantInfo())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode("ORD" + String.format("%08d", order.getOrderId()))
                .userId(order.getUserId())
                .recipientName(address != null ? address.getRecipientName() : null)
                .recipientPhone(address != null ? address.getPhone() : null)
                .shippingAddress(address != null ? address.getFullAddress() : null)
                .totalAmount(order.getTotalPrice())
                .shippingFee(order.getShippingFee())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .paymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null)
                .orderStatus(order.getStatus() != null ? order.getStatus().name() : null)
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    public static class UpdateProductRequest {
        private String productName;
        private String description;
        private Integer stock;
        private BigDecimal discount;
        private String status;
        private String imageUrl;

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public String getStatus() { return status; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class CreateProductRequest {
        private String productName;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private BigDecimal discount;
        private Integer categoryId;
        private Integer shopId;
        private String imageUrl;

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public Integer getCategoryId() { return categoryId; }
        public Integer getShopId() { return shopId; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class VariantMutationRequest {
        private String size;
        private String color;
        private String material;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;

        public String getSize() { return size; }
        public String getColor() { return color; }
        public String getMaterial() { return material; }
        public BigDecimal getPrice() { return price; }
        public Integer getStock() { return stock; }
        public String getImageUrl() { return imageUrl; }
    }

    public static class CategoryMutationRequest {
        private String categoryName;
        private String description;
        private String image;
        private Integer parentId;

        public String getCategoryName() { return categoryName; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public Integer getParentId() { return parentId; }
    }

    // ─── User management ──────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users;

        if (search != null && !search.isBlank()) {
            String q = normalize(search.trim());
            users = userRepository.findAll(pageable);
            List<User> filtered = users.getContent().stream()
                    .filter(u -> normalize(u.getEmail()).contains(q)
                            || normalize(u.getUsername()).contains(q)
                            || normalize(u.getFirstName()).contains(q)
                            || normalize(u.getLastName()).contains(q))
                    .collect(Collectors.toList());
            users = new PageImpl<>(filtered, pageable, filtered.size());
        } else {
            users = userRepository.findAll(pageable);
        }

        Page<UserResponse> response = users.map(UserResponse::fromEntity);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/{userId}/status")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Integer userId,
            @RequestBody UpdateStatusRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        User.UserStatus newStatus;
        try {
            newStatus = User.UserStatus.valueOf(request.getStatus().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Trạng thái không hợp lệ"));
        }

        user.setStatus(newStatus);
        user = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật trạng thái", UserResponse.fromEntity(user)));
    }

    public static class UpdateStatusRequest {
        private String status;
        public String getStatus() { return status; }
    }
}
