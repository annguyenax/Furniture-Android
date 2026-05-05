package com.furniture.app.data.remote;

import com.furniture.app.BuildConfig;
import com.furniture.app.data.remote.interceptor.AuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit client singleton for API calls
 */
public class RetrofitClient {

    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static final int TIMEOUT = 30; // seconds

    private static volatile RetrofitClient instance;
    private final Retrofit retrofit;
    private final AuthInterceptor authInterceptor;

    private RetrofitClient(String authToken) {
        // Logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Auth interceptor
        authInterceptor = new AuthInterceptor(authToken);

        // OkHttp client
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .build();

        // Gson configuration
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient()
                .create();

        // Retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance(String authToken) {
        if (instance == null) {
            instance = new RetrofitClient(authToken);
        } else if (authToken != null && !authToken.isEmpty()) {
            // Cập nhật token mới (ví dụ sau khi login)
            instance.authInterceptor.setAuthToken(authToken);
        }
        return instance;
    }

    public static synchronized RetrofitClient getInstance() {
        return getInstance(null);
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
