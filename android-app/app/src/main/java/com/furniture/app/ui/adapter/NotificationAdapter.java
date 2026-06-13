package com.furniture.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ChatRoomItem;
import com.furniture.app.data.model.Order;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    public interface OnNotificationClickListener {
        void onClick(NotificationItem item);
    }

    public static class NotificationItem {
        public static final int TYPE_ORDER = 1;
        public static final int TYPE_CHAT = 2;

        private final int type;
        private final Order order;
        private final ChatRoomItem chatRoom;

        private NotificationItem(int type, Order order, ChatRoomItem chatRoom) {
            this.type = type;
            this.order = order;
            this.chatRoom = chatRoom;
        }

        public static NotificationItem order(Order order) {
            return new NotificationItem(TYPE_ORDER, order, null);
        }

        public static NotificationItem chat(ChatRoomItem chatRoom) {
            return new NotificationItem(TYPE_CHAT, null, chatRoom);
        }

        public int getType() { return type; }
        public Order getOrder() { return order; }
        public ChatRoomItem getChatRoom() { return chatRoom; }
    }

    private final List<NotificationItem> items = new ArrayList<>();
    private final Integer currentUserId;
    private final String currentUserName;
    private final OnNotificationClickListener listener;

    public NotificationAdapter(Integer currentUserId, String currentUserName,
                               OnNotificationClickListener listener) {
        this.currentUserId = currentUserId;
        this.currentUserName = currentUserName;
        this.listener = listener;
    }

    public void setItems(List<NotificationItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = items.get(position);
        holder.title.setText(buildTitle(item));
        holder.message.setText(buildMessage(item));
        holder.time.setText(buildTime(item));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String buildTitle(NotificationItem item) {
        if (item.getType() == NotificationItem.TYPE_CHAT) {
            return "Tin nhắn mới từ " + resolveChatSenderName(item.getChatRoom());
        }

        Order order = item.getOrder();
        String orderCode = order != null && order.getOrderCode() != null
                ? order.getOrderCode()
                : "#" + (order != null ? order.getOrderId() : "");
        return "Đơn hàng " + orderCode;
    }

    private String buildMessage(NotificationItem item) {
        if (item.getType() == NotificationItem.TYPE_CHAT) {
            ChatRoomItem room = item.getChatRoom();
            long unread = room != null ? room.getUnreadCount() : 0;
            String lastMessage = room != null && room.getLastMessage() != null && !room.getLastMessage().isEmpty()
                    ? room.getLastMessage()
                    : "Bạn có tin nhắn mới.";
            return unread > 0 ? unread + " tin nhắn chưa đọc: " + lastMessage : lastMessage;
        }

        Order order = item.getOrder();
        String status = order != null ? order.getStatus() : null;
        if ("PENDING".equals(status)) return "Đơn hàng đang chờ xác nhận.";
        if ("PROCESSING".equals(status)) return "Đơn hàng đang được xử lý.";
        if ("SHIPPED".equals(status)) return "Đơn hàng đang được giao.";
        if ("DELIVERED".equals(status)) return "Đơn hàng đã giao thành công.";
        if ("CANCELLED".equals(status)) return "Đơn hàng đã bị hủy.";
        return "Trạng thái đơn hàng: " + (status != null ? status : "Không xác định");
    }

    private String buildTime(NotificationItem item) {
        String value = item.getType() == NotificationItem.TYPE_CHAT
                ? item.getChatRoom().getLastMessageTime()
                : item.getOrder().getCreatedAt();
        if (value == null || value.isEmpty()) return "";
        return value.length() >= 10 ? value.substring(0, 10) : value;
    }

    private String resolveChatSenderName(ChatRoomItem room) {
        if (room == null) return "Hỗ trợ Shop";
        boolean roomUserIsCurrentUser = currentUserId != null
                && room.getUserId() != null
                && currentUserId.equals(room.getUserId());
        boolean roomNameIsCurrentUser = currentUserName != null
                && room.getUserName() != null
                && currentUserName.trim().equalsIgnoreCase(room.getUserName().trim());
        if (roomUserIsCurrentUser || roomNameIsCurrentUser) {
            return "Hỗ trợ Shop";
        }
        return room.getUserName() != null && !room.getUserName().isEmpty()
                ? room.getUserName()
                : "Hỗ trợ Shop";
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView message;
        TextView time;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_notification_title);
            message = itemView.findViewById(R.id.tv_notification_message);
            time = itemView.findViewById(R.id.tv_notification_time);
        }
    }
}
