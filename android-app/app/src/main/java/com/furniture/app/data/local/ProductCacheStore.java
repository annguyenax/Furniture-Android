package com.furniture.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.furniture.app.data.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductCacheStore {

    private static final String PREF_NAME = "product_cache_pref";
    private static final String KEY_HOME_PRODUCTS = "home_products";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public ProductCacheStore(Context context) {
        sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveHomeProducts(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return;
        }
        sharedPreferences.edit()
                .putString(KEY_HOME_PRODUCTS, gson.toJson(products))
                .apply();
    }

    public List<Product> getHomeProducts() {
        String json = sharedPreferences.getString(KEY_HOME_PRODUCTS, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<Product>>() {}.getType();
            List<Product> products = gson.fromJson(json, type);
            return products != null ? products : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
