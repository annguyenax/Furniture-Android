package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.ui.customer.CustomerMainActivity;

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
            String status = data.getQueryParameter("status");
            String orderId = data.getQueryParameter("orderId");

            if ("success".equals(status)) {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
                // Navigate to order history
                Intent orderIntent = new Intent(this, OrderHistoryActivity.class);
                orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(orderIntent);
            } else {
                Toast.makeText(this, "Thanh toán thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                // Navigate back to main
                Intent mainIntent = new Intent(this, CustomerMainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        } else {
            // No data, go to main
            Intent mainIntent = new Intent(this, CustomerMainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
        }

        finish();
    }
}
