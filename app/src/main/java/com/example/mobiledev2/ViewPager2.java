package com.example.mobiledev2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPager2 extends FragmentStateAdapter {
    public ViewPager2(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new first();
            case 1: return new second();
            case 2: return new third();
            case 3: return new fourth();
            default: return new first();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Jumlah tab
    }
}
