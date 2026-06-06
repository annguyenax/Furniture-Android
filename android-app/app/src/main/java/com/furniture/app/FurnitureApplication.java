package com.furniture.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.interceptor.AuthInterceptor;
import com.furniture.app.ui.auth.LoginActivity;

public class FurnitureApplication extends Application {

    private static FurnitureApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setupAuthHandler();
    }

    public static FurnitureApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }

    // Đăng ký handler tự động logout khi token hết hạn (401)
    private void setupAuthHandler() {
        AuthInterceptor.setUnauthorizedHandler(() -> {
            RetrofitClient.resetInstance();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("message", "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
            getApplicationContext().startActivity(intent);
        });
    }
}
