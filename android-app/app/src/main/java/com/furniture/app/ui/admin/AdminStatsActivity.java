package com.furniture.app.ui.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.AdminStatsResponse;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AdminApi;
import com.furniture.app.util.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;

import java.math.BigDecimal;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminStatsActivity extends AppCompatActivity {

    private AdminApi adminApi;
    private ProgressBar progressBar;
    private TextView tvTotalRevenue, tvTotalOrders;
    private BarChart barChartRevenue;
    private HorizontalBarChart barChartProducts, barChartCategories;
    private final NumberFormat currencyFmt = NumberFormat.getInstance(new Locale("vi", "VN"));
    private AdminStatsResponse latestStats;
    private String currentPeriod = "day";

    private final ActivityResultLauncher<Intent> createCsvLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null
                        && result.getData().getData() != null) {
                    writeCsv(result.getData().getData());
                }
            });

    private static final String[] PERIODS = {"day", "month", "year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stats);

        SessionManager sm = new SessionManager(this);
        adminApi = RetrofitClient.getInstance(sm.getToken()).create(AdminApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        progressBar     = findViewById(R.id.progress_bar);
        tvTotalRevenue  = findViewById(R.id.tv_total_revenue);
        tvTotalOrders   = findViewById(R.id.tv_total_orders);
        barChartRevenue = findViewById(R.id.bar_chart_revenue);
        barChartProducts   = findViewById(R.id.bar_chart_products);
        barChartCategories = findViewById(R.id.bar_chart_categories);
        findViewById(R.id.btn_export_csv).setOnClickListener(v -> exportCsv());

        setupRevenueChart(barChartRevenue);
        setupHorizontalChart(barChartProducts);
        setupHorizontalChart(barChartCategories);

        TabLayout tabLayout = findViewById(R.id.tab_period);
        tabLayout.addTab(tabLayout.newTab().setText("30 ngày"));
        tabLayout.addTab(tabLayout.newTab().setText("12 tháng"));
        tabLayout.addTab(tabLayout.newTab().setText("Theo năm"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                loadStats(PERIODS[tab.getPosition()]);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadStats("day");
    }

    private void loadStats(String period) {
        currentPeriod = period;
        progressBar.setVisibility(View.VISIBLE);
        adminApi.getStats(period).enqueue(new Callback<ApiResponse<AdminStatsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AdminStatsResponse>> call,
                                   Response<ApiResponse<AdminStatsResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    bindStats(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AdminStatsResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminStatsActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindStats(AdminStatsResponse data) {
        latestStats = data;
        BigDecimal revenue = data.getTotalRevenue() != null ? data.getTotalRevenue() : BigDecimal.ZERO;
        tvTotalRevenue.setText("₫" + currencyFmt.format(revenue));
        tvTotalOrders.setText(data.getTotalOrders() + " đơn");

        updateRevenueChart(data.getRevenueData());
        updateHorizontalChart(barChartProducts, data.getTopProducts());
        updateHorizontalChart(barChartCategories, data.getByCategory());
    }

    private void exportCsv() {
        if (latestStats == null) {
            Toast.makeText(this, "Chua co du lieu de xuat", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "revenue_stats_" + currentPeriod + ".csv");
        createCsvLauncher.launch(intent);
    }

    private void writeCsv(Uri uri) {
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            if (out == null) throw new IllegalStateException("Cannot open output");
            String csv = buildCsv(latestStats);
            out.write(csv.getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, "Da xuat file CSV", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Khong the xuat file", Toast.LENGTH_SHORT).show();
        }
    }

    private String buildCsv(AdminStatsResponse data) {
        StringBuilder sb = new StringBuilder();
        sb.append("section,label,value\n");
        sb.append("summary,total_revenue,").append(valueOf(data.getTotalRevenue())).append('\n');
        sb.append("summary,total_orders,").append(data.getTotalOrders()).append('\n');
        appendPoints(sb, "revenue_" + currentPeriod, data.getRevenueData());
        appendPoints(sb, "top_products", data.getTopProducts());
        appendPoints(sb, "categories", data.getByCategory());
        return sb.toString();
    }

    private void appendPoints(StringBuilder sb, String section, List<AdminStatsResponse.DataPoint> points) {
        if (points == null) return;
        for (AdminStatsResponse.DataPoint p : points) {
            sb.append(section).append(',')
                    .append(csvEscape(p.getLabel())).append(',')
                    .append(valueOf(p.getValue())).append('\n');
        }
    }

    private String valueOf(BigDecimal value) {
        return value != null ? value.toPlainString() : "0";
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void setupRevenueChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(9f);
        xAxis.setLabelRotationAngle(-45f);

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float value) {
                if (value >= 1_000_000) return String.format("%.0fM", value / 1_000_000);
                if (value >= 1_000) return String.format("%.0fK", value / 1_000);
                return String.format("%.0f", value);
            }
        });
        chart.getAxisLeft().setDrawGridLines(true);
    }

    private void setupHorizontalChart(HorizontalBarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setPinchZoom(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float value) {
                if (value >= 1_000_000) return String.format("%.0fM", value / 1_000_000);
                if (value >= 1_000) return String.format("%.0fK", value / 1_000);
                return String.format("%.0f", value);
            }
        });
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setTextSize(9f);
    }

    private void updateRevenueChart(List<AdminStatsResponse.DataPoint> data) {
        if (data == null || data.isEmpty()) {
            barChartRevenue.clear();
            barChartRevenue.invalidate();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            float val = data.get(i).getValue() != null ? data.get(i).getValue().floatValue() : 0f;
            entries.add(new BarEntry(i, val));
            labels.add(data.get(i).getLabel());
        }

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(getResources().getColor(R.color.primary));
        set.setDrawValues(false);

        BarData barData = new BarData(set);
        barData.setBarWidth(0.6f);

        barChartRevenue.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChartRevenue.getXAxis().setLabelCount(Math.min(labels.size(), 8));
        barChartRevenue.setData(barData);
        barChartRevenue.animateY(600);
    }

    private void updateHorizontalChart(HorizontalBarChart chart,
                                        List<AdminStatsResponse.DataPoint> data) {
        if (data == null || data.isEmpty()) {
            chart.clear();
            chart.invalidate();
            return;
        }

        // Reverse so best item appears at top
        List<AdminStatsResponse.DataPoint> reversed = new ArrayList<>(data);
        Collections.reverse(reversed);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < reversed.size(); i++) {
            float val = reversed.get(i).getValue() != null ? reversed.get(i).getValue().floatValue() : 0f;
            entries.add(new BarEntry(i, val));
            String label = reversed.get(i).getLabel();
            labels.add(label.length() > 18 ? label.substring(0, 18) + "…" : label);
        }

        BarDataSet set = new BarDataSet(entries, "");
        set.setColor(0xFF4CAF50);
        set.setDrawValues(false);

        BarData barData = new BarData(set);
        barData.setBarWidth(0.6f);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(labels.size());
        chart.setData(barData);
        chart.animateY(600);
    }
}
