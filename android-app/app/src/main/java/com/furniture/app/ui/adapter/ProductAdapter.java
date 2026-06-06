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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private final OnProductClickListener listener;
    private final NumberFormat currencyFormat;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener, currencyFormat);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImageView;
        private final TextView productNameTextView;
        private final TextView priceTextView;
        private final TextView originalPriceTextView;
        private final TextView discountBadge;
        private final TextView soldTextView;
        private final RatingBar ratingBar;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.product_image_view);
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            originalPriceTextView = itemView.findViewById(R.id.original_price_text_view);
            discountBadge = itemView.findViewById(R.id.discount_badge);
            soldTextView = itemView.findViewById(R.id.sold_text_view);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }

        public void bind(Product product, OnProductClickListener listener, NumberFormat currencyFormat) {
            productNameTextView.setText(product.getProductName());
            // Format price
            BigDecimal price = product.getLowestPrice();
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                priceTextView.setText(String.format("\u20ab%s", currencyFormat.format(price)));
            } else {
                priceTextView.setText("Lien he");
            }

            boolean hasDiscount = product.getDiscount() != null
                    && product.getDiscount().compareTo(BigDecimal.ZERO) > 0;

            // Show original price when a discount is active.
            if (originalPriceTextView != null) {
                BigDecimal originalPrice = getOriginalPrice(product, price, hasDiscount);
                if (originalPrice != null) {
                    originalPriceTextView.setVisibility(View.VISIBLE);
                    originalPriceTextView.setText(String.format("\u20ab%s", currencyFormat.format(originalPrice)));
                    originalPriceTextView.setPaintFlags(
                            originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    originalPriceTextView.setVisibility(View.GONE);
                }
            }
            // Show discount badge
            if (discountBadge != null) {
                if (hasDiscount) {
                    discountBadge.setVisibility(View.VISIBLE);
                    discountBadge.setText(String.format("-%d%%", product.getDiscount().intValue()));
                } else {
                    discountBadge.setVisibility(View.GONE);
                }
            }

            // Show rating
            if (product.getAverageRating() != null) {
                ratingBar.setRating(product.getAverageRating().floatValue());
                ratingBar.setVisibility(View.VISIBLE);
            } else {
                ratingBar.setVisibility(View.GONE);
            }

            // Show sold count
            if (soldTextView != null) {
                soldTextView.setText(String.format("Đã bán %d", product.getSold()));
            }

            // Load image with Glide
            String imageUrl = product.getFirstImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(productImageView);
            } else {
                productImageView.setImageResource(R.drawable.placeholder_product);
            }

            // Click listener
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

            BigDecimal discount = product.getDiscount();
            BigDecimal remainingPercent = BigDecimal.valueOf(100).subtract(discount);
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
