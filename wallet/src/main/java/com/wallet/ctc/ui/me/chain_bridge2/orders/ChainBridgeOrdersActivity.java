package com.wallet.ctc.ui.me.chain_bridge2.orders;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.ActivityChainBridgeOrdersBinding;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;


public class ChainBridgeOrdersActivity extends BaseActivity<ChainBridgeOrdersVM> {
    ActivityChainBridgeOrdersBinding mVBinding;
    private List<ChainBridgeOrdersFragment> mFragments;
    private List<String> mTabTitles;
    private int mType = 0;

    @Override
    public void initParam() {
        mType = getIntent().getIntExtra("type", 0);
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityChainBridgeOrdersBinding.inflate(getLayoutInflater());
        return mVBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mVBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                ChooseWalletDialog.showDialog(ChainBridgeOrdersActivity.this, -1, (address, walletType) -> {
                    for(int i=0; i<mFragments.size(); i++){
                        mFragments.get(i).setFilte(address, walletType);
                    }
                });
            }
        });


        mFragments = new ArrayList<>();
        mFragments.add(ChainBridgeOrdersFragment.newInstance(ChainBridgeOrdersFragment.TYPE_ING));
        mFragments.add(ChainBridgeOrdersFragment.newInstance(ChainBridgeOrdersFragment.TYPE_COMPLETE));
        mTabTitles = new ArrayList<>();
        mTabTitles.add(getString(R.string.chain_bridge_status_ing));
        mTabTitles.add(getString(R.string.chain_bridge_status_complete));
        mVBinding.viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTabTitles.get(position);
            }
        });
        mVBinding.tabLayout.setupWithViewPager(mVBinding.viewPager);
        if(mType == 1){
            
            mVBinding.viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void initData() {
        super.initData();
    }
}
