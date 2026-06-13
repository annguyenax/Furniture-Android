package com.furniture.app.ui.customer.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.CategoryApi;
import com.furniture.app.data.repository.ProductRepository;
import com.furniture.app.receiver.WifiConnectionReceiver;
import com.furniture.app.ui.adapter.BannerAdapter;
import com.furniture.app.ui.adapter.CategoryAdapter;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.customer.chat.ChatActivity;
import com.furniture.app.ui.customer.product.CategoryProductsActivity;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.ui.viewmodel.ProductViewModel;
import com.furniture.app.ui.viewmodel.ProductViewModelFactory;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView featuredProductsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private ProgressBar progressBar;
    private View bannerCard;
    private View categoriesSectionHeader;
    private View featuredSectionHeader;
    private View emptyState;
    private View offlineState;
    private android.widget.TextView tvEmptyMessage;
    private com.google.android.material.button.MaterialButton btnRetry;
    private com.google.android.material.button.MaterialButton btnEnableWifi;
    private View btnChatHome;
    private View btnNotificationHome;
    private View seeAllFeatured;
    private ViewPager2 bannerViewPager;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;
    private CategoryApi categoryApi;
    private SessionManager sessionManager;
    private boolean refreshProductsOnResume = false;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private static final long BANNER_INTERVAL_MS = 3500;
    private boolean networkReceiverRegistered;
    private boolean showingCachedProducts;

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) {
                updateConnectionState();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        categoryApi = RetrofitClient.getPublicRetrofit().create(CategoryApi.class);

        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupListeners();
        setupBanner();
        updateConnectionState();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        featuredProductsRecyclerView = view.findViewById(R.id.featured_products_recycler_view);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        bannerCard = view.findViewById(R.id.banner_card);
        categoriesSectionHeader = view.findViewById(R.id.categories_section_header);
        featuredSectionHeader = view.findViewById(R.id.featured_section_header);
        emptyState = view.findViewById(R.id.empty_state);
        offlineState = view.findViewById(R.id.offline_state);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
        btnRetry = view.findViewById(R.id.btn_retry);
        btnEnableWifi = view.findViewById(R.id.btn_enable_wifi);
        btnChatHome = view.findViewById(R.id.btn_chat_home);
        btnNotificationHome = view.findViewById(R.id.btn_notification_home);
        seeAllFeatured = view.findViewById(R.id.see_all_featured);
        bannerViewPager = view.findViewById(R.id.banner_viewpager);
    }

    private void setupViewModel() {
        ProductRepository productRepository = new ProductRepository(requireContext());
        ProductViewModelFactory factory = new ProductViewModelFactory(productRepository);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (!isWifiConnected()) {
                if (products != null && !products.isEmpty()) {
                    showCachedContentState();
                    productAdapter.setProducts(products);
                    featuredProductsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    showOfflineState();
                }
                return;
            }
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);
                updateBannerFromProducts(products);
                emptyState.setVisibility(View.GONE);
                featuredProductsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                emptyState.setVisibility(View.VISIBLE);
                featuredProductsRecyclerView.setVisibility(View.GONE);
            }
        });

        productViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (!isWifiConnected()) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            swipeRefreshLayout.setRefreshing(isLoading && isWifiConnected());
        });

        productViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                if (!isWifiConnected()) {
                    loadCachedProductsOrShowOffline();
                    return;
                }
                emptyState.setVisibility(View.VISIBLE);
                featuredProductsRecyclerView.setVisibility(View.GONE);
                if (tvEmptyMessage != null) tvEmptyMessage.setText("Không thể tải dữ liệu. Kiểm tra kết nối mạng.");
                if (btnRetry != null) btnRetry.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerViews() {
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        featuredProductsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        featuredProductsRecyclerView.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this::onCategoryClick);
        categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!isWifiConnected()) {
                loadCachedProductsOrShowOffline();
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            if (btnRetry != null) btnRetry.setVisibility(View.GONE);
            if (tvEmptyMessage != null) tvEmptyMessage.setText("Không có sản phẩm");
            loadProducts();
            loadCategories();
        });

        if (btnRetry != null) btnRetry.setOnClickListener(v -> {
            if (!isWifiConnected()) {
                loadCachedProductsOrShowOffline();
                return;
            }
            btnRetry.setVisibility(View.GONE);
            if (tvEmptyMessage != null) tvEmptyMessage.setText("Không có sản phẩm");
            loadProducts();
            loadCategories();
        });

        if (btnEnableWifi != null) btnEnableWifi.setOnClickListener(v -> openWifiSettings());

        if (seeAllFeatured != null) seeAllFeatured.setOnClickListener(v -> navigateToTab(1));

        if (btnChatHome != null) btnChatHome.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                startLoginForReturn();
                return;
            }
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_SUPPORT_ID, 1);
            intent.putExtra(ChatActivity.EXTRA_SUPPORT_NAME, "Hỗ trợ Shop");
            intent.putExtra(ChatActivity.EXTRA_IS_ADMIN, false);
            startActivity(intent);
        });

        if (btnNotificationHome != null) btnNotificationHome.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                startLoginForReturn();
            } else {
                Toast.makeText(requireContext(), "Chưa có thông báo mới", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        registerNetworkReceiver();
        updateConnectionState();
    }

    @Override
    public void onStop() {
        unregisterNetworkReceiver();
        super.onStop();
    }

    private void startLoginForReturn() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.putExtra(LoginActivity.EXTRA_RETURN_AFTER_LOGIN, true);
        startActivity(intent);
    }

    private void setupBanner() {
        if (bannerViewPager == null) return;
        List<String> bannerUrls = Arrays.asList(
            "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80",
            "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&q=80",
            "https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=600&q=80",
            "https://images.unsplash.com/photo-1493663284031-b7e3aaa4cab8?w=600&q=80"
        );
        bannerAdapter = new BannerAdapter(bannerUrls);
        bannerViewPager.setAdapter(bannerAdapter);
        startBannerAutoScroll(bannerUrls.size());
    }

    private void updateBannerFromProducts(List<Product> products) {
        if (bannerAdapter == null || bannerViewPager == null) return;

        List<String> productImageUrls = new ArrayList<>();
        for (Product product : products) {
            String imageUrl = product.getFirstImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                productImageUrls.add(imageUrl);
            }
            if (productImageUrls.size() == 4) break;
        }

        if (productImageUrls.isEmpty()) return;

        bannerHandler.removeCallbacksAndMessages(null);
        bannerAdapter = new BannerAdapter(productImageUrls);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerViewPager.setCurrentItem(0, false);
        startBannerAutoScroll(productImageUrls.size());
    }

    private void startBannerAutoScroll(int count) {
        bannerRunnable = new Runnable() {
            @Override public void run() {
                if (bannerViewPager == null || !isAdded()) return;
                int next = (bannerViewPager.getCurrentItem() + 1) % count;
                bannerViewPager.setCurrentItem(next, true);
                bannerHandler.postDelayed(this, BANNER_INTERVAL_MS);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, BANNER_INTERVAL_MS);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refreshProductsOnResume) {
            loadProducts();
        }
        refreshProductsOnResume = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacksAndMessages(null);
        unregisterNetworkReceiver();
    }

    private void loadProducts() {
        if (!isWifiConnected()) {
            loadCachedProductsOrShowOffline();
            return;
        }
        showContentState();
        productViewModel.loadProducts(0, 20);
    }

    private void loadCategories() {
        if (!isWifiConnected()) {
            categoryAdapter.setCategories(new ArrayList<>());
            return;
        }
        categoryApi.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call,
                                   Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getData() != null) {
                    categoryAdapter.setCategories(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                // Silent fail - categories are supplementary
            }
        });
    }

    private void updateConnectionState() {
        if (isWifiConnected()) {
            showingCachedProducts = false;
            showContentState();
            loadProducts();
            loadCategories();
        } else {
            loadCachedProductsOrShowOffline();
        }
    }

    private void loadCachedProductsOrShowOffline() {
        showingCachedProducts = true;
        productViewModel.loadCachedHomeProducts();
        if (categoryAdapter != null) categoryAdapter.setCategories(new ArrayList<>());
    }

    private void showOfflineState() {
        showingCachedProducts = false;
        bannerHandler.removeCallbacksAndMessages(null);
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (bannerCard != null) bannerCard.setVisibility(View.GONE);
        if (categoriesSectionHeader != null) categoriesSectionHeader.setVisibility(View.GONE);
        if (categoriesRecyclerView != null) categoriesRecyclerView.setVisibility(View.GONE);
        if (featuredSectionHeader != null) featuredSectionHeader.setVisibility(View.GONE);
        if (featuredProductsRecyclerView != null) featuredProductsRecyclerView.setVisibility(View.GONE);
        if (emptyState != null) emptyState.setVisibility(View.GONE);
        if (offlineState != null) offlineState.setVisibility(View.VISIBLE);
        if (productAdapter != null) productAdapter.setProducts(new ArrayList<>());
        if (categoryAdapter != null) categoryAdapter.setCategories(new ArrayList<>());
    }

    private void showContentState() {
        showingCachedProducts = false;
        if (offlineState != null) offlineState.setVisibility(View.GONE);
        if (bannerCard != null) bannerCard.setVisibility(View.VISIBLE);
        if (categoriesSectionHeader != null) categoriesSectionHeader.setVisibility(View.VISIBLE);
        if (categoriesRecyclerView != null) categoriesRecyclerView.setVisibility(View.VISIBLE);
        if (featuredSectionHeader != null) featuredSectionHeader.setVisibility(View.VISIBLE);
        if (emptyState != null) emptyState.setVisibility(View.GONE);
    }

    private void showCachedContentState() {
        bannerHandler.removeCallbacksAndMessages(null);
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (offlineState != null) offlineState.setVisibility(View.GONE);
        if (bannerCard != null) bannerCard.setVisibility(View.GONE);
        if (categoriesSectionHeader != null) categoriesSectionHeader.setVisibility(View.GONE);
        if (categoriesRecyclerView != null) categoriesRecyclerView.setVisibility(View.GONE);
        if (featuredSectionHeader != null) featuredSectionHeader.setVisibility(View.VISIBLE);
        if (emptyState != null) emptyState.setVisibility(View.GONE);
    }

    private boolean isWifiConnected() {
        return isAdded() && WifiConnectionReceiver.isWifiConnected(requireContext());
    }

    private void openWifiSettings() {
        Intent intent = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? new Intent(Settings.Panel.ACTION_WIFI)
                : new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    private void registerNetworkReceiver() {
        if (networkReceiverRegistered) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        ContextCompat.registerReceiver(
                requireContext(),
                networkReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        );
        networkReceiverRegistered = true;
    }

    private void unregisterNetworkReceiver() {
        if (!networkReceiverRegistered || getContext() == null) return;
        requireContext().unregisterReceiver(networkReceiver);
        networkReceiverRegistered = false;
    }

    private void navigateToTab(int tab) {
        if (getActivity() instanceof CustomerMainActivity) {
            ((CustomerMainActivity) getActivity()).navigateToTab(tab);
        }
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    private void onCategoryClick(Category category) {
        Intent intent = new Intent(requireContext(), CategoryProductsActivity.class);
        intent.putExtra(CategoryProductsActivity.EXTRA_CATEGORY, category);
        startActivity(intent);
    }
}
