package com.furniture.app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.CartItem;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder> {

    private final List<CartItem> items;
    private final NumberFormat currencyFormat;

    public CheckoutItemAdapter(List<CartItem> items, NumberFormat currencyFormat) {
        this.items = items;
        this.currencyFormat = currencyFormat;
    }

    @NonNull
    @Override
    public CheckoutItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_product, parent, false);
        return new CheckoutItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemViewHolder holder, int position) {
        holder.bind(items.get(position), currencyFormat);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class CheckoutItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView variantName;
        private final TextView priceText;
        private final TextView quantityText;

        public CheckoutItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            variantName = itemView.findViewById(R.id.variant_name);
            priceText = itemView.findViewById(R.id.price_text);
            quantityText = itemView.findViewById(R.id.quantity_text);
        }

        public void bind(CartItem item, NumberFormat currencyFormat) {
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

            quantityText.setText(String.format("x%d", item.getQuantity()));

            // Load image
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
        }
    }
}
