package com.furniture.app.data.repository;

import android.content.Context;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ProductApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final ProductApi productApi;

    public ProductRepository(Context context) {
        productApi = RetrofitClient.getInstance().create(ProductApi.class);
    }

    public void getAllProducts(int page, int size, final ProductCallback callback) {
        productApi.getAllProducts(page, size, "createdAt", "DESC")
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                         Response<ApiResponse<PageResponse<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<Product> pageResponse = response.body().getData();
                            callback.onSuccess(pageResponse.getContent());
                        } else {
                            callback.onError("Failed to load products");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getProductById(int productId, final ProductDetailCallback callback) {
        productApi.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to load product details");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void searchProducts(String keyword, int page, int size, final ProductCallback callback) {
        productApi.searchProducts(keyword, page, size)
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                         Response<ApiResponse<PageResponse<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            PageResponse<Product> pageResponse = response.body().getData();
                            callback.onSuccess(pageResponse.getContent());
                        } else {
                            callback.onError("No products found");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public void getProductsByShop(int shopId, int page, int size, final ProductCallback callback) {
        productApi.getProductsByShop(shopId, page, size)
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                           Response<ApiResponse<PageResponse<Product>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            callback.onSuccess(response.body().getData().getContent());
                        } else {
                            callback.onError("Không tải được sản phẩm shop");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
    }

    public interface ProductCallback {
        void onSuccess(List<Product> products);
        void onError(String error);
    }

    public interface ProductDetailCallback {
        void onSuccess(Product product);
        void onError(String error);
    }
}
