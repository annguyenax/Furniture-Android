package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.AdminReviewItem;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminApi;
import com.furniture.app.util.SessionManager;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReviewListActivity extends AppCompatActivity {

    private AdminApi adminApi;
    private RecyclerView rvReviews;
    private ProgressBar progressBar;
    private View emptyState;
    private final List<AdminReviewItem> items = new ArrayList<>();
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review_list);

        SessionManager sm = new SessionManager(this);
        adminApi = RetrofitClient.getInstance(sm.getToken()).create(AdminApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvReviews    = findViewById(R.id.rv_reviews);
        progressBar  = findViewById(R.id.progress_bar);
        emptyState   = findViewById(R.id.empty_state);

        adapter = new ReviewAdapter(items, this::confirmDelete);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);

        loadReviews(0);
    }

    private void loadReviews(int page) {
        progressBar.setVisibility(View.VISIBLE);
        adminApi.getReviews(page, 50).enqueue(new Callback<ApiResponse<PageResponse<AdminReviewItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<AdminReviewItem>>> call,
                                   Response<ApiResponse<PageResponse<AdminReviewItem>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    List<AdminReviewItem> content = response.body().getData().getContent();
                    items.clear();
                    if (content != null) items.addAll(content);
                    adapter.notifyDataSetChanged();
                    emptyState.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                    rvReviews.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<AdminReviewItem>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminReviewListActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(AdminReviewItem item, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Xóa đánh giá của " + item.getUserName() + " về \"" + item.getProductName() + "\"?")
                .setPositiveButton("Xóa", (d, w) -> deleteReview(item, position))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteReview(AdminReviewItem item, int position) {
        adminApi.deleteReview(item.getReviewId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    items.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (items.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        rvReviews.setVisibility(View.GONE);
                    }
                    Toast.makeText(AdminReviewListActivity.this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(AdminReviewListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    interface OnDeleteListener { void onDelete(AdminReviewItem item, int position); }

    static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {
        private final List<AdminReviewItem> list;
        private final OnDeleteListener deleteListener;

        ReviewAdapter(List<AdminReviewItem> list, OnDeleteListener dl) {
            this.list = list; this.deleteListener = dl;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_review, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            AdminReviewItem item = list.get(position);
            h.tvProductName.setText(item.getProductName() != null ? item.getProductName() : "?");
            String user = item.getUserName() != null ? item.getUserName() : "?";
            if (item.getUserEmail() != null && !item.getUserEmail().isEmpty()) {
                user += " - " + item.getUserEmail();
            }
            h.tvUserName.setText(user);
            h.ratingBar.setRating(item.getRating() != null ? item.getRating() : 0);
            if (item.getComment() != null && !item.getComment().isEmpty()) {
                h.tvComment.setText(item.getComment());
                h.tvComment.setVisibility(View.VISIBLE);
            } else {
                h.tvComment.setVisibility(View.GONE);
            }
            if (item.getCreatedAt() != null && item.getCreatedAt().length() >= 10) {
                h.tvDate.setText(item.getCreatedAt().substring(0, 10));
            }
            if (Boolean.TRUE.equals(item.getIsVerified())) {
                h.tvVerified.setText("Da xac minh mua hang");
                h.tvVerified.setVisibility(View.VISIBLE);
            } else {
                h.tvVerified.setVisibility(View.GONE);
            }
            String image = firstImage(item.getImages());
            if (image != null) {
                h.ivReviewImage.setVisibility(View.VISIBLE);
                Glide.with(h.itemView.getContext())
                        .load(image)
                        .placeholder(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(h.ivReviewImage);
            } else {
                h.ivReviewImage.setVisibility(View.GONE);
            }
            h.ivDelete.setOnClickListener(v -> deleteListener.onDelete(item, h.getAdapterPosition()));
        }

        private String firstImage(String images) {
            if (images == null || images.trim().isEmpty()) return null;
            String first = images.split(",")[0].trim();
            return first.isEmpty() ? null : first;
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvProductName, tvUserName, tvComment, tvDate, tvVerified;
            RatingBar ratingBar;
            ImageView ivDelete, ivReviewImage;

            VH(View v) {
                super(v);
                tvProductName = v.findViewById(R.id.tv_product_name);
                tvUserName    = v.findViewById(R.id.tv_user_name);
                tvComment     = v.findViewById(R.id.tv_comment);
                tvDate        = v.findViewById(R.id.tv_date);
                tvVerified    = v.findViewById(R.id.tv_verified);
                ratingBar     = v.findViewById(R.id.rating_bar);
                ivDelete      = v.findViewById(R.id.iv_delete);
                ivReviewImage = v.findViewById(R.id.iv_review_image);
            }
        }
    }
}
