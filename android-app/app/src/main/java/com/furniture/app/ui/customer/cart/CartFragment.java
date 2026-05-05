package com.furniture.app.ui.customer.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.Cart;
import com.furniture.app.data.model.CartItem;
import com.furniture.app.data.repository.CartRepository;
import com.furniture.app.ui.adapter.CartItemAdapter;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.customer.order.CheckoutActivity;
import com.furniture.app.ui.viewmodel.CartViewModel;
import com.furniture.app.ui.viewmodel.CartViewModelFactory;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartItemAdapter.OnCartItemListener {

    private CartViewModel cartViewModel;
    private SessionManager sessionManager;
    private NumberFormat currencyFormat;

    private RecyclerView cartRecyclerView;
    private ProgressBar progressBar;
    private View emptyCartState;
    private MaterialCardView checkoutCard;
    private TextView totalText;
    private MaterialButton btnCheckout;
    private MaterialButton btnStartShopping;

    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        initViews(view);
        setupViewModel();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    private void initViews(View view) {
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyCartState = view.findViewById(R.id.empty_cart_state);
        checkoutCard = view.findViewById(R.id.checkout_card);
        totalText = view.findViewById(R.id.total_text);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnStartShopping = view.findViewById(R.id.btn_start_shopping);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartItemAdapter = new CartItemAdapter(cartItems, this, currencyFormat);
        cartRecyclerView.setAdapter(cartItemAdapter);
    }

    private void setupViewModel() {
        String token = sessionManager.getToken();
        CartRepository cartRepository = new CartRepository(token);
        cartViewModel = new ViewModelProvider(this,
                new CartViewModelFactory(cartRepository)).get(CartViewModel.class);

        cartViewModel.getCart().observe(getViewLifecycleOwner(), this::updateCartUI);
        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        btnStartShopping.setOnClickListener(v -> {
            if (getActivity() instanceof CustomerMainActivity) {
                ((CustomerMainActivity) getActivity()).navigateToTab(0);
            }
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                Intent intent = new Intent(requireContext(), CheckoutActivity.class);
                intent.putExtra(CheckoutActivity.EXTRA_FROM_CART, true);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Giỏ hàng của bạn đang trống", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCart() {
        cartViewModel.loadCart();
    }

    private void updateCartUI(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            showEmptyCart();
        } else {
            cartItems.clear();
            cartItems.addAll(cart.getItems());
            cartItemAdapter.notifyDataSetChanged();
            showCartItems();
            updateTotals(cart);
        }
    }

    private void showEmptyCart() {
        emptyCartState.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);
        checkoutCard.setVisibility(View.GONE);
    }

    private void showCartItems() {
        emptyCartState.setVisibility(View.GONE);
        cartRecyclerView.setVisibility(View.VISIBLE);
        checkoutCard.setVisibility(View.VISIBLE);
    }

    private void updateTotals(Cart cart) {
        BigDecimal total = cart.getTotalAmount() != null ? cart.getTotalAmount() : BigDecimal.ZERO;
        totalText.setText(String.format("₫%s", currencyFormat.format(total)));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity > 0) {
            cartViewModel.updateCartItem(item.getCartItemId(), newQuantity);
        }
    }

    @Override
    public void onRemoveItem(CartItem item) {
        cartViewModel.removeCartItem(item.getCartItemId());
    }
}
