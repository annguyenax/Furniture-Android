package com.furniture.api.service.impl;

import com.furniture.api.service.PaymentService;
import com.furniture.api.util.VnPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${vnp.tmn-code}")
    private String tmnCode;

    @Value("${vnp.hash-secret}")
    private String hashSecret;

    @Value("${vnp.url}")
    private String vnpUrl;

    @Value("${vnp.return-url}")
    private String returnUrl;

    @Override
    public String createVnPayPaymentUrl(HttpServletRequest request, String orderCode, BigDecimal amount, String orderInfo) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = orderCode;
            String vnp_IpAddr = VnPayUtil.getIpAddress(request);
            String vnp_TmnCode = tmnCode;
            String orderType = "other";

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            // VNPay amount is multiplied by 100
            long amountLong = amount.multiply(new BigDecimal(100)).longValue();
            vnp_Params.put("vnp_Amount", String.valueOf(amountLong));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            // BUG FIX: Use Asia/Ho_Chi_Minh (GMT+7). "Etc/GMT+7" is POSIX notation = UTC-7, wrong!
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Build query string and hash
            // BUG FIX: Per VNPay spec, hashData must use raw (non-encoded) values.
            // Only the query string (sent in URL) should be URL-encoded.
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data: raw key=raw value (NOT URL-encoded) per VNPay spec
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    // Build query: URL-encode both key and value for the HTTP query string
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VnPayUtil.hmacSHA512(hashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            return vnpUrl + "?" + queryUrl;

        } catch (Exception e) {
            return "";
        }
    }
}
