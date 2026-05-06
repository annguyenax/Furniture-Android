package com.example.broadcastrecieverdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomReceiver extends BroadcastReceiver {
    public static final String CUSTOM_ACTION = "com.example.broadcastrecieverdemo.CUSTOM_ACTION";
    public static final String EXTRA_MESSAGE = "extra_message";

    public interface OnCustomBroadcastListener {
        void onCustomBroadcastReceived(String message);
    }

    private OnCustomBroadcastListener listener;

    public CustomReceiver() {}

    public CustomReceiver(OnCustomBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CUSTOM_ACTION.equals(intent.getAction())) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (listener != null) {
                listener.onCustomBroadcastReceived(message);
            }
        }
    }
}
