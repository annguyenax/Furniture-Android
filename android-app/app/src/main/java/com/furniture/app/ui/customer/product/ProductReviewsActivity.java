package com.furniture.app.ui.customer.product;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.ReviewModel;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReviewApi;
import com.furniture.app.ui.adapter.ReviewAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductReviewsActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_PRODUCT_NAME = "product_name";

    private static final int PAGE_SIZE = 20;

    private ReviewApi reviewApi;
    private RecyclerView rvReviews;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ReviewAdapter adapter;
    private final List<ReviewModel> reviews = new ArrayList<>();
    private int productId;
    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_reviews);

        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        reviewApi = RetrofitClient.getPublicRetrofit().create(ReviewApi.class);

        setupToolbar();
        setupReviews();

        if (productId == -1) {
            showEmpty("Không tìm thấy sản phẩm");
        } else {
            loadReviews(0);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        if (getSupportActionBar() != null && productName != null && !productName.isBlank()) {
            getSupportActionBar().setSubtitle(productName);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupReviews() {
        rvReviews = findViewById(R.id.rv_reviews);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvReviews.setLayoutManager(layoutManager);
        adapter = new ReviewAdapter(reviews);
        rvReviews.setAdapter(adapter);
        rvReviews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                if (!isLoading && !isLastPage && lastVisible >= reviews.size() - 4) {
                    loadReviews(currentPage + 1);
                }
            }
        });
    }

    private void loadReviews(int page) {
        isLoading = true;
        progressBar.setVisibility(reviews.isEmpty() ? View.VISIBLE : View.GONE);
        reviewApi.getProductReviews(productId, page, PAGE_SIZE)
                .enqueue(new Callback<ApiResponse<PageResponse<ReviewModel>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<ReviewModel>>> call,
                                           Response<ApiResponse<PageResponse<ReviewModel>>> response) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);
                        if (!response.isSuccessful() || response.body() == null
                                || response.body().getData() == null) {
                            showLoadError();
                            return;
                        }

                        PageResponse<ReviewModel> data = response.body().getData();
                        List<ReviewModel> content = data.getContent();
                        if (page == 0) reviews.clear();
                        if (content != null) reviews.addAll(content);
                        currentPage = data.getNumber();
                        isLastPage = data.isLast();
                        adapter.notifyDataSetChanged();

                        tvEmpty.setVisibility(reviews.isEmpty() ? View.VISIBLE : View.GONE);
                        rvReviews.setVisibility(reviews.isEmpty() ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<ReviewModel>>> call, Throwable t) {
                        isLoading = false;
                        progressBar.setVisibility(View.GONE);
                        showLoadError();
                    }
                });
    }

    private void showLoadError() {
        if (reviews.isEmpty()) {
            showEmpty("Chưa tải được đánh giá");
        } else {
            Toast.makeText(this, "Chưa tải được thêm đánh giá", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmpty(String message) {
        progressBar.setVisibility(View.GONE);
        tvEmpty.setText(message);
        tvEmpty.setVisibility(View.VISIBLE);
        rvReviews.setVisibility(View.GONE);
    }
}
