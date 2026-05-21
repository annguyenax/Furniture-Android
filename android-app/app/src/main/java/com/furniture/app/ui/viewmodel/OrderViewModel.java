package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.CartItem;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.repository.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final MutableLiveData<List<Order>> orders = new MutableLiveData<>();
    private final MutableLiveData<Order> orderDetail = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<Order>> createOrderResult = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<Order>> cancelOrderResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public OrderViewModel(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public LiveData<List<Order>> getOrders() {
        return orders;
    }

    public LiveData<Order> getOrderDetail() {
        return orderDetail;
    }

    public void loadOrderById(Integer orderId) {
        isLoading.setValue(true);
        MediatorLiveData<Order> result = new MediatorLiveData<>();
        LiveData<Order> source = orderRepository.getOrderById(orderId);
        result.addSource(source, order -> {
            isLoading.setValue(false);
            orderDetail.setValue(order);
            result.removeSource(source);
        });
        result.observeForever(o -> {});
    }

    public LiveData<ApiResponse<Order>> getCreateOrderResult() {
        return createOrderResult;
    }

    public LiveData<ApiResponse<Order>> getCancelOrderResult() {
        return cancelOrderResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadOrders(int page, int size) {
        isLoading.setValue(true);
        MediatorLiveData<List<Order>> result = new MediatorLiveData<>();
        LiveData<List<Order>> source = orderRepository.getOrders(page, size);
        result.addSource(source, orderList -> {
            isLoading.setValue(false);
            orders.setValue(orderList);
            result.removeSource(source);
        });
        result.observeForever(o -> {});
    }

    public void createOrder(String recipientName, String phone, String address,
                            String paymentMethod, String note, List<CartItem> items) {
        boolean fromCart = (items == null);
        isLoading.setValue(true);
        MediatorLiveData<ApiResponse<Order>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Order>> source = orderRepository.createOrder(
                recipientName, phone, address, paymentMethod, note, fromCart, items);
        result.addSource(source, response -> {
            isLoading.setValue(false);
            createOrderResult.setValue(response);
            result.removeSource(source);
        });
        result.observeForever(r -> {});
    }

    public void cancelOrder(Integer orderId) {
        isLoading.setValue(true);
        MediatorLiveData<ApiResponse<Order>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Order>> source = orderRepository.cancelOrder(orderId);
        result.addSource(source, response -> {
            isLoading.setValue(false);
            cancelOrderResult.setValue(response);
            if (response != null && response.isSuccess()) {
                loadOrders(0, 20);
            }
            result.removeSource(source);
        });
        result.observeForever(r -> {});
    }
}
