package com.app.view.dposmarket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.R;
import com.app.databinding.ActivityDposMarketBinding;
import com.app.home.pojo.DposListEntity;
import com.app.home.ui.ver.detial.VWalletVeryActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.tabs.TabLayout;
import com.wallet.ctc.view.TitleBarView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.utils.TimeUtil;
import im.vector.app.core.platform.SimpleTextWatcher;


public class DPosMarketActivity extends BaseActivity<DPosMarketActivityVM> implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private ActivityDposMarketBinding mBinding;

    private DPosMarketAdapter mAdapter;
    public boolean isRefresh = true;

    
    private String yield, offline_penalties, redemption_cycle, signature_penalty;

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityDposMarketBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, DPosMarketActivity.class);
        return intent;
    }

    @Override
    public void initView(@Nullable View view) {
        Eyes.setTranslucent(this);
        Eyes.addStatusBar(this, mBinding.vStatusBar, ContextCompat.getColor(this, R.color.transparent));
        initTabLayout();

        mBinding.titleBarView.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });

        mBinding.refresh.setOnRefreshListener(this);
        mBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            mBinding.refresh.setEnabled(verticalOffset >= 0);
        });

        mAdapter = new DPosMarketAdapter(new ArrayList<>());
        mAdapter.setEmptyView(R.layout.mk_orders_empty, mBinding.rvList);
        mAdapter.setOnLoadMoreListener(this, mBinding.rvList);
        mAdapter.setPreLoadNumber(3);
        mBinding.rvList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            DposListEntity.ValidatorListEntity entity = (DposListEntity.ValidatorListEntity) adapter.getData().get(position);
            startActivity(VWalletVeryActivity.getIntent(this, entity.validatorAddr));
        });

        mBinding.etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(@NonNull Editable s) {
                getViewModel().searchKeyword = s.toString();
                isRefresh = true;
                getViewModel().onRefresh();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {
        super.initData();
        getViewModel().getTopInfo();
        getViewModel().observe(viewModel.mDposInfoLiveData, dposInfo -> {
            yield = dposInfo.mint_inflation;
            offline_penalties = dposInfo.slashing_params.getSlash_fraction_downtime();
            redemption_cycle = TimeUtil.getdd(dposInfo.staking_params.unbonding_time);
            signature_penalty = dposInfo.slashing_params.getSlash_fraction_double_sign();

            
            mBinding.yield.setText(getPrec(yield));
            mBinding.offlinePenalties.setText(offline_penalties);
            mBinding.redemptionCycle.setText(redemption_cycle + getString(R.string.day));
            mBinding.signaturePenalty.setText(signature_penalty);
        });

        getViewModel().onRefresh();
        getViewModel().observe(viewModel.mDposListLiveData, dposListEntity -> {
            
            setData(dposListEntity.validatorList);
        });
    }

    
    private String getPrec(String data) {
        try {
            BigDecimal decimal = new BigDecimal(data).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
            return decimal.toPlainString() + "%";
        } catch (Exception e) {
            return "--%";
        }
    }

    
    private void initTabLayout() {
        TabLayout tabLayout = mBinding.tabLayout;
        tabLayout.addTab(tabLayout.newTab().setText(R.string.dpos_new_string_tab_1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.dpos_new_string_tab_2));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.dpos_new_string_tab_3));
        mBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getViewModel().isDelegate = tab.getPosition();
                isRefresh = true;
                getViewModel().onRefresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        
        mBinding.tvZongHe.setOnClickListener(v -> {
            getViewModel().zhSortAsc = !getViewModel().zhSortAsc;
            sift(1);
        });

        mBinding.tvWeight.setOnClickListener(v -> {
            getViewModel().weightSortAsc = !getViewModel().weightSortAsc;
            viewModel.sortType = viewModel.weightSortAsc ? 1 : 2;
            sift(2);
            isRefresh = true;
            getViewModel().onRefresh();
        });
        mBinding.tvCommission.setOnClickListener(v -> {
            getViewModel().commissionSortAsc = !getViewModel().commissionSortAsc;
            viewModel.sortType = viewModel.commissionSortAsc ? 3 : 4;
            sift(3);
            isRefresh = true;
            getViewModel().onRefresh();
        });

        
        mBinding.tvAliveQuantity.setOnClickListener(v -> {
            getViewModel().aliveSortAsc = !getViewModel().aliveSortAsc;
            sift(4);
        });

        mBinding.tvTopValue5.setText(getString(R.string.dpos_new_string_vote_rate, "1"));
        
        
    }

    
    private void sift(int type) {
        
        
        
        siftUi(type == 2, getViewModel().weightSortAsc, mBinding.tvWeight);
        
        siftUi(type == 3, getViewModel().commissionSortAsc, mBinding.tvCommission);
        
        
    }

    
    private void siftUi(boolean isCheck, boolean sortType, TextView textView) {
        Drawable up = ContextCompat.getDrawable(this, R.mipmap.dpos_icon_up);
        Drawable up_s = ContextCompat.getDrawable(this, R.mipmap.dpos_icon_up_s);
        Drawable down = ContextCompat.getDrawable(this, R.mipmap.dpos_icon_down);
        Drawable down_s = ContextCompat.getDrawable(this, R.mipmap.dpos_icon_down_s);

        if (isCheck) {
            if (sortType) textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, up_s, null);
            else textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, down_s, null);
        } else {
            if (sortType) textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, up, null);
            else textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, down, null);
        }
    }

    
    @Override
    public void onRefresh() {
        isRefresh = true;
        getViewModel().getListData();
        getViewModel().onRefresh();
    }

    @Override
    public void onLoadMoreRequested() {
        isRefresh = false;
        getViewModel().onLoadMore();
    }

    private void setData(List<DposListEntity.ValidatorListEntity> data) {
        if (isRefresh) {
            mBinding.refresh.setRefreshing(false);
            mAdapter.setNewData(data);
        } else {
            if (data == null) mAdapter.loadMoreFail();
            else {
                mAdapter.addData(data);
                if (data.size() == 0) mAdapter.loadMoreEnd();
                else mAdapter.loadMoreComplete();
            }
        }
    }
}
