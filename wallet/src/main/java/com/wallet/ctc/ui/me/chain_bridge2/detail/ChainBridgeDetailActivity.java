package com.wallet.ctc.ui.me.chain_bridge2.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wallet.ctc.R;
import com.wallet.ctc.databinding.ActivityChainBridgeDetailBinding;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.ui.me.chain_bridge2.timeline.ChainBridgeTimeLineActivity;

import java.util.ArrayList;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;


public class ChainBridgeDetailActivity extends BaseActivity<ChainBridgeDetailVM> implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityChainBridgeDetailBinding mBinding;
    private ChainBridgeDetailAdapter mAdapter;
    private static final String KEY_ORDER_ID = "orderId";
    private long mMainOrderId;

    public static Intent getIntent(Context from, long mainOrderId){
        Intent intent = new Intent(from, ChainBridgeDetailActivity.class);
        intent.putExtra(KEY_ORDER_ID, mainOrderId);
        return intent;
    }

    @Override
    public void initParam() {
        mMainOrderId = getIntent().getLongExtra(KEY_ORDER_ID, 0);
        if (mMainOrderId == 0){
            setForceIntercept(true);
            showToast(R.string.data_error);
            finish();
            return;
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mBinding = ActivityChainBridgeDetailBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mBinding.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                startActivity(ChainBridgeTimeLineActivity.getIntent(ChainBridgeDetailActivity.this, mMainOrderId));
            }
        });

        mBinding.refreshView.setOnRefreshListener(this);
        mAdapter = new ChainBridgeDetailAdapter(new ArrayList<>());
        mAdapter.setEmptyView(R.layout.has_no_data, mBinding.rvList);
        mBinding.rvList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            ChainBridgeOrderBean entity = (ChainBridgeOrderBean) adapter.getData().get(position);
            entity.isExpand = !entity.isExpand;
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void initData() {
        getViewModel().mOrdersLD.observe(this, orders -> {
            if (mBinding.refreshView.isRefreshing()) mBinding.refreshView.setRefreshing(false);
            mAdapter.setNewData(orders);
        });

        getData(true);
    }

    
    @Override
    public void onRefresh() {
        getData(false);
    }


    private void getData(boolean showloading) {
        getViewModel().getData(mMainOrderId, showloading);
    }
}
