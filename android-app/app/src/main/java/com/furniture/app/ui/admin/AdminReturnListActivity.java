package com.furniture.app.ui.admin;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminReturnListActivity extends AppCompatActivity {

    private ReturnRequestApi returnApi;
    private ProgressBar progressBar;
    private ReturnAdapter adapter;
    private final List<ReturnRequestItem> items = new ArrayList<>();

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
        RecyclerView rv = findViewById(R.id.rv_returns);
        adapter = new ReturnAdapter(items, this::confirmUpdate);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        loadReturns();
    }

    private void loadReturns() {
        progressBar.setVisibility(View.VISIBLE);
        returnApi.getAdminReturns(null, 0, 100).enqueue(new Callback<ApiResponse<PageResponse<ReturnRequestItem>>>() {
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
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<ReturnRequestItem>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminReturnListActivity.this, "Loi tai yeu cau hoan tra", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmUpdate(ReturnRequestItem item, String status) {
        String title = "APPROVED".equals(status) ? "Xac nhan hoan tra" : "Tu choi hoan tra";
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Cap nhat yeu cau #" + item.getReturnId() + "?")
                .setPositiveButton("Dong y", (d, w) -> updateStatus(item, status))
                .setNegativeButton("Huy", null)
                .show();
    }

    private void updateStatus(ReturnRequestItem item, String status) {
        LoadingDialog loading = LoadingDialog.show(this, "Đang cập nhật trạng thái...");
        returnApi.updateReturnStatus(item.getReturnId(), status, null).enqueue(new Callback<ApiResponse<ReturnRequestItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReturnRequestItem>> call,
                                   Response<ApiResponse<ReturnRequestItem>> response) {
                loading.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(AdminReturnListActivity.this, "Da cap nhat", Toast.LENGTH_SHORT).show();
                    loadReturns();
                } else {
                    Toast.makeText(AdminReturnListActivity.this, "Cap nhat that bai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReturnRequestItem>> call, Throwable t) {
                loading.dismiss();
                Toast.makeText(AdminReturnListActivity.this, "Loi ket noi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    interface OnStatusClick {
        void onClick(ReturnRequestItem item, String status);
    }

    static class ReturnAdapter extends RecyclerView.Adapter<ReturnAdapter.VH> {
        private final List<ReturnRequestItem> list;
        private final OnStatusClick listener;

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
                    + " - " + (item.getProductName() != null ? item.getProductName() : "Toan bo don hang"));
            h.tvUser.setText((item.getUserName() != null ? item.getUserName() : "Khach hang")
                    + (item.getUserEmail() != null ? " - " + item.getUserEmail() : ""));
            h.tvReason.setText(item.getReason() != null ? item.getReason() : "");
            h.tvStatus.setText(item.getStatusDisplay());

            if (item.getEvidenceUrl() != null && !item.getEvidenceUrl().isEmpty()) {
                h.tvEvidenceLabel.setVisibility(View.VISIBLE);
                h.ivEvidence.setVisibility(View.VISIBLE);
                Glide.with(h.itemView.getContext())
                        .load(item.getEvidenceUrl())
                        .centerCrop()
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(h.ivEvidence);
            } else {
                h.tvEvidenceLabel.setVisibility(View.GONE);
                h.ivEvidence.setVisibility(View.GONE);
            }

            boolean pending = "PENDING".equals(item.getStatus());
            h.layoutActions.setVisibility(pending ? View.VISIBLE : View.GONE);
            h.btnApprove.setOnClickListener(v -> listener.onClick(item, "APPROVED"));
            h.btnReject.setOnClickListener(v -> listener.onClick(item, "REJECTED"));
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvUser, tvReason, tvStatus, tvEvidenceLabel;
            ImageView ivEvidence;
            View layoutActions;
            MaterialButton btnApprove, btnReject;

            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_title);
                tvUser = v.findViewById(R.id.tv_user);
                tvReason = v.findViewById(R.id.tv_reason);
                tvStatus = v.findViewById(R.id.tv_status);
                tvEvidenceLabel = v.findViewById(R.id.tv_evidence_label);
                ivEvidence = v.findViewById(R.id.iv_evidence);
                layoutActions = v.findViewById(R.id.layout_actions);
                btnApprove = v.findViewById(R.id.btn_approve);
                btnReject = v.findViewById(R.id.btn_reject);
            }
        }
    }
}
