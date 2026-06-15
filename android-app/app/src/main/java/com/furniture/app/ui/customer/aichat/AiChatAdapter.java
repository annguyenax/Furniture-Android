package com.furniture.app.ui.customer.aichat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ChatbotMessage;
import com.furniture.app.data.model.ChatbotSuggestedAction;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class AiChatAdapter extends RecyclerView.Adapter<AiChatAdapter.MessageViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_BOT = 1;

    public interface OnSuggestedActionClickListener {
        void onSuggestedActionClick(ChatbotSuggestedAction action);
    }

    private final List<ChatbotMessage> messages = new ArrayList<>();
    private final OnSuggestedActionClickListener actionClickListener;

    public AiChatAdapter(OnSuggestedActionClickListener actionClickListener) {
        this.actionClickListener = actionClickListener;
    }

    public void setMessages(List<ChatbotMessage> newMessages) {
        messages.clear();
        if (newMessages != null) {
            messages.addAll(newMessages);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? TYPE_USER : TYPE_BOT;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = viewType == TYPE_USER ? R.layout.item_ai_chat_user : R.layout.item_ai_chat_bot;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position), actionClickListener);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMessage;
        private final RecyclerView rvProducts;
        private final ChipGroup chipGroupActions;

        MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_ai_message);
            rvProducts = viewType == TYPE_BOT ? (RecyclerView) itemView.findViewById(R.id.rv_chat_products) : null;
            chipGroupActions = viewType == TYPE_BOT ? (ChipGroup) itemView.findViewById(R.id.chip_group_actions) : null;
        }

        void bind(ChatbotMessage message, OnSuggestedActionClickListener actionClickListener) {
            tvMessage.setText(message.getContent());

            if (rvProducts == null) return;

            if (message.getProducts() != null && !message.getProducts().isEmpty()) {
                if (rvProducts.getLayoutManager() == null) {
                    rvProducts.setLayoutManager(new LinearLayoutManager(
                            itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                    rvProducts.setAdapter(new ChatProductAdapter());
                }
                ((ChatProductAdapter) rvProducts.getAdapter()).setProducts(message.getProducts());
                rvProducts.setVisibility(View.VISIBLE);
            } else {
                rvProducts.setVisibility(View.GONE);
            }

            bindActions(message, actionClickListener);
        }

        private void bindActions(ChatbotMessage message, OnSuggestedActionClickListener actionClickListener) {
            if (chipGroupActions == null) return;

            List<ChatbotSuggestedAction> actions = message.getSuggestedActions();
            if (actions == null || actions.isEmpty()) {
                chipGroupActions.setVisibility(View.GONE);
                return;
            }

            chipGroupActions.removeAllViews();
            for (ChatbotSuggestedAction action : actions) {
                Chip chip = new Chip(itemView.getContext());
                chip.setText(action.getLabel());
                chip.setCheckable(false);
                chip.setClickable(true);
                chip.setOnClickListener(v -> {
                    if (actionClickListener != null) {
                        actionClickListener.onSuggestedActionClick(action);
                    }
                });
                chipGroupActions.addView(chip);
            }
            chipGroupActions.setVisibility(View.VISIBLE);
        }
    }
}
