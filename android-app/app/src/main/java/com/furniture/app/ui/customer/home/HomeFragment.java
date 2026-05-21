package com.furniture.app.ui.customer.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private View emptyState;
    private android.widget.TextView tvEmptyMessage;
    private com.google.android.material.button.MaterialButton btnRetry;
    private View searchBar;
    private View btnChatHome;
    private View btnNotificationHome;
    private View seeAllFeatured;
    private ViewPager2 bannerViewPager;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private CategoryApi categoryApi;
    private SessionManager sessionManager;

    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private static final long BANNER_INTERVAL_MS = 3500;

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
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);

        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupListeners();
        setupBanner();
        loadProducts();
        loadCategories();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        featuredProductsRecyclerView = view.findViewById(R.id.featured_products_recycler_view);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
        btnRetry = view.findViewById(R.id.btn_retry);
        searchBar = view.findViewById(R.id.search_bar);
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
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);
                emptyState.setVisibility(View.GONE);
                featuredProductsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                emptyState.setVisibility(View.VISIBLE);
                featuredProductsRecyclerView.setVisibility(View.GONE);
            }
        });

        productViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        productViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
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
            if (btnRetry != null) btnRetry.setVisibility(View.GONE);
            if (tvEmptyMessage != null) tvEmptyMessage.setText("Không có sản phẩm");
            loadProducts();
            loadCategories();
        });

        if (btnRetry != null) btnRetry.setOnClickListener(v -> {
            btnRetry.setVisibility(View.GONE);
            if (tvEmptyMessage != null) tvEmptyMessage.setText("Không có sản phẩm");
            loadProducts();
            loadCategories();
        });

        if (searchBar != null) searchBar.setOnClickListener(v -> navigateToTab(1));
        if (seeAllFeatured != null) seeAllFeatured.setOnClickListener(v -> navigateToTab(1));

        if (btnChatHome != null) btnChatHome.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                return;
            }
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_SHOP_ID, 1);
            intent.putExtra(ChatActivity.EXTRA_SHOP_NAME, "Hỗ trợ Shop");
            intent.putExtra(ChatActivity.EXTRA_IS_ADMIN, false);
            startActivity(intent);
        });

        if (btnNotificationHome != null) btnNotificationHome.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
            } else {
                Toast.makeText(requireContext(), "Chưa có thông báo mới", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBanner() {
        if (bannerViewPager == null) return;
        List<String> bannerUrls = Arrays.asList(
            "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80",
            "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&q=80",
            "https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=600&q=80",
            "https://images.unsplash.com/photo-1493663284031-b7e3aaa4cab8?w=600&q=80"
        );
        BannerAdapter bannerAdapter = new BannerAdapter(bannerUrls);
        bannerViewPager.setAdapter(bannerAdapter);
        startBannerAutoScroll(bannerUrls.size());
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
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacksAndMessages(null);
    }

    private void loadProducts() {
        productViewModel.loadProducts(0, 20);
    }

    private void loadCategories() {
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
