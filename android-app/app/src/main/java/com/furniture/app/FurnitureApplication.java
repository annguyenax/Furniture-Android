package com.furniture.app;

import android.app.Application;

/**
 * Application class for Furniture App
 * Initialize global components here
 */
public class FurnitureApplication extends Application {

    private static FurnitureApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static FurnitureApplication getInstance() {
        return instance;
    }
}
