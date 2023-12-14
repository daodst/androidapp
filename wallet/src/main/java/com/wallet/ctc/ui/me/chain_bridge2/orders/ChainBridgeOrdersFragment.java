package com.wallet.ctc.ui.me.chain_bridge2.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.wallet.ctc.databinding.FragmentChainBridgeOrdersBinding;

import common.app.base.BaseFragment;
import common.app.ui.view.PullToRefreshLayout;


public class ChainBridgeOrdersFragment extends BaseFragment<ChainBridgeOrdersVM> {
    FragmentChainBridgeOrdersBinding mVBinding;
    public static final int TYPE_ING = 0;
    public static final int TYPE_COMPLETE = 1;
    private static final String KEY_TYPE = "type";
    private int mType;
    private ChainBridgeOrdersAdapter mOrdersAdapter;
    private Integer mNowPage = 1;
    private String mFilterAddr;
    private int mFilterWType;

    public static ChainBridgeOrdersFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(KEY_TYPE, type);
        ChainBridgeOrdersFragment fragment = new ChainBridgeOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initParam() {
        mType = getArguments().getInt(KEY_TYPE);
    }

    @Override
    public View initBindingView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVBinding = FragmentChainBridgeOrdersBinding.inflate(inflater, container, false);
        return mVBinding.getRoot();
    }


    @Override
    public void initView(@Nullable View view) {
        mOrdersAdapter = new ChainBridgeOrdersAdapter(getContext(), mType);
        mVBinding.pullableListView.setAdapter(mOrdersAdapter);
        mVBinding.pullableListView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                mNowPage = 1;
                getDatas();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                mNowPage++;
                getDatas();
            }
        });
        mOrdersAdapter.setOnRefreshDataListener(()->{
            
            if (null != mVBinding.pullableListView){
                mVBinding.pullableListView.autoRefresh();
            }
        });

    }

    @Override
    public void initData() {
        getViewModel().observe(getViewModel().mOrdersLD, ordersList->{
            mVBinding.pullableListView.showDatas(ordersList, mNowPage, mOrdersAdapter);
        });

        getViewModel().observe(getViewModel().mRefreshStatusLD, success->{
            mVBinding.pullableListView.setPullState(success, mNowPage);
        });

        getDatas();
    }

    public void setFilte(String address, int walletType){
        if (isDetached()){
            return;
        }
        this.mFilterAddr = address;
        this.mFilterWType = walletType;
        getDatas();
    }


    private void getDatas() {
        getViewModel().getOrders(mNowPage, mType, mFilterAddr, mFilterWType);
    }

}
