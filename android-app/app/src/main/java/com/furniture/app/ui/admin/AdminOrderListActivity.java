package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminOrderApi;
import com.furniture.app.util.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminOrderListActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private AdminOrderAdapter adapter;
    private final List<Order> orders = new ArrayList<>();
    private AdminOrderApi adminOrderApi;

    private String currentStatus = null;
    private String currentSearch = null;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        SessionManager sessionManager = new SessionManager(this);
        adminOrderApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminOrderApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvOrders = findViewById(R.id.rv_orders);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(orders, this::onUpdateStatus);
        rvOrders.setAdapter(adapter);

        setupSearch();
        setupChips();
        loadOrders();
    }

    private void setupSearch() {
        TextInputEditText etSearch = findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    currentSearch = s.toString().trim().isEmpty() ? null : s.toString().trim();
                    loadOrders();
                };
                searchHandler.postDelayed(searchRunnable, 400);
            }
        });
    }

    private void setupChips() {
        ChipGroup chipGroup = findViewById(R.id.chip_group_status);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all) currentStatus = null;
            else if (id == R.id.chip_pending) currentStatus = "PENDING";
            else if (id == R.id.chip_processing) currentStatus = "PROCESSING";
            else if (id == R.id.chip_shipped) currentStatus = "SHIPPED";
            else if (id == R.id.chip_delivered) currentStatus = "DELIVERED";
            else if (id == R.id.chip_cancelled) currentStatus = "CANCELLED";
            loadOrders();
        });
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        adminOrderApi.getAllOrders(0, 100, currentStatus, currentSearch)
                .enqueue(new Callback<ApiResponse<PageResponse<Order>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Order>>> call,
                                           Response<ApiResponse<PageResponse<Order>>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            orders.clear();
                            orders.addAll(response.body().getData().getContent());
                            adapter.notifyDataSetChanged();
                            tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
                            rvOrders.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Order>>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminOrderListActivity.this, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onUpdateStatus(Order order) {
        String[] statuses = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
        String[] labels = {"Chờ xác nhận", "Đang xử lý", "Đang giao", "Đã giao", "Đã hủy"};

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái: " + (order.getOrderCode() != null ? order.getOrderCode() : ""))
                .setItems(labels, (dialog, which) -> {
                    String newStatus = statuses[which];
                    adminOrderApi.updateOrderStatus(order.getOrderId(), newStatus)
                            .enqueue(new Callback<ApiResponse<Order>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Order>> call,
                                                       Response<ApiResponse<Order>> response) {
                                    if (response.isSuccessful() && response.body() != null
                                            && response.body().isSuccess()) {
                                        Toast.makeText(AdminOrderListActivity.this,
                                                "Đã cập nhật → " + labels[which], Toast.LENGTH_SHORT).show();
                                        loadOrders();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                                    Toast.makeText(AdminOrderListActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .show();
    }

    // ── Adapter ───────────────────────────────────────────────────────────────

    static class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.VH> {

        private final List<Order> orders;
        private final java.util.function.Consumer<Order> onUpdateStatus;
        private final NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

        AdminOrderAdapter(List<Order> orders, java.util.function.Consumer<Order> onUpdateStatus) {
            this.orders = orders;
            this.onUpdateStatus = onUpdateStatus;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_order, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH h, int position) {
            Order o = orders.get(position);
            h.tvCode.setText(o.getOrderCode() != null ? o.getOrderCode() : "#" + o.getOrderId());
            h.tvCustomer.setText(o.getRecipientName() != null ? "👤 " + o.getRecipientName() : "");
            h.tvStatus.setText(o.getStatusDisplay());
            h.tvTotal.setText(o.getTotalAmount() != null ? "₫" + fmt.format(o.getTotalAmount()) : "");

            // Date
            String dateStr = o.getCreatedAt();
            if (dateStr != null && dateStr.length() >= 10) {
                h.tvDate.setText(dateStr.substring(0, 10));
            } else {
                h.tvDate.setText("");
            }

            // Status badge color
            String status = o.getStatus();
            int color;
            if ("DELIVERED".equals(status)) color = 0xFF4CAF50;
            else if ("CANCELLED".equals(status)) color = 0xFFF44336;
            else if ("SHIPPED".equals(status)) color = 0xFF2196F3;
            else if ("PROCESSING".equals(status)) color = 0xFFFF9800;
            else color = 0xFF9E9E9E;
            h.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));

            h.btnUpdate.setOnClickListener(v -> onUpdateStatus.accept(o));
        }

        @Override public int getItemCount() { return orders.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvCode, tvCustomer, tvStatus, tvTotal, tvDate;
            MaterialButton btnUpdate;

            VH(View v) {
                super(v);
                tvCode = v.findViewById(R.id.tv_order_code);
                tvCustomer = v.findViewById(R.id.tv_customer);
                tvStatus = v.findViewById(R.id.tv_status);
                tvTotal = v.findViewById(R.id.tv_total);
                tvDate = v.findViewById(R.id.tv_date);
                btnUpdate = v.findViewById(R.id.btn_update_status);
            }
        }
    }
}
