package com.furniture.app.util;

import android.os.Handler;
import android.os.Looper;

import com.furniture.app.data.model.Province;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * esgoo.net/api-tinhthanh — API phổ biến, dữ liệu cập nhật sau sáp nhập 1/7/2025.
 * Province / District / Ward đều có đủ.
 */
public class ProvinceService {

    private static final String BASE = "https://esgoo.net/api-tinhthanh/";
    private static ProvinceService instance;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();
    private final Gson gson = new Gson();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static class EsgooResponse<T> {
        @SerializedName("error")   int error;
        @SerializedName("data")    List<T> data;
    }

    public static ProvinceService getInstance() {
        if (instance == null) instance = new ProvinceService();
        return instance;
    }

    public interface ResultCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    /** Tất cả tỉnh / thành phố. */
    public void getProvinces(ResultCallback<List<Province>> callback) {
        fetch(BASE + "1/0.htm", body -> {
            Type type = new TypeToken<EsgooResponse<Province>>() {}.getType();
            EsgooResponse<Province> resp = gson.fromJson(body, type);
            if (resp == null || resp.error != 0 || resp.data == null) {
                mainHandler.post(() -> callback.onError("Lỗi tải danh sách tỉnh thành"));
            } else {
                mainHandler.post(() -> callback.onSuccess(resp.data));
            }
        }, msg -> mainHandler.post(() -> callback.onError(msg)));
    }

    /** Quận / huyện theo tỉnh. */
    public void getDistricts(String provinceId, ResultCallback<List<Province.District>> callback) {
        fetch(BASE + "2/" + provinceId + ".htm", body -> {
            Type type = new TypeToken<EsgooResponse<Province.District>>() {}.getType();
            EsgooResponse<Province.District> resp = gson.fromJson(body, type);
            if (resp == null || resp.error != 0 || resp.data == null) {
                mainHandler.post(() -> callback.onError("Lỗi tải quận/huyện"));
            } else {
                mainHandler.post(() -> callback.onSuccess(resp.data));
            }
        }, msg -> mainHandler.post(() -> callback.onError(msg)));
    }

    /** Phường / xã theo quận. */
    public void getWards(String districtId, ResultCallback<List<Province.Ward>> callback) {
        fetch(BASE + "3/" + districtId + ".htm", body -> {
            Type type = new TypeToken<EsgooResponse<Province.Ward>>() {}.getType();
            EsgooResponse<Province.Ward> resp = gson.fromJson(body, type);
            if (resp == null || resp.error != 0 || resp.data == null) {
                mainHandler.post(() -> callback.onError("Lỗi tải phường/xã"));
            } else {
                mainHandler.post(() -> callback.onSuccess(resp.data));
            }
        }, msg -> mainHandler.post(() -> callback.onError(msg)));
    }

    private void fetch(String url, ParseCallback onBody, ErrorCallback onError) {
        Request req = new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                onError.onError("Không tải được dữ liệu địa chỉ");
            }
            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    onError.onError("Lỗi tải dữ liệu (HTTP " + response.code() + ")");
                    return;
                }
                String body = response.body().string();
                try { onBody.parse(body); }
                catch (Exception e) { onError.onError("Lỗi phân tích dữ liệu"); }
            }
        });
    }

    private interface ParseCallback { void parse(String body) throws Exception; }
    private interface ErrorCallback  { void onError(String message); }
}
