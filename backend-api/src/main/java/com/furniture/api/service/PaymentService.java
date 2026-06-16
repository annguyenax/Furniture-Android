package com.furniture.api.service;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface PaymentService {
    String createVnPayPaymentUrl(HttpServletRequest request, String orderCode, BigDecimal amount, String orderInfo);
}
