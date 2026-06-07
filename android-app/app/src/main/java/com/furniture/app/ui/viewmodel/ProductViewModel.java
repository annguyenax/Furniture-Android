package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.furniture.app.data.model.Product;
import com.furniture.app.data.repository.ProductRepository;

import java.util.List;

public class ProductViewModel extends ViewModel {
    private final ProductRepository productRepository;

    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    public ProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public LiveData<List<Product>> getProducts() {
        return productsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    public void loadProducts(int page, int size) {
        loadingLiveData.setValue(true);
        productRepository.getAllProducts(page, size, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productsLiveData.setValue(products);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorLiveData.setValue(error);
                loadingLiveData.setValue(false);
            }
        });
    }

    public void searchProducts(String keyword) {
        loadingLiveData.setValue(true);
        productRepository.searchProducts(keyword, 0, 50, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productsLiveData.setValue(products);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorLiveData.setValue(error);
                loadingLiveData.setValue(false);
            }
        });
    }
}
