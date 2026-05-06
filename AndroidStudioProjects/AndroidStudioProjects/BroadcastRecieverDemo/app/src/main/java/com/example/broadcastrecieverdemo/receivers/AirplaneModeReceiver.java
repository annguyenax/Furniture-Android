package com.example.broadcastrecieverdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ============================================================
 * AIRPLANE MODE RECEIVER (Dang ky dong trong code)
 * ============================================================
 *
 * Lang nghe khi nguoi dung bat/tat che do may bay.
 * Action: Intent.ACTION_AIRPLANE_MODE_CHANGED
 */
public class AirplaneModeReceiver extends BroadcastReceiver {

    private static final String TAG = "AirplaneModeReceiver";
    public interface OnAirplaneModeListener {
        void onAirplaneModeChanged(boolean isAirplaneModeOn);
    }
    private OnAirplaneModeListener listener;
    public AirplaneModeReceiver() {
    }
    public AirplaneModeReceiver(OnAirplaneModeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
            boolean isOn = intent.getBooleanExtra("state", false);
            Log.d(TAG, "Che do may bay: " + (isOn ? "BAT" : "TAT"));
            if (listener != null) {
                listener.onAirplaneModeChanged(isOn);
            }
        }
    }
}
