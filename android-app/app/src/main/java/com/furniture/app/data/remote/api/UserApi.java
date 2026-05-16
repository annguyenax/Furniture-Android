package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.model.request.UpdateProfileRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserApi {

    @GET("users/me")
    Call<ApiResponse<User>> getMe();

    @PUT("users/me")
    Call<ApiResponse<User>> updateProfile(@Body UpdateProfileRequest request);
}
