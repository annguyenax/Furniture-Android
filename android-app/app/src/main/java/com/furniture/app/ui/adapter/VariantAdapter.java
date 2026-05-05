package com.furniture.app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ProductVariant;

import java.util.List;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    private final List<ProductVariant> variants;
    private final OnVariantClickListener listener;
    private int selectedPosition = 0;

    public interface OnVariantClickListener {
        void onVariantClick(ProductVariant variant);
    }

    public VariantAdapter(List<ProductVariant> variants, OnVariantClickListener listener) {
        this.variants = variants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_variant, parent, false);
        return new VariantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        ProductVariant variant = variants.get(position);
        holder.bind(variant, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onVariantClick(variant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants != null ? variants.size() : 0;
    }

    static class VariantViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvVariantName;
        private final View container;

        public VariantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVariantName = itemView.findViewById(R.id.tv_variant_name);
            container = itemView.findViewById(R.id.variant_container);
        }

        public void bind(ProductVariant variant, boolean isSelected) {
            String name = "";
            if (variant.getColor() != null && !variant.getColor().isEmpty()) {
                name = variant.getColor();
            }
            if (variant.getMaterial() != null && !variant.getMaterial().isEmpty()) {
                if (!name.isEmpty()) name += " - ";
                name += variant.getMaterial();
            }
            if (variant.getSize() != null && !variant.getSize().isEmpty()) {
                if (!name.isEmpty()) name += " - ";
                name += variant.getSize();
            }
            if (name.isEmpty()) {
                name = "Mặc định";
            }
            tvVariantName.setText(name);

            // Update selection state
            if (isSelected) {
                container.setBackgroundResource(R.drawable.bg_variant_selected);
                tvVariantName.setTextColor(Color.parseColor("#1976D2")); // Primary color
            } else {
                container.setBackgroundResource(R.drawable.bg_variant_normal);
                tvVariantName.setTextColor(Color.parseColor("#212121")); // Black
            }
        }
    }
}
