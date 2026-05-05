package com.furniture.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.furniture.app.data.repository.OrderRepository;

public class OrderViewModelFactory implements ViewModelProvider.Factory {

    private final OrderRepository orderRepository;

    public OrderViewModelFactory(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            return (T) new OrderViewModel(orderRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
