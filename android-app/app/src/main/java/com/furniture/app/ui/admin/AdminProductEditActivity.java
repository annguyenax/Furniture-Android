package com.furniture.app.ui.admin;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.model.ProductVariant;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminProductApi;
import com.furniture.app.data.remote.api.CategoryApi;
import com.furniture.app.data.remote.api.MediaApi;
import com.furniture.app.util.LoadingDialog;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductEditActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "product";

    private EditText etName, etDescription, etPrice, etStock, etDiscount, etImageUrl;
    private Spinner spinnerStatus, spinnerCategory;
    private TextInputLayout tilPrice;
    private MaterialButton btnSave, btnUploadImage, btnAddVariant;
    private LinearLayout layoutVariants, layoutVariantItems;
    private ProgressBar progressBar;
    private AdminProductApi adminProductApi;
    private CategoryApi categoryApi;
    private MediaApi mediaApi;
    private Product existingProduct;
    private List<Category> categories = new ArrayList<>();
    private EditText activeVariantImageInput;

    private final ActivityResultLauncher<String> productImagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) uploadProductImage(uri);
            });
    private final ActivityResultLauncher<String> variantImagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && activeVariantImageInput != null) uploadVariantImage(uri, activeVariantImageInput);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_edit);

        SessionManager sessionManager = new SessionManager(this);
        adminProductApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminProductApi.class);
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);
        mediaApi = RetrofitClient.getInstance(sessionManager.getToken()).create(MediaApi.class);

        initViews();
        loadCategories();

        existingProduct = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        boolean isEdit = existingProduct != null;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(isEdit ? "Chỉnh sửa sản phẩm" : "Thêm sản phẩm");
        }

        if (isEdit) {
            fillForm(existingProduct);
            switchToEditMode();
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
        etImageUrl = findViewById(R.id.et_image_url);
        btnSave = findViewById(R.id.btn_save);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        btnAddVariant = findViewById(R.id.btn_add_variant);
        layoutVariants = findViewById(R.id.layout_variants);
        layoutVariantItems = findViewById(R.id.layout_variant_items);
        progressBar = findViewById(R.id.progress_bar);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"ACTIVE", "INACTIVE"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        btnUploadImage.setOnClickListener(v -> productImagePicker.launch("image/*"));
        btnAddVariant.setOnClickListener(v -> showVariantDialog(null));
    }

    private void switchToEditMode() {
        tilPrice.setVisibility(View.GONE);
        layoutVariants.setVisibility(View.VISIBLE);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Chỉnh sửa sản phẩm");
        renderVariants();
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
        if (product.getVariants() != null && !product.getVariants().isEmpty()
                && product.getVariants().get(0).getImageUrl() != null) {
            etImageUrl.setText(product.getVariants().get(0).getImageUrl());
        }
        spinnerStatus.setSelection("ACTIVE".equals(product.getStatus()) ? 0 : 1);
    }

    private void renderVariants() {
        layoutVariantItems.removeAllViews();
        if (existingProduct == null || existingProduct.getVariants() == null || existingProduct.getVariants().isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Chưa có phân loại nào. Nhấn \"+ Thêm phân loại\" để tạo.");
            empty.setTextColor(0xFF757575);
            layoutVariantItems.addView(empty);
            return;
        }
        for (ProductVariant variant : existingProduct.getVariants()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(12, 10, 12, 10);
            row.setBackgroundResource(R.drawable.bg_search);

            TextView title = new TextView(this);
            title.setText(variant.getVariantInfo() == null || variant.getVariantInfo().isEmpty()
                    ? "Phân loại #" + variant.getVariantId() : variant.getVariantInfo());
            title.setTextColor(0xFF212121);
            title.setTextSize(14);
            title.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView meta = new TextView(this);
            meta.setText("Giá: ₫" + (variant.getPrice() != null ? variant.getPrice().toPlainString() : "0")
                    + " | Tồn: " + variant.getStock()
                    + (variant.getImageUrl() != null ? " | Có ảnh" : ""));
            meta.setTextColor(0xFF757575);
            meta.setTextSize(12);

            LinearLayout actions = new LinearLayout(this);
            actions.setOrientation(LinearLayout.HORIZONTAL);
            MaterialButton edit = new MaterialButton(this);
            edit.setText("Sửa");
            MaterialButton delete = new MaterialButton(this);
            delete.setText("Xóa");
            delete.setTextColor(0xFFE53935);
            actions.addView(edit, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            actions.addView(delete, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            row.addView(title);
            row.addView(meta);
            row.addView(actions);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 10);
            layoutVariantItems.addView(row, params);

            edit.setOnClickListener(v -> showVariantDialog(variant));
            delete.setOnClickListener(v -> confirmDeleteVariant(variant));
        }
    }

    private void showVariantDialog(ProductVariant variant) {
        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int px = (int) (16 * getResources().getDisplayMetrics().density);
        form.setPadding(px * 2, px, px * 2, px);

        EditText etColor = createDialogInput("Màu sắc (vd: Xanh, Nâu)", variant != null ? variant.getColor() : null);
        EditText etSize = createDialogInput("Kích thước (vd: S, M, L, 120x60cm)", variant != null ? variant.getSize() : null);
        EditText etMaterial = createDialogInput("Chất liệu (vd: Gỗ sồi, Vải nhung)", variant != null ? variant.getMaterial() : null);
        EditText etPrice = createDialogInput("Giá bán *", variant != null && variant.getPrice() != null ? variant.getPrice().toPlainString() : null);
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText etStock = createDialogInput("Số lượng tồn kho *", variant != null ? String.valueOf(variant.getStock()) : "0");
        etStock.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        EditText etImage = createDialogInput("URL ảnh phân loại (để trống nếu upload)", variant != null ? variant.getImageUrl() : null);
        MaterialButton btnUpload = new MaterialButton(this);
        btnUpload.setText("Chọn ảnh phân loại");
        btnUpload.setOnClickListener(v -> {
            activeVariantImageInput = etImage;
            variantImagePicker.launch("image/*");
        });

        form.addView(etColor);
        form.addView(etSize);
        form.addView(etMaterial);
        form.addView(etPrice);
        form.addView(etStock);
        form.addView(etImage);
        form.addView(btnUpload);
        scroll.addView(form);

        // Limit dialog height so it doesn't cover the full screen
        int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        scroll.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT, maxHeight));

        new AlertDialog.Builder(this)
                .setTitle(variant == null ? "Thêm phân loại" : "Sửa phân loại")
                .setView(scroll)
                .setPositiveButton("Lưu", (d, w) -> saveVariant(variant, etSize, etColor, etMaterial, etPrice, etStock, etImage))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private EditText createDialogInput(String hint, String value) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setSingleLine(true);
        editText.setText(value != null ? value : "");
        return editText;
    }

    private void saveVariant(ProductVariant variant, EditText etSize, EditText etColor, EditText etMaterial,
                             EditText etPrice, EditText etStock, EditText etImage) {
        String priceStr = etPrice.getText().toString().trim();
        String stockStr = etStock.getText().toString().trim();
        if (priceStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Giá và số lượng tồn kho không được trống", Toast.LENGTH_SHORT).show();
            return;
        }
        AdminProductApi.VariantRequest request = new AdminProductApi.VariantRequest(
                etSize.getText().toString().trim(),
                etColor.getText().toString().trim(),
                etMaterial.getText().toString().trim(),
                new BigDecimal(priceStr),
                Integer.parseInt(stockStr),
                etImage.getText().toString().trim().isEmpty() ? null : etImage.getText().toString().trim());

        Call<ApiResponse<ProductVariant>> call = variant == null
                ? adminProductApi.createVariant(existingProduct.getProductId(), request)
                : adminProductApi.updateVariant(variant.getVariantId(), request);
        LoadingDialog loading = LoadingDialog.show(this, variant == null ? "Đang thêm phân loại..." : "Đang lưu phân loại...");
        call.enqueue(new Callback<ApiResponse<ProductVariant>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductVariant>> call, Response<ApiResponse<ProductVariant>> response) {
                loading.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ProductVariant saved = response.body().getData();
                    if (existingProduct.getVariants() == null) {
                        existingProduct.setVariants(new ArrayList<>());
                    }
                    if (variant == null) {
                        existingProduct.getVariants().add(saved);
                    } else {
                        int idx = existingProduct.getVariants().indexOf(variant);
                        if (idx >= 0) existingProduct.getVariants().set(idx, saved);
                    }
                    renderVariants();
                    Toast.makeText(AdminProductEditActivity.this, "Đã lưu phân loại", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminProductEditActivity.this, "Lưu phân loại thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductVariant>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteVariant(ProductVariant variant) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa phân loại")
                .setMessage("Xóa phân loại này?")
                .setPositiveButton("Xóa", (d, w) -> deleteVariant(variant))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteVariant(ProductVariant variant) {
        LoadingDialog loading = LoadingDialog.show(this, "Đang xóa phân loại...");
        adminProductApi.deleteVariant(variant.getVariantId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                loading.dismiss();
                if (response.isSuccessful()) {
                    existingProduct.getVariants().remove(variant);
                    renderVariants();
                    Toast.makeText(AdminProductEditActivity.this, "Đã xóa phân loại", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminProductEditActivity.this, "Xóa phân loại thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
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
        String imageUrl = etImageUrl.getText().toString().trim();
        if (imageUrl.isEmpty()) imageUrl = null;

        Integer categoryId = null;
        int catPos = spinnerCategory.getSelectedItemPosition();
        if (catPos > 0 && catPos < categories.size() && categories.get(catPos) != null) {
            categoryId = categories.get(catPos).getCategoryId();
        }

        LoadingDialog saving = LoadingDialog.show(this, existingProduct != null ? "Đang cập nhật sản phẩm..." : "Đang tạo sản phẩm...");
        btnSave.setEnabled(false);

        if (existingProduct != null) {
            AdminProductApi.UpdateProductRequest request =
                    new AdminProductApi.UpdateProductRequest(name, description, stock, discount, status, imageUrl);
            adminProductApi.updateProduct(existingProduct.getProductId(), request)
                    .enqueue(new Callback<ApiResponse<Product>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Product>> call,
                                               Response<ApiResponse<Product>> response) {
                            saving.dismiss();
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
                            saving.dismiss();
                            btnSave.setEnabled(true);
                            Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String priceStr = etPrice.getText().toString().trim();
            if (priceStr.isEmpty()) {
                saving.dismiss();
                btnSave.setEnabled(true);
                Toast.makeText(this, "Vui lòng nhập giá bán", Toast.LENGTH_SHORT).show();
                return;
            }
            BigDecimal price = new BigDecimal(priceStr);
            AdminProductApi.CreateProductRequest request =
                    new AdminProductApi.CreateProductRequest(name, description, price, stock, discount, categoryId, imageUrl);
            adminProductApi.createProduct(request).enqueue(new Callback<ApiResponse<Product>>() {
                @Override
                public void onResponse(Call<ApiResponse<Product>> call,
                                       Response<ApiResponse<Product>> response) {
                    saving.dismiss();
                    btnSave.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        existingProduct = response.body().getData();
                        setResult(RESULT_OK);
                        switchToEditMode();
                        Toast.makeText(AdminProductEditActivity.this,
                                "Đã tạo sản phẩm! Thêm phân loại (variant) bên dưới.", Toast.LENGTH_LONG).show();
                    } else {
                        String msg = response.body() != null ? response.body().getMessage() : "Thêm sản phẩm thất bại";
                        Toast.makeText(AdminProductEditActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                    saving.dismiss();
                    btnSave.setEnabled(true);
                    Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadProductImage(Uri uri) {
        try {
            File file = copyUriToCache(uri);
            RequestBody body = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
            LoadingDialog loading = LoadingDialog.show(this, "Đang upload ảnh sản phẩm...");
            btnUploadImage.setEnabled(false);
            mediaApi.uploadImage(part, "products").enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    loading.dismiss();
                    btnUploadImage.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        etImageUrl.setText(response.body().getData());
                        Toast.makeText(AdminProductEditActivity.this, "Đã upload ảnh sản phẩm", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminProductEditActivity.this,
                                "Upload ảnh thất bại. Kiểm tra cấu hình Cloudinary.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    loading.dismiss();
                    btnUploadImage.setEnabled(true);
                    Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối khi upload ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Không thể đọc ảnh đã chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVariantImage(Uri uri, EditText target) {
        try {
            File file = copyUriToCache(uri);
            RequestBody body = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
            LoadingDialog loading = LoadingDialog.show(this, "Đang upload ảnh phân loại...");
            mediaApi.uploadImage(part, "product-variants").enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    loading.dismiss();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        target.setText(response.body().getData());
                        Toast.makeText(AdminProductEditActivity.this, "Đã upload ảnh phân loại", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminProductEditActivity.this,
                                "Upload ảnh thất bại. Kiểm tra cấu hình Cloudinary.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    loading.dismiss();
                    Toast.makeText(AdminProductEditActivity.this, "Lỗi kết nối khi upload ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Không thể đọc ảnh đã chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private File copyUriToCache(Uri uri) throws Exception {
        File dir = new File(getCacheDir(), "product_uploads");
        if (!dir.exists()) dir.mkdirs();
        File file = File.createTempFile("product_", ".jpg", dir);
        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(file)) {
            if (in == null) throw new IllegalStateException("Cannot open file");
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
        return file;
    }
}
