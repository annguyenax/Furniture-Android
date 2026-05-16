package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.model.Product;
import com.furniture.api.model.Wishlist;
import com.furniture.api.repository.ProductRepository;
import com.furniture.api.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistItem>>> getWishlist(Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        List<Wishlist> items = wishlistRepository.findByUserId(userId);
        List<WishlistItem> result = items.stream()
                .map(w -> {
                    Product p = productRepository.findById(w.getProductId()).orElse(null);
                    if (p == null) return null;
                    BigDecimal price = p.getVariants() != null && !p.getVariants().isEmpty()
                            ? p.getVariants().stream()
                                    .map(v -> v.getPrice())
                                    .filter(pr -> pr != null)
                                    .min(BigDecimal::compareTo)
                                    .orElse(BigDecimal.ZERO)
                            : BigDecimal.ZERO;
                    String image = p.getVariants() != null && !p.getVariants().isEmpty()
                            ? p.getVariants().get(0).getImageUrl() : null;
                    return new WishlistItem(w.getWishlistId(), p.getProductId(),
                            p.getProductName(), image, price, p.getDiscount());
                })
                .filter(i -> i != null)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> add(
            @PathVariable Integer productId, Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Sản phẩm đã có trong danh sách yêu thích"));
        }
        if (!productRepository.existsById(productId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Không tìm thấy sản phẩm"));
        }
        wishlistRepository.save(Wishlist.builder().userId(userId).productId(productId).build());
        return ResponseEntity.ok(ApiResponse.success("Đã thêm vào yêu thích", null));
    }

    @DeleteMapping("/{productId}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> remove(
            @PathVariable Integer productId, Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa khỏi yêu thích", null));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> check(
            @PathVariable Integer productId, Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(
                wishlistRepository.existsByUserIdAndProductId(userId, productId)));
    }

    public static class WishlistItem {
        private final Integer wishlistId;
        private final Integer productId;
        private final String productName;
        private final String productImage;
        private final BigDecimal price;
        private final BigDecimal discount;

        public WishlistItem(Integer wishlistId, Integer productId, String productName,
                            String productImage, BigDecimal price, BigDecimal discount) {
            this.wishlistId = wishlistId;
            this.productId = productId;
            this.productName = productName;
            this.productImage = productImage;
            this.price = price;
            this.discount = discount;
        }

        public Integer getWishlistId() { return wishlistId; }
        public Integer getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getProductImage() { return productImage; }
        public BigDecimal getPrice() { return price; }
        public BigDecimal getDiscount() { return discount; }
    }
}
