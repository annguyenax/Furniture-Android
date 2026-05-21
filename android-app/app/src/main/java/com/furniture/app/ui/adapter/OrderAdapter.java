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
import java.util.Set;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<Order> orders;
    private final OnOrderClickListener listener;
    private final NumberFormat currencyFormat;
    private final Set<Integer> reviewedOrderIds;
    private final Set<Integer> returnedOrderIds;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
        void onCancelOrder(Order order);
        void onReviewOrder(Order order);
        void onReturnOrder(Order order);
    }

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener,
                        Set<Integer> reviewedOrderIds, Set<Integer> returnedOrderIds) {
        this.orders = orders;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        this.reviewedOrderIds = reviewedOrderIds;
        this.returnedOrderIds = returnedOrderIds;
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
        holder.bind(orders.get(position), listener, currencyFormat, reviewedOrderIds, returnedOrderIds);
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
        private final MaterialButton btnReview;
        private final MaterialButton btnReturn;
        private final MaterialButton btnViewDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            btnReview = itemView.findViewById(R.id.btn_review);
            btnReturn = itemView.findViewById(R.id.btn_return);
            btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
        }

        public void bind(Order order, OnOrderClickListener listener, NumberFormat currencyFormat,
                         Set<Integer> reviewedOrderIds, Set<Integer> returnedOrderIds) {
            tvOrderCode.setText(order.getOrderCode() != null ? order.getOrderCode() : "#" + order.getOrderId());
            tvOrderDate.setText(order.getCreatedAt() != null ? order.getCreatedAt().substring(0, 10) : "");
            tvStatus.setText(order.getStatusDisplay());

            String status = order.getStatus();
            String returnStatus = order.getReturnStatus();
            if ("APPROVED".equals(returnStatus)) {
                tvStatus.setTextColor(0xFF9C27B0);
            } else if ("PENDING".equals(returnStatus)) {
                tvStatus.setTextColor(0xFFFF5722);
            } else if ("DELIVERED".equals(status)) {
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

            // Cancel: only for PENDING
            btnCancel.setVisibility("PENDING".equals(status) ? View.VISIBLE : View.GONE);
            btnCancel.setOnClickListener(v -> listener.onCancelOrder(order));

            // Đã hoàn hàng — ẩn review và return
            if ("APPROVED".equals(returnStatus)) {
                btnReview.setVisibility(View.GONE);
                if (btnReturn != null) btnReturn.setVisibility(View.GONE);
            } else if ("DELIVERED".equals(status)) {
                // Review: chỉ khi đã giao và chưa hoàn hàng
                btnReview.setVisibility(View.VISIBLE);
                boolean reviewed = reviewedOrderIds.contains(order.getOrderId());
                btnReview.setText(reviewed ? "Đã đánh giá" : "Đánh giá");
                btnReview.setEnabled(!reviewed);
                btnReview.setAlpha(reviewed ? 0.6f : 1f);
                btnReview.setOnClickListener(v -> {
                    if (!reviewed) listener.onReviewOrder(order);
                });

                // Return: chỉ khi chưa review
                if (btnReturn != null) {
                    boolean reviewed2 = reviewedOrderIds.contains(order.getOrderId());
                    boolean returned = returnedOrderIds.contains(order.getOrderId())
                            || "PENDING".equals(returnStatus);
                    if (reviewed2) {
                        btnReturn.setVisibility(View.GONE);
                    } else {
                        btnReturn.setVisibility(View.VISIBLE);
                        btnReturn.setText(returned ? "Đã yêu cầu hoàn hàng" : "Hoàn hàng");
                        btnReturn.setEnabled(!returned);
                        btnReturn.setAlpha(returned ? 0.6f : 1f);
                        btnReturn.setOnClickListener(v -> {
                            if (!returned) listener.onReturnOrder(order);
                        });
                    }
                }
            } else {
                btnReview.setVisibility(View.GONE);
                if (btnReturn != null) btnReturn.setVisibility(View.GONE);
            }

            btnViewDetail.setOnClickListener(v -> listener.onOrderClick(order));
            itemView.setOnClickListener(v -> listener.onOrderClick(order));
        }
    }
}
