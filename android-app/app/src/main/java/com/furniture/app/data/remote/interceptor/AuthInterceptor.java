package com.furniture.app.data.remote.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor to add Authorization header to requests
 */
public class AuthInterceptor implements Interceptor {

    private String authToken;
    private static UnauthorizedHandler unauthorizedHandler;

    public interface UnauthorizedHandler {
        void onUnauthorized();
    }

    public static void setUnauthorizedHandler(UnauthorizedHandler handler) {
        unauthorizedHandler = handler;
    }

    public AuthInterceptor(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (authToken == null || authToken.isEmpty()) {
            return chain.proceed(originalRequest);
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + authToken)
                .header("Accept", "application/json")
                .build();

        Response response = chain.proceed(newRequest);

        // Token hết hạn → tự động đăng xuất
        if (response.code() == 401 && unauthorizedHandler != null) {
            unauthorizedHandler.onUnauthorized();
        }

        return response;
    }
}
