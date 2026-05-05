package com.furniture.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.furniture.app.data.repository.CartRepository;

public class CartViewModelFactory implements ViewModelProvider.Factory {

    private final CartRepository cartRepository;

    public CartViewModelFactory(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(cartRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
