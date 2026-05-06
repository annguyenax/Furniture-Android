package com.example.broadcastrecieverdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * ============================================================
 * DYNAMIC BROADCAST RECEIVER (Dang ky trong code bang registerReceiver)
 * ============================================================
 *
 * Receiver nay duoc dang ky DONG trong Activity:
 *   registerReceiver(receiver, intentFilter)
 *
 * Va huy dang ky khi Activity destroy:
 *   unregisterReceiver(receiver)
 *
 * Dung de lang nghe thay doi trang thai mang (Wifi/Mobile Data)
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    // Interface callback de gui ket qua ve Activity
    public interface OnNetworkChangeListener {
        void onNetworkChanged(boolean isConnected, String networkType);
    }

    private OnNetworkChangeListener listener;

    public NetworkChangeReceiver() {
    }

    public NetworkChangeReceiver(OnNetworkChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnected());
        String networkType = "Khong co mang";

        if (isConnected) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "WiFi";
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkType = "Mobile Data";
            } else {
                networkType = "Khac";
            }
        }

        Log.d(TAG, "Mang thay doi: " + networkType + " | Connected: " + isConnected);

        // Gui ket qua ve Activity thong qua listener
        if (listener != null) {
            listener.onNetworkChanged(isConnected, networkType);
        }
    }
}
