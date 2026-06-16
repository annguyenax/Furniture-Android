package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.ui.customer.CustomerMainActivity;

/**
 * Handles the deep link redirect from our backend /payment/vnpay-return.
 *
 * Deep link format (defined in AndroidManifest):
 *   furnitureapp://payment?vnp_ResponseCode=00&vnp_TxnRef=ORD00000001&isValid=true
 *
 * VNPay response codes:
 *   "00" = success, anything else = failure
 */
public class PaymentCallbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handlePaymentCallback();
    }

    private void handlePaymentCallback() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            // BUG FIX: Read the correct VNPay params — "vnp_ResponseCode" and "vnp_TxnRef",
            // NOT "status" and "orderId" which don't exist in VNPay's callback.
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            String txnRef      = data.getQueryParameter("vnp_TxnRef");   // our orderCode
            String isValidStr  = data.getQueryParameter("isValid");
            boolean isValid    = "true".equals(isValidStr);

            // "00" means transaction successful, signature must also be valid
            boolean isSuccess  = "00".equals(responseCode) && isValid;

            if (isSuccess) {
                Toast.makeText(this, "Thanh toán thành công! Mã đơn: " + txnRef, Toast.LENGTH_LONG).show();
                // Navigate to order history
                Intent orderIntent = new Intent(this, OrderHistoryActivity.class);
                orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(orderIntent);
            } else {
                // Payment failed or cancelled
                String reason = getFailureReason(responseCode);
                Toast.makeText(this, "Thanh toán thất bại: " + reason + ". Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                // Go back to main — user can retry from Order History
                Intent mainIntent = new Intent(this, CustomerMainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
            }
        } else {
            // No data (e.g. direct launch) — go to main
            Intent mainIntent = new Intent(this, CustomerMainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
        }

        finish();
    }

    /**
     * Maps common VNPay response codes to human-readable messages.
     * Full list: https://sandbox.vnpayment.vn/apis/docs/thanh-toan-pay/pay.md
     */
    private String getFailureReason(String responseCode) {
        if (responseCode == null) return "Không xác định";
        switch (responseCode) {
            case "07": return "Giao dịch bị nghi ngờ gian lận";
            case "09": return "Thẻ/Tài khoản chưa đăng ký dịch vụ";
            case "10": return "Xác thực thông tin thẻ/tài khoản sai quá 3 lần";
            case "11": return "Đã hết hạn chờ thanh toán";
            case "12": return "Thẻ/Tài khoản bị khóa";
            case "13": return "OTP sai";
            case "24": return "Khách hàng hủy giao dịch";
            case "51": return "Tài khoản không đủ số dư";
            case "65": return "Vượt hạn mức giao dịch trong ngày";
            case "75": return "Ngân hàng bảo trì";
            case "79": return "Nhập sai mật khẩu thanh toán quá số lần";
            default:   return "Mã lỗi: " + responseCode;
        }
    }
}
