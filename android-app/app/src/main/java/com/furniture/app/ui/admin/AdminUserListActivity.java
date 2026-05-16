package com.furniture.app.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.furniture.app.R;
import com.furniture.app.data.model.AdminUser;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminUserApi;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserListActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private TextView tvEmpty, tvTotalCount;
    private TextInputEditText etSearch;
    private UserAdapter adapter;
    private final List<AdminUser> users = new ArrayList<>();
    private AdminUserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_list);

        SessionManager sessionManager = new SessionManager(this);
        userApi = RetrofitClient.getInstance(sessionManager.getToken()).create(AdminUserApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvUsers = findViewById(R.id.rv_users);
        tvEmpty = findViewById(R.id.tv_empty);
        tvTotalCount = findViewById(R.id.tv_total_count);
        etSearch = findViewById(R.id.et_search);

        adapter = new UserAdapter(users, this::confirmToggle);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvUsers.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadUsers("");
    }

    private void loadUsers(String search) {
        userApi.getUsers(0, 50, search.isEmpty() ? null : search)
                .enqueue(new Callback<ApiResponse<AdminUserApi.PagedUsers>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AdminUserApi.PagedUsers>> call,
                                           Response<ApiResponse<AdminUserApi.PagedUsers>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            AdminUserApi.PagedUsers paged = response.body().getData();
                            users.clear();
                            if (paged.getContent() != null) users.addAll(paged.getContent());
                            adapter.notifyDataSetChanged();
                            boolean empty = users.isEmpty();
                            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                            rvUsers.setVisibility(empty ? View.GONE : View.VISIBLE);
                            tvTotalCount.setText("Tổng: " + paged.getTotalElements() + " người dùng");
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<AdminUserApi.PagedUsers>> call, Throwable t) {
                        Toast.makeText(AdminUserListActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmToggle(AdminUser user) {
        boolean willBan = user.isActive();
        String action = willBan ? "khóa" : "mở khóa";
        new AlertDialog.Builder(this)
                .setTitle((willBan ? "Khóa" : "Mở khóa") + " tài khoản")
                .setMessage("Bạn có chắc muốn " + action + " tài khoản của " + user.getDisplayName() + "?")
                .setPositiveButton("Xác nhận", (d, w) -> toggleStatus(user))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void toggleStatus(AdminUser user) {
        String newStatus = user.isActive() ? "banned" : "active";
        userApi.updateStatus(user.getUserId(), new AdminUserApi.UpdateStatusRequest(newStatus))
                .enqueue(new Callback<ApiResponse<AdminUser>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AdminUser>> call, Response<ApiResponse<AdminUser>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String msg = "active".equals(newStatus) ? "Đã mở khóa tài khoản" : "Đã khóa tài khoản";
                            Toast.makeText(AdminUserListActivity.this, msg, Toast.LENGTH_SHORT).show();
                            loadUsers(etSearch.getText() != null ? etSearch.getText().toString() : "");
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<AdminUser>> call, Throwable t) {
                        Toast.makeText(AdminUserListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    interface OnToggleListener { void onToggle(AdminUser user); }

    static class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {
        private final List<AdminUser> list;
        private final OnToggleListener listener;

        UserAdapter(List<AdminUser> list, OnToggleListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            AdminUser u = list.get(position);
            h.tvName.setText(u.getDisplayName());
            h.tvEmail.setText(u.getEmail() != null ? u.getEmail() : "");
            h.tvPhone.setText(u.getPhone() != null ? "📞 " + u.getPhone() : "");

            String initial = u.getDisplayName().isEmpty() ? "U"
                    : String.valueOf(u.getDisplayName().charAt(0)).toUpperCase();
            h.tvAvatar.setText(initial);

            if (u.isActive()) {
                h.tvStatus.setText("Hoạt động");
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_active);
                h.btnToggle.setText("Khóa");
                h.btnToggle.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFFF44336));
            } else {
                h.tvStatus.setText("Đã khóa");
                h.tvStatus.setBackgroundResource(R.drawable.bg_status_banned);
                h.btnToggle.setText("Mở khóa");
                h.btnToggle.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            }

            h.btnToggle.setOnClickListener(v -> listener.onToggle(u));
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvAvatar, tvName, tvEmail, tvPhone, tvStatus;
            MaterialButton btnToggle;

            VH(@NonNull View v) {
                super(v);
                tvAvatar = v.findViewById(R.id.tv_avatar);
                tvName = v.findViewById(R.id.tv_name);
                tvEmail = v.findViewById(R.id.tv_email);
                tvPhone = v.findViewById(R.id.tv_phone);
                tvStatus = v.findViewById(R.id.tv_status_badge);
                btnToggle = v.findViewById(R.id.btn_toggle_status);
            }
        }
    }
}
