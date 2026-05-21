package com.furniture.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AlertDialog;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminProductApi;
import com.furniture.app.data.remote.api.ProductApi;
import com.furniture.app.ui.adapter.AdminProductAdapter;
import com.furniture.app.util.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductListActivity extends AppCompatActivity implements AdminProductAdapter.OnProductActionListener {

    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private View emptyState;
    private AdminProductAdapter adapter;
    private List<Product> products = new ArrayList<>();
    private final List<Product> allProducts = new ArrayList<>();
    private ProductApi productApi;
    private AdminProductApi adminProductApi;
    private EditText etSearchProduct;
    private Spinner spinnerStatusFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_list);

        SessionManager sessionManager = new SessionManager(this);
        productApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ProductApi.class);
        adminProductApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminProductApi.class);

        initViews();
        loadProducts();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý sản phẩm");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        etSearchProduct = findViewById(R.id.et_search_product);
        spinnerStatusFilter = findViewById(R.id.spinner_status_filter);

        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminProductAdapter(products, this);
        rvProducts.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AdminProductEditActivity.class)));

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Tat ca trang thai", "ACTIVE", "INACTIVE"});
        spinnerStatusFilter.setAdapter(statusAdapter);
        spinnerStatusFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        productApi.getAllProducts(0, 100, "createdAt", "DESC")
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                           Response<ApiResponse<PageResponse<Product>>> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            products.clear();
                            allProducts.clear();
                            allProducts.addAll(response.body().getData().getContent());
                            applyFilters();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AdminProductListActivity.this,
                                "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilters() {
        String keyword = etSearchProduct != null && etSearchProduct.getText() != null
                ? etSearchProduct.getText().toString().trim().toLowerCase()
                : "";
        String status = spinnerStatusFilter != null && spinnerStatusFilter.getSelectedItem() != null
                ? spinnerStatusFilter.getSelectedItem().toString()
                : "Tat ca trang thai";

        products.clear();
        for (Product product : allProducts) {
            boolean matchText = keyword.isEmpty()
                    || (product.getProductName() != null && product.getProductName().toLowerCase().contains(keyword))
                    || (product.getCategoryName() != null && product.getCategoryName().toLowerCase().contains(keyword));
            boolean matchStatus = "Tat ca trang thai".equals(status)
                    || (product.getStatus() != null && product.getStatus().equalsIgnoreCase(status));
            if (matchText && matchStatus) products.add(product);
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(products.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEditProduct(Product product) {
        Intent intent = new Intent(this, AdminProductEditActivity.class);
        intent.putExtra(AdminProductEditActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    @Override
    public void onDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa \"" + product.getProductName() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct(product))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Product product) {
        adminProductApi.deleteProduct(product.getProductId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AdminProductListActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    loadProducts();
                } else {
                    Toast.makeText(AdminProductListActivity.this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(AdminProductListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}
