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
import com.furniture.app.data.model.Order;
import com.furniture.app.data.repository.OrderRepository;
import com.furniture.app.ui.adapter.OrderAdapter;
import com.furniture.app.ui.viewmodel.OrderViewModel;
import com.furniture.app.ui.viewmodel.OrderViewModelFactory;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity implements OrderAdapter.OnOrderClickListener {

    private OrderViewModel orderViewModel;
    private SessionManager sessionManager;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvOrders;
    private View emptyState;
    private ProgressBar progressBar;

    private OrderAdapter orderAdapter;
    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        sessionManager = new SessionManager(this);

        initViews();
        setupToolbar();
        setupViewModel();
        setupListeners();
        loadOrders();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        rvOrders = findViewById(R.id.rv_orders);
        emptyState = findViewById(R.id.empty_state);
        progressBar = findViewById(R.id.progress_bar);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(orders, this);
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
            } else {
                showEmptyState();
            }
        });

        orderViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);

        findViewById(R.id.btn_start_shopping).setOnClickListener(v -> {
            finish();
        });
    }

    private void loadOrders() {
        orderViewModel.loadOrders(0, 50);
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
        startActivity(intent);
    }

    @Override
    public void onCancelOrder(Order order) {
        orderViewModel.cancelOrder(order.getOrderId());
    }
}
