package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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

    public LiveData<Cart> getCart() { return cart; }
    public LiveData<ApiResponse<Cart>> getAddToCartResult() { return addToCartResult; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadCart() {
        isLoading.setValue(true);
        LiveData<Cart> source = cartRepository.getCart();
        source.observeForever(new Observer<Cart>() {
            @Override
            public void onChanged(Cart cartData) {
                isLoading.setValue(false);
                cart.setValue(cartData);
                source.removeObserver(this);
            }
        });
    }

    public void addToCart(Integer productId, Integer variantId, Integer quantity) {
        isLoading.setValue(true);
        LiveData<ApiResponse<Cart>> source = cartRepository.addToCart(productId, variantId, quantity);
        source.observeForever(new Observer<ApiResponse<Cart>>() {
            @Override
            public void onChanged(ApiResponse<Cart> response) {
                isLoading.setValue(false);
                addToCartResult.setValue(response);
                if (response != null && response.isSuccess()) {
                    cart.setValue(response.getData());
                }
                source.removeObserver(this);
            }
        });
    }

    public void updateCartItem(Integer itemId, Integer quantity) {
        isLoading.setValue(true);
        LiveData<ApiResponse<Cart>> source = cartRepository.updateCartItem(itemId, quantity);
        source.observeForever(new Observer<ApiResponse<Cart>>() {
            @Override
            public void onChanged(ApiResponse<Cart> response) {
                isLoading.setValue(false);
                if (response != null && response.isSuccess()) {
                    cart.setValue(response.getData());
                }
                source.removeObserver(this);
            }
        });
    }

    public void removeCartItem(Integer itemId) {
        isLoading.setValue(true);
        LiveData<ApiResponse<Void>> source = cartRepository.removeCartItem(itemId);
        source.observeForever(new Observer<ApiResponse<Void>>() {
            @Override
            public void onChanged(ApiResponse<Void> response) {
                isLoading.setValue(false);
                if (response != null && response.isSuccess()) {
                    loadCart();
                }
                source.removeObserver(this);
            }
        });
    }

    public void clearCart() {
        isLoading.setValue(true);
        LiveData<ApiResponse<Void>> source = cartRepository.clearCart();
        source.observeForever(new Observer<ApiResponse<Void>>() {
            @Override
            public void onChanged(ApiResponse<Void> response) {
                isLoading.setValue(false);
                if (response != null && response.isSuccess()) {
                    cart.setValue(null);
                }
                source.removeObserver(this);
            }
        });
    }
}
