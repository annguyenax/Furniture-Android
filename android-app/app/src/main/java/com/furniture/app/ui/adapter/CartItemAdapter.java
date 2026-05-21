package com.furniture.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.CartItem;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private final List<CartItem> cartItems;
    private final OnCartItemListener listener;
    private final NumberFormat currencyFormat;
    private OnSelectionChangedListener selectionChangedListener;

    public interface OnCartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    public CartItemAdapter(List<CartItem> cartItems, OnCartItemListener listener, NumberFormat currencyFormat) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.currencyFormat = currencyFormat;
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener l) {
        this.selectionChangedListener = l;
    }

    public void selectAll(boolean selected) {
        for (CartItem item : cartItems) {
            item.setSelected(selected);
        }
        notifyDataSetChanged();
        if (selectionChangedListener != null) selectionChangedListener.onSelectionChanged();
    }

    public List<CartItem> getSelectedItems() {
        List<CartItem> result = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) result.add(item);
        }
        return result;
    }

    public boolean areAllSelected() {
        if (cartItems.isEmpty()) return false;
        for (CartItem item : cartItems) {
            if (!item.isSelected()) return false;
        }
        return true;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item, listener, currencyFormat, selectionChangedListener);
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox cbSelect;
        private final ImageView productImage;
        private final TextView productName;
        private final TextView variantName;
        private final TextView priceText;
        private final TextView quantityText;
        private final ImageButton btnDecrease;
        private final ImageButton btnIncrease;
        private final ImageButton btnRemove;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cb_select);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            variantName = itemView.findViewById(R.id.variant_name);
            priceText = itemView.findViewById(R.id.price_text);
            quantityText = itemView.findViewById(R.id.quantity_text);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(CartItem item, OnCartItemListener listener, NumberFormat currencyFormat,
                         OnSelectionChangedListener selectionChangedListener) {
            productName.setText(item.getProductName());

            if (item.getVariantName() != null && !item.getVariantName().isEmpty()) {
                variantName.setText(item.getVariantName());
                variantName.setVisibility(View.VISIBLE);
            } else {
                variantName.setVisibility(View.GONE);
            }

            BigDecimal price = item.getPrice();
            if (price != null) {
                priceText.setText(String.format("₫%s", currencyFormat.format(price)));
            }

            quantityText.setText(String.valueOf(item.getQuantity()));

            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getProductImage())
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.placeholder_product);
            }

            // Prevent recursive listener trigger when rebinding
            cbSelect.setOnCheckedChangeListener(null);
            cbSelect.setChecked(item.isSelected());
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
                if (selectionChangedListener != null) selectionChangedListener.onSelectionChanged();
            });

            btnDecrease.setOnClickListener(v -> {
                int currentQty = item.getQuantity();
                if (currentQty > 1) {
                    listener.onQuantityChanged(item, currentQty - 1);
                }
            });

            btnIncrease.setOnClickListener(v -> {
                int currentQty = item.getQuantity();
                int maxStock = item.getStock() != null ? item.getStock() : 99;
                if (currentQty < maxStock) {
                    listener.onQuantityChanged(item, currentQty + 1);
                }
            });

            btnRemove.setOnClickListener(v -> listener.onRemoveItem(item));
        }
    }
}
