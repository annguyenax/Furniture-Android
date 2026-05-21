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
import com.furniture.app.util.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton, btnGoogleLogin, btnFacebookLogin;
    private TextView signupButton, forgotPasswordButton;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    private GoogleSignInClient googleSignInClient;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        btnGoogleLogin = findViewById(R.id.btn_google_login);
        btnFacebookLogin = findViewById(R.id.btn_facebook_login);
        signupButton = findViewById(R.id.signup_button);
        forgotPasswordButton = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);
        errorTextView = findViewById(R.id.error_text_view);

        sessionManager = new SessionManager(this);

        String message = getIntent().getStringExtra("message");
        if (message != null) {
            errorTextView.setText(message);
            errorTextView.setVisibility(View.VISIBLE);
        }

        if (sessionManager.isLoggedIn()) {
            navigateToHome();
            return;
        }

        AuthRepository authRepository = new AuthRepository(this);
        AuthViewModelFactory factory = new AuthViewModelFactory(authRepository, sessionManager);
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);

        observeViewModel();

        loginButton.setOnClickListener(v -> handleLogin());
        signupButton.setOnClickListener(v -> navigateToSignup());
        forgotPasswordButton.setOnClickListener(v -> Toast.makeText(this,
                "Quên mật khẩu chưa cấu hình dịch vụ email", Toast.LENGTH_SHORT).show());
        btnFacebookLogin.setOnClickListener(v -> Toast.makeText(this,
                "Facebook login chưa được hỗ trợ", Toast.LENGTH_SHORT).show());

        setupGoogleSignIn();
        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        // Sign out first to always show account chooser
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
            Toast.makeText(this, "Đăng nhập Google thất bại (mã lỗi: " + e.getStatusCode() + ")", Toast.LENGTH_LONG).show();
        }
    }

    private void sendGoogleTokenToBackend(String idToken) {
        progressBar.setVisibility(View.VISIBLE);
        btnGoogleLogin.setEnabled(false);

        AuthApi authApi = RetrofitClient.getInstance().create(AuthApi.class);
        authApi.googleLogin(idToken).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                progressBar.setVisibility(View.GONE);
                btnGoogleLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AuthResponse authResponse = response.body().getData();
                    saveSessionAndNavigate(authResponse);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Đăng nhập Google thất bại";
                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnGoogleLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
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
        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    private void observeViewModel() {
        authViewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                errorTextView.setText(error);
                errorTextView.setVisibility(View.VISIBLE);
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

        errorTextView.setVisibility(View.GONE);
        authViewModel.login(email, password);
    }

    private void navigateToSignup() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void navigateToHome() {
        Class<?> targetActivity = sessionManager.isAdmin()
                ? AdminMainActivity.class
                : CustomerMainActivity.class;
        startActivity(new Intent(this, targetActivity));
        finish();
    }
}
