package com.furniture.app.ui.customer.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ChatMessage;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ChatApi;
import com.furniture.app.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_SHOP_ID = "shop_id";
    public static final String EXTRA_SHOP_NAME = "shop_name";
    public static final String EXTRA_CHAT_ID = "chat_id";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";
    public static final String EXTRA_RECIPIENT_USER_ID = "recipient_user_id";
    public static final String EXTRA_IS_ADMIN = "is_admin";

    private static final long POLL_INTERVAL_MS = 3000;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private SessionManager sessionManager;
    private ChatApi chatApi;
    private String chatId;
    private boolean isAdmin;
    private Integer recipientUserId;
    private String titleName;

    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            loadMessages(false);
            pollHandler.postDelayed(this, POLL_INTERVAL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);
        chatApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ChatApi.class);

        isAdmin = getIntent().getBooleanExtra(EXTRA_IS_ADMIN, false);
        recipientUserId = getIntent().hasExtra(EXTRA_RECIPIENT_USER_ID)
                ? getIntent().getIntExtra(EXTRA_RECIPIENT_USER_ID, -1) : null;

        if (isAdmin) {
            chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
            titleName = getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);
            if (titleName == null) titleName = "Khách hàng";
        } else {
            int shopId = getIntent().getIntExtra(EXTRA_SHOP_ID, 1);
            String shopName = getIntent().getStringExtra(EXTRA_SHOP_NAME);
            titleName = shopName != null ? shopName : "Hỗ trợ Shop";
            chatId = sessionManager.getUserId() + "-" + shopId;
        }

        initViews();
        setupHeader();
        setupRecyclerView();
        setupListeners();
        loadMessages(true);
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

    private void initViews() {
        rvMessages = findViewById(R.id.rv_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupHeader() {
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        TextView tvName = findViewById(R.id.tv_toolbar_name);
        TextView tvAvatar = findViewById(R.id.tv_toolbar_avatar);

        tvName.setText(titleName);
        String initial = (titleName != null && !titleName.isEmpty())
                ? String.valueOf(titleName.charAt(0)).toUpperCase() : "?";
        tvAvatar.setText(initial);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages, titleName);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages(boolean scrollToBottom) {
        if (chatId == null) return;
        chatApi.getMessages(chatId).enqueue(new Callback<ApiResponse<List<ChatMessage>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChatMessage>>> call,
                                   Response<ApiResponse<List<ChatMessage>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<ChatMessage> newMessages = response.body().getData();
                    if (newMessages.size() != messages.size()) {
                        messages.clear();
                        messages.addAll(newMessages);
                        chatAdapter.notifyDataSetChanged();
                        if (scrollToBottom || !messages.isEmpty()) {
                            rvMessages.scrollToPosition(messages.size() - 1);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ChatMessage>>> call, Throwable t) {}
        });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Integer recUserId = isAdmin ? recipientUserId : null;
        ChatApi.SendMessageRequest request = new ChatApi.SendMessageRequest(text, recUserId);

        btnSend.setEnabled(false);
        chatApi.sendMessage(request).enqueue(new Callback<ApiResponse<ChatMessage>>() {
            @Override
            public void onResponse(Call<ApiResponse<ChatMessage>> call,
                                   Response<ApiResponse<ChatMessage>> response) {
                btnSend.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    etMessage.setText("");
                    loadMessages(true);
                } else {
                    Toast.makeText(ChatActivity.this, "Gửi thất bại, thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ChatMessage>> call, Throwable t) {
                btnSend.setEnabled(true);
                Toast.makeText(ChatActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private static final int VIEW_SENDER = 1;
        private static final int VIEW_RECEIVER = 2;

        private final List<ChatMessage> messages;
        private final String otherInitial;
        private final SimpleDateFormat inputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        private final SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

        ChatAdapter(List<ChatMessage> messages, String otherName) {
            this.messages = messages;
            this.otherInitial = (otherName != null && !otherName.isEmpty())
                    ? String.valueOf(otherName.charAt(0)).toUpperCase() : "?";
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).isMine() ? VIEW_SENDER : VIEW_RECEIVER;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = viewType == VIEW_SENDER ? R.layout.item_chat_sender : R.layout.item_chat_receiver;
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ChatViewHolder(view, viewType == VIEW_RECEIVER);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            holder.tvMessage.setText(msg.getMessage());

            if (msg.getCreatedAt() != null) {
                try {
                    Date date = inputFmt.parse(msg.getCreatedAt().replace("Z", "").substring(0, 19));
                    holder.tvTime.setText(timeFmt.format(date));
                } catch (Exception e) {
                    holder.tvTime.setText("");
                }
            }

            if (holder.tvAvatar != null) {
                holder.tvAvatar.setText(otherInitial);
            }
        }

        @Override
        public int getItemCount() { return messages.size(); }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView tvMessage, tvTime, tvAvatar;

            ChatViewHolder(@NonNull View v, boolean isReceiver) {
                super(v);
                tvMessage = v.findViewById(R.id.tv_message);
                tvTime = v.findViewById(R.id.tv_time);
                tvAvatar = isReceiver ? v.findViewById(R.id.tv_avatar) : null;
            }
        }
    }
}
