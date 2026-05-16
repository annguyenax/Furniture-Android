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

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.User;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.UserApi;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.ui.customer.chat.ChatActivity;
import com.furniture.app.ui.customer.order.OrderHistoryActivity;
import com.furniture.app.ui.customer.profile.AddressListActivity;
import com.furniture.app.ui.customer.profile.WishlistActivity;
import com.furniture.app.util.SessionManager;
import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImage;
    private TextView userName;
    private TextView userEmail;
    private View menuOrders;
    private View menuWishlist;
    private View menuAddresses;
    private View menuChat;
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
        menuWishlist = view.findViewById(R.id.menu_wishlist);
        menuAddresses = view.findViewById(R.id.menu_addresses);
        menuChat = view.findViewById(R.id.menu_chat);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void loadUserProfile() {
        // Show session data first (fast)
        String name = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();
        userName.setText(name != null && !name.isEmpty() ? name : "User");
        userEmail.setText(email != null && !email.isEmpty() ? email : "user@example.com");

        // Refresh from server
        String token = sessionManager.getToken();
        if (token == null) return;
        UserApi userApi = RetrofitClient.getInstance(token).create(UserApi.class);
        userApi.getMe().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    User user = response.body().getData();
                    sessionManager.updateUserInfo(user.getFirstName(), user.getLastName(), user.getPhone());
                    String fullName = user.getFullName();
                    userName.setText(fullName != null && !fullName.isEmpty() ? fullName : user.getUsername());
                    userEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                        Glide.with(requireContext()).load(user.getProfilePicture())
                                .placeholder(R.drawable.ic_person).into(profileImage);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Keep session data
            }
        });
    }

    private void setupListeners() {
        // Click on profile header to edit profile
        profileImage.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        menuOrders.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), OrderHistoryActivity.class)));

        menuWishlist.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), WishlistActivity.class)));

        menuAddresses.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddressListActivity.class)));

        menuChat.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_SHOP_ID, 1);
            intent.putExtra(ChatActivity.EXTRA_SHOP_NAME, "Hỗ trợ Shop");
            intent.putExtra(ChatActivity.EXTRA_IS_ADMIN, false);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> handleLogout());
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
