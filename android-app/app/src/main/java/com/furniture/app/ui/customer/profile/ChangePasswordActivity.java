package com.furniture.app.ui.customer.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.UserApi;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        sessionManager = new SessionManager(this);
        userApi = RetrofitClient.getInstance(sessionManager.getToken()).create(UserApi.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        progressBar = findViewById(R.id.progress_bar);

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = textOf(etOldPassword);
        String newPassword = textOf(etNewPassword);
        String confirmPassword = textOf(etConfirmPassword);

        if (oldPassword.isEmpty()) {
            etOldPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            etOldPassword.requestFocus();
            return;
        }
        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu mới phải từ 6 ký tự");
            etNewPassword.requestFocus();
            return;
        }
        if (newPassword.equals(oldPassword)) {
            etNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
            etNewPassword.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);
        userApi.changePassword(oldPassword, newPassword).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this,
                            "Đã đổi mật khẩu, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
                    sessionManager.clearSession();
                    RetrofitClient.resetInstance();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChangePasswordActivity.this,
                            "Đổi mật khẩu thất bại, kiểm tra mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ChangePasswordActivity.this,
                        "Không có kết nối mạng, thử lại sau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnChangePassword.setEnabled(!loading);
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
