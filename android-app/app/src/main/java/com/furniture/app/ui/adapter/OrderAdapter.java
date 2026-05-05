package com.furniture.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.Order;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final OnOrderClickListener listener;
    private final NumberFormat currencyFormat;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onCancelOrder(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position), listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvOrderCode;
        private final TextView tvOrderDate;
        private final TextView tvStatus;
        private final TextView tvItemCount;
        private final TextView tvTotalAmount;
        private final MaterialButton btnCancel;
        private final MaterialButton btnViewDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
        }

        public void bind(Order order, OnOrderClickListener listener, NumberFormat currencyFormat) {
            tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getOrderId());
            tvOrderDate.setText(order.getCreatedAt() != null ? order.getCreatedAt().substring(0, 10) : "");
            tvStatus.setText(order.getStatusDisplay());

            // Set status color
            String status = order.getStatus();
            if ("DELIVERED".equals(status)) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else if ("CANCELLED".equals(status)) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else if ("SHIPPED".equals(status)) {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }

            int itemCount = order.getItems() != null ? order.getItems().size() : 0;
            tvItemCount.setText(String.format("%d sản phẩm", itemCount));

            BigDecimal total = order.getTotalAmount();
            if (total != null) {
                tvTotalAmount.setText(String.format("₫%s", currencyFormat.format(total)));
            }

            // Show cancel button only for pending orders
            if ("PENDING".equals(status)) {
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
            }

            btnCancel.setOnClickListener(v -> listener.onCancelOrder(order));
            btnViewDetail.setOnClickListener(v -> listener.onOrderClick(order));
            itemView.setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
}
