package com.furniture.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Cart;
import com.furniture.app.data.model.request.AddToCartRequest;
import com.furniture.app.data.model.request.UpdateCartItemRequest;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.CartApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private final CartApi cartApi;

    public CartRepository(String token) {
        cartApi = RetrofitClient.getInstance(token).create(CartApi.class);
    }

    public LiveData<Cart> getCart() {
        MutableLiveData<Cart> cartLiveData = new MutableLiveData<>();

        cartApi.getCart().enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    cartLiveData.setValue(response.body().getData());
                } else {
                    cartLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                cartLiveData.setValue(null);
            }
        });

        return cartLiveData;
    }

    public LiveData<ApiResponse<Cart>> addToCart(Integer productId, Integer variantId, Integer quantity) {
        MutableLiveData<ApiResponse<Cart>> resultLiveData = new MutableLiveData<>();

        AddToCartRequest request = new AddToCartRequest(productId, variantId, quantity);
        cartApi.addToCart(request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.setValue(response.body());
                } else {
                    ApiResponse<Cart> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không thể thêm vào giỏ hàng");
                    resultLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                ApiResponse<Cart> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                resultLiveData.setValue(errorResponse);
            }
        });

        return resultLiveData;
    }

    public LiveData<ApiResponse<Cart>> updateCartItem(Integer itemId, Integer quantity) {
        MutableLiveData<ApiResponse<Cart>> resultLiveData = new MutableLiveData<>();

        UpdateCartItemRequest request = new UpdateCartItemRequest(quantity);
        cartApi.updateCartItem(itemId, request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.setValue(response.body());
                } else {
                    ApiResponse<Cart> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không thể cập nhật giỏ hàng");
                    resultLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                ApiResponse<Cart> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                resultLiveData.setValue(errorResponse);
            }
        });

        return resultLiveData;
    }

    public LiveData<ApiResponse<Void>> removeCartItem(Integer itemId) {
        MutableLiveData<ApiResponse<Void>> resultLiveData = new MutableLiveData<>();

        cartApi.removeCartItem(itemId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.setValue(response.body());
                } else {
                    ApiResponse<Void> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không thể xóa sản phẩm");
                    resultLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                ApiResponse<Void> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                resultLiveData.setValue(errorResponse);
            }
        });

        return resultLiveData;
    }

    public LiveData<ApiResponse<Void>> clearCart() {
        MutableLiveData<ApiResponse<Void>> resultLiveData = new MutableLiveData<>();

        cartApi.clearCart().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.setValue(response.body());
                } else {
                    ApiResponse<Void> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Không thể xóa giỏ hàng");
                    resultLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                ApiResponse<Void> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                resultLiveData.setValue(errorResponse);
            }
        });

        return resultLiveData;
    }
}
