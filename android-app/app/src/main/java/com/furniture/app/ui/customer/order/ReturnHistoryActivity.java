package com.furniture.app.ui.customer.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.ReturnRequestItem;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReturnRequestApi;
import com.furniture.app.util.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnHistoryActivity extends AppCompatActivity {

    private ReturnRequestApi returnApi;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private View emptyState;
    private RecyclerView rvReturns;
    private ReturnHistoryAdapter adapter;
    private final List<ReturnRequestItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_history);

        SessionManager sm = new SessionManager(this);
        returnApi = RetrofitClient.getInstance(sm.getToken()).create(ReturnRequestApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        rvReturns = findViewById(R.id.rv_returns);

        adapter = new ReturnHistoryAdapter(items);
        rvReturns.setLayoutManager(new LinearLayoutManager(this));
        rvReturns.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadReturns);

        loadReturns();
    }

    private void loadReturns() {
        if (!swipeRefreshLayout.isRefreshing()) progressBar.setVisibility(View.VISIBLE);
        returnApi.getMyReturns(0, 50).enqueue(new Callback<ApiResponse<PageResponse<ReturnRequestItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<ReturnRequestItem>>> call,
                                    Response<ApiResponse<PageResponse<ReturnRequestItem>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
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
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(ReturnHistoryActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void updateEmptyState() {
        boolean empty = items.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvReturns.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ─── Adapter ──────────────────────────────────────────────────────────────

    static class ReturnHistoryAdapter extends RecyclerView.Adapter<ReturnHistoryAdapter.VH> {
        private final List<ReturnRequestItem> list;
        private final SimpleDateFormat inputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        private final SimpleDateFormat outputFmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        ReturnHistoryAdapter(List<ReturnRequestItem> list) {
            this.list = list;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_return_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ReturnRequestItem item = list.get(position);

            h.tvTitle.setText((item.getOrderCode() != null ? item.getOrderCode() : "#" + item.getOrderId())
                    + " - " + (item.getProductName() != null ? item.getProductName() : "Toàn bộ đơn hàng"));

            h.tvDate.setText(formatDate(item.getCreatedAt()));
            h.tvReason.setText(item.getReason() != null ? item.getReason() : "");

            h.tvStatus.setText(item.getStatusDisplay());
            h.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor(item.getStatus())));

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

            if ("REJECTED".equals(item.getStatus()) && item.getAdminNote() != null && !item.getAdminNote().isEmpty()) {
                h.tvAdminNote.setVisibility(View.VISIBLE);
                h.tvAdminNote.setText("Lý do từ chối: " + item.getAdminNote());
            } else {
                h.tvAdminNote.setVisibility(View.GONE);
            }
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

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvReason, tvStatus, tvEvidenceLabel, tvAdminNote;
            ImageView ivEvidence;

            VH(@NonNull View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_title);
                tvDate = v.findViewById(R.id.tv_date);
                tvReason = v.findViewById(R.id.tv_reason);
                tvStatus = v.findViewById(R.id.tv_status);
                tvEvidenceLabel = v.findViewById(R.id.tv_evidence_label);
                ivEvidence = v.findViewById(R.id.iv_evidence);
                tvAdminNote = v.findViewById(R.id.tv_admin_note);
            }
        }
    }
}
