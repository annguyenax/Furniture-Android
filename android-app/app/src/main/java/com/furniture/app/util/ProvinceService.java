package com.furniture.app.util;

import android.os.Handler;
import android.os.Looper;

import com.furniture.app.data.model.Province;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Calls provinces.open-api.vn — free, no API key required.
 * Provides full list of Vietnamese provinces, districts, and wards.
 */
public class ProvinceService {

    private static final String BASE = "https://provinces.open-api.vn/api/";
    private static ProvinceService instance;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static ProvinceService getInstance() {
        if (instance == null) instance = new ProvinceService();
        return instance;
    }

    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    /** Fetch all provinces (no districts/wards). */
    public void getProvinces(ResultCallback<List<Province>> callback) {
        Request request = new Request.Builder().url(BASE + "p/").build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Lỗi kết nối"));
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    mainHandler.post(() -> callback.onError("Lỗi tải dữ liệu"));
                    return;
                }
                String body = response.body().string();
                Type type = new TypeToken<List<Province>>() {}.getType();
                List<Province> provinces = gson.fromJson(body, type);
                mainHandler.post(() -> callback.onSuccess(provinces));
            }
        });
    }

    /** Fetch a province with its districts (depth=2). */
    public void getDistricts(int provinceCode, ResultCallback<Province> callback) {
        Request request = new Request.Builder().url(BASE + "p/" + provinceCode + "?depth=2").build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Lỗi kết nối"));
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    mainHandler.post(() -> callback.onError("Lỗi tải quận/huyện"));
                    return;
                }
                String body = response.body().string();
                Province province = gson.fromJson(body, Province.class);
                mainHandler.post(() -> callback.onSuccess(province));
            }
        });
    }

    /** Fetch a district with its wards (depth=2). */
    public void getWards(int districtCode, ResultCallback<Province.District> callback) {
        Request request = new Request.Builder().url(BASE + "d/" + districtCode + "?depth=2").build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Lỗi kết nối"));
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    mainHandler.post(() -> callback.onError("Lỗi tải phường/xã"));
                    return;
                }
                String body = response.body().string();
                Province.District district = gson.fromJson(body, Province.District.class);
                mainHandler.post(() -> callback.onSuccess(district));
            }
        });
    }
}
