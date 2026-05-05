package com.furniture.api.controller;

import com.furniture.api.dto.request.AddToCartRequest;
import com.furniture.api.dto.request.UpdateCartItemRequest;
import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.CartResponse;
import com.furniture.api.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestAttribute("userId") Integer userId) {

        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @RequestAttribute("userId") Integer userId,
            @RequestBody AddToCartRequest request) {

        CartResponse cart = cartService.addToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Added to cart", cart));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer cartItemId,
            @RequestBody UpdateCartItemRequest request) {

        CartResponse cart = cartService.updateCartItem(userId, cartItemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cart));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer cartItemId) {

        cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.ok(ApiResponse.successMessage("Item removed from cart"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @RequestAttribute("userId") Integer userId) {

        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.successMessage("Cart cleared"));
    }
}
