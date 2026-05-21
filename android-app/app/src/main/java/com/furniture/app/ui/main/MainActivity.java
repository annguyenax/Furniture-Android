package com.furniture.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.R;
import com.furniture.app.ui.admin.AdminMainActivity;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn() && sessionManager.isAdmin()) {
                intent = new Intent(this, AdminMainActivity.class);
            } else {
                intent = new Intent(this, CustomerMainActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
