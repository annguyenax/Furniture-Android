package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.model.Order;
import com.furniture.api.model.OrderItem;
import com.furniture.api.model.ReturnRequest;
import com.furniture.api.model.SubOrder;
import com.furniture.api.model.User;
import com.furniture.api.repository.OrderItemRepository;
import com.furniture.api.repository.OrderRepository;
import com.furniture.api.repository.ProductRepository;
import com.furniture.api.repository.ReturnRequestRepository;
import com.furniture.api.repository.SubOrderRepository;
import com.furniture.api.repository.UserRepository;
import com.furniture.api.service.CloudinaryService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReturnRequestController {

    private final ReturnRequestRepository returnRequestRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SubOrderRepository subOrderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final com.furniture.api.repository.ProductReviewRepository reviewRepository;

    @PostMapping(value = "/returns", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> createReturnRequest(
            @RequestAttribute("userId") Integer userId,
            @RequestPart("orderId") String orderIdValue,
            @RequestPart(value = "orderItemId", required = false) String orderItemIdValue,
            @RequestPart("reason") String reason,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        Integer orderId = parseInteger(orderIdValue, "orderId");
        Integer orderItemId = parseOptionalInteger(orderItemIdValue, "orderItemId");
        if (reason == null || reason.trim().length() < 10) {
            throw new BadRequestException("Ly do hoan tra can toi thieu 10 ky tu");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Ban khong co quyen tao yeu cau hoan tra cho don hang nay");
        }
        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Chi co the hoan tra don hang da giao thanh cong");
        }

        OrderItem orderItem = null;
        if (orderItemId != null) {
            orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order item", "id", orderItemId));
            validateOrderItemBelongsToOrder(orderItem, orderId);
        }

        // Block return if user has already reviewed any product in this order
        if (reviewRepository.existsByUserIdAndOrderId(userId, orderId)) {
            throw new BadRequestException("Không thể hoàn hàng sau khi đã đánh giá sản phẩm");
        }

        boolean duplicated = returnRequestRepository.existsByOrderIdAndOrderItemIdAndUserIdAndStatusIn(
                orderId,
                orderItemId,
                userId,
                List.of(ReturnRequest.ReturnStatus.PENDING, ReturnRequest.ReturnStatus.APPROVED));
        if (duplicated) {
            throw new BadRequestException("San pham/don hang nay da co yeu cau hoan tra dang xu ly");
        }

        CloudinaryService.UploadResult upload = null;
        ReturnRequest.EvidenceType evidenceType = null;
        if (file != null && !file.isEmpty()) {
            upload = cloudinaryService.uploadReturnEvidence(file);
            evidenceType = file.getContentType() != null && file.getContentType().startsWith("video/")
                    ? ReturnRequest.EvidenceType.VIDEO
                    : ReturnRequest.EvidenceType.IMAGE;
        }

        ReturnRequest request = ReturnRequest.builder()
                .orderId(orderId)
                .orderItemId(orderItemId)
                .userId(userId)
                .reason(reason.trim())
                .evidenceUrl(upload != null ? upload.url() : null)
                .evidencePublicId(upload != null ? upload.publicId() : null)
                .evidenceType(evidenceType)
                .status(ReturnRequest.ReturnStatus.PENDING)
                .build();

        ReturnRequest saved = returnRequestRepository.save(request);
        return ResponseEntity.ok(ApiResponse.success("Return request created", toResponse(saved, order, orderItem)));
    }

    @GetMapping("/returns/check/{orderId}")
    public ResponseEntity<ApiResponse<String>> checkReturnStatus(
            @PathVariable Integer orderId,
            @RequestAttribute("userId") Integer userId) {
        return returnRequestRepository.findTopByOrderIdAndUserIdOrderByCreatedAtDesc(orderId, userId)
                .map(r -> ResponseEntity.ok(ApiResponse.success(r.getStatus().name())))
                .orElse(ResponseEntity.ok(ApiResponse.success((String) null)));
    }

    @GetMapping("/returns/my-returned-order-ids")
    public ResponseEntity<ApiResponse<List<Integer>>> getMyReturnedOrderIds(
            @RequestAttribute("userId") Integer userId) {
        List<Integer> ids = returnRequestRepository.findOrderIdsByUserIdAndStatusIn(
                userId,
                List.of(ReturnRequest.ReturnStatus.PENDING, ReturnRequest.ReturnStatus.APPROVED));
        return ResponseEntity.ok(ApiResponse.success(ids));
    }

    @GetMapping("/returns/my")
    public ResponseEntity<ApiResponse<Page<ReturnRequestResponse>>> getMyReturnRequests(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReturnRequestResponse> data = returnRequestRepository.findByUserId(userId, pageable)
                .map(this::toResponse);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping("/admin/returns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReturnRequestResponse>>> getAdminReturnRequests(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReturnRequest> requests;
        if (status != null && !status.isBlank()) {
            requests = returnRequestRepository.findByStatus(ReturnRequest.ReturnStatus.valueOf(status), pageable);
        } else {
            requests = returnRequestRepository.findAll(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success(requests.map(this::toResponse)));
    }

    @PutMapping("/admin/returns/{returnId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReturnRequestResponse>> updateReturnStatus(
            @PathVariable Integer returnId,
            @RequestParam String status,
            @RequestParam(required = false) String adminNote) {

        ReturnRequest request = returnRequestRepository.findById(returnId)
                .orElseThrow(() -> new ResourceNotFoundException("Return request", "id", returnId));

        ReturnRequest.ReturnStatus newStatus = ReturnRequest.ReturnStatus.valueOf(status);
        if (newStatus == ReturnRequest.ReturnStatus.PENDING) {
            throw new BadRequestException("Admin chi co the APPROVED hoac REJECTED yeu cau hoan tra");
        }

        request.setStatus(newStatus);
        request.setAdminNote(adminNote);
        ReturnRequest saved = returnRequestRepository.save(request);

        return ResponseEntity.ok(ApiResponse.success("Return status updated", toResponse(saved)));
    }

    private void validateOrderItemBelongsToOrder(OrderItem item, Integer orderId) {
        SubOrder subOrder = subOrderRepository.findById(item.getSubOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Sub order", "id", item.getSubOrderId()));
        if (!subOrder.getOrderId().equals(orderId)) {
            throw new BadRequestException("San pham khong thuoc don hang nay");
        }
    }

    private Integer parseInteger(String value, String field) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw new BadRequestException(field + " khong hop le");
        }
    }

    private Integer parseOptionalInteger(String value, String field) {
        if (value == null || value.isBlank()) return null;
        return parseInteger(value, field);
    }

    private ReturnRequestResponse toResponse(ReturnRequest request) {
        Order order = request.getOrderId() != null
                ? orderRepository.findById(request.getOrderId()).orElse(null)
                : null;
        OrderItem item = request.getOrderItemId() != null
                ? orderItemRepository.findById(request.getOrderItemId()).orElse(null)
                : null;
        return toResponse(request, order, item);
    }

    private ReturnRequestResponse toResponse(ReturnRequest request, Order order, OrderItem item) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        String productName = null;
        if (item != null && item.getProductId() != null) {
            productName = productRepository.findById(item.getProductId())
                    .map(product -> product.getProductName())
                    .orElse(null);
        }

        return ReturnRequestResponse.builder()
                .returnId(request.getReturnId())
                .orderId(request.getOrderId())
                .orderCode(order != null ? "#" + order.getOrderId() : "#" + request.getOrderId())
                .orderItemId(request.getOrderItemId())
                .userId(request.getUserId())
                .userName(user != null ? user.getFullName() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .productName(productName)
                .reason(request.getReason())
                .evidenceUrl(request.getEvidenceUrl())
                .evidenceType(request.getEvidenceType() != null ? request.getEvidenceType().name() : null)
                .status(request.getStatus().name())
                .adminNote(request.getAdminNote())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnRequestResponse {
        private Integer returnId;
        private Integer orderId;
        private String orderCode;
        private Integer orderItemId;
        private Integer userId;
        private String userName;
        private String userEmail;
        private String productName;
        private String reason;
        private String evidenceUrl;
        private String evidenceType;
        private String status;
        private String adminNote;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
