package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.model.request.UpdateProfileRequest;

import retrofit2.Call;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApi {

    @GET("users/me")
    Call<ApiResponse<User>> getMe();

    @PUT("users/me")
    Call<ApiResponse<User>> updateProfile(@Body UpdateProfileRequest request);

    @FormUrlEncoded
    @POST("users/me/change-password")
    Call<ApiResponse<Void>> changePassword(
            @Field("oldPassword") String oldPassword,
            @Field("newPassword") String newPassword);

    @Multipart
    @POST("users/me/avatar")
    Call<ApiResponse<String>> uploadAvatar(@Part MultipartBody.Part file);
}
