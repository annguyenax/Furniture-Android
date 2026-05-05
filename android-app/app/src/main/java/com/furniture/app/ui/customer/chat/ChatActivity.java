package com.furniture.app.ui.customer.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_SHOP_ID = "shop_id";
    public static final String EXTRA_SHOP_NAME = "shop_name";

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private int shopId;
    private String shopName;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sessionManager = new SessionManager(this);
        shopId = getIntent().getIntExtra(EXTRA_SHOP_ID, -1);
        shopName = getIntent().getStringExtra(EXTRA_SHOP_NAME);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupListeners();
        loadMessages();
    }

    private void initViews() {
        rvMessages = findViewById(R.id.rv_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(shopName != null ? shopName : "Chat");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        // Add welcome message
        ChatMessage welcomeMsg = new ChatMessage();
        welcomeMsg.setMessage("Xin chào! Tôi có thể giúp gì cho bạn?");
        welcomeMsg.setSender(false);
        welcomeMsg.setTimestamp(new Date());
        messages.add(welcomeMsg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // Add user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setMessage(text);
        userMsg.setSender(true);
        userMsg.setTimestamp(new Date());
        messages.add(userMsg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        rvMessages.scrollToPosition(messages.size() - 1);

        etMessage.setText("");

        // Simulate auto reply after 1 second
        rvMessages.postDelayed(() -> {
            ChatMessage reply = new ChatMessage();
            reply.setMessage("Cảm ơn bạn đã liên hệ. Chúng tôi sẽ phản hồi sớm nhất có thể!");
            reply.setSender(false);
            reply.setTimestamp(new Date());
            messages.add(reply);
            chatAdapter.notifyItemInserted(messages.size() - 1);
            rvMessages.scrollToPosition(messages.size() - 1);
        }, 1000);
    }

    // Chat Message Model
    static class ChatMessage {
        private String message;
        private boolean isSender;
        private Date timestamp;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public boolean isSender() { return isSender; }
        public void setSender(boolean sender) { isSender = sender; }
        public Date getTimestamp() { return timestamp; }
        public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    }

    // Chat Adapter
    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private static final int VIEW_TYPE_SENDER = 1;
        private static final int VIEW_TYPE_RECEIVER = 2;

        private List<ChatMessage> messages;
        private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).isSender() ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layoutRes = viewType == VIEW_TYPE_SENDER ? R.layout.item_chat_sender : R.layout.item_chat_receiver;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);
            holder.tvMessage.setText(msg.getMessage());
            holder.tvTime.setText(timeFormat.format(msg.getTimestamp()));
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView tvMessage, tvTime;

            ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMessage = itemView.findViewById(R.id.tv_message);
                tvTime = itemView.findViewById(R.id.tv_time);
            }
        }
    }
}
