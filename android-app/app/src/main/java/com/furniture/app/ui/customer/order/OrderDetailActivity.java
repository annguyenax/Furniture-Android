package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.OrderItem;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReviewApi;
import com.furniture.app.data.repository.OrderRepository;
import com.furniture.app.ui.adapter.CheckoutItemAdapter;
import com.furniture.app.ui.viewmodel.OrderViewModel;
import com.furniture.app.ui.viewmodel.OrderViewModelFactory;
import com.furniture.app.util.SessionManager;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";
    private static final int REQUEST_REVIEW = 2001;

    private OrderViewModel orderViewModel;
    private ReviewApi reviewApi;
    private ProgressBar progressBar;

    private TextView tvOrderCode, tvOrderDate, tvStatus;
    private TextView tvRecipientName, tvPhone, tvAddress;
    private RecyclerView rvOrderItems;
    private TextView tvSubtotal, tvShipping, tvTotal, tvPaymentMethod;
    private MaterialCardView cardReview;
    private MaterialButton btnReview;
    private MaterialButton btnCancel;
    private int currentOrderId = -1;

    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private final Set<Integer> reviewedProductIds = new HashSet<>();
    private List<OrderItem> currentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initViews();
        setupToolbar();

        currentOrderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        if (currentOrderId == -1) {
            finish();
            return;
        }

        setupViewModel(currentOrderId);
    }

    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);
        tvOrderCode = findViewById(R.id.tv_order_code);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvStatus = findViewById(R.id.tv_status);
        tvRecipientName = findViewById(R.id.tv_recipient_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        rvOrderItems = findViewById(R.id.rv_order_items);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTotal = findViewById(R.id.tv_total);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        cardReview = findViewById(R.id.card_review);
        btnReview = cardReview.findViewById(R.id.btn_review);
        btnCancel = findViewById(R.id.btn_cancel_order);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Chi tiết đơn hàng");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModel(int orderId) {
        SessionManager sessionManager = new SessionManager(this);
        reviewApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReviewApi.class);

        OrderRepository repository = new OrderRepository(sessionManager.getToken());
        orderViewModel = new ViewModelProvider(this,
                new OrderViewModelFactory(repository)).get(OrderViewModel.class);

        orderViewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        orderViewModel.getOrderDetail().observe(this, this::displayOrder);

        orderViewModel.loadOrderById(orderId);
    }

    private void displayOrder(Order order) {
        if (order == null) return;

        tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getOrderId());
        tvOrderDate.setText(order.getCreatedAt() != null ? order.getCreatedAt().substring(0, 10) : "");
        tvStatus.setText(order.getStatusDisplay());

        String status = order.getStatus();
        if ("DELIVERED".equals(status)) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else if ("CANCELLED".equals(status)) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if ("SHIPPED".equals(status)) {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        }

        tvRecipientName.setText(order.getRecipientName() != null ? order.getRecipientName() : "");
        tvPhone.setText(order.getPhone() != null ? order.getPhone() : "");
        tvAddress.setText(order.getAddress() != null ? order.getAddress() : "");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            CheckoutItemAdapter adapter = new CheckoutItemAdapter(
                    convertOrderItemsToCartItems(order), currencyFormat);
            rvOrderItems.setAdapter(adapter);
        }

        BigDecimal subtotal = order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO;
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : subtotal.add(shipping);

        tvSubtotal.setText(String.format("₫%s", currencyFormat.format(subtotal)));
        tvShipping.setText(shipping.compareTo(BigDecimal.ZERO) > 0
                ? String.format("₫%s", currencyFormat.format(shipping)) : "Miễn phí");
        tvTotal.setText(String.format("₫%s", currencyFormat.format(total)));

        String paymentMethod = "COD".equals(order.getPaymentMethod()) ? "Tiền mặt khi nhận hàng" :
                "BANK".equals(order.getPaymentMethod()) ? "Chuyển khoản ngân hàng" :
                        order.getPaymentMethod() != null ? order.getPaymentMethod() : "";
        tvPaymentMethod.setText(paymentMethod);

        // Cancel button: chỉ hiện khi PENDING hoặc PROCESSING
        if (("PENDING".equals(status) || "PROCESSING".equals(status)) && btnCancel != null) {
            btnCancel.setVisibility(View.VISIBLE);
            btnCancel.setOnClickListener(v -> confirmCancel());
        } else if (btnCancel != null) {
            btnCancel.setVisibility(View.GONE);
        }

        if ("DELIVERED".equals(status) && order.getItems() != null && !order.getItems().isEmpty()) {
            currentItems = order.getItems();
            cardReview.setVisibility(View.VISIBLE);
            checkReviewedStatus(order.getItems());
        } else {
            cardReview.setVisibility(View.GONE);
        }
    }

    private void checkReviewedStatus(List<OrderItem> items) {
        if (items == null || items.isEmpty()) return;

        Set<Integer> seen = new HashSet<>();
        List<OrderItem> uniqueItems = items.stream()
                .filter(i -> i.getProductId() != null)
                .filter(i -> seen.add(i.getProductId()))
                .collect(java.util.stream.Collectors.toList());

        if (uniqueItems.isEmpty()) {
            updateReviewButton();
            return;
        }

        AtomicInteger pending = new AtomicInteger(uniqueItems.size());
        for (OrderItem item : uniqueItems) {
            reviewApi.hasReviewed(item.getProductId()).enqueue(new Callback<ApiResponse<Boolean>>() {
                @Override
                public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getData() != null
                            && Boolean.TRUE.equals(response.body().getData())) {
                        reviewedProductIds.add(item.getProductId());
                    }
                    if (pending.decrementAndGet() == 0) {
                        runOnUiThread(() -> updateReviewButton());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                    if (pending.decrementAndGet() == 0) {
                        runOnUiThread(() -> updateReviewButton());
                    }
                }
            });
        }
    }

    private void updateReviewButton() {
        if (currentItems == null) return;

        long unreviewed = currentItems.stream()
                .filter(i -> i.getProductId() != null && !reviewedProductIds.contains(i.getProductId()))
                .count();

        if (unreviewed == 0) {
            btnReview.setText("Đã đánh giá");
            btnReview.setEnabled(false);
            btnReview.setAlpha(0.6f);
            btnReview.setOnClickListener(null);
        } else {
            btnReview.setText("Đánh giá");
            btnReview.setEnabled(true);
            btnReview.setAlpha(1f);
            btnReview.setOnClickListener(v -> showProductPicker(
                    currentItems.stream()
                            .filter(i -> i.getProductId() != null && !reviewedProductIds.contains(i.getProductId()))
                            .collect(java.util.stream.Collectors.toList())));
        }
    }

    private void showProductPicker(List<OrderItem> unreviewedItems) {
        if (unreviewedItems.isEmpty()) return;
        if (unreviewedItems.size() == 1) {
            openReview(unreviewedItems.get(0));
            return;
        }
        String[] names = unreviewedItems.stream()
                .map(i -> i.getProductName() != null ? i.getProductName() : "Sản phẩm #" + i.getProductId())
                .toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Chọn sản phẩm để đánh giá")
                .setItems(names, (d, which) -> openReview(unreviewedItems.get(which)))
                .show();
    }

    private void openReview(OrderItem item) {
        Intent intent = new Intent(this, WriteReviewActivity.class);
        intent.putExtra(WriteReviewActivity.EXTRA_PRODUCT_ID, item.getProductId());
        intent.putExtra(WriteReviewActivity.EXTRA_PRODUCT_NAME,
                item.getProductName() != null ? item.getProductName() : "Sản phẩm #" + item.getProductId());
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REVIEW && resultCode == RESULT_OK && data != null) {
            int reviewedId = data.getIntExtra(WriteReviewActivity.EXTRA_PRODUCT_ID, -1);
            if (reviewedId != -1) {
                reviewedProductIds.add(reviewedId);
                updateReviewButton();
            }
        }
    }

    private void confirmCancel() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                .setPositiveButton("Hủy đơn", (d, w) -> {
                    orderViewModel.cancelOrder(currentOrderId);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private java.util.List<com.furniture.app.data.model.CartItem> convertOrderItemsToCartItems(Order order) {
        java.util.List<com.furniture.app.data.model.CartItem> items = new java.util.ArrayList<>();
        for (com.furniture.app.data.model.OrderItem oi : order.getItems()) {
            com.furniture.app.data.model.CartItem ci = new com.furniture.app.data.model.CartItem();
            ci.setProductId(oi.getProductId());
            ci.setProductName(oi.getProductName());
            ci.setProductImage(oi.getProductImage());
            ci.setVariantId(oi.getVariantId());
            ci.setVariantName(oi.getVariantName());
            ci.setPrice(oi.getPrice());
            ci.setQuantity(oi.getQuantity());
            items.add(ci);
        }
        return items;
    }
}
