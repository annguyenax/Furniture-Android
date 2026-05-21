package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface MediaApi {

    @Multipart
    @POST("media/upload-image")
    Call<ApiResponse<String>> uploadImage(
            @Part MultipartBody.Part file,
            @Query("folder") String folder);
}
