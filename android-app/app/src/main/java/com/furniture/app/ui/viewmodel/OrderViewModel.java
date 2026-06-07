package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
    private final MutableLiveData<ApiResponse<Order>> confirmReceivedResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public OrderViewModel(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public LiveData<List<Order>> getOrders() { return orders; }
    public LiveData<Order> getOrderDetail() { return orderDetail; }
    public LiveData<ApiResponse<Order>> getCreateOrderResult() { return createOrderResult; }
    public LiveData<ApiResponse<Order>> getCancelOrderResult() { return cancelOrderResult; }
    public LiveData<ApiResponse<Order>> getConfirmReceivedResult() { return confirmReceivedResult; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadOrders(int page, int size) {
        isLoading.setValue(true);
        LiveData<List<Order>> source = orderRepository.getOrders(page, size);
        source.observeForever(new Observer<List<Order>>() {
            @Override
            public void onChanged(List<Order> orderList) {
                isLoading.setValue(false);
                orders.setValue(orderList);
                source.removeObserver(this);
            }
        });
    }

    public void loadOrderById(Integer orderId) {
        isLoading.setValue(true);
        LiveData<Order> source = orderRepository.getOrderById(orderId);
        source.observeForever(new Observer<Order>() {
            @Override
            public void onChanged(Order order) {
                isLoading.setValue(false);
                orderDetail.setValue(order);
                source.removeObserver(this);
            }
        });
    }

    public void createOrder(String recipientName, String phone, String address,
                            String paymentMethod, String note, List<CartItem> items) {
        boolean fromCart = (items == null);
        isLoading.setValue(true);
        LiveData<ApiResponse<Order>> source = orderRepository.createOrder(
                recipientName, phone, address, paymentMethod, note, fromCart, items);
        source.observeForever(new Observer<ApiResponse<Order>>() {
            @Override
            public void onChanged(ApiResponse<Order> response) {
                isLoading.setValue(false);
                createOrderResult.setValue(response);
                source.removeObserver(this);
            }
        });
    }

    public void cancelOrder(Integer orderId) {
        isLoading.setValue(true);
        LiveData<ApiResponse<Order>> source = orderRepository.cancelOrder(orderId);
        source.observeForever(new Observer<ApiResponse<Order>>() {
            @Override
            public void onChanged(ApiResponse<Order> response) {
                isLoading.setValue(false);
                cancelOrderResult.setValue(response);
                if (response != null && response.isSuccess()) {
                    loadOrders(0, 20);
                }
                source.removeObserver(this);
            }
        });
    }

    public void confirmReceived(Integer orderId) {
        isLoading.setValue(true);
        LiveData<ApiResponse<Order>> source = orderRepository.confirmReceived(orderId);
        source.observeForever(new Observer<ApiResponse<Order>>() {
            @Override
            public void onChanged(ApiResponse<Order> response) {
                isLoading.setValue(false);
                confirmReceivedResult.setValue(response);
                if (response != null && response.isSuccess()) {
                    loadOrders(0, 20);
                    if (response.getData() != null && response.getData().getOrderId() != null) {
                        orderDetail.setValue(response.getData());
                    }
                }
                source.removeObserver(this);
            }
        });
    }
}
