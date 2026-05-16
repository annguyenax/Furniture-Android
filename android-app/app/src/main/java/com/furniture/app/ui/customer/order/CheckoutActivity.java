package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.Address;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Cart;
import com.furniture.app.data.model.CartItem;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.model.ProductVariant;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AddressApi;
import com.furniture.app.data.repository.CartRepository;
import com.furniture.app.data.repository.OrderRepository;
import com.furniture.app.ui.adapter.CheckoutItemAdapter;
import com.furniture.app.ui.customer.profile.AddAddressActivity;
import com.furniture.app.ui.viewmodel.CartViewModel;
import com.furniture.app.ui.viewmodel.CartViewModelFactory;
import com.furniture.app.ui.viewmodel.OrderViewModel;
import com.furniture.app.ui.viewmodel.OrderViewModelFactory;
import com.furniture.app.util.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_FROM_CART = "from_cart";
    public static final String EXTRA_PRODUCT = "product";
    public static final String EXTRA_VARIANT = "variant";
    public static final String EXTRA_QUANTITY = "quantity";

    private static final int REQUEST_ADD_ADDRESS = 1002;

    private SessionManager sessionManager;
    private NumberFormat currencyFormat;
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;
    private AddressApi addressApi;

    // Views
    private TextView tvRecipientName, tvPhone, tvAddress, tvNoAddress;
    private RecyclerView rvOrderItems;
    private RadioGroup rgPaymentMethod;
    private TextInputEditText etNote;
    private TextView tvSubtotal, tvShipping, tvTotal, tvBottomTotal;
    private MaterialButton btnPlaceOrder;
    private ProgressBar progressBar;

    private List<CartItem> orderItems = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private boolean isFromCart = false;

    private List<Address> savedAddresses = new ArrayList<>();
    private Address selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sessionManager = new SessionManager(this);
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        addressApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AddressApi.class);

        initViews();
        setupToolbar();
        setupViewModels();
        loadOrderData();
        loadSavedAddresses();
        setupListeners();
    }

    private void initViews() {
        tvRecipientName = findViewById(R.id.tv_recipient_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvNoAddress = findViewById(R.id.tv_no_address);
        rvOrderItems = findViewById(R.id.rv_order_items);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        etNote = findViewById(R.id.et_note);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTotal = findViewById(R.id.tv_total);
        tvBottomTotal = findViewById(R.id.tv_bottom_total);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        progressBar = findViewById(R.id.progress_bar);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        showNoAddressState();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Thanh toán");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModels() {
        String token = sessionManager.getToken();

        CartRepository cartRepository = new CartRepository(token);
        cartViewModel = new ViewModelProvider(this,
                new CartViewModelFactory(cartRepository)).get(CartViewModel.class);

        OrderRepository orderRepository = new OrderRepository(token);
        orderViewModel = new ViewModelProvider(this,
                new OrderViewModelFactory(orderRepository)).get(OrderViewModel.class);

        cartViewModel.getCart().observe(this, this::populateFromCart);

        orderViewModel.getCreateOrderResult().observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            btnPlaceOrder.setEnabled(true);

            if (result != null && result.isSuccess()) {
                showOrderSuccessDialog(result.getData());
            } else {
                String message = result != null ? result.getMessage() : "Đặt hàng thất bại";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        orderViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnPlaceOrder.setEnabled(!isLoading);
        });
    }

    private void loadOrderData() {
        isFromCart = getIntent().getBooleanExtra(EXTRA_FROM_CART, false);

        if (isFromCart) {
            cartViewModel.loadCart();
        } else {
            Product product = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
            ProductVariant variant = (ProductVariant) getIntent().getSerializableExtra(EXTRA_VARIANT);
            int quantity = getIntent().getIntExtra(EXTRA_QUANTITY, 1);

            if (product != null) {
                CartItem item = new CartItem();
                item.setProductId(product.getProductId());
                item.setProductName(product.getProductName());
                item.setProductImage(product.getFirstImageUrl());
                item.setQuantity(quantity);

                if (variant != null) {
                    item.setVariantId(variant.getVariantId());
                    item.setVariantName(variant.getColor());
                    item.setPrice(variant.getPrice());
                } else {
                    item.setPrice(product.getLowestPrice());
                }

                orderItems.add(item);
                updateItemsUI();
            }
        }
    }

    private void loadSavedAddresses() {
        addressApi.getAddresses().enqueue(new Callback<ApiResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Address>>> call,
                                   Response<ApiResponse<List<Address>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    savedAddresses = response.body().getData();
                    if (!savedAddresses.isEmpty()) {
                        selectedAddress = null;
                        for (Address a : savedAddresses) {
                            if (Boolean.TRUE.equals(a.getIsDefault())) {
                                selectedAddress = a;
                                break;
                            }
                        }
                        if (selectedAddress == null) selectedAddress = savedAddresses.get(0);
                        updateAddressUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Address>>> call, Throwable t) {
                // No addresses loaded — keep no-address state
            }
        });
    }

    private void updateAddressUI() {
        if (selectedAddress == null) {
            showNoAddressState();
            return;
        }
        tvNoAddress.setVisibility(View.GONE);
        tvRecipientName.setVisibility(View.VISIBLE);
        tvPhone.setVisibility(View.VISIBLE);
        tvAddress.setVisibility(View.VISIBLE);

        tvRecipientName.setText(selectedAddress.getRecipientName());
        tvPhone.setText(selectedAddress.getPhone());

        String full = selectedAddress.getFullAddress();
        if (full == null || full.isEmpty()) {
            full = buildFullAddress(selectedAddress);
        }
        tvAddress.setText(full);
    }

    private void showNoAddressState() {
        tvRecipientName.setVisibility(View.GONE);
        tvPhone.setVisibility(View.GONE);
        tvAddress.setVisibility(View.GONE);
        if (tvNoAddress != null) tvNoAddress.setVisibility(View.VISIBLE);
    }

    private String buildFullAddress(Address a) {
        StringBuilder sb = new StringBuilder();
        if (a.getAddressLine() != null && !a.getAddressLine().isEmpty()) {
            sb.append(a.getAddressLine()).append(", ");
        }
        if (a.getWard() != null) sb.append(a.getWard()).append(", ");
        if (a.getDistrict() != null) sb.append(a.getDistrict()).append(", ");
        if (a.getCity() != null) sb.append(a.getCity());
        return sb.toString().replaceAll(", $", "");
    }

    private void setupListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
        findViewById(R.id.btn_change_address).setOnClickListener(v -> showAddressPicker());
    }

    private void showAddressPicker() {
        BottomSheetDialog sheet = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_address_picker, null);
        sheet.setContentView(sheetView);

        RecyclerView rv = sheetView.findViewById(R.id.rv_addresses);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new AddressPickerAdapter(savedAddresses, selectedAddress, address -> {
            selectedAddress = address;
            updateAddressUI();
            sheet.dismiss();
        }));

        sheetView.findViewById(R.id.btn_add_new_address).setOnClickListener(v -> {
            sheet.dismiss();
            startActivityForResult(
                    new Intent(this, AddAddressActivity.class),
                    REQUEST_ADD_ADDRESS);
        });

        sheet.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == RESULT_OK) {
            loadSavedAddresses();
        }
    }

    private void populateFromCart(Cart cart) {
        if (cart != null && cart.getItems() != null) {
            orderItems.clear();
            orderItems.addAll(cart.getItems());
            subtotal = cart.getTotalAmount() != null ? cart.getTotalAmount() : BigDecimal.ZERO;
            updateItemsUI();
        }
    }

    private void updateItemsUI() {
        CheckoutItemAdapter adapter = new CheckoutItemAdapter(orderItems, currencyFormat);
        rvOrderItems.setAdapter(adapter);

        if (!isFromCart) {
            subtotal = BigDecimal.ZERO;
            for (CartItem item : orderItems) {
                if (item.getPrice() != null) {
                    subtotal = subtotal.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
        }
        updateTotals();
    }

    private void updateTotals() {
        BigDecimal total = subtotal.add(shippingFee);
        tvSubtotal.setText(String.format("₫%s", currencyFormat.format(subtotal)));
        tvShipping.setText(shippingFee.compareTo(BigDecimal.ZERO) > 0
                ? String.format("₫%s", currencyFormat.format(shippingFee)) : "Miễn phí");
        tvTotal.setText(String.format("₫%s", currencyFormat.format(total)));
        tvBottomTotal.setText(String.format("₫%s", currencyFormat.format(total)));
    }

    private void placeOrder() {
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAddress == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String recipientName = selectedAddress.getRecipientName();
        String phone = selectedAddress.getPhone();
        String full = selectedAddress.getFullAddress();
        if (full == null || full.isEmpty()) full = buildFullAddress(selectedAddress);

        String paymentMethod = rgPaymentMethod.getCheckedRadioButtonId() == R.id.rb_cod ? "COD" : "BANK";
        String note = etNote.getText() != null ? etNote.getText().toString().trim() : "";

        progressBar.setVisibility(View.VISIBLE);
        btnPlaceOrder.setEnabled(false);

        orderViewModel.createOrder(
                recipientName,
                phone,
                full,
                paymentMethod,
                note,
                isFromCart ? null : orderItems
        );
    }

    private void showOrderSuccessDialog(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Đặt hàng thành công!")
                .setMessage("Mã đơn hàng: " + (order != null ? order.getOrderCode() : "N/A") +
                        "\n\nCảm ơn bạn đã đặt hàng. Chúng tôi sẽ liên hệ với bạn sớm nhất.")
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isFromCart) cartViewModel.clearCart();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    // ── Address Picker Adapter ────────────────────────────────────────────────

    interface OnAddressSelected {
        void onSelected(Address address);
    }

    private static class AddressPickerAdapter
            extends RecyclerView.Adapter<AddressPickerAdapter.VH> {

        private final List<Address> addresses;
        private Address selected;
        private final OnAddressSelected callback;

        AddressPickerAdapter(List<Address> addresses, Address selected, OnAddressSelected callback) {
            this.addresses = addresses;
            this.selected = selected;
            this.callback = callback;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_address_picker, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int position) {
            Address a = addresses.get(position);
            h.tvName.setText(a.getRecipientName());
            h.tvPhone.setText(a.getPhone());

            String full = a.getFullAddress();
            if (full == null || full.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                if (a.getAddressLine() != null && !a.getAddressLine().isEmpty())
                    sb.append(a.getAddressLine()).append(", ");
                if (a.getWard() != null) sb.append(a.getWard()).append(", ");
                if (a.getDistrict() != null) sb.append(a.getDistrict()).append(", ");
                if (a.getCity() != null) sb.append(a.getCity());
                full = sb.toString().replaceAll(", $", "");
            }
            h.tvAddress.setText(full);

            h.tvDefault.setVisibility(Boolean.TRUE.equals(a.getIsDefault()) ? View.VISIBLE : View.GONE);

            boolean isSelected = selected != null
                    && selected.getAddressId() != null
                    && selected.getAddressId().equals(a.getAddressId());
            h.rbSelect.setChecked(isSelected);

            h.itemView.setOnClickListener(v -> {
                selected = a;
                notifyDataSetChanged();
                callback.onSelected(a);
            });
        }

        @Override
        public int getItemCount() { return addresses.size(); }

        static class VH extends RecyclerView.ViewHolder {
            RadioButton rbSelect;
            TextView tvName, tvPhone, tvAddress, tvDefault;

            VH(View v) {
                super(v);
                rbSelect = v.findViewById(R.id.rb_select);
                tvName = v.findViewById(R.id.tv_addr_name);
                tvPhone = v.findViewById(R.id.tv_addr_phone);
                tvAddress = v.findViewById(R.id.tv_addr_text);
                tvDefault = v.findViewById(R.id.tv_addr_default);
            }
        }
    }
}
