package com.furniture.app.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RelatedProductAdapter extends RecyclerView.Adapter<RelatedProductAdapter.ViewHolder> {

    private final List<Product> products;
    private final OnProductClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public RelatedProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_related_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvOriginalPrice;
        private final TextView tvDiscount;
        private final TextView tvSold;
        private final RatingBar ratingBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_related_product);
            tvName = itemView.findViewById(R.id.tv_related_name);
            tvPrice = itemView.findViewById(R.id.tv_related_price);
            tvOriginalPrice = itemView.findViewById(R.id.tv_related_original_price);
            tvDiscount = itemView.findViewById(R.id.tv_related_discount);
            tvSold = itemView.findViewById(R.id.tv_related_sold);
            ratingBar = itemView.findViewById(R.id.rb_related_rating);
        }

        void bind(Product product) {
            tvName.setText(product.getProductName());

            BigDecimal price = product.getLowestPrice();
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                tvPrice.setText(String.format("%s đ", currencyFormat.format(price)));
            } else {
                tvPrice.setText("Lien he");
            }

            boolean hasDiscount = product.getDiscount() != null
                    && product.getDiscount().compareTo(BigDecimal.ZERO) > 0;
            if (hasDiscount) {
                tvDiscount.setVisibility(View.VISIBLE);
                tvDiscount.setText(String.format("-%d%%", product.getDiscount().intValue()));
            } else {
                tvDiscount.setVisibility(View.GONE);
            }

            BigDecimal originalPrice = getOriginalPrice(product, price, hasDiscount);
            if (originalPrice != null) {
                tvOriginalPrice.setVisibility(View.VISIBLE);
                    tvOriginalPrice.setText(String.format("%s đ", currencyFormat.format(originalPrice)));
                tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvOriginalPrice.setVisibility(View.GONE);
            }

            if (product.getAverageRating() != null) {
                ratingBar.setRating(product.getAverageRating().floatValue());
                ratingBar.setVisibility(View.VISIBLE);
            } else {
                ratingBar.setVisibility(View.GONE);
            }
            tvSold.setText(String.format("\u0110\u00e3 b\u00e1n %d", product.getSold()));

            String imageUrl = product.getFirstImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.placeholder_product);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }

        private BigDecimal getOriginalPrice(Product product, BigDecimal salePrice, boolean hasDiscount) {
            if (!hasDiscount || salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }
            BigDecimal basePrice = product.getBasePrice();
            if (basePrice != null && basePrice.compareTo(salePrice) > 0) {
                return basePrice;
            }
            BigDecimal remainingPercent = BigDecimal.valueOf(100).subtract(product.getDiscount());
            if (remainingPercent.compareTo(BigDecimal.ZERO) <= 0) {
                return null;
            }
            BigDecimal original = salePrice
                    .multiply(BigDecimal.valueOf(100))
                    .divide(remainingPercent, 0, RoundingMode.HALF_UP);
            return original
                    .divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(1000));
        }
    }
}
