package com.furniture.app.ui.customer.product;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.model.ProductVariant;
import com.furniture.app.data.model.ReviewModel;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.api.ReviewApi;
import com.furniture.app.data.remote.api.WishlistApi;
import com.furniture.app.data.repository.CartRepository;
import com.furniture.app.data.repository.ProductRepository;
import com.furniture.app.ui.adapter.ImageSliderAdapter;
import com.furniture.app.ui.adapter.ReviewAdapter;
import com.furniture.app.ui.adapter.VariantAdapter;
import com.furniture.app.ui.customer.order.CheckoutActivity;
import com.furniture.app.ui.viewmodel.CartViewModel;
import com.furniture.app.ui.viewmodel.CartViewModelFactory;
import com.furniture.app.ui.viewmodel.ProductViewModel;
import com.furniture.app.ui.viewmodel.ProductViewModelFactory;
import com.furniture.app.util.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_PRODUCT = "product";

    private ProductViewModel productViewModel;
    private CartViewModel cartViewModel;
    private ReviewApi reviewApi;
    private WishlistApi wishlistApi;
    private SessionManager sessionManager;
    private boolean isWishlisted = false;
    private NumberFormat currencyFormat;

    // Views
    private ViewPager2 imageViewPager;
    private TabLayout imageIndicator;
    private TextView tvPrice, tvOriginalPrice, tvDiscount;
    private TextView tvProductName, tvRating, tvSold;
    private TextView tvDimensions, tvWeight, tvCategory, tvStock;
    private TextView tvDescription;
    private android.widget.EditText tvQuantity;
    private TextView tvReviewCount, tvNoReviews;
    private RatingBar ratingBar;
    private RecyclerView rvVariants, rvReviews;
    private ProgressBar progressBar;

    private Product currentProduct;
    private ProductVariant selectedVariant;
    private int quantity = 1;
    private VariantAdapter variantAdapter;
    private final List<Integer> variantImagePositions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        sessionManager = new SessionManager(this);
        currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        reviewApi = RetrofitClient.getInstance(sessionManager.getToken()).create(ReviewApi.class);
        wishlistApi = RetrofitClient.getInstance(sessionManager.getToken()).create(WishlistApi.class);

        initViews();
        setupToolbar();
        setupViewModels();
        setupQuantityButtons();
        setupActionButtons();

        // Load product data
        if (getIntent().hasExtra(EXTRA_PRODUCT)) {
            currentProduct = (Product) getIntent().getSerializableExtra(EXTRA_PRODUCT);
            if (currentProduct != null) {
                displayProduct(currentProduct);
            }
        } else if (getIntent().hasExtra(EXTRA_PRODUCT_ID)) {
            int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
            if (productId != -1) {
                loadProduct(productId);
            }
        }
    }

    private void initViews() {
        imageViewPager = findViewById(R.id.image_view_pager);
        imageIndicator = findViewById(R.id.image_indicator);
        tvPrice = findViewById(R.id.tv_price);
        tvOriginalPrice = findViewById(R.id.tv_original_price);
        tvDiscount = findViewById(R.id.tv_discount);
        tvProductName = findViewById(R.id.tv_product_name);
        tvRating = findViewById(R.id.tv_rating);
        tvSold = findViewById(R.id.tv_sold);
        ratingBar = findViewById(R.id.rating_bar);
        tvDimensions = findViewById(R.id.tv_dimensions);
        tvWeight = findViewById(R.id.tv_weight);
        tvCategory = findViewById(R.id.tv_category);
        tvStock = findViewById(R.id.tv_stock);
        tvDescription = findViewById(R.id.tv_description);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvReviewCount = findViewById(R.id.tv_review_count);
        tvNoReviews = findViewById(R.id.tv_no_reviews);
        rvVariants = findViewById(R.id.rv_variants);
        rvReviews = findViewById(R.id.rv_reviews);
        progressBar = findViewById(R.id.progress_bar);

        // Setup variants RecyclerView
        rvVariants.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setNestedScrollingEnabled(false);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModels() {
        String token = sessionManager.getToken();

        ProductRepository productRepository = new ProductRepository(this);
        productViewModel = new ViewModelProvider(this,
                new ProductViewModelFactory(productRepository)).get(ProductViewModel.class);

        CartRepository cartRepository = new CartRepository(token);
        cartViewModel = new ViewModelProvider(this,
                new CartViewModelFactory(cartRepository)).get(CartViewModel.class);

        // Observe cart result
        cartViewModel.getAddToCartResult().observe(this, result -> {
            if (result != null && result.isSuccess()) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else if (result != null) {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupQuantityButtons() {
        ImageButton btnDecrease = findViewById(R.id.btn_decrease);
        ImageButton btnIncrease = findViewById(R.id.btn_increase);

        btnDecrease.setOnClickListener(v -> {
            syncQuantityFromInput();
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            syncQuantityFromInput();
            int maxStock = getMaxStock();
            if (quantity < maxStock) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Số lượng tối đa: " + maxStock, Toast.LENGTH_SHORT).show();
            }
        });

        // Allow typing a number directly; clamp to valid range on focus loss
        tvQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) syncQuantityFromInput();
        });
        tvQuantity.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                syncQuantityFromInput();
                android.view.inputmethod.InputMethodManager imm =
                        (android.view.inputmethod.InputMethodManager)
                                getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(tvQuantity.getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    private void syncQuantityFromInput() {
        try {
            int typed = Integer.parseInt(tvQuantity.getText().toString().trim());
            int maxStock = getMaxStock();
            quantity = Math.max(1, Math.min(typed, maxStock));
        } catch (NumberFormatException e) {
            quantity = 1;
        }
        tvQuantity.setText(String.valueOf(quantity));
    }

    private int getMaxStock() {
        if (selectedVariant != null) {
            return selectedVariant.getStock();
        } else if (currentProduct != null) {
            return currentProduct.getStock();
        }
        return 99;
    }

    private void setupActionButtons() {
        findViewById(R.id.btn_add_to_cart).setOnClickListener(v -> addToCart());
        findViewById(R.id.btn_buy_now).setOnClickListener(v -> buyNow());
    }

    private void setupWishlistButton(int productId) {
        ImageButton btnWishlist = findViewById(R.id.btn_wishlist);
        if (btnWishlist == null) return;
        wishlistApi.checkWishlist(productId).enqueue(new Callback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(Call<ApiResponse<Boolean>> call, Response<ApiResponse<Boolean>> response) {
                if (response.isSuccessful() && response.body() != null
                        && Boolean.TRUE.equals(response.body().getData())) {
                    isWishlisted = true;
                    btnWishlist.setImageResource(android.R.drawable.btn_star_big_on);
                } else {
                    isWishlisted = false;
                    btnWishlist.setImageResource(android.R.drawable.btn_star_big_off);
                }
            }
            @Override public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {}
        });
        btnWishlist.setOnClickListener(v -> requireLogin(() -> toggleWishlist(productId, btnWishlist)));
    }

    private void toggleWishlist(int productId, ImageButton btn) {
        if (isWishlisted) {
            wishlistApi.removeFromWishlist(productId).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    isWishlisted = false;
                    btn.setImageResource(android.R.drawable.btn_star_big_off);
                    Toast.makeText(ProductDetailActivity.this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                }
                @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        } else {
            wishlistApi.addToWishlist(productId).enqueue(new Callback<ApiResponse<Void>>() {
                @Override
                public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        isWishlisted = true;
                        btn.setImageResource(android.R.drawable.btn_star_big_on);
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    } else if (response.body() != null) {
                        Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        }
    }

    private void loadProduct(int productId) {
        progressBar.setVisibility(View.VISIBLE);
        ProductRepository productRepository = new ProductRepository(this);
        productRepository.getProductById(productId, new ProductRepository.ProductDetailCallback() {
            @Override
            public void onSuccess(Product product) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    currentProduct = product;
                    displayProduct(product);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ProductDetailActivity.this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayProduct(Product product) {
        // Product name
        tvProductName.setText(product.getProductName());

        // Price
        updatePriceDisplay(product.getLowestPrice(), product.getBasePrice(), product.getDiscount());

        // Rating
        if (product.getAverageRating() != null) {
            ratingBar.setRating(product.getAverageRating().floatValue());
            tvRating.setText(String.format("%.1f", product.getAverageRating()));
        }

        // Sold
        tvSold.setText(String.format("Đã bán %d", product.getSold()));

        // Details
        if (product.getDimensions() != null && !product.getDimensions().isEmpty()) {
            tvDimensions.setText(product.getDimensions());
        }
        if (product.getWeight() != null) {
            tvWeight.setText(String.format("%.1f kg", product.getWeight()));
        }
        if (product.getCategoryName() != null) {
            tvCategory.setText(product.getCategoryName());
        }
        tvStock.setText(String.format("%d sản phẩm", product.getStock()));

        // Description
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            tvDescription.setText(product.getDescription());
        }

        // Images
        setupImageSlider(product);

        // Variants
        setupVariants(product);

        // Reviews
        loadReviews(product.getProductId());

        // Wishlist
        setupWishlistButton(product.getProductId());
    }

    private void loadReviews(int productId) {
        reviewApi.getProductReviews(productId, 0, 20).enqueue(new Callback<ApiResponse<PageResponse<ReviewModel>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PageResponse<ReviewModel>>> call,
                                   Response<ApiResponse<PageResponse<ReviewModel>>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getData() == null) return;
                PageResponse<ReviewModel> page = response.body().getData();
                List<ReviewModel> reviews = page.getContent();
                runOnUiThread(() -> {
                    int total = (int) page.getTotalElements();
                    tvReviewCount.setText(total > 0 ? total + " đánh giá" : "");
                    if (reviews == null || reviews.isEmpty()) {
                        tvNoReviews.setVisibility(View.VISIBLE);
                        rvReviews.setVisibility(View.GONE);
                    } else {
                        tvNoReviews.setVisibility(View.GONE);
                        rvReviews.setVisibility(View.VISIBLE);
                        rvReviews.setAdapter(new ReviewAdapter(reviews));
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<PageResponse<ReviewModel>>> call, Throwable t) {
                // silently fail — reviews are non-critical
            }
        });
    }

    private void updatePriceDisplay(BigDecimal price, BigDecimal originalPrice, BigDecimal discount) {
        if (price != null) {
            tvPrice.setText(String.format("₫%s", currencyFormat.format(price)));
        }

        if (discount != null && discount.compareTo(BigDecimal.ZERO) > 0 && originalPrice != null) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvOriginalPrice.setText(String.format("₫%s", currencyFormat.format(originalPrice)));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            tvDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText(String.format("-%d%%", discount.intValue()));
        } else {
            tvOriginalPrice.setVisibility(View.GONE);
            tvDiscount.setVisibility(View.GONE);
        }
    }

    private void setupImageSlider(Product product) {
        List<String> imageUrls = new ArrayList<>();
        variantImagePositions.clear();

        if (product.getVariants() != null) {
            for (ProductVariant variant : product.getVariants()) {
                if (variant.getImageUrl() != null && !variant.getImageUrl().isEmpty()) {
                    variantImagePositions.add(imageUrls.size());
                    imageUrls.add(variant.getImageUrl());
                } else {
                    variantImagePositions.add(-1);
                }
            }
        }

        if (imageUrls.isEmpty()) imageUrls.add("");

        ImageSliderAdapter adapter = new ImageSliderAdapter(imageUrls);
        imageViewPager.setAdapter(adapter);

        if (imageUrls.size() > 1) {
            new TabLayoutMediator(imageIndicator, imageViewPager, (tab, position) -> {}).attach();
            imageIndicator.setVisibility(View.VISIBLE);
        } else {
            imageIndicator.setVisibility(View.GONE);
        }
    }

    private void setupVariants(Product product) {
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            variantAdapter = new VariantAdapter(product.getVariants(), variant -> {
                selectedVariant = variant;
                // Reset quantity to 1 when switching variant
                quantity = 1;
                tvQuantity.setText("1");
                updatePriceDisplay(variant.getPrice(), product.getBasePrice(), product.getDiscount());
                int stock = variant.getStock();
                tvStock.setText(stock > 0
                        ? String.format("%d sản phẩm còn lại", stock)
                        : "Hết hàng");
                // Sync image slider to this variant's image
                int variantIdx = product.getVariants().indexOf(variant);
                if (variantIdx >= 0 && variantIdx < variantImagePositions.size()) {
                    int imgPos = variantImagePositions.get(variantIdx);
                    if (imgPos >= 0) imageViewPager.setCurrentItem(imgPos, true);
                }
            });
            rvVariants.setAdapter(variantAdapter);

            selectedVariant = product.getVariants().get(0);
            tvStock.setText(selectedVariant.getStock() > 0
                    ? String.format("%d sản phẩm còn lại", selectedVariant.getStock())
                    : "Hết hàng");
            findViewById(R.id.variant_section).setVisibility(View.VISIBLE);
        } else {
            tvStock.setText(String.format("%d sản phẩm còn lại", product.getStock()));
            findViewById(R.id.variant_section).setVisibility(View.GONE);
        }
    }

    private void requireLogin(Runnable action) {
        if (sessionManager.isLoggedIn()) {
            action.run();
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.furniture.app.ui.auth.LoginActivity.class));
        }
    }

    private boolean isOutOfStock() {
        int stock = getMaxStock();
        return stock <= 0;
    }

    private void addToCart() {
        if (currentProduct == null) return;
        if (isOutOfStock()) {
            Toast.makeText(this, "Sản phẩm này đã hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        syncQuantityFromInput();
        requireLogin(() -> {
            Integer variantId = selectedVariant != null ? selectedVariant.getVariantId() : null;
            cartViewModel.addToCart(currentProduct.getProductId(), variantId, quantity);
        });
    }

    private void buyNow() {
        if (currentProduct == null) return;
        if (isOutOfStock()) {
            Toast.makeText(this, "Sản phẩm này đã hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        syncQuantityFromInput();
        requireLogin(() -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra(CheckoutActivity.EXTRA_PRODUCT, currentProduct);
            intent.putExtra(CheckoutActivity.EXTRA_VARIANT, selectedVariant);
            intent.putExtra(CheckoutActivity.EXTRA_QUANTITY, quantity);
            startActivity(intent);
        });
    }
}
