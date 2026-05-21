package com.furniture.app.ui.customer.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.WishlistItem;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.WishlistApi;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.util.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends AppCompatActivity {

    private WishlistApi wishlistApi;
    private RecyclerView rvWishlist;
    private ProgressBar progressBar;
    private View emptyState;
    private final List<WishlistItem> items = new ArrayList<>();
    private WishlistAdapter adapter;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        SessionManager sm = new SessionManager(this);
        wishlistApi = RetrofitClient.getInstance(sm.getToken()).create(WishlistApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvWishlist = findViewById(R.id.rv_wishlist);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);

        adapter = new WishlistAdapter(items, this::openProduct, this::removeItem);
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));
        rvWishlist.setAdapter(adapter);

        loadWishlist();
    }

    private void loadWishlist() {
        progressBar.setVisibility(View.VISIBLE);
        wishlistApi.getWishlist().enqueue(new Callback<ApiResponse<List<WishlistItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<WishlistItem>>> call,
                                   Response<ApiResponse<List<WishlistItem>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    items.clear();
                    items.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                    rvWishlist.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<WishlistItem>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WishlistActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                showEmpty();
            }
        });
    }

    private void removeItem(WishlistItem item, int position) {
        wishlistApi.removeFromWishlist(item.getProductId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                items.remove(position);
                adapter.notifyItemRemoved(position);
                if (items.isEmpty()) showEmpty();
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(WishlistActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openProduct(WishlistItem item) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, item.getProductId());
        startActivity(intent);
    }

    private void showEmpty() {
        emptyState.setVisibility(View.VISIBLE);
        rvWishlist.setVisibility(View.GONE);
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    interface OnRemoveListener { void onRemove(WishlistItem item, int position); }
    interface OnClickListener { void onClick(WishlistItem item); }

    class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.VH> {
        private final List<WishlistItem> list;
        private final OnClickListener clickListener;
        private final OnRemoveListener removeListener;

        WishlistAdapter(List<WishlistItem> list, OnClickListener cl, OnRemoveListener rl) {
            this.list = list; this.clickListener = cl; this.removeListener = rl;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_wishlist, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            WishlistItem item = list.get(position);
            h.tvName.setText(item.getProductName());
            if (item.getPrice() != null) {
                h.tvPrice.setText(String.format("₫%s", currencyFormat.format(item.getPrice())));
            }
            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(h.ivImage.getContext()).load(item.getProductImage())
                        .placeholder(android.R.color.darker_gray).into(h.ivImage);
            }
            h.itemView.setOnClickListener(v -> clickListener.onClick(item));
            h.ivRemove.setOnClickListener(v -> removeListener.onRemove(item, h.getAdapterPosition()));
            h.btnBuyNow.setOnClickListener(v -> clickListener.onClick(item));
        }

        @Override public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivImage, ivRemove;
            TextView tvName, tvPrice;
            com.google.android.material.button.MaterialButton btnBuyNow;
            VH(View v) {
                super(v);
                ivImage = v.findViewById(R.id.iv_product_image);
                ivRemove = v.findViewById(R.id.iv_remove);
                tvName = v.findViewById(R.id.tv_product_name);
                tvPrice = v.findViewById(R.id.tv_price);
                btnBuyNow = v.findViewById(R.id.btn_buy_now);
            }
        }
    }
}
