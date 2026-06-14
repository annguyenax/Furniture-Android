package com.furniture.api.service.ai;

import com.furniture.api.dto.request.AddToCartRequest;
import com.furniture.api.dto.response.CartResponse;
import com.furniture.api.dto.response.CategoryResponse;
import com.furniture.api.dto.response.ChatbotMessageResponse;
import com.furniture.api.dto.response.OrderResponse;
import com.furniture.api.dto.response.ProductResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.model.Address;
import com.furniture.api.model.Wishlist;
import com.furniture.api.repository.AddressRepository;
import com.furniture.api.repository.WishlistRepository;
import com.furniture.api.service.CartService;
import com.furniture.api.service.CategoryService;
import com.furniture.api.service.OrderService;
import com.furniture.api.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ChatbotToolExecutor {

    private static final int DEFAULT_LIMIT = 12;
    private static final int MAX_CART_QUANTITY = 10;
    private static final int DEFAULT_ORDER_LIMIT = 5;
    private static final int MAX_ORDER_LIMIT = 10;

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;
    private final OrderService orderService;
    private final WishlistRepository wishlistRepository;
    private final AddressRepository addressRepository;

    public ToolExecutionResult execute(String toolName, Map<String, Object> args, Integer userId) {
        return switch (toolName) {
            case "search_products" -> searchProducts(args);
            case "get_product_detail" -> getProductDetail(args);
            case "list_categories" -> listCategories();
            case "add_to_cart" -> addToCart(args, userId);
            case "get_cart" -> getCart(userId);
            case "remove_cart_item" -> removeCartItem(args, userId);
            case "get_my_orders" -> getMyOrders(args, userId);
            case "get_order_detail" -> getOrderDetail(args, userId);
            case "get_wishlist" -> getWishlist(userId);
            case "add_to_wishlist" -> addToWishlist(args, userId);
            case "get_shipping_addresses" -> getShippingAddresses(userId);
            case "get_default_shipping_address" -> getDefaultShippingAddress(userId);
            default -> ToolExecutionResult.error("Tool không được hỗ trợ: " + toolName);
        };
    }

    private ToolExecutionResult searchProducts(Map<String, Object> args) {
        String keyword = stringArg(args, "keyword");
        String categoryName = stringArg(args, "categoryName");
        BigDecimal minPrice = decimalArg(args, "minPrice");
        BigDecimal maxPrice = decimalArg(args, "maxPrice");
        String sortBy = stringArg(args, "sortBy");

        List<ProductResponse> products;
        CategoryResponse category = findCategory(categoryName);
        if (category != null) {
            products = productService.getProductsByCategory(category.getCategoryId(), keyword, PageRequest.of(0, 100))
                    .getContent();
        } else {
            products = productService.searchProducts(keyword, PageRequest.of(0, 100)).getContent();
        }

        List<ChatbotMessageResponse.ChatbotProductResponse> mapped = products.stream()
                .map(this::toChatbotProduct)
                .filter(p -> priceInRange(p.getFinalPrice(), minPrice, maxPrice))
                .sorted(comparator(sortBy))
                .limit(DEFAULT_LIMIT)
                .toList();

        return ToolExecutionResult.success(Map.of("products", mapped, "count", mapped.size()), mapped);
    }

    private ToolExecutionResult getProductDetail(Map<String, Object> args) {
        Integer productId = intArg(args, "productId");
        if (productId == null) {
            return ToolExecutionResult.error("Thiếu productId");
        }

        ChatbotMessageResponse.ChatbotProductResponse product =
                toChatbotProduct(productService.getProductById(productId));
        return ToolExecutionResult.success(Map.of("product", product), List.of(product));
    }

    private ToolExecutionResult listCategories() {
        List<Map<String, Object>> categories = categoryService.getAllCategories().stream()
                .map(c -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("categoryId", c.getCategoryId());
                    item.put("name", c.getCategoryName());
                    item.put("description", c.getDescription() != null ? c.getDescription() : "");
                    return item;
                })
                .toList();
        return ToolExecutionResult.success(Map.of("categories", categories), List.of());
    }

    private ToolExecutionResult addToCart(Map<String, Object> args, Integer userId) {
        Integer productId = intArg(args, "productId");
        if (productId == null) {
            return ToolExecutionResult.error("Thiếu productId");
        }

        Integer quantity = normalizeQuantity(intArg(args, "quantity"));
        if (quantity == null) {
            return ToolExecutionResult.error("Số lượng phải từ 1 đến " + MAX_CART_QUANTITY);
        }

        Integer requestedVariantId = intArg(args, "variantId");
        ChatbotMessageResponse.ChatbotProductResponse product;
        try {
            product = toChatbotProduct(productService.getProductById(productId));
        } catch (ResourceNotFoundException e) {
            return ToolExecutionResult.error(e.getMessage());
        }

        VariantSelection selection = resolveVariant(product, requestedVariantId, quantity);
        if (!selection.canAdd()) {
            return new ToolExecutionResult(selection.response(), List.of(product), List.of(viewProductAction(productId)));
        }

        try {
            CartResponse cart = cartService.addToCart(userId, AddToCartRequest.builder()
                    .productId(productId)
                    .variantId(selection.variant().getVariantId())
                    .quantity(quantity)
                    .build());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Đã thêm sản phẩm vào giỏ hàng");
            response.put("productId", productId);
            response.put("productName", product.getName());
            response.put("variantId", selection.variant().getVariantId());
            response.put("variantName", variantLabel(selection.variant()));
            response.put("quantity", quantity);
            response.put("cartTotalItems", cart.getTotalItems());
            response.put("cartTotalAmount", cart.getTotalAmount());

            return new ToolExecutionResult(response, List.of(product), List.of(viewCartAction(), checkoutAction()));
        } catch (ResourceNotFoundException | BadRequestException e) {
            return ToolExecutionResult.error(e.getMessage());
        }
    }

    private ToolExecutionResult getCart(Integer userId) {
        CartResponse cart = cartService.getCart(userId);
        List<Map<String, Object>> items = cart.getItems() == null ? List.of() : cart.getItems().stream()
                .map(this::toCartItemMap)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", items);
        response.put("totalItems", cart.getTotalItems());
        response.put("totalAmount", cart.getTotalAmount());

        List<ChatbotMessageResponse.ChatbotSuggestedAction> actions =
                items.isEmpty() ? List.of() : List.of(viewCartAction(), checkoutAction());
        return new ToolExecutionResult(response, List.of(), actions);
    }

    private ToolExecutionResult removeCartItem(Map<String, Object> args, Integer userId) {
        Integer cartItemId = intArg(args, "cartItemId");
        if (cartItemId == null) {
            return ToolExecutionResult.error("Thiếu cartItemId. Hãy gọi get_cart trước để xác định dòng cần xóa.");
        }

        try {
            cartService.removeCartItem(userId, cartItemId);
            return new ToolExecutionResult(
                    Map.of("success", true, "message", "Đã xóa sản phẩm khỏi giỏ hàng"),
                    List.of(),
                    List.of(viewCartAction())
            );
        } catch (ResourceNotFoundException | BadRequestException e) {
            return ToolExecutionResult.error(e.getMessage());
        }
    }

    private ToolExecutionResult getMyOrders(Map<String, Object> args, Integer userId) {
        int limit = boundedLimit(intArg(args, "limit"), DEFAULT_ORDER_LIMIT, MAX_ORDER_LIMIT);
        String status = normalizeOrderStatus(stringArg(args, "status"));
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("createdAt").descending());

        List<Map<String, Object>> orders = (status == null
                ? orderService.getUserOrders(userId, pageRequest)
                : orderService.getUserOrdersByStatus(userId, status, pageRequest))
                .getContent()
                .stream()
                .map(this::toOrderSummaryMap)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("orders", orders);
        response.put("count", orders.size());
        if (status != null) {
            response.put("status", status);
        }
        return ToolExecutionResult.success(response, List.of());
    }

    private ToolExecutionResult getOrderDetail(Map<String, Object> args, Integer userId) {
        Integer orderId = intArg(args, "orderId");
        if (orderId == null) {
            return ToolExecutionResult.error("Thiếu orderId");
        }

        try {
            OrderResponse order = orderService.getOrderById(userId, orderId);
            return ToolExecutionResult.success(Map.of("order", toOrderDetailMap(order)), List.of());
        } catch (ResourceNotFoundException | BadRequestException e) {
            return ToolExecutionResult.error(e.getMessage());
        }
    }

    private ToolExecutionResult getWishlist(Integer userId) {
        List<ChatbotMessageResponse.ChatbotProductResponse> products = wishlistRepository.findByUserId(userId).stream()
                .map(Wishlist::getProductId)
                .map(this::safeChatbotProduct)
                .filter(Objects::nonNull)
                .toList();

        return new ToolExecutionResult(
                Map.of("products", products, "count", products.size()),
                products,
                List.of()
        );
    }

    private ToolExecutionResult addToWishlist(Map<String, Object> args, Integer userId) {
        Integer productId = intArg(args, "productId");
        if (productId == null) {
            return ToolExecutionResult.error("Thiếu productId");
        }

        ChatbotMessageResponse.ChatbotProductResponse product = safeChatbotProduct(productId);
        if (product == null) {
            return ToolExecutionResult.error("Không tìm thấy sản phẩm");
        }
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return new ToolExecutionResult(
                    Map.of("success", true, "message", "Sản phẩm đã có trong danh sách yêu thích"),
                    List.of(product),
                    List.of(viewProductAction(productId))
            );
        }

        wishlistRepository.save(Wishlist.builder().userId(userId).productId(productId).build());
        return new ToolExecutionResult(
                Map.of("success", true, "message", "Đã thêm vào danh sách yêu thích"),
                List.of(product),
                List.of(viewProductAction(productId))
        );
    }

    private ToolExecutionResult getShippingAddresses(Integer userId) {
        List<Map<String, Object>> addresses = addressRepository.findByUserId(userId).stream()
                .map(this::toAddressMap)
                .toList();
        return ToolExecutionResult.success(Map.of("addresses", addresses, "count", addresses.size()), List.of());
    }

    private ToolExecutionResult getDefaultShippingAddress(Integer userId) {
        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .map(address -> ToolExecutionResult.success(Map.of("address", toAddressMap(address)), List.of()))
                .orElseGet(() -> ToolExecutionResult.success(Map.of("address", Map.of(), "message", "Bạn chưa có địa chỉ mặc định"), List.of()));
    }

    private Map<String, Object> toCartItemMap(CartResponse.CartItemResponse item) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cartItemId", item.getCartItemId());
        row.put("productId", item.getProductId());
        row.put("productName", item.getProductName());
        row.put("variantId", item.getVariantId());
        row.put("variantName", item.getVariantName() != null ? item.getVariantName() : "");
        row.put("quantity", item.getQuantity());
        row.put("price", item.getPrice());
        row.put("subtotal", item.getSubtotal());
        row.put("stock", item.getStock());
        return row;
    }

    private Map<String, Object> toOrderSummaryMap(OrderResponse order) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("orderId", order.getOrderId());
        row.put("orderCode", order.getOrderCode());
        row.put("orderStatus", order.getOrderStatus());
        row.put("paymentStatus", order.getPaymentStatus());
        row.put("paymentMethod", order.getPaymentMethod());
        row.put("totalAmount", order.getTotalAmount());
        row.put("createdAt", order.getCreatedAt());
        row.put("itemCount", order.getItems() != null ? order.getItems().size() : 0);
        return row;
    }

    private Map<String, Object> toOrderDetailMap(OrderResponse order) {
        Map<String, Object> row = toOrderSummaryMap(order);
        row.put("recipientName", order.getRecipientName());
        row.put("recipientPhone", maskPhone(order.getRecipientPhone()));
        row.put("shippingAddress", order.getShippingAddress());
        row.put("shippingFee", order.getShippingFee());
        row.put("subtotal", order.getSubtotal());
        row.put("note", order.getNote());
        row.put("items", order.getItems() == null ? List.of() : order.getItems().stream()
                .map(item -> {
                    Map<String, Object> i = new LinkedHashMap<>();
                    i.put("productId", item.getProductId());
                    i.put("productName", item.getProductName());
                    i.put("variantId", item.getVariantId());
                    i.put("variantName", item.getVariantName());
                    i.put("price", item.getPrice());
                    i.put("quantity", item.getQuantity());
                    i.put("subtotal", item.getSubtotal());
                    return i;
                })
                .toList());
        return row;
    }

    private Map<String, Object> toAddressMap(Address address) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("addressId", address.getAddressId());
        row.put("recipientName", address.getRecipientName());
        row.put("phone", maskPhone(address.getPhone()));
        row.put("city", address.getCity());
        row.put("district", address.getDistrict());
        row.put("ward", address.getWard());
        row.put("addressLine", address.getAddressLine());
        row.put("fullAddress", address.getFullAddress());
        row.put("isDefault", Boolean.TRUE.equals(address.getIsDefault()));
        return row;
    }

    private ChatbotMessageResponse.ChatbotProductResponse safeChatbotProduct(Integer productId) {
        try {
            return toChatbotProduct(productService.getProductById(productId));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String normalizeOrderStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return null;
        }
        String normalized = normalize(rawStatus);
        if (normalized.contains("cho") || normalized.contains("pending")) return "PENDING";
        if (normalized.contains("xu ly") || normalized.contains("processing")) return "PROCESSING";
        if (normalized.contains("giao") || normalized.contains("ship")) return "SHIPPED";
        if (normalized.contains("nhan") || normalized.contains("xong") || normalized.contains("delivered")) return "DELIVERED";
        if (normalized.contains("huy") || normalized.contains("cancel")) return "CANCELLED";
        String upper = rawStatus.trim().toUpperCase();
        return List.of("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED").contains(upper) ? upper : null;
    }

    private int boundedLimit(Integer value, int defaultValue, int maxValue) {
        if (value == null) {
            return defaultValue;
        }
        return Math.max(1, Math.min(value, maxValue));
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return "***" + phone.substring(phone.length() - 4);
    }

    private VariantSelection resolveVariant(ChatbotMessageResponse.ChatbotProductResponse product,
                                            Integer requestedVariantId,
                                            int quantity) {
        List<ChatbotMessageResponse.ChatbotVariantResponse> variants =
                product.getVariants() == null ? List.of() : product.getVariants();

        if (variants.isEmpty()) {
            return VariantSelection.blocked(Map.of(
                    "needs_variant_selection", false,
                    "error", "Sản phẩm chưa có phân loại/giá để thêm vào giỏ hàng"
            ));
        }

        if (requestedVariantId != null) {
            return variants.stream()
                    .filter(v -> requestedVariantId.equals(v.getVariantId()))
                    .findFirst()
                    .map(v -> safeStock(v.getStock()) >= quantity
                            ? VariantSelection.selected(v)
                            : VariantSelection.blocked(Map.of(
                                    "out_of_stock", true,
                                    "message", "Phân loại đã chọn không đủ hàng",
                                    "availableStock", safeStock(v.getStock())
                            )))
                    .orElseGet(() -> VariantSelection.blocked(Map.of(
                            "needs_variant_selection", true,
                            "message", "Phân loại đã chọn không thuộc sản phẩm này",
                            "variants", variantOptions(variants)
                    )));
        }

        List<ChatbotMessageResponse.ChatbotVariantResponse> inStock = variants.stream()
                .filter(v -> safeStock(v.getStock()) >= quantity)
                .toList();

        if (inStock.isEmpty()) {
            return VariantSelection.blocked(Map.of(
                    "out_of_stock", true,
                    "message", "Sản phẩm hiện không đủ hàng cho số lượng đã chọn",
                    "variants", variantOptions(variants)
            ));
        }

        if (inStock.size() == 1) {
            return VariantSelection.selected(inStock.get(0));
        }

        return VariantSelection.blocked(Map.of(
                "needs_variant_selection", true,
                "message", "Sản phẩm có nhiều phân loại. Hãy hỏi khách chọn màu/kích thước trước khi thêm vào giỏ.",
                "variants", variantOptions(inStock)
        ));
    }

    private List<Map<String, Object>> variantOptions(List<ChatbotMessageResponse.ChatbotVariantResponse> variants) {
        return variants.stream()
                .map(v -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("variantId", v.getVariantId());
                    row.put("label", variantLabel(v));
                    row.put("price", v.getPrice());
                    row.put("stock", safeStock(v.getStock()));
                    return row;
                })
                .toList();
    }

    private ChatbotMessageResponse.ChatbotSuggestedAction viewCartAction() {
        return ChatbotMessageResponse.ChatbotSuggestedAction.builder()
                .type("VIEW_CART")
                .label("Xem giỏ hàng")
                .build();
    }

    private ChatbotMessageResponse.ChatbotSuggestedAction checkoutAction() {
        return ChatbotMessageResponse.ChatbotSuggestedAction.builder()
                .type("CHECKOUT")
                .label("Đi thanh toán")
                .build();
    }

    private ChatbotMessageResponse.ChatbotSuggestedAction viewProductAction(Integer productId) {
        return ChatbotMessageResponse.ChatbotSuggestedAction.builder()
                .type("VIEW_PRODUCT")
                .label("Xem chi tiết")
                .productId(productId)
                .build();
    }

    private CategoryResponse findCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        String q = normalize(categoryName);
        return categoryService.getAllCategories().stream()
                .filter(c -> normalize(c.getCategoryName()).contains(q) || q.contains(normalize(c.getCategoryName())))
                .findFirst()
                .orElse(null);
    }

    private ChatbotMessageResponse.ChatbotProductResponse toChatbotProduct(ProductResponse product) {
        List<ChatbotMessageResponse.ChatbotVariantResponse> variants = product.getVariants() == null
                ? List.of()
                : product.getVariants().stream()
                    .map(v -> ChatbotMessageResponse.ChatbotVariantResponse.builder()
                            .variantId(v.getVariantId())
                            .color(v.getColor())
                            .size(v.getSize())
                            .price(v.getPrice())
                            .stock(v.getStock())
                            .imageUrl(v.getImageUrl())
                            .build())
                    .toList();

        BigDecimal price = variants.stream()
                .map(ChatbotMessageResponse.ChatbotVariantResponse::getPrice)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        BigDecimal discount = product.getDiscount() != null ? product.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalPrice = price.subtract(price.multiply(discount).divide(BigDecimal.valueOf(100)));
        String imageUrl = variants.stream()
                .map(ChatbotMessageResponse.ChatbotVariantResponse::getImageUrl)
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null);

        return ChatbotMessageResponse.ChatbotProductResponse.builder()
                .productId(product.getProductId())
                .name(product.getProductName())
                .category(product.getCategoryName())
                .price(price)
                .discount(discount)
                .finalPrice(finalPrice)
                .stock(product.getStock())
                .rating(product.getAverageRating())
                .shortDescription(shorten(product.getDescription()))
                .imageUrl(imageUrl)
                .variants(variants)
                .build();
    }

    private boolean priceInRange(BigDecimal price, BigDecimal minPrice, BigDecimal maxPrice) {
        if (price == null) {
            return true;
        }
        if (minPrice != null && price.compareTo(minPrice) < 0) {
            return false;
        }
        return maxPrice == null || price.compareTo(maxPrice) <= 0;
    }

    private Comparator<ChatbotMessageResponse.ChatbotProductResponse> comparator(String sortBy) {
        if ("price_desc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(ChatbotMessageResponse.ChatbotProductResponse::getFinalPrice,
                    Comparator.nullsLast(Comparator.reverseOrder()));
        }
        if ("rating_desc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(ChatbotMessageResponse.ChatbotProductResponse::getRating,
                    Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return Comparator.comparing(ChatbotMessageResponse.ChatbotProductResponse::getFinalPrice,
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private Integer normalizeQuantity(Integer quantity) {
        int resolved = quantity != null ? quantity : 1;
        if (resolved < 1 || resolved > MAX_CART_QUANTITY) {
            return null;
        }
        return resolved;
    }

    private int safeStock(Integer stock) {
        return stock != null ? stock : 0;
    }

    private String variantLabel(ChatbotMessageResponse.ChatbotVariantResponse variant) {
        List<String> parts = new ArrayList<>();
        if (variant.getColor() != null && !variant.getColor().isBlank()) {
            parts.add(variant.getColor());
        }
        if (variant.getSize() != null && !variant.getSize().isBlank()) {
            parts.add(variant.getSize());
        }
        return parts.isEmpty() ? "Phân loại #" + variant.getVariantId() : String.join(" - ", parts);
    }

    private String shorten(String description) {
        if (description == null || description.length() <= 180) {
            return description;
        }
        return description.substring(0, 177) + "...";
    }

    private String stringArg(Map<String, Object> args, String key) {
        Object value = args != null ? args.get(key) : null;
        return value == null ? null : String.valueOf(value).trim();
    }

    private Integer intArg(Map<String, Object> args, String key) {
        Object value = args != null ? args.get(key) : null;
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private BigDecimal decimalArg(Map<String, Object> args, String key) {
        Object value = args != null ? args.get(key) : null;
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        if (value != null && !String.valueOf(value).isBlank()) {
            try {
                return new BigDecimal(String.valueOf(value));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        String lower = text.toLowerCase().replace("đ", "d");
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private record VariantSelection(ChatbotMessageResponse.ChatbotVariantResponse variant,
                                    Map<String, Object> response) {
        private static VariantSelection selected(ChatbotMessageResponse.ChatbotVariantResponse variant) {
            return new VariantSelection(variant, Map.of());
        }

        private static VariantSelection blocked(Map<String, Object> response) {
            return new VariantSelection(null, response);
        }

        private boolean canAdd() {
            return variant != null;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ToolExecutionResult {
        private Map<String, Object> response;
        private List<ChatbotMessageResponse.ChatbotProductResponse> products;
        private List<ChatbotMessageResponse.ChatbotSuggestedAction> suggestedActions;

        public static ToolExecutionResult success(Map<String, Object> response,
                                                  List<ChatbotMessageResponse.ChatbotProductResponse> products) {
            return new ToolExecutionResult(response, products, List.of());
        }

        public static ToolExecutionResult error(String message) {
            return new ToolExecutionResult(Map.of("error", message), List.of(), List.of());
        }
    }
}
