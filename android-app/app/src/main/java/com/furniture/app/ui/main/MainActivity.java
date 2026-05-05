package com.furniture.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.R;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.util.SessionManager;

/**
 * Splash/Entry Activity - Redirects to appropriate screen based on login status
 */
public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 1 second splash delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);

        // Delay for splash effect then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (sessionManager.isLoggedIn()) {
                intent = new Intent(this, CustomerMainActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
