package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminCategoryApi {

    @POST("admin/categories")
    Call<ApiResponse<Category>> createCategory(@Body CategoryRequest request);

    @PUT("admin/categories/{categoryId}")
    Call<ApiResponse<Category>> updateCategory(
            @Path("categoryId") int categoryId,
            @Body CategoryRequest request
    );

    @DELETE("admin/categories/{categoryId}")
    Call<ApiResponse<Void>> deleteCategory(@Path("categoryId") int categoryId);

    class CategoryRequest {
        private String categoryName;
        private String description;
        private String image;

        public CategoryRequest(String categoryName, String description, String image) {
            this.categoryName = categoryName;
            this.description = description;
            this.image = image;
        }

        public String getCategoryName() { return categoryName; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
    }
}
