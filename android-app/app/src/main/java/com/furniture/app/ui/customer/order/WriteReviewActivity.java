package com.furniture.app.ui.customer.order;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.ReviewModel;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReviewApi;
import com.furniture.app.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_PRODUCT_NAME = "product_name";

    private int productId;
    private RatingBar ratingBar;
    private TextView tvRatingLabel, tvProductName;
    private TextInputEditText etComment;
    private LinearLayout llImages;
    private ReviewApi reviewApi;

    private final List<Uri> selectedImages = new ArrayList<>();

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && selectedImages.size() < 3) {
                    selectedImages.add(uri);
                    addImageThumb(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);

        if (productId == -1) { finish(); return; }

        SessionManager sessionManager = new SessionManager(this);
        reviewApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReviewApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvProductName = findViewById(R.id.tv_product_name);
        ratingBar = findViewById(R.id.rating_bar);
        tvRatingLabel = findViewById(R.id.tv_rating_label);
        etComment = findViewById(R.id.et_comment);
        llImages = findViewById(R.id.ll_images);

        tvProductName.setText(productName != null ? productName : "");

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) ->
                tvRatingLabel.setText(getRatingLabel((int) rating)));

        findViewById(R.id.btn_add_image).setOnClickListener(v -> {
            if (selectedImages.size() >= 3) {
                Toast.makeText(this, "Tối đa 3 ảnh", Toast.LENGTH_SHORT).show();
            } else {
                pickImageLauncher.launch("image/*");
            }
        });

        findViewById(R.id.btn_submit).setOnClickListener(v -> submitReview());
    }

    private void addImageThumb(Uri uri) {
        int size = (int) (80 * getResources().getDisplayMetrics().density);
        ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.setMarginEnd((int)(4 * getResources().getDisplayMetrics().density));
        iv.setLayoutParams(params);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            iv.setImageBitmap(bmp);
        } catch (Exception e) {
            iv.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        iv.setOnLongClickListener(v -> {
            selectedImages.remove(uri);
            llImages.removeView(iv);
            return true;
        });
        llImages.addView(iv);
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagesStr = null;
        if (!selectedImages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Uri u : selectedImages) {
                if (sb.length() > 0) sb.append(",");
                sb.append(u.toString());
            }
            imagesStr = sb.toString();
        }

        ReviewApi.ReviewRequest request = new ReviewApi.ReviewRequest(productId, rating, comment, imagesStr);
        reviewApi.createReview(request).enqueue(new Callback<ApiResponse<ReviewModel>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewModel>> call, Response<ApiResponse<ReviewModel>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(WriteReviewActivity.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                    Intent result = new Intent();
                    result.putExtra(EXTRA_PRODUCT_ID, productId);
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Gửi đánh giá thất bại";
                    Toast.makeText(WriteReviewActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewModel>> call, Throwable t) {
                Toast.makeText(WriteReviewActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRatingLabel(int rating) {
        switch (rating) {
            case 1: return "Rất tệ";
            case 2: return "Tệ";
            case 3: return "Bình thường";
            case 4: return "Tốt";
            case 5: return "Rất hài lòng";
            default: return "";
        }
    }
}
