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

    private EditText usernameEditText, emailEditText, passwordEditText;
    private EditText firstNameEditText, lastNameEditText, phoneEditText;
    private Button registerButton, btnGoogleRegister, btnFacebookRegister;
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
        btnGoogleRegister.setOnClickListener(v -> showSocialTodo("Google"));
        btnFacebookRegister.setOnClickListener(v -> showSocialTodo("Facebook"));
    }

    private void initViews() {
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        firstNameEditText = findViewById(R.id.first_name_edit_text);
        lastNameEditText = findViewById(R.id.last_name_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        registerButton = findViewById(R.id.register_button);
        btnGoogleRegister = findViewById(R.id.btn_google_register);
        btnFacebookRegister = findViewById(R.id.btn_facebook_register);
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
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);

                if (error.toLowerCase().contains("phone") || error.contains("điện thoại")) {
                    phoneEditText.setError("Số điện thoại đã tồn tại trong hệ thống");
                    phoneEditText.requestFocus();
                } else if (error.toLowerCase().contains("email")) {
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

        if (validateInput(username, email, password, firstName, lastName, phone)) {
            errorTextView.setVisibility(View.GONE);
            authViewModel.register(username, email, password, firstName, lastName, phone);
        }
    }

    private boolean validateInput(String username, String email, String password,
                                  String firstName, String lastName, String phone) {
        if (firstName.isEmpty()) {
            firstNameEditText.setError("Vui lòng nhập họ");
            firstNameEditText.requestFocus();
            return false;
        }
        if (lastName.isEmpty()) {
            lastNameEditText.setError("Vui lòng nhập tên");
            lastNameEditText.requestFocus();
            return false;
        }
        if (username.length() < 3) {
            usernameEditText.setError("Tên đăng nhập phải từ 3 ký tự");
            usernameEditText.requestFocus();
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Email không hợp lệ");
            emailEditText.requestFocus();
            return false;
        }
        if (phone.isEmpty() || !phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
            phoneEditText.setError("Số điện thoại không hợp lệ");
            phoneEditText.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            passwordEditText.setError("Mật khẩu phải từ 6 ký tự");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void showSocialTodo(String provider) {
        Toast.makeText(this,
                provider + " login chưa được cấu hình OAuth ở backend", Toast.LENGTH_SHORT).show();
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
