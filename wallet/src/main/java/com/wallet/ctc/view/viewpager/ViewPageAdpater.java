

package com.wallet.ctc.view.viewpager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wallet.ctc.base.BaseFragment;

import java.util.List;


public class ViewPageAdpater extends FragmentPagerAdapter{

    private List<BaseFragment> fragments;

    public ViewPageAdpater(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
