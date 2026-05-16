package com.furniture.app.ui.customer.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ProductApi;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductsActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY = "category";

    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private View emptyState;
    private ProductAdapter adapter;
    private List<Product> products = new ArrayList<>();
    private ProductApi productApi;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_products);

        SessionManager sessionManager = new SessionManager(this);
        productApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ProductApi.class);

        category = (Category) getIntent().getSerializableExtra(EXTRA_CATEGORY);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category != null ? category.getCategoryName() : "Sản phẩm");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(products, this::onProductClick);
        rvProducts.setAdapter(adapter);

        if (category != null) {
            loadProducts(category.getCategoryId());
        }
    }

    private void loadProducts(int categoryId) {
        progressBar.setVisibility(View.VISIBLE);
        productApi.getProductsByCategory(categoryId, 0, 50)
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                           Response<ApiResponse<PageResponse<Product>>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            List<Product> data = response.body().getData().getContent();
                            products.clear();
                            products.addAll(data);
                            adapter.setProducts(new ArrayList<>(products));
                            emptyState.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
                            rvProducts.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CategoryProductsActivity.this, "Lỗi tải sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
