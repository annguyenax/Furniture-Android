package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.CategoryResponse;
import com.furniture.api.dto.response.OrderResponse;
import com.furniture.api.dto.response.ProductResponse;
import com.furniture.api.dto.response.UserResponse;
import com.furniture.api.model.Category;
import com.furniture.api.model.Order;
import com.furniture.api.model.Product;
import com.furniture.api.model.ProductVariant;
import com.furniture.api.model.User;
import com.furniture.api.repository.AddressRepository;
import com.furniture.api.repository.CategoryRepository;
import com.furniture.api.repository.OrderItemRepository;
import com.furniture.api.repository.OrderRepository;
import com.furniture.api.repository.ProductRepository;
import com.furniture.api.repository.ProductVariantRepository;
import com.furniture.api.repository.SubOrderRepository;
import com.furniture.api.repository.UserRepository;
import com.furniture.api.model.Address;
import com.furniture.api.model.OrderItem;
import com.furniture.api.model.SubOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
            String q = search.trim().toLowerCase();
            responses = responses.stream()
                    .filter(r -> (r.getOrderCode() != null && r.getOrderCode().toLowerCase().contains(q))
                            || (r.getRecipientName() != null && r.getRecipientName().toLowerCase().contains(q)))
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
        return ResponseEntity.ok(ApiResponse.success("Product updated", ProductResponse.fromEntity(product)));
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
        categoryRepository.deleteById(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa danh mục", null));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

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

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public String getStatus() { return status; }
    }

    public static class CreateProductRequest {
        private String productName;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private BigDecimal discount;
        private Integer categoryId;
        private Integer shopId;

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public Integer getCategoryId() { return categoryId; }
        public Integer getShopId() { return shopId; }
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
            String q = search.trim().toLowerCase();
            users = userRepository.findAll(pageable);
            List<User> filtered = users.getContent().stream()
                    .filter(u -> (u.getEmail() != null && u.getEmail().toLowerCase().contains(q))
                            || (u.getUsername() != null && u.getUsername().toLowerCase().contains(q))
                            || (u.getFirstName() != null && u.getFirstName().toLowerCase().contains(q))
                            || (u.getLastName() != null && u.getLastName().toLowerCase().contains(q)))
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
