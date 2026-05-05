package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Cart;
import com.furniture.app.data.repository.CartRepository;

public class CartViewModel extends ViewModel {

    private final CartRepository cartRepository;
    private final MutableLiveData<Cart> cart = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse<Cart>> addToCartResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public CartViewModel(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public LiveData<Cart> getCart() {
        return cart;
    }

    public LiveData<ApiResponse<Cart>> getAddToCartResult() {
        return addToCartResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadCart() {
        isLoading.setValue(true);
        MediatorLiveData<Cart> result = new MediatorLiveData<>();
        LiveData<Cart> source = cartRepository.getCart();
        result.addSource(source, cartData -> {
            isLoading.setValue(false);
            cart.setValue(cartData);
            result.removeSource(source);
        });
        result.observeForever(c -> {});
    }

    public void addToCart(Integer productId, Integer variantId, Integer quantity) {
        isLoading.setValue(true);
        MediatorLiveData<ApiResponse<Cart>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Cart>> source = cartRepository.addToCart(productId, variantId, quantity);
        result.addSource(source, response -> {
            isLoading.setValue(false);
            addToCartResult.setValue(response);
            if (response != null && response.isSuccess()) {
                cart.setValue(response.getData());
            }
            result.removeSource(source);
        });
        result.observeForever(r -> {});
    }

    public void updateCartItem(Integer itemId, Integer quantity) {
        isLoading.setValue(true);
        MediatorLiveData<ApiResponse<Cart>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Cart>> source = cartRepository.updateCartItem(itemId, quantity);
        result.addSource(source, response -> {
            isLoading.setValue(false);
            if (response != null && response.isSuccess()) {
                cart.setValue(response.getData());
            }
            result.removeSource(source);
        });
        result.observeForever(r -> {});
    }

    public void removeCartItem(Integer itemId) {
        isLoading.setValue(true);
        MediatorLiveData<ApiResponse<Void>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Void>> source = cartRepository.removeCartItem(itemId);
        result.addSource(source, response -> {
            isLoading.setValue(false);
            if (response != null && response.isSuccess()) {
                loadCart(); // Reload cart after removing item
            }
            result.removeSource(source);
        });
        result.observeForever(r -> {});
    }

    public void clearCart() {
        isLoading.setValue(true);
        MediatorLiveData<ApiResponse<Void>> result = new MediatorLiveData<>();
        LiveData<ApiResponse<Void>> source = cartRepository.clearCart();
        result.addSource(source, response -> {
            isLoading.setValue(false);
            if (response != null && response.isSuccess()) {
                cart.setValue(null);
            }
            result.removeSource(source);
        });
        result.observeForever(r -> {});
    }
}
