package com.furniture.app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.furniture.app.data.model.AuthResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.repository.AuthRepository;
import com.furniture.app.util.SessionManager;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<AuthResponse> authResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();

    public AuthViewModel(AuthRepository authRepository, SessionManager sessionManager) {
        this.authRepository = authRepository;
        this.sessionManager = sessionManager;
    }

    public LiveData<AuthResponse> getAuthResponse() {
        return authResponseLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    public void login(String email, String password) {
        loadingLiveData.setValue(true);
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                User user = response.getUser();
                String role = (user.getRoles() != null && !user.getRoles().isEmpty())
                        ? user.getRoles().get(0) : "CUSTOMER";
                sessionManager.saveUserSession(
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getProfilePicture(),
                        role
                );
                authResponseLiveData.setValue(response);
                loadingLiveData.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorLiveData.setValue(error);
                loadingLiveData.setValue(false);
            }
        });
    }

    public void register(String username, String email, String password, String firstName, String lastName, String phone) {
        loadingLiveData.setValue(true);
        authRepository.register(username, email, password, firstName, lastName, phone, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                authResponseLiveData.setValue(response);
                // Save session
                User user = response.getUser();
                sessionManager.saveUserSession(
                        response.getAccessToken(),
                        response.getRefreshToken(),
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getProfilePicture()
                );
                loadingLiveData.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorLiveData.setValue(error);
                loadingLiveData.setValue(false);
            }
        });
    }

    public void logout() {
        sessionManager.clearSession();
        authResponseLiveData.setValue(null);
        errorLiveData.setValue(null);
    }
}
