package com.furniture.api.controller;

import com.furniture.api.model.Order;
import com.furniture.api.repository.OrderRepository;
import com.furniture.api.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Handles VNPay payment callbacks:
 *  - GET /payment/vnpay-return  → browser redirect after user pays (shown to user)
 *  - GET /payment/vnpay-ipn     → server-to-server notification from VNPay (for DB update)
 */
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${vnp.hash-secret}")
    private String hashSecret;

    @Value("${app.frontend-url:furnitureapp://payment}")
    private String deepLinkBase;

    private final OrderRepository orderRepository;

    /**
     * VNPay return URL — called by user's browser after payment.
     * Verifies signature, then redirects to Android deep link so
     * PaymentCallbackActivity handles it.
     */
    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> vnPayReturn(HttpServletRequest request)
            throws UnsupportedEncodingException {

        Map<String, String> fields = extractVnPayParams(request);
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = buildHashData(fields);
        String calculatedHash = VnPayUtil.hmacSHA512(hashSecret, signValue);

        String responseCode = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef"); // our orderCode e.g. "ORD00000001"
        boolean isValid = calculatedHash.equalsIgnoreCase(vnp_SecureHash);
        boolean isSuccess = isValid && "00".equals(responseCode);

        // Update payment status in DB
        if (txnRef != null) {
            // orderCode format is "ORD" + 8-digit orderId
            updateOrderPaymentStatus(txnRef, isSuccess);
        }

        // Redirect to Android deep link: furnitureapp://payment?vnp_ResponseCode=00&vnp_TxnRef=...
        String redirectUrl = deepLinkBase
                + "?vnp_ResponseCode=" + (responseCode != null ? responseCode : "99")
                + "&vnp_TxnRef=" + (txnRef != null ? URLEncoder.encode(txnRef, StandardCharsets.UTF_8.toString()) : "")
                + "&isValid=" + isValid;

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    /**
     * VNPay IPN — server-to-server notification.
     * Must respond with {"RspCode":"00","Message":"Confirm Success"} on success.
     */
    @GetMapping("/vnpay-ipn")
    public ResponseEntity<Map<String, String>> vnPayIpn(HttpServletRequest request)
            throws UnsupportedEncodingException {

        Map<String, String> fields = extractVnPayParams(request);
        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = buildHashData(fields);
        String calculatedHash = VnPayUtil.hmacSHA512(hashSecret, signValue);

        Map<String, String> response = new HashMap<>();

        if (!calculatedHash.equalsIgnoreCase(vnp_SecureHash)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid signature");
            return ResponseEntity.ok(response);
        }

        String responseCode = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");
        boolean isSuccess = "00".equals(responseCode);

        if (txnRef != null) {
            updateOrderPaymentStatus(txnRef, isSuccess);
        }

        response.put("RspCode", "00");
        response.put("Message", "Confirm Success");
        return ResponseEntity.ok(response);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Extracts all vnp_* params from the request into a sorted TreeMap.
     */
    private Map<String, String> extractVnPayParams(HttpServletRequest request) {
        Map<String, String> fields = new TreeMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            String value = request.getParameter(name);
            if (value != null && !value.isEmpty()) {
                fields.put(name, value);
            }
        }
        return fields;
    }

    /**
     * Builds the hash data string per VNPay spec:
     * sorted keys, URL-encoded values, joined with '&'.
     */
    private String buildHashData(Map<String, String> fields) throws UnsupportedEncodingException {
        List<String> keys = new ArrayList<>(fields.keySet());
        Collections.sort(keys);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = fields.get(key);
            if (value != null && !value.isEmpty()) {
                if (hashData.length() > 0) hashData.append('&');
                hashData.append(key).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8.toString()));
            }
        }
        return hashData.toString();
    }

    /**
     * Finds order by orderCode (vnp_TxnRef) and updates payment status.
     * orderCode format: "ORD" + zero-padded orderId (e.g. "ORD00000001")
     */
    private void updateOrderPaymentStatus(String orderCode, boolean paid) {
        try {
            // Parse orderId from "ORD00000001" → 1
            String idStr = orderCode.replace("ORD", "").replaceFirst("^0+(?!$)", "");
            Integer orderId = Integer.parseInt(idStr);
            orderRepository.findById(orderId).ifPresent(order -> {
                if (paid) {
                    order.setPaymentStatus(Order.PaymentStatus.PAID);
                    // Optionally move from PENDING → PROCESSING when payment confirmed
                    if (order.getStatus() == Order.OrderStatus.PENDING) {
                        order.setStatus(Order.OrderStatus.PROCESSING);
                    }
                } else {
                    order.setPaymentStatus(Order.PaymentStatus.FAILED);
                }
                orderRepository.save(order);
            });
        } catch (Exception e) {
            // Log but don't rethrow — VNPay IPN must always get a response
        }
    }
}
