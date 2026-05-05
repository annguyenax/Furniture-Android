package com.furniture.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signupButton;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        progressBar = findViewById(R.id.progress_bar);
        errorTextView = findViewById(R.id.error_text_view);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Hiển thị thông báo nếu bị redirect do hết hạn token
        String message = getIntent().getStringExtra("message");
        if (message != null) {
            errorTextView.setText(message);
            errorTextView.setVisibility(android.view.View.VISIBLE);
        }

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        // Initialize ViewModel
        AuthRepository authRepository = new AuthRepository(this);
        AuthViewModelFactory factory = new AuthViewModelFactory(authRepository, sessionManager);
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        // Setup observers
        observeViewModel();

        // Setup click listeners
        loginButton.setOnClickListener(v -> handleLogin());
        signupButton.setOnClickListener(v -> navigateToSignup());
    }

    private void observeViewModel() {
        authViewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                errorTextView.setText(error);
                errorTextView.setVisibility(android.view.View.VISIBLE);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        authViewModel.getAuthResponse().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Vui lòng nhập email");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Vui lòng nhập mật khẩu");
            return;
        }

        errorTextView.setVisibility(android.view.View.GONE);
        authViewModel.login(email, password);
    }

    private void navigateToSignup() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void navigateToHome() {
        startActivity(new Intent(this, CustomerMainActivity.class));
        finish();
    }
}
