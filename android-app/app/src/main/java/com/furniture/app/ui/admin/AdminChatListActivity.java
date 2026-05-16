package com.furniture.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatRoomItem;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ChatApi;
import com.furniture.app.ui.customer.chat.ChatActivity;
import com.furniture.app.util.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChatListActivity extends AppCompatActivity {

    private static final long POLL_INTERVAL_MS = 5000;

    private RecyclerView rvChatRooms;
    private TextView tvEmpty;
    private ChatRoomAdapter adapter;
    private final List<ChatRoomItem> rooms = new ArrayList<>();

    private ChatApi chatApi;

    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            loadRooms();
            pollHandler.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat_list);

        SessionManager sessionManager = new SessionManager(this);
        chatApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ChatApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tin nhắn khách hàng");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvChatRooms = findViewById(R.id.rv_chat_rooms);
        tvEmpty = findViewById(R.id.tv_empty);

        adapter = new ChatRoomAdapter(rooms, room -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_CHAT_ID, room.getChatId());
            intent.putExtra(ChatActivity.EXTRA_CUSTOMER_NAME, room.getUserName());
            intent.putExtra(ChatActivity.EXTRA_RECIPIENT_USER_ID, room.getUserId());
            intent.putExtra(ChatActivity.EXTRA_IS_ADMIN, true);
            startActivity(intent);
        });

        rvChatRooms.setLayoutManager(new LinearLayoutManager(this));
        rvChatRooms.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvChatRooms.setAdapter(adapter);

        loadRooms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollHandler.removeCallbacks(pollRunnable);
    }

    private void loadRooms() {
        chatApi.getChatRooms().enqueue(new Callback<ApiResponse<List<ChatRoomItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChatRoomItem>>> call,
                                   Response<ApiResponse<List<ChatRoomItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ChatRoomItem> data = response.body().getData();
                    rooms.clear();
                    rooms.addAll(data);
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(rooms.isEmpty() ? View.VISIBLE : View.GONE);
                    rvChatRooms.setVisibility(rooms.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChatRoomItem>>> call, Throwable t) {}
        });
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    interface OnRoomClickListener {
        void onClick(ChatRoomItem room);
    }

    static class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.RoomViewHolder> {

        private final List<ChatRoomItem> rooms;
        private final OnRoomClickListener listener;
        private final SimpleDateFormat inputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        ChatRoomAdapter(List<ChatRoomItem> rooms, OnRoomClickListener listener) {
            this.rooms = rooms;
            this.listener = listener;
        }

        @NonNull
        @Override
        public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_room, parent, false);
            return new RoomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
            ChatRoomItem room = rooms.get(position);
            String name = room.getUserName() != null ? room.getUserName() : "Khách hàng";

            holder.tvCustomerName.setText(name);
            holder.tvLastMessage.setText(room.getLastMessage() != null ? room.getLastMessage() : "");

            // Avatar initial
            String initial = name.isEmpty() ? "K" : String.valueOf(name.charAt(0)).toUpperCase();
            holder.tvAvatarInitial.setText(initial);

            if (room.getLastMessageTime() != null) {
                try {
                    String raw = room.getLastMessageTime().replace("Z", "").substring(0, 19);
                    Date date = inputFmt.parse(raw);
                    if (date != null) {
                        holder.tvLastTime.setText(DateUtils.getRelativeTimeSpanString(
                                date.getTime(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
                    }
                } catch (ParseException e) {
                    holder.tvLastTime.setText("");
                }
            }

            long unread = room.getUnreadCount();
            if (unread > 0) {
                holder.tvUnread.setVisibility(View.VISIBLE);
                holder.tvUnread.setText(unread > 99 ? "99+" : String.valueOf(unread));
            } else {
                holder.tvUnread.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> listener.onClick(room));
        }

        @Override
        public int getItemCount() { return rooms.size(); }

        static class RoomViewHolder extends RecyclerView.ViewHolder {
            TextView tvCustomerName, tvLastMessage, tvLastTime, tvUnread, tvAvatarInitial;

            RoomViewHolder(@NonNull View v) {
                super(v);
                tvCustomerName = v.findViewById(R.id.tv_customer_name);
                tvLastMessage = v.findViewById(R.id.tv_last_message);
                tvLastTime = v.findViewById(R.id.tv_last_time);
                tvUnread = v.findViewById(R.id.tv_unread_count);
                tvAvatarInitial = v.findViewById(R.id.tv_avatar_initial);
            }
        }
    }
}
