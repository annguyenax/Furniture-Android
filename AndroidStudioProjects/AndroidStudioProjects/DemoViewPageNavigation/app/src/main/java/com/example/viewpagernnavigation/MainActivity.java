package com.example.viewpagernnavigation;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.viewpagernnavigation.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.view_pager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.colorAccent)));
            getSupportActionBar().setTitle("Person");
        }

        ViewpageAdapter viewpageAdapter = new ViewpageAdapter(getSupportFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(viewpageAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mBottomNavigationView.getMenu().findItem(R.id.person).setChecked(true);
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Person");
                } else if (position == 1) {
                    mBottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Home");
                } else if (position == 2) {
                    mBottomNavigationView.getMenu().findItem(R.id.settings).setChecked(true);
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Setting");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.person) {
                    mViewPager.setCurrentItem(0);
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Person");
                } else if (itemId == R.id.home) {
                    mViewPager.setCurrentItem(1);
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Home");
                } else if (itemId == R.id.settings) {
                    mViewPager.setCurrentItem(2);
                    if (getSupportActionBar() != null) getSupportActionBar().setTitle("Setting");
                }
                return true;
            }
        });
    }
}
