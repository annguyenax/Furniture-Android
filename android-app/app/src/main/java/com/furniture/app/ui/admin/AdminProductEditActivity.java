package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminProductApi;
import com.furniture.app.data.remote.api.CategoryApi;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductEditActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "product";

    private EditText etName, etDescription, etPrice, etStock, etDiscount;
    private Spinner spinnerStatus, spinnerCategory;
    private TextInputLayout tilPrice;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private AdminProductApi adminProductApi;
    private CategoryApi categoryApi;
    private Product existingProduct;
    private List<Category> categories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_edit);

        SessionManager sessionManager = new SessionManager(this);
        adminProductApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminProductApi.class);
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);

        initViews();
        loadCategories();

        existingProduct = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        boolean isEdit = existingProduct != null;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(isEdit ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm");
        }

        if (isEdit) {
            fillForm(existingProduct);
            tilPrice.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etStock = findViewById(R.id.et_stock);
        etDiscount = findViewById(R.id.et_discount);
        tilPrice = findViewById(R.id.til_price);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerCategory = findViewById(R.id.spinner_category);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"ACTIVE", "INACTIVE"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void loadCategories() {
        categoryApi.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call,
                                   Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    categories.clear();
                    categories.add(null);
                    categories.addAll(response.body().getData());

                    List<String> names = new ArrayList<>();
                    names.add("-- Chọn danh mục --");
                    for (Category c : response.body().getData()) {
                        names.add(c.getCategoryName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminProductEditActivity.this,
                            android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);

                    if (existingProduct != null && existingProduct.getCategoryId() != null) {
                        for (int i = 1; i < categories.size(); i++) {
                            if (categories.get(i) != null &&
                                    categories.get(i).getCategoryId().equals(existingProduct.getCategoryId())) {
                                spinnerCategory.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {}
        });
    }

    private void fillForm(Product product) {
        etName.setText(product.getProductName());
        etDescription.setText(product.getDescription());
        etStock.setText(String.valueOf(product.getStock()));
        if (product.getDiscount() != null) {
            etDiscount.setText(product.getDiscount().toPlainString());
        }
        spinnerStatus.setSelection("ACTIVE".equals(product.getStatus()) ? 0 : 1);
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        if (stockStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số lượng tồn kho", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock = Integer.parseInt(stockStr);
        String discountStr = etDiscount.getText().toString().trim();
        BigDecimal discount = discountStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(discountStr);
        String status = spinnerStatus.getSelectedItem().toString();

        Integer categoryId = null;
        int catPos = spinnerCategory.getSelectedItemPosition();
        if (catPos > 0 && catPos < categories.size() && categories.get(catPos) != null) {
            categoryId = categories.get(catPos).getCategoryId();
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        if (existingProduct != null) {
            AdminProductApi.UpdateProductRequest request =
                    new AdminProductApi.UpdateProductRequest(name, description, stock, discount, status);
            adminProductApi.updateProduct(existingProduct.getProductId(), request)
                    .enqueue(new Callback<ApiResponse<Product>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Product>> call,
                                               Response<ApiResponse<Product>> response) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(AdminProductEditActivity.this,
                                        "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AdminProductEditActivity.this,
                                        "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String priceStr = etPrice.getText().toString().trim();
            if (priceStr.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                Toast.makeText(this, "Vui lòng nhập giá bán", Toast.LENGTH_SHORT).show();
                return;
            }
            BigDecimal price = new BigDecimal(priceStr);
            AdminProductApi.CreateProductRequest request =
                    new AdminProductApi.CreateProductRequest(name, description, price, stock, discount, categoryId);
            adminProductApi.createProduct(request).enqueue(new Callback<ApiResponse<Product>>() {
                @Override
                public void onResponse(Call<ApiResponse<Product>> call,
                                       Response<ApiResponse<Product>> response) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(AdminProductEditActivity.this,
                                "Đã thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String msg = response.body() != null ? response.body().getMessage() : "Thêm sản phẩm thất bại";
                        Toast.makeText(AdminProductEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
