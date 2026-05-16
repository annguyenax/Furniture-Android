package com.furniture.app.ui.customer.home;

import android.content.Intent;
import android.os.Bundle;
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

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.CategoryApi;
import com.furniture.app.data.repository.ProductRepository;
import com.furniture.app.ui.adapter.CategoryAdapter;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.customer.product.CategoryProductsActivity;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.ui.viewmodel.ProductViewModel;
import com.furniture.app.ui.viewmodel.ProductViewModelFactory;
import com.furniture.app.util.SessionManager;

import java.util.ArrayList;
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
    private View searchBar;
    private View btnCartHome;
    private View seeAllFeatured;
    private View seeAllCategories;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private CategoryApi categoryApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(requireContext());
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);

        initViews(view);
        setupViewModel();
        setupRecyclerViews();
        setupListeners();
        loadProducts();
        loadCategories();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        featuredProductsRecyclerView = view.findViewById(R.id.featured_products_recycler_view);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);
        searchBar = view.findViewById(R.id.search_bar);
        btnCartHome = view.findViewById(R.id.btn_cart_home);
        seeAllFeatured = view.findViewById(R.id.see_all_featured);
        seeAllCategories = view.findViewById(R.id.see_all_categories);
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
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
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
            loadProducts();
            loadCategories();
        });

        if (searchBar != null) searchBar.setOnClickListener(v -> navigateToTab(1));
        if (btnCartHome != null) btnCartHome.setOnClickListener(v -> navigateToTab(2));
        if (seeAllFeatured != null) seeAllFeatured.setOnClickListener(v -> navigateToTab(1));
        if (seeAllCategories != null) seeAllCategories.setOnClickListener(v -> navigateToTab(1));
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
