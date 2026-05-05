package com.furniture.api.service;

import com.furniture.api.dto.request.AddToCartRequest;
import com.furniture.api.dto.request.UpdateCartItemRequest;
import com.furniture.api.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Integer userId);

    CartResponse addToCart(Integer userId, AddToCartRequest request);

    CartResponse updateCartItem(Integer userId, Integer cartItemId, UpdateCartItemRequest request);

    void removeCartItem(Integer userId, Integer cartItemId);

    void clearCart(Integer userId);
}
