package com.furniture.app.ui.customer.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Category;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.CategoryApi;
import com.furniture.app.data.remote.api.ProductApi;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.util.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private static final int SORT_DEFAULT = 0;
    private static final int SORT_PRICE_ASC = 1;
    private static final int SORT_PRICE_DESC = 2;
    private static final long DEBOUNCE_MS = 400;

    private EditText searchEditText;
    private ImageView clearSearchBtn;
    private RecyclerView searchResultsRecyclerView;
    private ProgressBar progressBar;
    private View emptyState;
    private ChipGroup filterChipGroup, sortChipGroup;
    private ProductAdapter productAdapter;
    private ProductApi productApi;
    private CategoryApi categoryApi;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;

    private List<Product> rawResults = new ArrayList<>();
    private int currentSort = SORT_DEFAULT;
    private Integer selectedCategoryId = null;

    private static final int[] CATEGORY_CHIP_IDS = {
            R.id.chip_all, R.id.chip_living_room, R.id.chip_bedroom,
            R.id.chip_kitchen, R.id.chip_office
    };
    private final Integer[] categoryIds = new Integer[5];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(requireContext());
        productApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ProductApi.class);
        categoryApi = RetrofitClient.getInstance(sessionManager.getToken()).create(CategoryApi.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        loadCategoryIds();

        // Load initial product list
        loadAllProducts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        debounceHandler.removeCallbacksAndMessages(null);
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.search_edit_text);
        clearSearchBtn = view.findViewById(R.id.clear_search);
        searchResultsRecyclerView = view.findViewById(R.id.search_results_recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        sortChipGroup = view.findViewById(R.id.sort_chip_group);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        searchResultsRecyclerView.setAdapter(productAdapter);
    }

    private void loadCategoryIds() {
        categoryIds[0] = null;
        categoryApi.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call,
                                   Response<ApiResponse<List<Category>>> response) {
                if (!response.isSuccessful() || response.body() == null
                        || response.body().getData() == null) return;
                List<Category> cats = response.body().getData();
                String[] keywords = {"", "Phòng khách", "Phòng ngủ", "Phòng ăn", "Phòng làm việc"};
                for (int i = 1; i < keywords.length; i++) {
                    for (Category c : cats) {
                        if (c.getCategoryName() != null && c.getCategoryName().contains(keywords[i])) {
                            categoryIds[i] = c.getCategoryId();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {}
        });
    }

    private void setupListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearchBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
                debounceRunnable = () -> triggerSearch();
                debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_MS);
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
                triggerSearch();
                hideKeyboard();
                return true;
            }
            return false;
        });

        clearSearchBtn.setOnClickListener(v -> {
            searchEditText.setText("");
            hideKeyboard();
            // Reset to all products with current category
            triggerSearch();
        });

        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int chipId = checkedIds.get(0);
            selectedCategoryId = null;
            for (int i = 0; i < CATEGORY_CHIP_IDS.length; i++) {
                if (CATEGORY_CHIP_IDS[i] == chipId) {
                    selectedCategoryId = categoryIds[i];
                    break;
                }
            }
            triggerSearch();
        });

        sortChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int chipId = checkedIds.get(0);
            if (chipId == R.id.chip_sort_price_asc) {
                currentSort = SORT_PRICE_ASC;
            } else if (chipId == R.id.chip_sort_price_desc) {
                currentSort = SORT_PRICE_DESC;
            } else {
                currentSort = SORT_DEFAULT;
            }
            applyFilterAndSort();
        });
    }

    private void triggerSearch() {
        String keyword = searchEditText.getText() != null
                ? searchEditText.getText().toString().trim() : "";

        if (selectedCategoryId != null) {
            // Load by category, then client-side filter by keyword
            loadByCategory(selectedCategoryId, keyword);
        } else if (!keyword.isEmpty()) {
            searchByKeyword(keyword);
        } else {
            loadAllProducts();
        }
    }

    private void loadAllProducts() {
        setLoading(true);
        productApi.getAllProducts(0, 60, "createdAt", "desc")
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                           Response<ApiResponse<PageResponse<Product>>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            rawResults = new ArrayList<>(response.body().getData().getContent());
                            applyFilterAndSort();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        setLoading(false);
                        if (isAdded()) Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void searchByKeyword(String keyword) {
        setLoading(true);
        productApi.searchProducts(keyword, 0, 60)
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                           Response<ApiResponse<PageResponse<Product>>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            rawResults = new ArrayList<>(response.body().getData().getContent());
                            applyFilterAndSort();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        setLoading(false);
                        if (isAdded()) Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadByCategory(int categoryId, String keyword) {
        setLoading(true);
        productApi.getProductsByCategory(categoryId, 0, 60)
                .enqueue(new Callback<ApiResponse<PageResponse<Product>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<PageResponse<Product>>> call,
                                           Response<ApiResponse<PageResponse<Product>>> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getData() != null) {
                            rawResults = new ArrayList<>(response.body().getData().getContent());
                            // Filter by keyword client-side
                            if (!keyword.isEmpty()) {
                                String kw = keyword.toLowerCase();
                                rawResults = rawResults.stream()
                                        .filter(p -> p.getProductName() != null
                                                && p.getProductName().toLowerCase().contains(kw))
                                        .collect(Collectors.toList());
                            }
                            applyFilterAndSort();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<PageResponse<Product>>> call, Throwable t) {
                        setLoading(false);
                        if (isAdded()) Toast.makeText(requireContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilterAndSort() {
        List<Product> results = new ArrayList<>(rawResults);

        // Sort
        if (currentSort == SORT_PRICE_ASC) {
            Collections.sort(results, (a, b) -> {
                BigDecimal pa = a.getLowestPrice() != null ? a.getLowestPrice() : BigDecimal.ZERO;
                BigDecimal pb = b.getLowestPrice() != null ? b.getLowestPrice() : BigDecimal.ZERO;
                return pa.compareTo(pb);
            });
        } else if (currentSort == SORT_PRICE_DESC) {
            Collections.sort(results, (a, b) -> {
                BigDecimal pa = a.getLowestPrice() != null ? a.getLowestPrice() : BigDecimal.ZERO;
                BigDecimal pb = b.getLowestPrice() != null ? b.getLowestPrice() : BigDecimal.ZERO;
                return pb.compareTo(pa);
            });
        }

        if (!results.isEmpty()) {
            productAdapter.setProducts(results);
            emptyState.setVisibility(View.GONE);
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            productAdapter.setProducts(new ArrayList<>());
            searchResultsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    private void setLoading(boolean loading) {
        if (!isAdded()) return;
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading) {
            searchResultsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard() {
        if (!isAdded()) return;
        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
