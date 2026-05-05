package com.furniture.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.furniture.app.R;
import com.furniture.app.data.repository.AuthRepository;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.viewmodel.AuthViewModel;
import com.furniture.app.ui.viewmodel.AuthViewModelFactory;
import com.furniture.app.util.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, firstNameEditText, lastNameEditText, phoneEditText;
    private Button registerButton;
    private TextView loginButton;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        sessionManager = new SessionManager(this);

        AuthRepository authRepository = new AuthRepository(this);
        AuthViewModelFactory factory = new AuthViewModelFactory(authRepository, sessionManager);
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        observeViewModel();

        registerButton.setOnClickListener(v -> handleRegister());
        loginButton.setOnClickListener(v -> navigateToLogin());
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);
        errorTextView = findViewById(R.id.error_text_view);
    }

    private void observeViewModel() {
        authViewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            registerButton.setEnabled(!isLoading);
        });

        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                // Xử lý hiển thị lỗi chi tiết
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
                
                // Rung nhẹ hoặc focus vào trường lỗi nếu là lỗi duplicate
                if (error.contains("Số điện thoại") || error.contains("0334074016")) {
                    phoneEditText.setError("Số điện thoại đã tồn tại trong hệ thống");
                    phoneEditText.requestFocus();
                } else if (error.contains("email") || error.contains("Email")) {
                    emailEditText.setError("Email đã được sử dụng");
                    emailEditText.requestFocus();
                }
            }
        });

        authViewModel.getAuthResponse().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });
    }

    private void handleRegister() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (validateInput(username, email, password, phone)) {
            errorTextView.setVisibility(View.GONE);
            authViewModel.register(username, email, password, firstName, lastName, phone);
        }
    }

    private boolean validateInput(String username, String email, String password, String phone) {
        if (username.isEmpty()) { usernameEditText.setError("Yêu cầu nhập tên đăng nhập"); return false; }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) { 
            emailEditText.setError("Email không hợp lệ"); return false; 
        }
        if (password.length() < 6) { passwordEditText.setError("Mật khẩu phải từ 6 ký tự"); return false; }
        if (phone.isEmpty() || phone.length() < 10) { phoneEditText.setError("Số điện thoại không hợp lệ"); return false; }
        return true;
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, CustomerMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
