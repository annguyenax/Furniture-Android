package com.furniture.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.furniture.app.data.repository.ProductRepository;

public class ProductViewModelFactory implements ViewModelProvider.Factory {
    private final ProductRepository productRepository;

    public ProductViewModelFactory(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(productRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
