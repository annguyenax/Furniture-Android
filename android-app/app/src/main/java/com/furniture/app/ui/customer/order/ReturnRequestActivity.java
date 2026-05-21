package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ReturnRequestItem;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReturnRequestApi;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnRequestActivity extends AppCompatActivity {

    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_ORDER_ITEM_ID = "order_item_id";

    private ReturnRequestApi returnApi;
    private TextInputEditText etReason;
    private TextView tvOrderInfo, tvFileName;
    private MaterialButton btnPickFile, btnSubmit;
    private ProgressBar progressBar;
    private Uri selectedFileUri;
    private int orderId;
    private int orderItemId;

    private final ActivityResultLauncher<String> filePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    tvFileName.setText(getDisplayName(uri));
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_request);

        orderId = getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
        orderItemId = getIntent().getIntExtra(EXTRA_ORDER_ITEM_ID, -1);
        if (orderId == -1) {
            finish();
            return;
        }

        SessionManager sm = new SessionManager(this);
        returnApi = RetrofitClient.getInstance(sm.getToken()).create(ReturnRequestApi.class);

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        etReason = findViewById(R.id.et_reason);
        tvOrderInfo = findViewById(R.id.tv_order_info);
        tvFileName = findViewById(R.id.tv_file_name);
        btnPickFile = findViewById(R.id.btn_pick_file);
        btnSubmit = findViewById(R.id.btn_submit);
        progressBar = findViewById(R.id.progress_bar);
        tvOrderInfo.setText("Đơn hàng #" + orderId);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupListeners() {
        btnPickFile.setOnClickListener(v -> filePicker.launch("*/*"));
        btnSubmit.setOnClickListener(v -> submitReturn());
    }

    private void submitReturn() {
        String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";
        if (reason.length() < 10) {
            etReason.setError("Lý do cần tối thiểu 10 ký tự");
            return;
        }

        try {
            MultipartBody.Part filePart = null;
            if (selectedFileUri != null) {
                File file = copyUriToCache(selectedFileUri);
                String mime = getContentResolver().getType(selectedFileUri);
                if (mime == null) mime = "application/octet-stream";
                RequestBody fileBody = RequestBody.create(file, MediaType.parse(mime));
                filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
            }

            RequestBody orderIdPart = RequestBody.create(String.valueOf(orderId), MediaType.parse("text/plain"));
            RequestBody orderItemIdPart = orderItemId != -1
                    ? RequestBody.create(String.valueOf(orderItemId), MediaType.parse("text/plain"))
                    : null;
            RequestBody reasonPart = RequestBody.create(reason, MediaType.parse("text/plain"));

            setLoading(true);
            returnApi.createReturn(orderIdPart, orderItemIdPart, reasonPart, filePart)
                    .enqueue(new Callback<ApiResponse<ReturnRequestItem>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ReturnRequestItem>> call,
                                               Response<ApiResponse<ReturnRequestItem>> response) {
                            setLoading(false);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(ReturnRequestActivity.this,
                                        "Đã gửi yêu cầu hoàn trả", Toast.LENGTH_SHORT).show();
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(EXTRA_ORDER_ID, orderId);
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            } else {
                                String msg = response.body() != null ? response.body().getMessage() : "Không gửi được yêu cầu";
                                Toast.makeText(ReturnRequestActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<ReturnRequestItem>> call, Throwable t) {
                            setLoading(false);
                            Toast.makeText(ReturnRequestActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            setLoading(false);
            Toast.makeText(this, "Không thể đọc file đã chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
        btnPickFile.setEnabled(!loading);
    }

    private String getDisplayName(Uri uri) {
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) return cursor.getString(index);
            }
        } catch (Exception ignored) {}
        return "File đã chọn";
    }

    private File copyUriToCache(Uri uri) throws Exception {
        File dir = new File(getCacheDir(), "return_evidence");
        if (!dir.exists()) dir.mkdirs();
        File file = File.createTempFile("return_", ".upload", dir);
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
