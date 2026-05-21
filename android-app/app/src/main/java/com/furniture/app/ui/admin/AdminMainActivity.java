package com.furniture.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.R;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.ui.customer.profile.EditProfileActivity;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

public class AdminMainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        sessionManager = new SessionManager(this);

        // Re-init RetrofitClient với token đã lưu
        String token = sessionManager.getToken();
        if (token != null && !token.isEmpty()) {
            RetrofitClient.getInstance(token);
        }

        initViews();
    }

    private void initViews() {
        TextView tvAdminEmail = findViewById(R.id.tv_admin_email);
        tvAdminEmail.setText(sessionManager.getUserEmail());

        MaterialButton btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> logout());

        findViewById(R.id.card_products).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminProductListActivity.class));
        });

        findViewById(R.id.card_orders).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminOrderListActivity.class));
        });

        findViewById(R.id.card_categories).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminCategoryListActivity.class));
        });

        findViewById(R.id.card_users).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminUserListActivity.class));
        });

        findViewById(R.id.card_chat).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminChatListActivity.class));
        });

        findViewById(R.id.card_reviews).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminReviewListActivity.class));
        });

        findViewById(R.id.card_stats).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminStatsActivity.class));
        });

        findViewById(R.id.card_returns).setOnClickListener(v -> {
            startActivity(new Intent(this, AdminReturnListActivity.class));
        });

        findViewById(R.id.card_profile).setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });
    }

    private void logout() {
        sessionManager.clearSession();
        RetrofitClient.resetInstance();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
