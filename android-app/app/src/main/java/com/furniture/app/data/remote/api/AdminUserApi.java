package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.AdminUser;
import com.furniture.app.data.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminUserApi {

    @GET("admin/users")
    Call<ApiResponse<PagedUsers>> getUsers(
            @Query("page") int page,
            @Query("size") int size,
            @Query("search") String search);

    @PUT("admin/users/{userId}/status")
    Call<ApiResponse<AdminUser>> updateStatus(
            @Path("userId") int userId,
            @Body UpdateStatusRequest request);

    class PagedUsers {
        private java.util.List<AdminUser> content;
        private int totalElements;
        private int totalPages;
        private int number;

        public java.util.List<AdminUser> getContent() { return content; }
        public int getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getNumber() { return number; }
    }

    class UpdateStatusRequest {
        private String status;
        public UpdateStatusRequest(String status) { this.status = status; }
        public String getStatus() { return status; }
    }
}
