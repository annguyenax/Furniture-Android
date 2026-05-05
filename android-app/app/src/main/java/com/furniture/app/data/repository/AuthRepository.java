package com.furniture.app.data.repository;

import android.content.Context;
import com.furniture.app.data.model.AuthResponse;
import com.furniture.app.data.model.LoginRequest;
import com.furniture.app.data.model.RegisterRequest;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AuthApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final AuthApi authApi;

    public AuthRepository(Context context) {
        authApi = RetrofitClient.getInstance().create(AuthApi.class);
    }

    private String parseErrorMessage(Response<?> response, String fallback) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                Type type = new TypeToken<ApiResponse<Void>>() {}.getType();
                ApiResponse<Void> errorResponse = new Gson().fromJson(errorJson, type);
                if (errorResponse != null) {
                    if (errorResponse.getErrors() != null && !errorResponse.getErrors().isEmpty()) {
                        return errorResponse.getErrors().values().iterator().next();
                    }
                    if (errorResponse.getMessage() != null) {
                        return errorResponse.getMessage();
                    }
                }
            }
        } catch (Exception ignored) {}
        return fallback;
    }

    public void register(String username, String email, String password, String firstName, String lastName, String phone,
                        final AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(username, email, password);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhone(phone);

        authApi.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(parseErrorMessage(response, "Đăng ký thất bại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ");
            }
        });
    }

    public void login(String email, String password, final AuthCallback callback) {
        LoginRequest request = new LoginRequest(email, password);

        authApi.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError(parseErrorMessage(response, "Email hoặc mật khẩu không đúng"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                callback.onError("Không thể kết nối đến máy chủ");
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(AuthResponse response);
        void onError(String error);
    }
}
