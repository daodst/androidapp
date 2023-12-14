package com.app.view.groupinformation.introduction.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;


public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    private final ArrayList<Fragment> mFragments;

    public ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> pFragments) {
        super(fragmentActivity);
        mFragments = pFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }
}
