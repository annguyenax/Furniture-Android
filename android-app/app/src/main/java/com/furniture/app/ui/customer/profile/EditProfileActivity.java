package com.furniture.app.ui.customer.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.model.request.UpdateProfileRequest;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.UserApi;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etAddress;
    private MaterialButton btnSave;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);
        userApi = RetrofitClient.getInstance(sessionManager.getToken()).create(UserApi.class);

        initViews();
        setupToolbar();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar == null) {
            progressBar = new ProgressBar(this);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Chỉnh sửa hồ sơ");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        etFirstName.setText(sessionManager.getFirstName());
        etLastName.setText(sessionManager.getLastName());
        etPhone.setText(sessionManager.getPhone());
        etAddress.setText(sessionManager.getUserAddress());

        // Load fresh from server
        userApi.getMe().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    etFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                    etLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                    etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Keep local data
            }
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);

        UpdateProfileRequest request = new UpdateProfileRequest(firstName, lastName, phone);
        userApi.updateProfile(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        sessionManager.updateUserInfo(user.getFirstName(), user.getLastName(), user.getPhone());
                    } else {
                        sessionManager.updateUserInfo(firstName, lastName, phone);
                    }
                    if (!address.isEmpty()) {
                        sessionManager.setUserAddress(address);
                    }
                    Toast.makeText(EditProfileActivity.this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Lưu thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnSave.setEnabled(true);
                // Save locally even if API fails
                sessionManager.updateUserInfo(firstName, lastName, phone);
                if (!address.isEmpty()) {
                    sessionManager.setUserAddress(address);
                }
                Toast.makeText(EditProfileActivity.this, "Đã lưu thông tin cục bộ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
