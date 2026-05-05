package com.furniture.api.service;

import com.furniture.api.dto.request.CreateOrderRequest;
import com.furniture.api.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(Integer userId, CreateOrderRequest request);

    OrderResponse getOrderById(Integer userId, Integer orderId);

    Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable);

    Page<OrderResponse> getUserOrdersByStatus(Integer userId, String status, Pageable pageable);

    OrderResponse cancelOrder(Integer userId, Integer orderId);
}
