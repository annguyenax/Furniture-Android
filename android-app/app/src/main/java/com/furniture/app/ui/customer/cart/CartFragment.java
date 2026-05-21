package com.furniture.app.ui.customer.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
    private View layoutGuest;
    private LinearLayout layoutSelectAll;
    private CheckBox cbSelectAll;
    private TextView tvSelectedCount;
    private MaterialCardView checkoutCard;
    private TextView totalText;
    private MaterialButton btnCheckout;
    private MaterialButton btnStartShopping;
    private MaterialButton btnLoginCart;

    private CartItemAdapter cartItemAdapter;
    private final List<CartItem> cartItems = new ArrayList<>();
    private boolean isUpdatingSelectAll = false;

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
        layoutGuest = view.findViewById(R.id.layout_guest);
        layoutSelectAll = view.findViewById(R.id.layout_select_all);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        tvSelectedCount = view.findViewById(R.id.tv_selected_count);
        checkoutCard = view.findViewById(R.id.checkout_card);
        totalText = view.findViewById(R.id.total_text);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnStartShopping = view.findViewById(R.id.btn_start_shopping);
        btnLoginCart = view.findViewById(R.id.btn_login_cart);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartItemAdapter = new CartItemAdapter(cartItems, this, currencyFormat);
        cartItemAdapter.setOnSelectionChangedListener(this::onSelectionChanged);
        cartRecyclerView.setAdapter(cartItemAdapter);
    }

    private void setupViewModel() {
        String token = sessionManager.getToken();
        CartRepository cartRepository = new CartRepository(token);
        cartViewModel = new ViewModelProvider(this,
                new CartViewModelFactory(cartRepository)).get(CartViewModel.class);

        cartViewModel.getCart().observe(getViewLifecycleOwner(), this::updateCartUI);
        cartViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));
    }

    private void setupListeners() {
        btnLoginCart.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), com.furniture.app.ui.auth.LoginActivity.class)));

        btnStartShopping.setOnClickListener(v -> {
            if (getActivity() instanceof CustomerMainActivity) {
                ((CustomerMainActivity) getActivity()).navigateToTab(0);
            }
        });

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingSelectAll) return;
            cartItemAdapter.selectAll(isChecked);
        });

        btnCheckout.setOnClickListener(v -> {
            List<CartItem> selected = cartItemAdapter.getSelectedItems();
            if (selected.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
            intent.putExtra(CheckoutActivity.EXTRA_CART_ITEMS, new ArrayList<>(selected));
            startActivity(intent);
        });
    }

    private void loadCart() {
        if (!sessionManager.isLoggedIn()) {
            showGuestState();
            return;
        }
        cartViewModel.loadCart();
    }

    private void showGuestState() {
        layoutGuest.setVisibility(View.VISIBLE);
        emptyCartState.setVisibility(View.GONE);
        cartRecyclerView.setVisibility(View.GONE);
        layoutSelectAll.setVisibility(View.GONE);
        checkoutCard.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void updateCartUI(Cart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            showEmptyCart();
        } else {
            cartItems.clear();
            List<CartItem> incoming = cart.getItems();
            // Preserve selection state across reloads; default to selected=true for new items
            for (CartItem item : incoming) {
                item.setSelected(true);
            }
            cartItems.addAll(incoming);
            cartItemAdapter.notifyDataSetChanged();
            showCartItems();
            onSelectionChanged();
        }
    }

    private void showEmptyCart() {
        layoutGuest.setVisibility(View.GONE);
        emptyCartState.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);
        layoutSelectAll.setVisibility(View.GONE);
        checkoutCard.setVisibility(View.GONE);
    }

    private void showCartItems() {
        layoutGuest.setVisibility(View.GONE);
        emptyCartState.setVisibility(View.GONE);
        cartRecyclerView.setVisibility(View.VISIBLE);
        layoutSelectAll.setVisibility(View.VISIBLE);
        checkoutCard.setVisibility(View.VISIBLE);
    }

    private void onSelectionChanged() {
        List<CartItem> selected = cartItemAdapter.getSelectedItems();
        int selectedCount = selected.size();
        int total = cartItems.size();

        // Update count label
        tvSelectedCount.setText(String.format("Đã chọn %d/%d", selectedCount, total));

        // Sync select-all checkbox without triggering listener
        isUpdatingSelectAll = true;
        cbSelectAll.setChecked(selectedCount == total && total > 0);
        isUpdatingSelectAll = false;

        // Recalculate total from selected items only
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : selected) {
            if (item.getPrice() != null) {
                sum = sum.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        totalText.setText(String.format("₫%s", currencyFormat.format(sum)));

        btnCheckout.setEnabled(selectedCount > 0);
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
