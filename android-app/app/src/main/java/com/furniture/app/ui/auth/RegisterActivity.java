package com.furniture.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.furniture.app.BuildConfig;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.AuthResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.AuthApi;
import com.furniture.app.data.repository.AuthRepository;
import com.furniture.app.ui.admin.AdminMainActivity;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.viewmodel.AuthViewModel;
import com.furniture.app.ui.viewmodel.AuthViewModelFactory;
import com.furniture.app.util.InputValidator;
import com.furniture.app.util.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private EditText firstNameEditText, lastNameEditText, phoneEditText;
    private Button registerButton, btnGoogleRegister, btnFacebookRegister;
    private TextView loginButton;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;
    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                } else {
                    Toast.makeText(this, "Bạn đã hủy đăng ký Google", Toast.LENGTH_SHORT).show();
                }
            });

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
        setupGoogleSignIn();
        btnGoogleRegister.setOnClickListener(v -> signInWithGoogle());
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
                finishAfterAuth();
            }
        });
    }

    private void handleRegister() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (validateInput(email, password, firstName, lastName, phone)) {
            errorTextView.setVisibility(View.GONE);
            authViewModel.register(buildUsernameFromEmail(email), email, password, firstName, lastName, phone);
        }
    }

    private boolean validateInput(String email, String password, String firstName, String lastName, String phone) {
        if (!InputValidator.validateRequired(firstNameEditText, "họ")) return false;
        if (!InputValidator.validateRequired(lastNameEditText, "tên")) return false;
        if (!InputValidator.validateEmail(emailEditText)) return false;
        if (!InputValidator.validatePhone(phoneEditText)) return false;
        if (!InputValidator.validatePassword(passwordEditText, 6)) return false;
        return true;
    }

    private String buildUsernameFromEmail(String email) {
        String base = email != null ? email.trim() : "";
        if (base.length() <= 50) return base;

        int atIndex = base.indexOf('@');
        String prefix = atIndex > 0 ? base.substring(0, atIndex) : "user";
        String suffix = "_" + Integer.toHexString(base.hashCode());
        int maxPrefixLength = Math.max(3, 50 - suffix.length());
        if (prefix.length() > maxPrefixLength) {
            prefix = prefix.substring(0, maxPrefixLength);
        }
        return prefix + suffix;
    }

    private void showSocialTodo(String provider) {
        Toast.makeText(this,
                provider + " login chưa được cấu hình OAuth ở backend", Toast.LENGTH_SHORT).show();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        if (BuildConfig.GOOGLE_WEB_CLIENT_ID.startsWith("YOUR_WEB_CLIENT_ID")) {
            Toast.makeText(this, "Chưa cấu hình Google Web Client ID", Toast.LENGTH_LONG).show();
            return;
        }
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            if (idToken != null) {
                sendGoogleTokenToBackend(idToken);
            } else {
                Toast.makeText(this, "Không lấy được token Google", Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Toast.makeText(this, "Đăng ký Google thất bại (mã lỗi: " + e.getStatusCode() + ")", Toast.LENGTH_LONG).show();
        }
    }

    private void sendGoogleTokenToBackend(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        btnGoogleRegister.setEnabled(false);

        AuthApi authApi = RetrofitClient.getPublicRetrofit().create(AuthApi.class);
        authApi.googleRegister(idToken).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                progressBar.setVisibility(View.GONE);
                btnGoogleRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    saveSessionAndNavigate(response.body().getData());
                } else {
                    String msg = parseGoogleRegisterError(response);
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnGoogleRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String parseGoogleRegisterError(Response<ApiResponse<AuthResponse>> response) {
        String message = null;
        try {
            if (response.body() != null) {
                message = response.body().getMessage();
            } else if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                Type type = new TypeToken<ApiResponse<Void>>() {}.getType();
                ApiResponse<Void> errorResponse = new Gson().fromJson(errorJson, type);
                if (errorResponse != null) {
                    if (errorResponse.getErrors() != null && !errorResponse.getErrors().isEmpty()) {
                        message = errorResponse.getErrors().values().iterator().next();
                    } else {
                        message = errorResponse.getMessage();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        if (message != null && message.toLowerCase().contains("email already exists")) {
            return "Email này đã có tài khoản. Vui lòng đăng nhập bằng Google.";
        }
        return message != null && !message.isEmpty() ? message : "Đăng ký Google thất bại";
    }

    private void saveSessionAndNavigate(AuthResponse authResponse) {
        User user = authResponse.getUser();
        String role = (user.getRoles() != null && !user.getRoles().isEmpty())
                ? user.getRoles().get(0) : "CUSTOMER";
        sessionManager.saveUserSession(
                authResponse.getAccessToken(),
                authResponse.getRefreshToken(),
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePicture(),
                role
        );
        Toast.makeText(this, "Đăng ký Google thành công!", Toast.LENGTH_SHORT).show();
        finishAfterAuth();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_RETURN_AFTER_LOGIN,
                getIntent().getBooleanExtra(LoginActivity.EXTRA_RETURN_AFTER_LOGIN, false));
        startActivity(intent);
        finish();
    }

    private void navigateToHome() {
        Class<?> targetActivity = sessionManager.isAdmin()
                ? AdminMainActivity.class
                : CustomerMainActivity.class;
        Intent intent = new Intent(this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void finishAfterAuth() {
        if (getIntent().getBooleanExtra(LoginActivity.EXTRA_RETURN_AFTER_LOGIN, false)) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        navigateToHome();
    }
}
