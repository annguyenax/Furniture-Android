package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryListActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private ProgressBar progressBar;
    private View emptyState;
    private CategoryAdapter adapter;
    private List<Category> categories = new ArrayList<>();
    private CategoryApi categoryApi;
    private AdminCategoryApi adminCategoryApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category_list);

        SessionManager sessionManager = new SessionManager(this);
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);
        adminCategoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminCategoryApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Quản lý danh mục");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvCategories = findViewById(R.id.rv_categories);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CategoryAdapter(categories,
                category -> showEditDialog(category),
                category -> showDeleteConfirm(category));
        rvCategories.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showCreateDialog());

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
                    categories.clear();
                    categories.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    emptyState.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
                    rvCategories.setVisibility(categories.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminCategoryListActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCreateDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etName = new EditText(this);
        etName.setHint("Tên danh mục *");
        EditText etDesc = new EditText(this);
        etDesc.setHint("Mô tả");

        layout.addView(etName);
        layout.addView(etDesc);

        new AlertDialog.Builder(this)
                .setTitle("Thêm danh mục")
                .setView(layout)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String desc = etDesc.getText().toString().trim();
                    adminCategoryApi.createCategory(new AdminCategoryApi.CategoryRequest(name, desc, null))
                            .enqueue(new Callback<ApiResponse<Category>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Category>> call,
                                                       Response<ApiResponse<Category>> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                        Toast.makeText(AdminCategoryListActivity.this,
                                                "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
                                        loadCategories();
                                    } else {
                                        String msg = response.body() != null ? response.body().getMessage() : "Thêm thất bại";
                                        Toast.makeText(AdminCategoryListActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                                    Toast.makeText(AdminCategoryListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditDialog(Category category) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etName = new EditText(this);
        etName.setHint("Tên danh mục *");
        etName.setText(category.getCategoryName());
        EditText etDesc = new EditText(this);
        etDesc.setHint("Mô tả");
        etDesc.setText(category.getDescription() != null ? category.getDescription() : "");

        layout.addView(etName);
        layout.addView(etDesc);

        new AlertDialog.Builder(this)
                .setTitle("Sửa danh mục")
                .setView(layout)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Tên không được trống", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String desc = etDesc.getText().toString().trim();
                    adminCategoryApi.updateCategory(category.getCategoryId(),
                                    new AdminCategoryApi.CategoryRequest(name, desc, category.getImage()))
                            .enqueue(new Callback<ApiResponse<Category>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Category>> call,
                                                       Response<ApiResponse<Category>> response) {
                                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                        Toast.makeText(AdminCategoryListActivity.this,
                                                "Đã cập nhật danh mục", Toast.LENGTH_SHORT).show();
                                        loadCategories();
                                    } else {
                                        Toast.makeText(AdminCategoryListActivity.this,
                                                "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                                    Toast.makeText(AdminCategoryListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteConfirm(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Xóa danh mục \"" + category.getCategoryName() + "\"?\nCác sản phẩm thuộc danh mục này sẽ không còn được phân loại.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    adminCategoryApi.deleteCategory(category.getCategoryId())
                            .enqueue(new Callback<ApiResponse<Void>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<Void>> call,
                                                       Response<ApiResponse<Void>> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(AdminCategoryListActivity.this,
                                                "Đã xóa danh mục", Toast.LENGTH_SHORT).show();
                                        loadCategories();
                                    } else {
                                        Toast.makeText(AdminCategoryListActivity.this,
                                                "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                                    Toast.makeText(AdminCategoryListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    interface OnCategoryAction {
        void onAction(Category category);
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
