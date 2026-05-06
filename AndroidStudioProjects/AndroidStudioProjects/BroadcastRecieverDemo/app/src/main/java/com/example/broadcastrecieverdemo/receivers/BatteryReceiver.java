package com.example.broadcastrecieverdemo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * ============================================================
 * BATTERY BROADCAST RECEIVER (Dang ky dong trong code)
 * ============================================================
 *
 * Lang nghe cac thay doi ve pin:
 * - ACTION_BATTERY_CHANGED: Muc pin thay doi
 * - ACTION_BATTERY_LOW: Pin yeu
 * - ACTION_POWER_CONNECTED: Cam sac
 * - ACTION_POWER_DISCONNECTED: Rut sac
 */
public class BatteryReceiver extends BroadcastReceiver {

    private static final String TAG = "BatteryReceiver";

    public interface OnBatteryChangeListener {
        void onBatteryChanged(int level, boolean isCharging, String status);
    }

    private OnBatteryChangeListener listener;

    public BatteryReceiver() {
    }

    public BatteryReceiver(OnBatteryChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int batteryPct = (int) ((level / (float) scale) * 100);

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean isCharging = (plugged == BatteryManager.BATTERY_PLUGGED_AC
                    || plugged == BatteryManager.BATTERY_PLUGGED_USB
                    || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS);

            String status = isCharging ? "Dang sac" : "Khong sac";

            Log.d(TAG, "Pin: " + batteryPct + "% | " + status);

            if (listener != null) {
                listener.onBatteryChanged(batteryPct, isCharging, status);
            }
        }
    }
}
