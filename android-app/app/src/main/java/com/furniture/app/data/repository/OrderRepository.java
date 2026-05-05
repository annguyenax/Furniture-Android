package com.furniture.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.CartItem;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.request.CreateOrderRequest;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.OrderApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final OrderApi orderApi;

    public OrderRepository(String token) {
        orderApi = RetrofitClient.getInstance(token).create(OrderApi.class);
    }

    public LiveData<List<Order>> getOrders(int page, int size) {
        MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();

        orderApi.getOrders(page, size).enqueue(new Callback<ApiResponse<PageResponse<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Order>>> call,
                                   Response<ApiResponse<PageResponse<Order>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ordersLiveData.setValue(response.body().getData().getContent());
                } else {
                    ordersLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Order>>> call, Throwable t) {
                ordersLiveData.setValue(null);
            }
        });

        return ordersLiveData;
    }

    public LiveData<Order> getOrderById(Integer orderId) {
        MutableLiveData<Order> orderLiveData = new MutableLiveData<>();

        orderApi.getOrderById(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    orderLiveData.setValue(response.body().getData());
                } else {
                    orderLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                orderLiveData.setValue(null);
            }
        });

        return orderLiveData;
    }

    public LiveData<ApiResponse<Order>> createOrder(String recipientName, String phone,
                                                     String address, String paymentMethod,
                                                     String note, boolean fromCart,
                                                     List<CartItem> items) {
        MutableLiveData<ApiResponse<Order>> resultLiveData = new MutableLiveData<>();

        List<CreateOrderRequest.OrderItemRequest> orderItemRequests = null;
        if (!fromCart && items != null && !items.isEmpty()) {
            orderItemRequests = new ArrayList<>();
            for (CartItem item : items) {
                orderItemRequests.add(new CreateOrderRequest.OrderItemRequest(
                        item.getProductId(),
                        item.getVariantId(),
                        item.getQuantity()
                ));
            }
        }

        CreateOrderRequest request = new CreateOrderRequest(
                recipientName, phone, address, paymentMethod, note, fromCart, orderItemRequests
        );

        orderApi.createOrder(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.setValue(response.body());
                } else {
                    ApiResponse<Order> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Đặt hàng thất bại");
                    resultLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                ApiResponse<Order> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                resultLiveData.setValue(errorResponse);
            }
        });

        return resultLiveData;
    }

    public LiveData<ApiResponse<Order>> cancelOrder(Integer orderId) {
        MutableLiveData<ApiResponse<Order>> resultLiveData = new MutableLiveData<>();

        orderApi.cancelOrder(orderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultLiveData.setValue(response.body());
                } else {
                    ApiResponse<Order> errorResponse = new ApiResponse<>();
                    errorResponse.setSuccess(false);
                    errorResponse.setMessage("Hủy đơn hàng thất bại");
                    resultLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                ApiResponse<Order> errorResponse = new ApiResponse<>();
                errorResponse.setSuccess(false);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                resultLiveData.setValue(errorResponse);
            }
        });

        return resultLiveData;
    }
}
