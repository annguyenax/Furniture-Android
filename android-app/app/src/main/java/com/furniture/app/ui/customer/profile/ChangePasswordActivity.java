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
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout oldPasswordLayout;
    private TextInputLayout newPasswordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText oldPasswordEditText;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialButton changePasswordButton;
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

        oldPasswordLayout = findViewById(R.id.old_password_layout);
        newPasswordLayout = findViewById(R.id.new_password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        oldPasswordEditText = findViewById(R.id.et_old_password);
        newPasswordEditText = findViewById(R.id.et_new_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        changePasswordButton = findViewById(R.id.btn_change_password);
        progressBar = findViewById(R.id.progress_bar);

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPassword = textOf(oldPasswordEditText);
        String newPassword = textOf(newPasswordEditText);
        String confirmPassword = textOf(confirmPasswordEditText);

        if (!validateInput(oldPassword, newPassword, confirmPassword)) {
            return;
        }

        setLoading(true);
        userApi.changePassword(oldPassword, newPassword).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ChangePasswordActivity.this,
                            "Đã đổi mật khẩu. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                    sessionManager.clearSession();
                    RetrofitClient.resetInstance();
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    oldPasswordLayout.setError("Mật khẩu cũ không đúng");
                    oldPasswordEditText.requestFocus();
                    Toast.makeText(ChangePasswordActivity.this,
                            "Đổi mật khẩu thất bại. Vui lòng kiểm tra mật khẩu cũ.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                setLoading(false);
                Toast.makeText(ChangePasswordActivity.this,
                        "Không có kết nối mạng, thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInput(String oldPassword, String newPassword, String confirmPassword) {
        clearErrors();

        if (oldPassword.isEmpty()) {
            oldPasswordLayout.setError("Vui lòng nhập mật khẩu cũ");
            oldPasswordEditText.requestFocus();
            return false;
        }
        if (newPassword.length() < 6) {
            newPasswordLayout.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
            newPasswordEditText.requestFocus();
            return false;
        }
        if (newPassword.equals(oldPassword)) {
            newPasswordLayout.setError("Mật khẩu mới phải khác mật khẩu cũ");
            newPasswordEditText.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Vui lòng xác nhận mật khẩu mới");
            confirmPasswordEditText.requestFocus();
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Xác nhận mật khẩu mới không khớp");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        changePasswordButton.setEnabled(!loading);
        oldPasswordEditText.setEnabled(!loading);
        newPasswordEditText.setEnabled(!loading);
        confirmPasswordEditText.setEnabled(!loading);
    }

    private void clearErrors() {
        oldPasswordLayout.setError(null);
        newPasswordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
}
