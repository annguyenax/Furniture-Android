package com.furniture.app.data.remote.interceptor;

import androidx.annotation.NonNull;

import com.furniture.app.BuildConfig;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.AuthResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Adds the Authorization header and refreshes an expired access token once.
 */
public class AuthInterceptor implements Interceptor {

    private static final MediaType TEXT_PLAIN = MediaType.get("text/plain; charset=utf-8");
    private static final Object refreshLock = new Object();

    private volatile String authToken;
    private static UnauthorizedHandler unauthorizedHandler;
    private static TokenStore tokenStore;

    private final OkHttpClient refreshClient = new OkHttpClient();
    private final Gson gson = new Gson();

    public interface UnauthorizedHandler {
        void onUnauthorized();
    }

    public interface TokenStore {
        String getRefreshToken();
        void saveTokens(String accessToken, String refreshToken);
    }

    public static void setUnauthorizedHandler(UnauthorizedHandler handler) {
        unauthorizedHandler = handler;
    }

    public static void setTokenStore(TokenStore store) {
        tokenStore = store;
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

        String tokenForRequest = authToken;
        Request authorizedRequest = withBearerToken(originalRequest, tokenForRequest);
        Response response = chain.proceed(authorizedRequest);

        if (response.code() != 401 && response.code() != 403) {
            return response;
        }

        response.close();

        String refreshedToken = refreshAccessToken(tokenForRequest);
        if (refreshedToken != null && !refreshedToken.isEmpty()) {
            return chain.proceed(withBearerToken(originalRequest, refreshedToken));
        }

        authToken = null;
        notifyUnauthorized();
        return chain.proceed(originalRequest);
    }

    private Request withBearerToken(Request request, String token) {
        return request.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .build();
    }

    private String refreshAccessToken(String expiredToken) {
        if (tokenStore == null) {
            return null;
        }

        synchronized (refreshLock) {
            if (authToken != null && !authToken.equals(expiredToken)) {
                return authToken;
            }

            String refreshToken = tokenStore.getRefreshToken();
            if (refreshToken == null || refreshToken.isEmpty()) {
                return null;
            }

            Request refreshRequest = new Request.Builder()
                    .url(BuildConfig.BASE_URL + "auth/refresh-token")
                    .post(RequestBody.create(TEXT_PLAIN, refreshToken))
                    .header("Accept", "application/json")
                    .build();

            try (Response refreshResponse = refreshClient.newCall(refreshRequest).execute()) {
                if (!refreshResponse.isSuccessful() || refreshResponse.body() == null) {
                    return null;
                }

                Type responseType = new TypeToken<ApiResponse<AuthResponse>>() {}.getType();
                ApiResponse<AuthResponse> apiResponse = gson.fromJson(
                        refreshResponse.body().charStream(),
                        responseType
                );
                if (apiResponse == null || !apiResponse.isSuccess() || apiResponse.getData() == null) {
                    return null;
                }

                AuthResponse authResponse = apiResponse.getData();
                String newAccessToken = authResponse.getAccessToken();
                if (newAccessToken == null || newAccessToken.isEmpty()) {
                    return null;
                }

                authToken = newAccessToken;
                tokenStore.saveTokens(newAccessToken, authResponse.getRefreshToken());
                return newAccessToken;
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private void notifyUnauthorized() {
        if (unauthorizedHandler != null) {
            unauthorizedHandler.onUnauthorized();
        }
    }
}
