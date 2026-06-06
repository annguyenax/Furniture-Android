package com.furniture.app.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.furniture.app.R;
import com.furniture.app.data.repository.AuthRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout newPasswordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialButton resetPasswordButton;
    private TextView backToLoginButton;
    private TextView errorTextView;
    private View loadingOverlay;
    private AuthRepository authRepository;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        authRepository = new AuthRepository(this);
        token = extractToken();

        newPasswordLayout = findViewById(R.id.new_password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        backToLoginButton = findViewById(R.id.back_to_login_button);
        errorTextView = findViewById(R.id.error_text_view);
        loadingOverlay = findViewById(R.id.loading_overlay);

        if (token == null || token.trim().isEmpty()) {
            showError("Reset link is invalid or missing token.");
            resetPasswordButton.setEnabled(false);
        }

        resetPasswordButton.setOnClickListener(v -> handleResetPassword());
        backToLoginButton.setOnClickListener(v -> navigateToLogin());
    }

    private String extractToken() {
        Uri data = getIntent().getData();
        if (data != null) {
            return data.getQueryParameter("token");
        }
        return getIntent().getStringExtra("token");
    }

    private void handleResetPassword() {
        String newPassword = getText(newPasswordEditText);
        String confirmPassword = getText(confirmPasswordEditText);

        newPasswordLayout.setError(null);
        confirmPasswordLayout.setError(null);
        errorTextView.setVisibility(View.GONE);

        if (newPassword.length() < 6) {
            newPasswordLayout.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            return;
        }

        setLoading(true);
        authRepository.resetPassword(token, newPassword, new AuthRepository.SimpleCallback() {
            @Override
            public void onSuccess(String message) {
                setLoading(false);
                Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                navigateToLogin();
            }

            @Override
            public void onError(String error) {
                setLoading(false);
                showError(error);
            }
        });
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void setLoading(boolean isLoading) {
        loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        resetPasswordButton.setEnabled(!isLoading);
        backToLoginButton.setEnabled(!isLoading);
    }

    private void showError(String error) {
        errorTextView.setText(error);
        errorTextView.setVisibility(View.VISIBLE);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
