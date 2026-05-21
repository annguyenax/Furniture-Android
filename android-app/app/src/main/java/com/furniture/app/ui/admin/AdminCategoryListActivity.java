package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminCategoryApi;
import com.furniture.app.data.remote.api.CategoryApi;
import com.furniture.app.data.remote.api.MediaApi;
import com.furniture.app.util.LoadingDialog;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryListActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private ProgressBar progressBar;
    private View emptyState;
    private CategoryAdapter adapter;
    private final List<Category> categories = new ArrayList<>();
    private final List<Category> allCategories = new ArrayList<>();
    private CategoryApi categoryApi;
    private AdminCategoryApi adminCategoryApi;
    private MediaApi mediaApi;
    private EditText etSearchCategory;
    private EditText activeImageInput;

    private final ActivityResultLauncher<String> categoryImagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && activeImageInput != null) uploadCategoryImage(uri, activeImageInput);
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_list);

        SessionManager sessionManager = new SessionManager(this);
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);
        adminCategoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminCategoryApi.class);
        mediaApi = RetrofitClient.getInstance(sessionManager.getToken()).create(MediaApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Quan ly danh muc");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvCategories = findViewById(R.id.rv_categories);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        etSearchCategory = findViewById(R.id.et_search_category);

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categories, this::showEditDialog, this::showDeleteConfirm);
        rvCategories.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showCreateDialog());

        etSearchCategory.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyCategoryFilter(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadCategories();
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        categoryApi.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call,
                                   Response<ApiResponse<List<Category>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    allCategories.clear();
                    allCategories.addAll(response.body().getData());
                    applyCategoryFilter();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCategoryListActivity.this, "Loi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String normalizeVi(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase().replace("đ", "d").replace("Đ", "d");
        String nfd = java.text.Normalizer.normalize(lower, java.text.Normalizer.Form.NFD);
        return nfd.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }

    private void applyCategoryFilter() {
        String keyword = etSearchCategory != null && etSearchCategory.getText() != null
                ? normalizeVi(etSearchCategory.getText().toString().trim())
                : "";
        categories.clear();
        for (Category category : allCategories) {
            boolean match = keyword.isEmpty()
                    || normalizeVi(category.getCategoryName()).contains(keyword)
                    || normalizeVi(category.getDescription()).contains(keyword);
            if (match) categories.add(category);
        }
        adapter.notifyDataSetChanged();
        emptyState.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
        rvCategories.setVisibility(categories.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showCreateDialog() {
        LinearLayout layout = buildCategoryForm(null, null, null);
        EditText etName = layout.findViewWithTag("name");
        EditText etDesc = layout.findViewWithTag("desc");
        EditText etImage = layout.findViewWithTag("image");

        new AlertDialog.Builder(this)
                .setTitle("Them danh muc")
                .setView(layout)
                .setPositiveButton("Them", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Vui long nhap ten danh muc", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String desc = etDesc.getText().toString().trim();
                    String image = etImage.getText().toString().trim();
                    LoadingDialog loading = LoadingDialog.show(this, "Đang thêm danh mục...");
                    adminCategoryApi.createCategory(new AdminCategoryApi.CategoryRequest(name, desc, image.isEmpty() ? null : image))
                            .enqueue(new SaveCategoryCallback("Da them danh muc", "Them that bai", loading));
                })
                .setNegativeButton("Huy", null)
                .show();
    }

    private void showEditDialog(Category category) {
        LinearLayout layout = buildCategoryForm(
                category.getCategoryName(),
                category.getDescription(),
                category.getImage());
        EditText etName = layout.findViewWithTag("name");
        EditText etDesc = layout.findViewWithTag("desc");
        EditText etImage = layout.findViewWithTag("image");

        new AlertDialog.Builder(this)
                .setTitle("Sua danh muc")
                .setView(layout)
                .setPositiveButton("Luu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Ten khong duoc trong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String desc = etDesc.getText().toString().trim();
                    String image = etImage.getText().toString().trim();
                    LoadingDialog loading = LoadingDialog.show(this, "Đang lưu danh mục...");
                    adminCategoryApi.updateCategory(category.getCategoryId(),
                                    new AdminCategoryApi.CategoryRequest(name, desc, image.isEmpty() ? null : image))
                            .enqueue(new SaveCategoryCallback("Da cap nhat danh muc", "Cap nhat that bai", loading));
                })
                .setNegativeButton("Huy", null)
                .show();
    }

    private LinearLayout buildCategoryForm(String name, String desc, String image) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etName = new EditText(this);
        etName.setTag("name");
        etName.setHint("Ten danh muc *");
        etName.setText(name != null ? name : "");

        EditText etDesc = new EditText(this);
        etDesc.setTag("desc");
        etDesc.setHint("Mo ta");
        etDesc.setText(desc != null ? desc : "");

        EditText etImage = new EditText(this);
        etImage.setTag("image");
        etImage.setHint("URL hinh anh danh muc");
        etImage.setText(image != null ? image : "");
        MaterialButton btnUpload = new MaterialButton(this);
        btnUpload.setText("Chon anh tu may");
        btnUpload.setOnClickListener(v -> {
            activeImageInput = etImage;
            categoryImagePicker.launch("image/*");
        });

        layout.addView(etName);
        layout.addView(etDesc);
        layout.addView(etImage);
        layout.addView(btnUpload);
        return layout;
    }

    private void uploadCategoryImage(Uri uri, EditText target) {
        try {
            File file = copyUriToCache(uri);
            RequestBody body = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
            LoadingDialog loading = LoadingDialog.show(this, "Đang upload ảnh danh mục...");
            mediaApi.uploadImage(part, "categories").enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    loading.dismiss();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        target.setText(response.body().getData());
                        Toast.makeText(AdminCategoryListActivity.this, "Da upload anh", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AdminCategoryListActivity.this,
                                "Upload anh that bai. Kiem tra Cloudinary.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    loading.dismiss();
                    Toast.makeText(AdminCategoryListActivity.this, "Loi ket noi khi upload anh", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Khong the doc anh da chon", Toast.LENGTH_SHORT).show();
        }
    }

    private File copyUriToCache(Uri uri) throws Exception {
        File dir = new File(getCacheDir(), "category_uploads");
        if (!dir.exists()) dir.mkdirs();
        File file = File.createTempFile("category_", ".jpg", dir);
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

    private void showDeleteConfirm(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xoa danh muc")
                .setMessage("Xoa danh muc \"" + category.getCategoryName() + "\"?")
                .setPositiveButton("Xoa", (dialog, which) -> {
                    LoadingDialog loading = LoadingDialog.show(this, "Đang xóa danh mục...");
                    adminCategoryApi.deleteCategory(category.getCategoryId())
                            .enqueue(new Callback<ApiResponse<Void>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Void>> call,
                                                       Response<ApiResponse<Void>> response) {
                                    loading.dismiss();
                                    if (response.isSuccessful()) {
                                        Toast.makeText(AdminCategoryListActivity.this,
                                                "Da xoa danh muc", Toast.LENGTH_SHORT).show();
                                        loadCategories();
                                    } else {
                                        String msg = "Xoa that bai";
                                        try {
                                            if (response.errorBody() != null) {
                                                String err = response.errorBody().string();
                                                if (err.contains("san pham") || err.contains("sản phẩm")) {
                                                    msg = "Danh muc dang co san pham, khong the xoa";
                                                }
                                            }
                                        } catch (Exception ignored) {}
                                        Toast.makeText(AdminCategoryListActivity.this, msg, Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                                    loading.dismiss();
                                    Toast.makeText(AdminCategoryListActivity.this, "Loi ket noi", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Huy", null)
                .show();
    }

    interface OnCategoryAction {
        void onAction(Category category);
    }

    private class SaveCategoryCallback implements Callback<ApiResponse<Category>> {
        private final String successMessage;
        private final String fallbackError;
        private final LoadingDialog loading;

        SaveCategoryCallback(String successMessage, String fallbackError, LoadingDialog loading) {
            this.successMessage = successMessage;
            this.fallbackError = fallbackError;
            this.loading = loading;
        }

        @Override
        public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
            loading.dismiss();
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                Toast.makeText(AdminCategoryListActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                loadCategories();
            } else {
                String msg = response.body() != null ? response.body().getMessage() : fallbackError;
                Toast.makeText(AdminCategoryListActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
            loading.dismiss();
            Toast.makeText(AdminCategoryListActivity.this, "Loi ket noi", Toast.LENGTH_SHORT).show();
        }
    }

    static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private final List<Category> categories;
        private final OnCategoryAction onEdit;
        private final OnCategoryAction onDelete;

        CategoryAdapter(List<Category> categories, OnCategoryAction onEdit, OnCategoryAction onDelete) {
            this.categories = categories;
            this.onEdit = onEdit;
            this.onDelete = onDelete;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Category cat = categories.get(position);
            holder.tvName.setText(cat.getCategoryName());
            holder.tvDesc.setText(cat.getDescription() != null ? cat.getDescription() : "");
            Glide.with(holder.itemView.getContext())
                    .load(cat.getImage())
                    .placeholder(R.drawable.placeholder_product)
                    .centerCrop()
                    .into(holder.ivImage);
            holder.btnEdit.setOnClickListener(v -> onEdit.onAction(cat));
            holder.btnDelete.setOnClickListener(v -> onDelete.onAction(cat));
        }

        @Override
        public int getItemCount() { return categories != null ? categories.size() : 0; }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;
            TextView tvName, tvDesc;
            MaterialButton btnEdit, btnDelete;

            ViewHolder(View v) {
                super(v);
                ivImage = v.findViewById(R.id.iv_category);
                tvName = v.findViewById(R.id.tv_category_name);
                tvDesc = v.findViewById(R.id.tv_description);
                btnEdit = v.findViewById(R.id.btn_edit_category);
                btnDelete = v.findViewById(R.id.btn_delete_category);
            }
        }
    }
}
