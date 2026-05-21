package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.AuthResponse;
import com.furniture.app.data.model.LoginRequest;
import com.furniture.app.data.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Auth API endpoints
 */
public interface AuthApi {

    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("auth/google")
    Call<ApiResponse<AuthResponse>> googleLogin(@Body String idToken);

    @POST("auth/refresh-token")
    Call<ApiResponse<AuthResponse>> refreshToken(@Body String refreshToken);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();
}
