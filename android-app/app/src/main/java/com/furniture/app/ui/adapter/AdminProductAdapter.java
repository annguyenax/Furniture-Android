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
import com.furniture.app.data.model.Product;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ViewHolder> {

    private final List<Product> products;
    private final OnProductActionListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public interface OnProductActionListener {
        void onEditProduct(Product product);
        void onDeleteProduct(Product product);
        void onToggleStatus(Product product);
    }

    public AdminProductAdapter(List<Product> products, OnProductActionListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getProductName());
        holder.tvStock.setText("Kho: " + product.getStock() + "  •  Đã bán: " + product.getSold());
        holder.tvCategory.setText(product.getCategoryName() != null ? product.getCategoryName() : "");

        String price = product.getLowestPrice() != null
                ? "₫" + currencyFormat.format(product.getLowestPrice()) : "Chưa có giá";
        holder.tvPrice.setText(price);

        String status = product.getStatus();
        boolean active = "ACTIVE".equals(status);
        holder.tvStatus.setText(active ? "Đang bán" : "Ngừng bán");
        holder.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                active ? 0xFF4CAF50 : 0xFF9E9E9E));

        String imageUrl = product.getFirstImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEditProduct(product));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteProduct(product));
        holder.tvStatus.setOnClickListener(v -> listener.onToggleStatus(product));
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvCategory, tvStock, tvPrice, tvStatus;
        MaterialButton btnEdit, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_product_image);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvStock = itemView.findViewById(R.id.tv_stock);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
