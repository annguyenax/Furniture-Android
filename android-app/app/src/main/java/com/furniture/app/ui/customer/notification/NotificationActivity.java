package com.furniture.app.ui.customer.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatRoomItem;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ChatApi;
import com.furniture.app.data.remote.api.OrderApi;
import com.furniture.app.receiver.WifiConnectionReceiver;
import com.furniture.app.ui.adapter.NotificationAdapter;
import com.furniture.app.ui.customer.chat.ChatActivity;
import com.furniture.app.ui.customer.order.OrderDetailActivity;
import com.furniture.app.util.NotificationReadStore;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView notificationRecyclerView;
    private View emptyState;
    private TextView emptyTitle;
    private TextView emptyMessage;
    private ProgressBar progressBar;
    private NotificationAdapter adapter;
    private OrderApi orderApi;
    private ChatApi chatApi;
    private SessionManager sessionManager;
    private NotificationReadStore notificationReadStore;
    private final List<NotificationAdapter.NotificationItem> notificationItems = new ArrayList<>();
    private final List<Order> loadedOrders = new ArrayList<>();
    private int pendingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        sessionManager = new SessionManager(this);
        notificationReadStore = new NotificationReadStore(this);
        orderApi = RetrofitClient.getInstance(sessionManager.getToken()).create(OrderApi.class);
        chatApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ChatApi.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadNotifications();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        notificationRecyclerView = findViewById(R.id.rv_notifications);
        emptyState = findViewById(R.id.empty_state);
        emptyTitle = findViewById(R.id.tv_empty_title);
        emptyMessage = findViewById(R.id.tv_empty_message);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(
                sessionManager.getUserId(),
                sessionManager.getUserName(),
                this::openNotification);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);
    }

    private void loadNotifications() {
        if (!WifiConnectionReceiver.isWifiConnected(this)) {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            showEmpty("Không thể tải thông báo", "Vui lòng kết nối Wi-Fi để xem thông báo.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        notificationItems.clear();
        loadedOrders.clear();
        pendingRequests = 2;

        orderApi.getOrders(0, 20).enqueue(new Callback<ApiResponse<PageResponse<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<Order>>> call,
                                   Response<ApiResponse<PageResponse<Order>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null
                        && response.body().getData().getContent() != null) {
                    for (Order order : response.body().getData().getContent()) {
                        notificationItems.add(NotificationAdapter.NotificationItem.order(order));
                        loadedOrders.add(order);
                    }
                }
                finishOneRequest();
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<Order>>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                finishOneRequest();
            }
        });

        chatApi.getChatRooms().enqueue(new Callback<ApiResponse<List<ChatRoomItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChatRoomItem>>> call,
                                   Response<ApiResponse<List<ChatRoomItem>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().isSuccess() && response.body().getData() != null) {
                    for (ChatRoomItem room : response.body().getData()) {
                        if (room.getUnreadCount() > 0) {
                            notificationItems.add(NotificationAdapter.NotificationItem.chat(room));
                        }
                    }
                }
                finishOneRequest();
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChatRoomItem>>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                finishOneRequest();
            }
        });
    }

    private void finishOneRequest() {
        pendingRequests--;
        if (pendingRequests > 0) return;

        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        notificationReadStore.markOrderNotificationsRead(loadedOrders);

        if (notificationItems.isEmpty()) {
            showEmpty("Chưa có thông báo", "Thông báo về đơn hàng và tin nhắn mới sẽ xuất hiện ở đây.");
        } else {
            adapter.setItems(notificationItems);
            notificationRecyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showEmpty(String title, String message) {
        adapter.setItems(new ArrayList<>());
        notificationRecyclerView.setVisibility(View.GONE);
        emptyTitle.setText(title);
        emptyMessage.setText(message);
        emptyState.setVisibility(View.VISIBLE);
    }

    private void openNotification(NotificationAdapter.NotificationItem item) {
        if (item.getType() == NotificationAdapter.NotificationItem.TYPE_CHAT) {
            ChatRoomItem room = item.getChatRoom();
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_SUPPORT_ID, resolveOtherUserId(room));
            intent.putExtra(ChatActivity.EXTRA_SUPPORT_NAME, resolveChatTitle(room));
            intent.putExtra(ChatActivity.EXTRA_IS_ADMIN, false);
            startActivity(intent);
            return;
        }

        Order order = item.getOrder();
        if (order != null && order.getOrderId() != null) {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.getOrderId());
            startActivity(intent);
        }
    }

    private int resolveOtherUserId(ChatRoomItem room) {
        if (room == null) return 1;
        int currentUserId = sessionManager.getUserId();
        String chatId = room.getChatId();
        if (chatId != null && chatId.contains("-")) {
            String[] parts = chatId.split("-");
            for (String part : parts) {
                try {
                    int id = Integer.parseInt(part.trim());
                    if (id != currentUserId) {
                        return id;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (room.getUserId() != null && room.getUserId() != currentUserId) {
            return room.getUserId();
        }
        return 1;
    }

    private String resolveChatTitle(ChatRoomItem room) {
        if (room == null) return "Hỗ trợ Shop";
        boolean roomUserIsCurrentUser = room.getUserId() != null
                && room.getUserId() == sessionManager.getUserId();
        boolean roomNameIsCurrentUser = room.getUserName() != null
                && sessionManager.getUserName() != null
                && room.getUserName().trim().equalsIgnoreCase(sessionManager.getUserName().trim());
        if (roomUserIsCurrentUser || roomNameIsCurrentUser) {
            return "Hỗ trợ Shop";
        }
        return room.getUserName() != null && !room.getUserName().isEmpty()
                ? room.getUserName()
                : "Hỗ trợ Shop";
    }
}
