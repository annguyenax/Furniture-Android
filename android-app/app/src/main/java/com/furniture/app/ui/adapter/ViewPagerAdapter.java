package com.furniture.app.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.furniture.app.ui.customer.cart.CartFragment;
import com.furniture.app.ui.customer.home.HomeFragment;
import com.furniture.app.ui.customer.profile.ProfileFragment;
import com.furniture.app.ui.customer.search.SearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 4;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new CartFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
