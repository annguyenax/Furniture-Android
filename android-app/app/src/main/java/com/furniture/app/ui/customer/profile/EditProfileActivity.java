package com.furniture.app.ui.customer.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.model.request.UpdateProfileRequest;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.UserApi;
import com.furniture.app.util.LoadingDialog;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etAddress;
    private MaterialButton btnSave, btnChangePassword;
    private TextView btnChangePhoto;
    private CircleImageView profileImage;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private UserApi userApi;

    private final ActivityResultLauncher<String> avatarPicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) uploadAvatar(uri);
            });

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
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnChangePhoto = findViewById(R.id.btn_change_photo);
        profileImage = findViewById(R.id.profile_image);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Chinh sua ho so");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadUserData() {
        etFirstName.setText(sessionManager.getFirstName());
        etLastName.setText(sessionManager.getLastName());
        etPhone.setText(sessionManager.getPhone());
        etAddress.setText(sessionManager.getUserAddress());

        userApi.getMe().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    etFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                    etLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                    etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                    if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(user.getProfilePicture())
                                .placeholder(R.drawable.ic_person)
                                .into(profileImage);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Keep local data when offline.
            }
        });
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ChangePasswordActivity.class)));
        btnChangePhoto.setOnClickListener(v -> avatarPicker.launch("image/*"));
    }

    private void uploadAvatar(Uri uri) {
        try {
            File file = copyUriToCache(uri, "avatar_");
            RequestBody body = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);

            LoadingDialog loading = LoadingDialog.show(this, "Đang upload ảnh đại diện...");
            userApi.uploadAvatar(part).enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    loading.dismiss();
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        String url = response.body().getData();
                        Glide.with(EditProfileActivity.this)
                                .load(url)
                                .placeholder(R.drawable.ic_person)
                                .into(profileImage);
                        sessionManager.saveAvatarUrl(url);
                        Toast.makeText(EditProfileActivity.this, "Da doi anh dai dien", Toast.LENGTH_SHORT).show();
                    } else {
                        String errMsg = "Upload anh that bai";
                        if (response.body() != null && response.body().getMessage() != null) {
                            errMsg = response.body().getMessage();
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    errMsg = "Upload that bai (HTTP " + response.code() + ")";
                                }
                            } catch (Exception ignored) {}
                        }
                        Toast.makeText(EditProfileActivity.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    loading.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Loi ket noi khi upload anh", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Khong the doc anh da chon", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (firstName.isEmpty()) {
            etFirstName.setError("Vui long nhap ho");
            etFirstName.requestFocus();
            return;
        }
        if (lastName.isEmpty()) {
            etLastName.setError("Vui long nhap ten");
            etLastName.requestFocus();
            return;
        }
        if (!phone.isEmpty() && !phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            etPhone.setError("So dien thoai khong hop le");
            etPhone.requestFocus();
            return;
        }

        LoadingDialog loading = LoadingDialog.show(this, "Đang lưu thông tin...");
        btnSave.setEnabled(false);

        UpdateProfileRequest request = new UpdateProfileRequest(firstName, lastName, phone);
        userApi.updateProfile(request).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                loading.dismiss();
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
                    Toast.makeText(EditProfileActivity.this, "Da luu thong tin", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this,
                            "Luu that bai, vui long thu lai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                loading.dismiss();
                btnSave.setEnabled(true);
                Toast.makeText(EditProfileActivity.this,
                        "Khong co ket noi mang, chua the luu len may chu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File copyUriToCache(Uri uri, String prefix) throws Exception {
        File dir = new File(getCacheDir(), "profile_uploads");
        if (!dir.exists()) dir.mkdirs();
        File file = File.createTempFile(prefix, ".jpg", dir);
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
