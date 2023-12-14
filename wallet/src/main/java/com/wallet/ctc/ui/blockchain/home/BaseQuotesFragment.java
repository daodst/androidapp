

package com.wallet.ctc.ui.blockchain.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;



public class BaseQuotesFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R2.id.main_tab)
    TabLayout mTab;
    @BindView(R2.id.main_viewpager)
    ViewPager mViewPager;
    private ArrayList<BaseFragment> fragmentList;
    private List<String> titleList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_quotes, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }


    private void initView(View view) {
        fragmentList = new ArrayList<>();
        
        titleList = new ArrayList<>();
        titleList.add("binance");
        titleList.add(getString(R.string.fire_coin));
        mTab = (TabLayout) view.findViewById(R.id.main_tab);
        initData();
    }

    private void initData() {
        for (int i = 0, a = titleList.size(); i < a; i++) {
            QuotesFragment fragment = new QuotesFragment();
            fragment.setType(titleList.get(i));
            fragmentList.add(fragment);
        }
        
        for (int i = 0; i < titleList.size(); i++) {
            mTab.addTab(mTab.newTab().setText(titleList.get(i)));
        }
        
        mTab.setupWithViewPager(mViewPager);
        
        
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return titleList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setOffscreenPageLimit(titleList.size());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R2.id.quotes_more)
    public void onViewClicked() {

    }
}
