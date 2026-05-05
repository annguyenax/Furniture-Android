package com.furniture.app.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.furniture.app.data.repository.AuthRepository;
import com.furniture.app.util.SessionManager;

public class AuthViewModelFactory implements ViewModelProvider.Factory {
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    public AuthViewModelFactory(AuthRepository authRepository, SessionManager sessionManager) {
        this.authRepository = authRepository;
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(authRepository, sessionManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
