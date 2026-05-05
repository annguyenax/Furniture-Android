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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.furniture.app.R;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.repository.ProductRepository;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.ui.customer.CustomerMainActivity;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.ui.viewmodel.ProductViewModel;
import com.furniture.app.ui.viewmodel.ProductViewModelFactory;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView featuredProductsRecyclerView;
    private ProgressBar progressBar;
    private View emptyState;
    private View searchBar;
    private View btnCartHome;
    private View seeAllFeatured;
    private View seeAllCategories;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();
        loadProducts();
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        featuredProductsRecyclerView = view.findViewById(R.id.featured_products_recycler_view);
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

        // Observe products
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

        // Observe loading state
        productViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            swipeRefreshLayout.setRefreshing(isLoading);
        });

        // Observe errors
        productViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        featuredProductsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        featuredProductsRecyclerView.setAdapter(productAdapter);
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::loadProducts);

        if (searchBar != null) {
            searchBar.setOnClickListener(v -> navigateToTab(1));
        }
        if (btnCartHome != null) {
            btnCartHome.setOnClickListener(v -> navigateToTab(2));
        }
        if (seeAllFeatured != null) {
            seeAllFeatured.setOnClickListener(v -> navigateToTab(1));
        }
        if (seeAllCategories != null) {
            seeAllCategories.setOnClickListener(v -> navigateToTab(1));
        }
    }

    private void navigateToTab(int tab) {
        if (getActivity() instanceof CustomerMainActivity) {
            ((CustomerMainActivity) getActivity()).navigateToTab(tab);
        }
    }

    private void loadProducts() {
        productViewModel.loadProducts(0, 20);
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
