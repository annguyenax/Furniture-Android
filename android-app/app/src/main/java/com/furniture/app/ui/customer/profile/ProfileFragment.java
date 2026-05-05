package com.furniture.app.ui.customer.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.furniture.app.R;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.ui.customer.order.OrderHistoryActivity;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private View menuOrders;
    private View menuAddresses;
    private View menuPayment;
    private View menuSettings;
    private View menuHelp;
    private MaterialButton btnLogout;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        loadUserProfile();
        setupListeners();
    }

    private void initViews(View view) {
        profileImage = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        menuOrders = view.findViewById(R.id.menu_orders);
        menuAddresses = view.findViewById(R.id.menu_addresses);
        menuPayment = view.findViewById(R.id.menu_payment);
        menuSettings = view.findViewById(R.id.menu_settings);
        menuHelp = view.findViewById(R.id.menu_help);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void loadUserProfile() {
        // Load user info from session
        String name = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();

        if (name != null && !name.isEmpty()) {
            userName.setText(name);
        } else {
            userName.setText("User");
        }

        if (email != null && !email.isEmpty()) {
            userEmail.setText(email);
        } else {
            userEmail.setText("user@example.com");
        }
    }

    private void setupListeners() {
        // Click on profile header to edit profile
        profileImage.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        menuOrders.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), OrderHistoryActivity.class));
        });

        menuAddresses.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        menuPayment.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Phương thức thanh toán đang phát triển", Toast.LENGTH_SHORT).show();
        });

        menuSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Cài đặt đang phát triển", Toast.LENGTH_SHORT).show();
        });

        menuHelp.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Hỗ trợ: hotline@furniture.com", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            handleLogout();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void handleLogout() {
        sessionManager.clearSession();
        RetrofitClient.resetInstance();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
