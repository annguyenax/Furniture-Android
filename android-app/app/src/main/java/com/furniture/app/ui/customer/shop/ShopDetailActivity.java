package com.furniture.app.ui.customer.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.furniture.app.R;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.model.Shop;
import com.furniture.app.data.repository.ProductRepository;
import com.furniture.app.ui.adapter.ProductAdapter;
import com.furniture.app.ui.customer.chat.ChatActivity;
import com.furniture.app.ui.customer.product.ProductDetailActivity;
import com.furniture.app.ui.viewmodel.ProductViewModel;
import com.furniture.app.ui.viewmodel.ProductViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SHOP_ID = "shop_id";
    public static final String EXTRA_SHOP = "shop";

    private ProductViewModel productViewModel;
    private ProductAdapter productAdapter;

    // Views
    private CircleImageView shopLogo;
    private TextView shopName;
    private TextView shopLocation;
    private RatingBar ratingBar;
    private TextView tvRating;
    private TextView tvProducts;
    private TextView tvFollowers;
    private TextView tvResponseRate;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private View emptyState;
    private MaterialButton btnChat;
    private MaterialButton btnFollow;

    private Shop shop;
    private int shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        initViews();
        setupToolbar();
        loadShopData();
        setupViewModel();
        setupListeners();
    }

    private void initViews() {
        shopLogo = findViewById(R.id.shop_logo);
        shopName = findViewById(R.id.shop_name);
        shopLocation = findViewById(R.id.shop_location);
        ratingBar = findViewById(R.id.rating_bar);
        tvRating = findViewById(R.id.tv_rating);
        tvProducts = findViewById(R.id.tv_products);
        tvFollowers = findViewById(R.id.tv_followers);
        tvResponseRate = findViewById(R.id.tv_response_rate);
        rvProducts = findViewById(R.id.rv_products);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        btnChat = findViewById(R.id.btn_chat);
        btnFollow = findViewById(R.id.btn_follow);

        // Setup RecyclerView
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadShopData() {
        if (getIntent().hasExtra(EXTRA_SHOP)) {
            shop = (Shop) getIntent().getSerializableExtra(EXTRA_SHOP);
            if (shop != null) {
                displayShopInfo(shop);
                shopId = shop.getShopId();
            }
        } else {
            shopId = getIntent().getIntExtra(EXTRA_SHOP_ID, -1);
            // Load shop from API if only ID is provided
        }
    }

    private void displayShopInfo(Shop shop) {
        shopName.setText(shop.getShopName());
        shopLocation.setText(shop.getAddress() != null ? shop.getAddress() : "Việt Nam");

        if (shop.getLogo() != null && !shop.getLogo().isEmpty()) {
            Glide.with(this).load(shop.getLogo()).into(shopLogo);
        }

        if (shop.getRating() != null) {
            ratingBar.setRating(shop.getRating().floatValue());
            tvRating.setText(String.format("%.1f", shop.getRating()));
        }

        tvProducts.setText(String.valueOf(shop.getProductCount()));
        tvFollowers.setText(String.valueOf(shop.getFollowers()));
        tvResponseRate.setText(shop.getResponseRate() != null ? shop.getResponseRate() + "%" : "90%");
    }

    private void setupViewModel() {
        ProductRepository productRepository = new ProductRepository(this);
        ProductViewModelFactory factory = new ProductViewModelFactory(productRepository);
        productViewModel = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        productViewModel.getProducts().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);
                emptyState.setVisibility(View.GONE);
                rvProducts.setVisibility(View.VISIBLE);
            } else {
                emptyState.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
            }
        });

        productViewModel.getLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        productViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Load shop products
        if (shopId > 0) {
            productViewModel.loadProducts(0, 50); // TODO: Filter by shop ID
        }
    }

    private void setupListeners() {
        btnChat.setOnClickListener(v -> {
            if (shop != null) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_SHOP_ID, shopId);
                intent.putExtra(ChatActivity.EXTRA_SHOP_NAME, shop.getShopName());
                startActivity(intent);
            }
        });

        btnFollow.setOnClickListener(v -> {
            Toast.makeText(this, "Đã theo dõi shop", Toast.LENGTH_SHORT).show();
            btnFollow.setText("Đang theo dõi");
            btnFollow.setEnabled(false);
        });
    }

    private void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
