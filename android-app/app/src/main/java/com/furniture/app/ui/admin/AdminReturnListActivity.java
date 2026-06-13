package com.furniture.app.ui.admin;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.ReturnRequestItem;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReturnRequestApi;
import com.furniture.app.util.LoadingDialog;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReturnListActivity extends AppCompatActivity {

    private ReturnRequestApi returnApi;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private RecyclerView rvReturns;
    private ReturnAdapter adapter;
    private final List<ReturnRequestItem> items = new ArrayList<>();
    private String currentStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_return_list);

        SessionManager sm = new SessionManager(this);
        returnApi = RetrofitClient.getInstance(sm.getToken()).create(ReturnRequestApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        rvReturns = findViewById(R.id.rv_returns);
        adapter = new ReturnAdapter(items, this::confirmUpdate);
        rvReturns.setLayoutManager(new LinearLayoutManager(this));
        rvReturns.setAdapter(adapter);

        setupChips();
        loadReturns();
    }

    private void setupChips() {
        ChipGroup chipGroup = findViewById(R.id.chip_group_status);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all) currentStatus = null;
            else if (id == R.id.chip_pending) currentStatus = "PENDING";
            else if (id == R.id.chip_approved) currentStatus = "APPROVED";
            else if (id == R.id.chip_rejected) currentStatus = "REJECTED";
            loadReturns();
        });
    }

    private void loadReturns() {
        progressBar.setVisibility(View.VISIBLE);
        returnApi.getAdminReturns(currentStatus, 0, 100).enqueue(new Callback<ApiResponse<PageResponse<ReturnRequestItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<ReturnRequestItem>>> call,
                                   Response<ApiResponse<PageResponse<ReturnRequestItem>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    items.clear();
                    List<ReturnRequestItem> content = response.body().getData().getContent();
                    if (content != null) items.addAll(content);
                    adapter.notifyDataSetChanged();
                }
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<ReturnRequestItem>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminReturnListActivity.this, "Lỗi tải yêu cầu hoàn trả", Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        boolean empty = items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvReturns.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void confirmUpdate(ReturnRequestItem item, String status) {
        if ("REJECTED".equals(status)) {
            TextInputLayout noteLayout = new TextInputLayout(this);
            noteLayout.setHint("Lý do từ chối (tùy chọn)");
            noteLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

            TextInputEditText noteInput = new TextInputEditText(noteLayout.getContext());
            noteInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            noteInput.setMinLines(2);
            noteLayout.addView(noteInput);

            int margin = (int) (18 * getResources().getDisplayMetrics().density);
            int topMargin = (int) (8 * getResources().getDisplayMetrics().density);
            noteLayout.setPadding(margin, topMargin, margin, 0);

            new AlertDialog.Builder(this)
                    .setTitle("Từ chối hoàn trả")
                    .setMessage("Cập nhật yêu cầu #" + item.getReturnId() + "?")
                    .setView(noteLayout)
                    .setPositiveButton("Đồng ý", (d, w) -> {
                        String note = noteInput.getText() != null ? noteInput.getText().toString().trim() : "";
                        updateStatus(item, status, note.isEmpty() ? null : note);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận hoàn trả")
                    .setMessage("Cập nhật yêu cầu #" + item.getReturnId() + "?")
                    .setPositiveButton("Đồng ý", (d, w) -> updateStatus(item, status, null))
                    .setNegativeButton("Hủy", null)
                    .show();
        }
    }

    private void updateStatus(ReturnRequestItem item, String status, String adminNote) {
        LoadingDialog loading = LoadingDialog.show(this, "Đang cập nhật trạng thái...");
        returnApi.updateReturnStatus(item.getReturnId(), status, adminNote).enqueue(new Callback<ApiResponse<ReturnRequestItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReturnRequestItem>> call,
                                   Response<ApiResponse<ReturnRequestItem>> response) {
                loading.dismiss();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminReturnListActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                    loadReturns();
                } else {
                    String msg = response.body() != null && response.body().getMessage() != null
                            ? response.body().getMessage()
                            : "Cập nhật thất bại";
                    Toast.makeText(AdminReturnListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReturnRequestItem>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(AdminReturnListActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface OnStatusClick {
        void onClick(ReturnRequestItem item, String status);
    }

    static class ReturnAdapter extends RecyclerView.Adapter<ReturnAdapter.VH> {
        private final List<ReturnRequestItem> list;
        private final OnStatusClick listener;
        private final SimpleDateFormat inputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        private final SimpleDateFormat outputFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        ReturnAdapter(List<ReturnRequestItem> list, OnStatusClick listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admin_return, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ReturnRequestItem item = list.get(position);
            h.tvTitle.setText((item.getOrderCode() != null ? item.getOrderCode() : "#" + item.getOrderId())
                    + " - " + (item.getProductName() != null ? item.getProductName() : "Toàn bộ đơn hàng"));
            h.tvUser.setText((item.getUserName() != null ? item.getUserName() : "Khách hàng")
                    + (item.getUserEmail() != null ? " - " + item.getUserEmail() : ""));
            h.tvDate.setText(formatDate(item.getCreatedAt()));
            h.tvReason.setText(item.getReason() != null ? item.getReason() : "");

            h.tvStatus.setText(item.getStatusDisplay());
            h.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor(item.getStatus())));

            if ("REJECTED".equals(item.getStatus()) && item.getAdminNote() != null && !item.getAdminNote().isEmpty()) {
                h.tvAdminNote.setVisibility(View.VISIBLE);
                h.tvAdminNote.setText("Lý do từ chối: " + item.getAdminNote());
            } else {
                h.tvAdminNote.setVisibility(View.GONE);
            }

            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(h.itemView.getContext())
                        .load(item.getProductImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .into(h.ivProductImage);
            } else {
                h.ivProductImage.setImageResource(R.drawable.placeholder_product);
            }

            if (item.getEvidenceUrl() != null && !item.getEvidenceUrl().isEmpty()) {
                h.tvEvidenceLabel.setVisibility(View.VISIBLE);
                h.ivEvidence.setVisibility(View.VISIBLE);
                Glide.with(h.itemView.getContext())
                        .load(item.getEvidenceUrl())
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(h.ivEvidence);
                h.ivEvidence.setOnClickListener(v -> showImagePreview(h.itemView.getContext(), item.getEvidenceUrl()));
            } else {
                h.tvEvidenceLabel.setVisibility(View.GONE);
                h.ivEvidence.setVisibility(View.GONE);
            }

            boolean pending = "PENDING".equals(item.getStatus());
            h.layoutActions.setVisibility(pending ? View.VISIBLE : View.GONE);
            h.btnApprove.setOnClickListener(v -> listener.onClick(item, "APPROVED"));
            h.btnReject.setOnClickListener(v -> listener.onClick(item, "REJECTED"));
        }

        private String formatDate(String raw) {
            if (raw == null || raw.isEmpty()) return "";
            try {
                String trimmed = raw.replace("Z", "");
                if (trimmed.length() > 19) trimmed = trimmed.substring(0, 19);
                Date date = inputFmt.parse(trimmed);
                return date != null ? outputFmt.format(date) : raw;
            } catch (ParseException e) {
                return raw;
            }
        }

        private int statusColor(String status) {
            if ("APPROVED".equals(status)) return 0xFF4CAF50;
            if ("REJECTED".equals(status)) return 0xFFF44336;
            return 0xFFFF9800;
        }

        private void showImagePreview(android.content.Context context, String imageUrl) {
            Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.dialog_image_preview);
            ImageView ivPreview = dialog.findViewById(R.id.iv_preview);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivPreview);
            ivPreview.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvUser, tvDate, tvReason, tvStatus, tvAdminNote, tvEvidenceLabel;
            ImageView ivEvidence, ivProductImage;
            View layoutActions;
            MaterialButton btnApprove, btnReject;

            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_title);
                tvUser = v.findViewById(R.id.tv_user);
                tvDate = v.findViewById(R.id.tv_date);
                tvReason = v.findViewById(R.id.tv_reason);
                tvStatus = v.findViewById(R.id.tv_status);
                tvAdminNote = v.findViewById(R.id.tv_admin_note);
                tvEvidenceLabel = v.findViewById(R.id.tv_evidence_label);
                ivEvidence = v.findViewById(R.id.iv_evidence);
                ivProductImage = v.findViewById(R.id.iv_product_image);
                layoutActions = v.findViewById(R.id.layout_actions);
                btnApprove = v.findViewById(R.id.btn_approve);
                btnReject = v.findViewById(R.id.btn_reject);
            }
        }
    }
}
