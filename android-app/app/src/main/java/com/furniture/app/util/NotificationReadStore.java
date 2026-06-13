package com.furniture.app.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.furniture.app.data.model.Order;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NotificationReadStore {

    private static final String PREF_NAME = "notification_read_store";
    private static final String KEY_READ_ORDERS = "read_orders";

    private final SharedPreferences preferences;

    public NotificationReadStore(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean hasUnreadOrderNotifications(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return false;
        Set<String> readKeys = preferences.getStringSet(KEY_READ_ORDERS, new HashSet<>());
        for (Order order : orders) {
            String key = buildOrderKey(order);
            if (key != null && !readKeys.contains(key)) {
                return true;
            }
        }
        return false;
    }

    public void markOrderNotificationsRead(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return;
        Set<String> readKeys = new HashSet<>(preferences.getStringSet(KEY_READ_ORDERS, new HashSet<>()));
        for (Order order : orders) {
            String key = buildOrderKey(order);
            if (key != null) readKeys.add(key);
        }
        preferences.edit().putStringSet(KEY_READ_ORDERS, readKeys).apply();
    }

    private String buildOrderKey(Order order) {
        if (order == null || order.getOrderId() == null) return null;
        return order.getOrderId()
                + "|" + safe(order.getStatus())
                + "|" + safe(order.getReturnStatus())
                + "|" + safe(order.getCreatedAt());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
