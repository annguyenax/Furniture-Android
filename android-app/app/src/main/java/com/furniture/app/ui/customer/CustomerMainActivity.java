package com.furniture.app.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.furniture.app.R;
import com.furniture.app.data.remote.RetrofitClient;
import com.furniture.app.data.remote.interceptor.AuthInterceptor;
import com.furniture.app.ui.adapter.ViewPagerAdapter;
import com.furniture.app.ui.auth.LoginActivity;
import com.furniture.app.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerMainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        // Khi token hết hạn (401), tự động đăng xuất và về màn login
        AuthInterceptor.setUnauthorizedHandler(() -> runOnUiThread(() -> {
            new SessionManager(this).clearSession();
            RetrofitClient.resetInstance();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("message", "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
            startActivity(intent);
        }));

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup ViewPager with adapter
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Disable swipe gesture (optional - remove this line if you want swipe navigation)
        viewPager.setUserInputEnabled(false);

        // Sync ViewPager with BottomNavigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int position = 0;
            if (item.getItemId() == R.id.nav_home) {
                position = 0;
            } else if (item.getItemId() == R.id.nav_search) {
                position = 1;
            } else if (item.getItemId() == R.id.nav_cart) {
                position = 2;
            } else if (item.getItemId() == R.id.nav_profile) {
                position = 3;
            }
            viewPager.setCurrentItem(position, false);
            return true;
        });

        // Sync BottomNavigation with ViewPager (when swiping)
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int itemId;
                switch (position) {
                    case 1:
                        itemId = R.id.nav_search;
                        break;
                    case 2:
                        itemId = R.id.nav_cart;
                        break;
                    case 3:
                        itemId = R.id.nav_profile;
                        break;
                    default:
                        itemId = R.id.nav_home;
                        break;
                }
                bottomNavigationView.setSelectedItemId(itemId);
            }
        });
    }

    public void navigateToTab(int position) {
        if (viewPager != null && position >= 0 && position < 4) {
            viewPager.setCurrentItem(position, true);
        }
    }
}
