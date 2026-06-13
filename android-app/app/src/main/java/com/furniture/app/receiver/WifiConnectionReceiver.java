package com.furniture.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.furniture.app.ui.common.WifiRequiredActivity;

public class WifiConnectionReceiver extends BroadcastReceiver {

    private static final long PROMPT_COOLDOWN_MS = 15000L;
    private static long lastPromptAt;
    private static boolean promptShowing;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null || !isNetworkAction(intent.getAction())) {
            return;
        }

        if (isWifiConnected(context)) {
            promptShowing = false;
            return;
        }

        long now = System.currentTimeMillis();
        if (promptShowing || now - lastPromptAt < PROMPT_COOLDOWN_MS) {
            return;
        }

        promptShowing = true;
        lastPromptAt = now;
        Intent promptIntent = new Intent(context, WifiRequiredActivity.class);
        promptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(promptIntent);
    }

    public static void markPromptClosed() {
        promptShowing = false;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) {
                return false;
            }
            NetworkCapabilities capabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork);
            return capabilities != null
                    && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null
                && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private boolean isNetworkAction(String action) {
        return ConnectivityManager.CONNECTIVITY_ACTION.equals(action)
                || WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)
                || WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action);
    }
}
