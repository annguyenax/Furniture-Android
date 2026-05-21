package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReturnRequestApi;
import com.furniture.app.data.remote.api.ReviewApi;
import com.furniture.app.data.repository.OrderRepository;
import com.furniture.app.ui.adapter.OrderAdapter;
import com.furniture.app.ui.viewmodel.OrderViewModel;
import com.furniture.app.ui.viewmodel.OrderViewModelFactory;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private static final int REQUEST_DETAIL = 200;
    private static final int REQUEST_RETURN = 201;

    private OrderViewModel orderViewModel;
    private SessionManager sessionManager;
    private ReviewApi reviewApi;
    private ReturnRequestApi returnApi;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvOrders;
    private View emptyState;
    private ProgressBar progressBar;

    private OrderAdapter orderAdapter;
    private final List<Order> orders = new ArrayList<>();
    private final Set<Integer> reviewedOrderIds = new HashSet<>();
    private final Set<Integer> returnedOrderIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        sessionManager = new SessionManager(this);
        reviewApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReviewApi.class);
        returnApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReturnRequestApi.class);

        initViews();
        setupToolbar();
        setupViewModel();
        setupListeners();
        loadOrders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        rvOrders = findViewById(R.id.rv_orders);
        emptyState = findViewById(R.id.empty_state);
        progressBar = findViewById(R.id.progress_bar);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orders, this, reviewedOrderIds, returnedOrderIds);
        rvOrders.setAdapter(orderAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Đơn hàng của tôi");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModel() {
        String token = sessionManager.getToken();
        OrderRepository orderRepository = new OrderRepository(token);
        orderViewModel = new ViewModelProvider(this,
                new OrderViewModelFactory(orderRepository)).get(OrderViewModel.class);

        orderViewModel.getOrders().observe(this, orderList -> {
            swipeRefreshLayout.setRefreshing(false);
            if (orderList != null && !orderList.isEmpty()) {
                orders.clear();
                orders.addAll(orderList);
                orderAdapter.notifyDataSetChanged();
                showOrders();
                loadReviewedOrders();
                loadReturnedOrders();
            } else {
                showEmptyState();
            }
        });

        orderViewModel.getIsLoading().observe(this, isLoading ->
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        orderViewModel.getCancelOrderResult().observe(this, response -> {
            // Orders list is reloaded automatically inside ViewModel.cancelOrder
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        findViewById(R.id.btn_start_shopping).setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        orderViewModel.loadOrders(0, 50);
    }

    private void loadReturnedOrders() {
        returnApi.getReturnedOrderIds().enqueue(new Callback<ApiResponse<List<Integer>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Integer>>> call,
                                   Response<ApiResponse<List<Integer>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    returnedOrderIds.addAll(response.body().getData());
                    orderAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {}
        });
    }

    private void loadReviewedOrders() {
        reviewApi.getFullyReviewedOrders().enqueue(new Callback<ApiResponse<List<Integer>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Integer>>> call,
                                   Response<ApiResponse<List<Integer>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    reviewedOrderIds.addAll(response.body().getData());
                    orderAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Integer>>> call, Throwable t) {}
        });
    }

    private void showOrders() {
        rvOrders.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        rvOrders.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
        startActivityForResult(intent, REQUEST_DETAIL);
    }

    @Override
    public void onCancelOrder(Order order) {
        orderViewModel.cancelOrder(order.getOrderId());
    }

    @Override
    public void onReviewOrder(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
        intent.putExtra(OrderDetailActivity.EXTRA_OPEN_REVIEW, true);
        startActivityForResult(intent, REQUEST_DETAIL);
    }

    @Override
    public void onReturnOrder(Order order) {
        Intent intent = new Intent(this, ReturnRequestActivity.class);
        intent.putExtra(ReturnRequestActivity.EXTRA_ORDER_ID, order.getOrderId());
        startActivityForResult(intent, REQUEST_RETURN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQUEST_DETAIL) {
            int orderId = data.getIntExtra(OrderDetailActivity.RESULT_EXTRA_ORDER_ID, -1);
            if (orderId == -1) return;
            if (data.getBooleanExtra(OrderDetailActivity.RESULT_EXTRA_REVIEWED, false)) {
                reviewedOrderIds.add(orderId);
            }
            if (data.getBooleanExtra(OrderDetailActivity.RESULT_EXTRA_RETURNED, false)) {
                returnedOrderIds.add(orderId);
            }
            orderAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_RETURN) {
            int orderId = data.getIntExtra(ReturnRequestActivity.EXTRA_ORDER_ID, -1);
            if (orderId != -1) {
                returnedOrderIds.add(orderId);
                orderAdapter.notifyDataSetChanged();
            }
        }
    }
}
