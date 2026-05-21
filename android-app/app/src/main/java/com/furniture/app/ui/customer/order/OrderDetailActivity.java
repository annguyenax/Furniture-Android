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
import com.furniture.app.data.remote.api.ReturnRequestApi;
import com.furniture.app.data.remote.api.ReviewApi;
import com.furniture.app.data.repository.OrderRepository;
import com.furniture.app.ui.adapter.CheckoutItemAdapter;
import com.furniture.app.ui.viewmodel.OrderViewModel;
import com.furniture.app.ui.viewmodel.OrderViewModelFactory;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_OPEN_REVIEW = "open_review";
    public static final String RESULT_EXTRA_ORDER_ID = "result_order_id";
    public static final String RESULT_EXTRA_REVIEWED = "result_reviewed";
    public static final String RESULT_EXTRA_RETURNED = "result_returned";

    private static final int REQUEST_REVIEW = 2001;
    private static final int REQUEST_RETURN = 2002;

    private OrderViewModel orderViewModel;
    private ReviewApi reviewApi;
    private ReturnRequestApi returnApi;
    private ProgressBar progressBar;

    private TextView tvOrderCode, tvOrderDate, tvStatus;
    private TextView tvRecipientName, tvPhone, tvAddress;
    private RecyclerView rvOrderItems;
    private TextView tvSubtotal, tvShipping, tvTotal, tvPaymentMethod;
    private MaterialCardView cardReview;
    private MaterialButton btnReview;
    private MaterialButton btnCancel;
    private MaterialButton btnReturn;

    private int currentOrderId = -1;
    private boolean autoOpenReview = false;
    private boolean resultReviewed = false;
    private boolean resultReturned = false;
    private final Set<Integer> reviewedProductIds = new HashSet<>();
    private String returnStatus = null;

    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    private List<OrderItem> currentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initViews();
        setupToolbar();

        currentOrderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        autoOpenReview = getIntent().getBooleanExtra(EXTRA_OPEN_REVIEW, false);
        if (currentOrderId == -1) {
            finish();
            return;
        }

        SessionManager sessionManager = new SessionManager(this);
        reviewApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReviewApi.class);
        returnApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReturnRequestApi.class);

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
        btnReview = cardReview != null ? cardReview.findViewById(R.id.btn_review) : null;
        btnCancel = findViewById(R.id.btn_cancel_order);
        btnReturn = findViewById(R.id.btn_return_order);

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
        OrderRepository repository = new OrderRepository(sessionManager.getToken());
        orderViewModel = new ViewModelProvider(this,
                new OrderViewModelFactory(repository)).get(OrderViewModel.class);

        orderViewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        orderViewModel.getOrderDetail().observe(this, this::displayOrder);

        orderViewModel.getCancelOrderResult().observe(this, response -> {
            if (response != null && response.isSuccess()) {
                orderViewModel.loadOrderById(currentOrderId);
            } else if (response != null && !response.isSuccess()) {
                Toast.makeText(this, "Hủy đơn thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        orderViewModel.loadOrderById(orderId);
    }

    private void displayOrder(Order order) {
        if (order == null) return;

        tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getOrderId());
        tvOrderDate.setText(order.getCreatedAt() != null ? order.getCreatedAt().substring(0, 10) : "");
        tvStatus.setText(order.getStatusDisplay());

        String status = order.getStatus();
        String orderReturnStatus = order.getReturnStatus();
        if ("APPROVED".equals(orderReturnStatus)) {
            tvStatus.setTextColor(0xFF9C27B0);
        } else if ("PENDING".equals(orderReturnStatus)) {
            tvStatus.setTextColor(0xFFFF5722);
        } else if ("DELIVERED".equals(status)) {
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
            rvOrderItems.setAdapter(new CheckoutItemAdapter(
                    convertOrderItemsToCartItems(order), currencyFormat));
        }

        BigDecimal subtotal = order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO;
        BigDecimal shipping = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : subtotal.add(shipping);

        tvSubtotal.setText(String.format("₫%s", currencyFormat.format(subtotal)));
        if (tvShipping != null) {
            tvShipping.setText(shipping.compareTo(BigDecimal.ZERO) > 0
                    ? String.format("₫%s", currencyFormat.format(shipping)) : "Miễn phí");
        }
        tvTotal.setText(String.format("₫%s", currencyFormat.format(total)));

        String pm = "COD".equals(order.getPaymentMethod()) ? "Tiền mặt khi nhận hàng" :
                "BANK".equals(order.getPaymentMethod()) ? "Chuyển khoản ngân hàng" :
                        order.getPaymentMethod() != null ? order.getPaymentMethod() : "";
        tvPaymentMethod.setText(pm);

        if (btnCancel != null) {
            boolean canCancel = "PENDING".equals(status) || "PROCESSING".equals(status);
            btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);
            btnCancel.setOnClickListener(v -> confirmCancel());
        }

        if ("DELIVERED".equals(status)) {
            currentItems = order.getItems() != null ? order.getItems() : new java.util.ArrayList<>();

            if ("APPROVED".equals(orderReturnStatus)) {
                // Đã hoàn hàng — ẩn cả review lẫn nút hoàn trả
                if (btnReturn != null) btnReturn.setVisibility(View.GONE);
                if (cardReview != null) cardReview.setVisibility(View.GONE);
            } else {
                // Show return button, then check actual status from server
                if (btnReturn != null) {
                    btnReturn.setVisibility(View.VISIBLE);
                    updateReturnButton();
                    btnReturn.setOnClickListener(v -> {
                        if (returnStatus == null || "REJECTED".equals(returnStatus)) {
                            Intent intent = new Intent(this, ReturnRequestActivity.class);
                            intent.putExtra(ReturnRequestActivity.EXTRA_ORDER_ID, currentOrderId);
                            startActivityForResult(intent, REQUEST_RETURN);
                        }
                    });
                }

                // Show review card, then check actual status from server
                if (cardReview != null) cardReview.setVisibility(View.VISIBLE);
                if (btnReview != null) {
                    updateReviewButton();
                    btnReview.setOnClickListener(v -> {
                        if (!isAllReviewed()) showProductPicker(currentItems);
                    });
                }

                checkStatuses();

                if (autoOpenReview) {
                    autoOpenReview = false;
                    showProductPicker(currentItems);
                }
            }
        } else {
            if (btnReturn != null) btnReturn.setVisibility(View.GONE);
            if (cardReview != null) cardReview.setVisibility(View.GONE);
        }
    }

    private void checkStatuses() {
        // Check which products have been reviewed for this order
        reviewApi.getReviewedProductsForOrder(currentOrderId)
                .enqueue(new Callback<ApiResponse<List<Integer>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Integer>>> call,
                                           Response<ApiResponse<List<Integer>>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            reviewedProductIds.clear();
                            reviewedProductIds.addAll(response.body().getData());
                            updateReviewButton();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {}
                });

        // Check if a return request exists for this order
        returnApi.checkReturnStatus(currentOrderId)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call,
                                           Response<ApiResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            returnStatus = response.body().getData();
                            updateReturnButton();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {}
                });
    }

    private boolean isAllReviewed() {
        if (currentItems == null || currentItems.isEmpty()) return false;
        for (OrderItem item : currentItems) {
            if (!reviewedProductIds.contains(item.getProductId())) return false;
        }
        return true;
    }

    private void updateReviewButton() {
        if (btnReview == null) return;
        if (isAllReviewed()) {
            btnReview.setText("Đã đánh giá");
            btnReview.setEnabled(false);
            btnReview.setAlpha(0.6f);
        } else {
            btnReview.setText("Đánh giá");
            btnReview.setEnabled(true);
            btnReview.setAlpha(1f);
        }
    }

    private void updateReturnButton() {
        if (btnReturn == null) return;
        // Hide return button if user has reviewed any product in this order
        if (!reviewedProductIds.isEmpty()) {
            btnReturn.setVisibility(View.GONE);
            return;
        }
        btnReturn.setVisibility(View.VISIBLE);
        if ("PENDING".equals(returnStatus) || "APPROVED".equals(returnStatus)) {
            btnReturn.setText("Đã yêu cầu hoàn hàng");
            btnReturn.setEnabled(false);
            btnReturn.setAlpha(0.6f);
        } else if ("REJECTED".equals(returnStatus)) {
            btnReturn.setText("Yêu cầu hoàn trả lại");
            btnReturn.setEnabled(true);
            btnReturn.setAlpha(1f);
        } else {
            btnReturn.setText("Yêu cầu hoàn trả");
            btnReturn.setEnabled(true);
            btnReturn.setAlpha(1f);
        }
    }

    private void showProductPicker(List<OrderItem> items) {
        if (items == null || items.isEmpty()) return;

        // Filter out already-reviewed products
        List<OrderItem> unreviewed = new java.util.ArrayList<>();
        for (OrderItem item : items) {
            if (!reviewedProductIds.contains(item.getProductId())) {
                unreviewed.add(item);
            }
        }
        if (unreviewed.isEmpty()) {
            updateReviewButton();
            return;
        }
        if (unreviewed.size() == 1) {
            openReview(unreviewed.get(0));
            return;
        }
        String[] names = unreviewed.stream()
                .map(i -> i.getProductName() != null ? i.getProductName() : "Sản phẩm #" + i.getProductId())
                .toArray(String[]::new);
        new AlertDialog.Builder(this)
                .setTitle("Chọn sản phẩm để đánh giá")
                .setItems(names, (d, which) -> openReview(unreviewed.get(which)))
                .show();
    }

    private void openReview(OrderItem item) {
        Intent intent = new Intent(this, WriteReviewActivity.class);
        intent.putExtra(WriteReviewActivity.EXTRA_PRODUCT_ID, item.getProductId());
        intent.putExtra(WriteReviewActivity.EXTRA_ORDER_ID, currentOrderId);
        intent.putExtra(WriteReviewActivity.EXTRA_PRODUCT_NAME,
                item.getProductName() != null ? item.getProductName() : "Sản phẩm #" + item.getProductId());
        startActivityForResult(intent, REQUEST_REVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REVIEW && resultCode == RESULT_OK) {
            int reviewedProductId = data != null ? data.getIntExtra(WriteReviewActivity.EXTRA_PRODUCT_ID, -1) : -1;
            if (reviewedProductId != -1) reviewedProductIds.add(reviewedProductId);
            updateReviewButton();
            resultReviewed = true;
            propagateResult();
            Toast.makeText(this, "Đánh giá đã được gửi!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_RETURN && resultCode == RESULT_OK) {
            returnStatus = "PENDING";
            updateReturnButton();
            resultReturned = true;
            propagateResult();
        }
    }

    private void propagateResult() {
        Intent result = new Intent();
        result.putExtra(RESULT_EXTRA_ORDER_ID, currentOrderId);
        result.putExtra(RESULT_EXTRA_REVIEWED, resultReviewed);
        result.putExtra(RESULT_EXTRA_RETURNED, resultReturned);
        setResult(RESULT_OK, result);
    }

    private void confirmCancel() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                .setPositiveButton("Hủy đơn", (d, w) -> {
                    if (btnCancel != null) btnCancel.setEnabled(false);
                    orderViewModel.cancelOrder(currentOrderId);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private java.util.List<com.furniture.app.data.model.CartItem> convertOrderItemsToCartItems(Order order) {
        java.util.List<com.furniture.app.data.model.CartItem> items = new java.util.ArrayList<>();
        for (OrderItem oi : order.getItems()) {
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
