package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.AdminReviewItem;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminApi;
import com.furniture.app.util.SessionManager;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReviewListActivity extends AppCompatActivity {

    private AdminApi adminApi;
    private RecyclerView rvReviews;
    private ProgressBar progressBar;
    private View emptyState;
    private TextInputEditText etProductFilter;
    private ChipGroup chipGroupRating, chipGroupTime;
    private TextView tvResultSummary;
    private final List<AdminReviewItem> items = new ArrayList<>();
    private ReviewAdapter adapter;
    private final Handler filterHandler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_review_list);

        SessionManager sm = new SessionManager(this);
        adminApi = RetrofitClient.getInstance(sm.getToken()).create(AdminApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvReviews = findViewById(R.id.rv_reviews);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        etProductFilter = findViewById(R.id.et_product_filter);
        chipGroupRating = findViewById(R.id.chip_group_rating);
        chipGroupTime = findViewById(R.id.chip_group_time);
        tvResultSummary = findViewById(R.id.tv_result_summary);

        adapter = new ReviewAdapter(items, this::confirmDelete, this::showReviewDetail);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);

        setupFilters();
        loadReviews(0);
    }

    private void setupFilters() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                scheduleFilterReload();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        etProductFilter.addTextChangedListener(watcher);
        chipGroupRating.setOnCheckedStateChangeListener((group, checkedIds) -> scheduleFilterReload());
        chipGroupTime.setOnCheckedStateChangeListener((group, checkedIds) -> scheduleFilterReload());
    }

    private void scheduleFilterReload() {
        filterHandler.removeCallbacksAndMessages(null);
        filterHandler.postDelayed(() -> loadReviews(0), 350);
    }

    private void loadReviews(int page) {
        progressBar.setVisibility(View.VISIBLE);
        adminApi.getReviews(
                page,
                50,
                null,
                clean(etProductFilter.getText().toString()),
                selectedRating(),
                selectedFromDate(),
                null
        ).enqueue(new Callback<ApiResponse<PageResponse<AdminReviewItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<AdminReviewItem>>> call,
                                   Response<ApiResponse<PageResponse<AdminReviewItem>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    PageResponse<AdminReviewItem> pageData = response.body().getData();
                    List<AdminReviewItem> content = pageData.getContent();
                    items.clear();
                    if (content != null) items.addAll(content);
                    adapter.notifyDataSetChanged();
                    tvResultSummary.setText(pageData.getTotalElements() + " đánh giá phù hợp");
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

    private String clean(String value) {
        String trimmed = value != null ? value.trim() : "";
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer selectedRating() {
        int id = chipGroupRating.getCheckedChipId();
        if (id == R.id.chip_rating_5) return 5;
        if (id == R.id.chip_rating_4) return 4;
        if (id == R.id.chip_rating_3) return 3;
        if (id == R.id.chip_rating_2) return 2;
        if (id == R.id.chip_rating_1) return 1;
        return null;
    }

    private String selectedFromDate() {
        int id = chipGroupTime.getCheckedChipId();
        if (id == R.id.chip_time_all) return null;
        Calendar calendar = Calendar.getInstance();
        if (id == R.id.chip_time_7) {
            calendar.add(Calendar.DAY_OF_YEAR, -7);
        } else if (id == R.id.chip_time_30) {
            calendar.add(Calendar.DAY_OF_YEAR, -30);
        } else if (id == R.id.chip_time_year) {
            calendar.set(Calendar.DAY_OF_YEAR, 1);
        }
        return dateFormat.format(calendar.getTime());
    }

    private void confirmDelete(AdminReviewItem item, int position) {
        if (position == RecyclerView.NO_POSITION) return;
        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Xóa đánh giá của " + item.getUserName() + " về \"" + item.getProductName() + "\"?")
                .setPositiveButton("Xóa", (d, w) -> deleteReview(item))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteReview(AdminReviewItem item) {
        adminApi.deleteReview(item.getReviewId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    loadReviews(0);
                    Toast.makeText(AdminReviewListActivity.this, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(AdminReviewListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReviewDetail(AdminReviewItem item) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_review_detail, null);
        TextView tvProductName = view.findViewById(R.id.tv_product_name);
        TextView tvUserName = view.findViewById(R.id.tv_user_name);
        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvVerified = view.findViewById(R.id.tv_verified);
        TextView tvComment = view.findViewById(R.id.tv_comment);
        RatingBar ratingBar = view.findViewById(R.id.rating_bar);
        View imagesScroll = view.findViewById(R.id.images_scroll);
        LinearLayout llImages = view.findViewById(R.id.ll_images);

        tvProductName.setText(item.getProductName() != null ? item.getProductName() : "?");
        tvUserName.setText(item.getUserName() != null ? item.getUserName() : "?");
        tvDate.setText(item.getCreatedAt() != null && item.getCreatedAt().length() >= 10
                ? item.getCreatedAt().substring(0, 10) : "");
        ratingBar.setRating(item.getRating() != null ? item.getRating() : 0);
        tvComment.setText(item.getComment() != null && !item.getComment().isEmpty()
                ? item.getComment() : "Không có nội dung đánh giá");
        if (Boolean.TRUE.equals(item.getIsVerified())) {
            tvVerified.setText("Đã xác minh mua hàng");
            tvVerified.setVisibility(View.VISIBLE);
        }

        List<String> images = imageList(item.getImages());
        if (images.isEmpty()) {
            imagesScroll.setVisibility(View.GONE);
        } else {
            for (String image : images) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(120), dp(120));
                params.setMarginEnd(dp(8));
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setBackgroundColor(0xFFF2F2F2);
                Glide.with(this)
                        .load(image)
                        .placeholder(R.drawable.placeholder_product)
                        .centerCrop()
                        .into(imageView);
                llImages.addView(imageView);
            }
        }

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private List<String> imageList(String images) {
        List<String> result = new ArrayList<>();
        if (images == null || images.trim().isEmpty()) return result;
        for (String image : images.split(",")) {
            String trimmed = image.trim();
            if (!trimmed.isEmpty()) result.add(trimmed);
        }
        return result;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    interface OnDeleteListener { void onDelete(AdminReviewItem item, int position); }
    interface OnReviewClickListener { void onClick(AdminReviewItem item); }

    static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VH> {
        private final List<AdminReviewItem> list;
        private final OnDeleteListener deleteListener;
        private final OnReviewClickListener clickListener;

        ReviewAdapter(List<AdminReviewItem> list, OnDeleteListener dl, OnReviewClickListener cl) {
            this.list = list;
            this.deleteListener = dl;
            this.clickListener = cl;
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
            h.tvUserName.setText(item.getUserName() != null ? item.getUserName() : "?");
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
                h.tvVerified.setText("Đã xác minh mua hàng");
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
                h.ivReviewImage.setVisibility(View.VISIBLE);
                h.ivReviewImage.setImageResource(R.drawable.placeholder_product);
            }
            h.ivDelete.setOnClickListener(v -> deleteListener.onDelete(item, h.getAdapterPosition()));
            h.itemView.setOnClickListener(v -> clickListener.onClick(item));
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
                tvUserName = v.findViewById(R.id.tv_user_name);
                tvComment = v.findViewById(R.id.tv_comment);
                tvDate = v.findViewById(R.id.tv_date);
                tvVerified = v.findViewById(R.id.tv_verified);
                ratingBar = v.findViewById(R.id.rating_bar);
                ivDelete = v.findViewById(R.id.iv_delete);
                ivReviewImage = v.findViewById(R.id.iv_review_image);
            }
        }
    }
}
