package com.furniture.app.ui.customer.order;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.furniture.app.data.model.Cart;
import com.furniture.app.data.model.CartItem;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.model.ProductVariant;
import com.furniture.app.data.repository.CartRepository;
import com.furniture.app.data.repository.OrderRepository;
import com.furniture.app.ui.adapter.CheckoutItemAdapter;
import com.furniture.app.ui.viewmodel.CartViewModel;
import com.furniture.app.ui.viewmodel.CartViewModelFactory;
import com.furniture.app.ui.viewmodel.OrderViewModel;
import com.furniture.app.ui.viewmodel.OrderViewModelFactory;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_FROM_CART = "from_cart";
    public static final String EXTRA_PRODUCT = "product";
    public static final String EXTRA_VARIANT = "variant";
    public static final String EXTRA_QUANTITY = "quantity";

    private SessionManager sessionManager;
    private NumberFormat currencyFormat;
    private CartViewModel cartViewModel;
    private OrderViewModel orderViewModel;

    // Views
    private TextView tvRecipientName, tvPhone, tvAddress;
    private RecyclerView rvOrderItems;
    private RadioGroup rgPaymentMethod;
    private EditText etNote;
    private TextView tvSubtotal, tvShipping, tvTotal, tvBottomTotal;
    private MaterialButton btnPlaceOrder;
    private ProgressBar progressBar;

    private List<CartItem> orderItems = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private boolean isFromCart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sessionManager = new SessionManager(this);
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        initViews();
        setupToolbar();
        setupViewModels();
        loadOrderData();
        setupListeners();
    }

    private void initViews() {
        tvRecipientName = findViewById(R.id.tv_recipient_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
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

        // Load shipping info (ưu tiên thông tin đã lưu, fallback về thông tin tài khoản)
        tvRecipientName.setText(sessionManager.getShippingName());
        String savedPhone = sessionManager.getShippingPhone();
        tvPhone.setText(savedPhone != null ? savedPhone : "Chưa có số điện thoại");
        String savedAddress = sessionManager.getUserAddress();
        tvAddress.setText(savedAddress != null ? savedAddress : "Chưa có địa chỉ");
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

        // Observe cart
        cartViewModel.getCart().observe(this, this::populateFromCart);

        // Observe order result
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
            // Load from cart
            cartViewModel.loadCart();
        } else {
            // Direct buy - create single item
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

        // Calculate subtotal if not from cart
        if (!isFromCart) {
            subtotal = BigDecimal.ZERO;
            for (CartItem item : orderItems) {
                if (item.getPrice() != null) {
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    subtotal = subtotal.add(itemTotal);
                }
            }
        }

        updateTotals();
    }

    private void updateTotals() {
        BigDecimal total = subtotal.add(shippingFee);

        tvSubtotal.setText(String.format("₫%s", currencyFormat.format(subtotal)));
        tvShipping.setText(shippingFee.compareTo(BigDecimal.ZERO) > 0 ?
                String.format("₫%s", currencyFormat.format(shippingFee)) : "Miễn phí");
        tvTotal.setText(String.format("₫%s", currencyFormat.format(total)));
        tvBottomTotal.setText(String.format("₫%s", currencyFormat.format(total)));
    }

    private void setupListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        findViewById(R.id.btn_change_address).setOnClickListener(v -> showAddressEditDialog());
    }

    private void showAddressEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_address, null);
        EditText etName = dialogView.findViewById(R.id.et_name);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        EditText etAddress = dialogView.findViewById(R.id.et_address);

        // Pre-fill current values
        etName.setText(tvRecipientName.getText());
        String phone = tvPhone.getText().toString();
        if (!phone.equals("Chưa có số điện thoại")) {
            etPhone.setText(phone);
        }
        String address = tvAddress.getText().toString();
        if (!address.equals("Chưa có địa chỉ")) {
            etAddress.setText(address);
        }

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật địa chỉ giao hàng")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String newPhone = etPhone.getText().toString().trim();
                    String newAddress = etAddress.getText().toString().trim();

                    if (name.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
                        Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    tvRecipientName.setText(name);
                    tvPhone.setText(newPhone);
                    tvAddress.setText(newAddress);

                    // Save to session for future use
                    sessionManager.saveShippingInfo(name, newPhone, newAddress);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void placeOrder() {
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để đặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String recipientName = tvRecipientName.getText().toString();
        String phone = tvPhone.getText().toString();
        String address = tvAddress.getText().toString();

        if (address.equals("Chưa có địa chỉ") || phone.equals("Chưa có số điện thoại")) {
            Toast.makeText(this, "Vui lòng cập nhật thông tin giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = rgPaymentMethod.getCheckedRadioButtonId() == R.id.rb_cod ? "COD" : "BANK";
        String note = etNote.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);
        btnPlaceOrder.setEnabled(false);

        orderViewModel.createOrder(
                recipientName,
                phone,
                address,
                paymentMethod,
                note,
                isFromCart ? null : orderItems  // If from cart, backend will get items from cart
        );
    }

    private void showOrderSuccessDialog(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Đặt hàng thành công!")
                .setMessage("Mã đơn hàng: " + (order != null ? order.getOrderCode() : "N/A") +
                        "\n\nCảm ơn bạn đã đặt hàng. Chúng tôi sẽ liên hệ với bạn sớm nhất.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear cart if ordered from cart
                    if (isFromCart) {
                        cartViewModel.clearCart();
                    }
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
