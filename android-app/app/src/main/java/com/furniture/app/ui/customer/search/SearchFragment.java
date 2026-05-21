package com.furniture.app.ui.customer.search;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private RecyclerView rvSuggestions;
    private ProgressBar progressBar;
    private View emptyState;
    private ChipGroup filterChipGroup, sortChipGroup;
    private ProductAdapter productAdapter;
    private SuggestionAdapter suggestionAdapter;
    private ProductApi productApi;
    private CategoryApi categoryApi;

    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;

    private List<Product> rawResults = new ArrayList<>();
    private List<String> allProductNames = new ArrayList<>();
    private int currentSort = SORT_DEFAULT;
    private Integer selectedCategoryId = null;

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
        setupRecyclerViews();
        setupListeners();
        loadCategories();
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
        rvSuggestions = view.findViewById(R.id.rv_suggestions);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        sortChipGroup = view.findViewById(R.id.sort_chip_group);
    }

    private void setupRecyclerViews() {
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        searchResultsRecyclerView.setAdapter(productAdapter);

        suggestionAdapter = new SuggestionAdapter(new ArrayList<>(), name -> {
            searchEditText.setText(name);
            searchEditText.setSelection(name.length());
            hideSuggestions();
            hideKeyboard();
            triggerSearch();
        });
        rvSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSuggestions.setAdapter(suggestionAdapter);
    }

    private void loadCategories() {
        categoryApi.getAllCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call,
                                   Response<ApiResponse<List<Category>>> response) {
                if (!isAdded() || !response.isSuccessful() || response.body() == null
                        || response.body().getData() == null) return;
                buildCategoryChips(response.body().getData());
            }
            @Override public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {}
        });
    }

    private void buildCategoryChips(List<Category> categories) {
        filterChipGroup.removeAllViews();

        Chip chipAll = createChip("Tất cả", View.generateViewId());
        chipAll.setChecked(true);
        chipAll.setTag(null);
        filterChipGroup.addView(chipAll);

        for (Category cat : categories) {
            Chip chip = createChip(cat.getCategoryName(), View.generateViewId());
            chip.setTag(cat.getCategoryId());
            filterChipGroup.addView(chip);
        }

        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            View checkedChip = group.findViewById(checkedIds.get(0));
            if (checkedChip instanceof Chip) {
                Object tag = ((Chip) checkedChip).getTag();
                selectedCategoryId = tag instanceof Integer ? (Integer) tag : null;
                triggerSearch();
            }
        });
    }

    private Chip createChip(String text, int id) {
        Chip chip = (Chip) LayoutInflater.from(requireContext())
                .inflate(R.layout.item_chip_choice, filterChipGroup, false);
        chip.setId(id);
        chip.setText(text);
        return chip;
    }

    private void setupListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearchBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                updateSuggestions(s.toString().trim());
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
                hideSuggestions();
                triggerSearch();
                hideKeyboard();
                return true;
            }
            return false;
        });

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Delay so suggestion item click registers before the list disappears
                debounceHandler.postDelayed(this::hideSuggestions, 200);
            }
        });

        clearSearchBtn.setOnClickListener(v -> {
            searchEditText.setText("");
            hideSuggestions();
            hideKeyboard();
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

    private void updateSuggestions(String query) {
        if (query.length() < 2 || allProductNames.isEmpty()) {
            hideSuggestions();
            return;
        }
        String kw = query.toLowerCase();
        List<String> suggestions = allProductNames.stream()
                .filter(name -> name.toLowerCase().contains(kw))
                .distinct().limit(6)
                .collect(Collectors.toList());
        if (suggestions.isEmpty()) {
            hideSuggestions();
        } else {
            suggestionAdapter.setItems(suggestions);
            rvSuggestions.setVisibility(View.VISIBLE);
        }
    }

    private void hideSuggestions() {
        if (rvSuggestions != null) rvSuggestions.setVisibility(View.GONE);
    }

    private void triggerSearch() {
        String keyword = searchEditText.getText() != null
                ? searchEditText.getText().toString().trim() : "";

        if (selectedCategoryId != null) {
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
                            cacheProductNames(rawResults);
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

    private void cacheProductNames(List<Product> products) {
        allProductNames = products.stream()
                .map(Product::getProductName)
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.toList());
    }

    private void applyFilterAndSort() {
        List<Product> results = new ArrayList<>(rawResults);

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
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    // ── Suggestion Adapter ───────────────────────────────────────────────────

    interface OnSuggestionClick { void onClick(String name); }

    static class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.VH> {
        private List<String> items;
        private final OnSuggestionClick listener;

        SuggestionAdapter(List<String> items, OnSuggestionClick listener) {
            this.items = items;
            this.listener = listener;
        }

        void setItems(List<String> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_suggestion, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            String name = items.get(position);
            h.tvSuggestion.setText(name);
            h.itemView.setOnClickListener(v -> listener.onClick(name));
        }

        @Override public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvSuggestion;
            VH(View v) { super(v); tvSuggestion = v.findViewById(R.id.tv_suggestion); }
        }
    }
}
