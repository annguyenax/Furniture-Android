package com.furniture.app.ui.common;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.receiver.WifiConnectionReceiver;

public class WifiRequiredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (WifiConnectionReceiver.isWifiConnected(this)) {
            finish();
            return;
        }

        showWifiPrompt();
    }

    private void showWifiPrompt() {
        new AlertDialog.Builder(this)
                .setTitle("Cần kết nối Wi-Fi")
                .setMessage("Thiết bị chưa có kết nối Wi-Fi. Vui lòng bật hoặc kết nối Wi-Fi để sử dụng ứng dụng ổn định hơn.")
                .setNegativeButton("Để sau", (dialog, which) -> finish())
                .setPositiveButton("Mở Wi-Fi", (dialog, which) -> {
                    openWifiSettings();
                    finish();
                })
                .setOnCancelListener(dialog -> finish())
                .show();
    }

    private void openWifiSettings() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent = new Intent(Settings.Panel.ACTION_WIFI);
        } else {
            intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        WifiConnectionReceiver.markPromptClosed();
        super.onDestroy();
    }
}
