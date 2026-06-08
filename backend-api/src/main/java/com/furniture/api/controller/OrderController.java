package com.furniture.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.furniture.api.dto.request.CreateOrderRequest;
import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.OrderResponse;
import com.furniture.api.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestAttribute("userId") Integer userId,
            @Valid @RequestBody CreateOrderRequest request) {

        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer orderId) {

        OrderResponse order = orderService.getOrderById(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getUserOrders(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getUserOrders(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getUserOrdersByStatus(
            @RequestAttribute("userId") Integer userId,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> orders = orderService.getUserOrdersByStatus(userId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer orderId) {

        OrderResponse order = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }

    @PostMapping("/{orderId}/confirm-received")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmReceived(
            @RequestAttribute("userId") Integer userId,
            @PathVariable Integer orderId) {

        OrderResponse order = orderService.confirmReceived(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success("Order received successfully", order));
    }
}
