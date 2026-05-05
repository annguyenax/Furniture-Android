package com.furniture.app.ui.customer.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.repository.ProductRepository;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.ui.viewmodel.ProductViewModel;
import com.furniture.app.ui.viewmodel.ProductViewModelFactory;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private ImageView clearSearchBtn;
    private RecyclerView searchResultsRecyclerView;
    private ProgressBar progressBar;
    private View emptyState;
    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.search_edit_text);
        clearSearchBtn = view.findViewById(R.id.clear_search);
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);
    }

    private void setupViewModel() {
        ProductRepository productRepository = new ProductRepository(requireContext());
        ProductViewModelFactory factory = new ProductViewModelFactory(productRepository);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        productViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);
                emptyState.setVisibility(View.GONE);
                searchResultsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                emptyState.setVisibility(View.VISIBLE);
                searchResultsRecyclerView.setVisibility(View.GONE);
            }
        });

        productViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        productViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        searchResultsRecyclerView.setAdapter(productAdapter);
    }

    private void setupListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearchBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        clearSearchBtn.setOnClickListener(v -> {
            searchEditText.setText("");
            productAdapter.setProducts(new ArrayList<>());
            emptyState.setVisibility(View.VISIBLE);
            searchResultsRecyclerView.setVisibility(View.GONE);
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            productViewModel.searchProducts(query);
        }
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
