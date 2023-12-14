package com.wallet.ctc.ui.me.chain_bridge2.timeline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityChainBridgeTimelineBinding;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderDetailBean;
import com.wallet.ctc.util.AllUtils;

import common.app.base.BaseActivity;
import common.app.ui.view.TitleBarView;
import common.app.utils.TimeUtil;


public class ChainBridgeTimeLineActivity extends BaseActivity<ChainBridgeTimeLineVM> {
    
    private ActivityChainBridgeTimelineBinding mVBinding;
    private ChainBridgeTimeLineAdapter mTimeLineAdapter;
    private static final String KEY_MAIN_ORDER_ID = "mainOrderId";
    private long mMainOrderId;


    public static Intent getIntent(Context from, long mainOrderId){
        Intent intent = new Intent(from, ChainBridgeTimeLineActivity.class);
        intent.putExtra(KEY_MAIN_ORDER_ID, mainOrderId);
        return intent;
    }

    @Override
    public void initParam() {
        mMainOrderId = getIntent().getLongExtra(KEY_MAIN_ORDER_ID, 0);
        if (mMainOrderId == 0){
            setForceIntercept(true);
            showToast(R.string.data_error);
            finish();
            return;
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityChainBridgeTimelineBinding.inflate(getLayoutInflater());
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
            }
        });
        mTimeLineAdapter = new ChainBridgeTimeLineAdapter(this);
        mVBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mVBinding.recyclerView.setAdapter(mTimeLineAdapter);
    }

    @Override
    public void initData() {
        getViewModel().observe(getViewModel().mOrdersLD, detail->{
            showData(detail);
        });
        startTimerGetData();
    }

    private void startTimerGetData() {
        getViewModel().startTimer(mMainOrderId);
    }


    @SuppressLint("SetTextI18n")
    private void showData(ChainBridgeOrderDetailBean orderDetailData){
        ChainBridgeOrderBean mainOrder = orderDetailData.getMainOrder();
        if (null == mainOrder){
            return;
        }

        
        if (mainOrder.status == 0){
            
            if (mainOrder.hasNoPrivateKeyError()){
                
                mVBinding.statusIv.setImageResource(R.mipmap.ico_chain_bridge_status_pause);
                mVBinding.statusTv.setText(R.string.chain_bridge_status_pause);
            } else {
                
                mVBinding.statusIv.setImageResource(R.mipmap.ico_chain_bridge_status_ing);
                mVBinding.statusTv.setText(R.string.chain_bridge_status_ing);
            }
        } else {
            
            mVBinding.statusIv.setImageResource(R.mipmap.ico_chain_bridge_status_ok);
            if (mainOrder.status == 1) {
                
                mVBinding.statusTv.setText(R.string.chain_bridge_status_complete);
            } else if(mainOrder.status == 3){
                
                mVBinding.statusTv.setText(R.string.chain_b_order_has_cancel);
            } else {
                mVBinding.statusTv.setText(R.string.chain_b_step_status_complete);
            }
        }

        
        int buyWalletType = ChatSdk.chainNameToType(mainOrder.buy_chain);
        AssertBean fromExAsset = WalletUtil.getUsdtAssert(buyWalletType);
        AssertBean fromChainAsset = WalletUtil.getMainChainAssert(this, buyWalletType);

        int sellWalletType = ChatSdk.chainNameToType(mainOrder.sell_chain);
        AssertBean toExAsset = WalletUtil.getUsdtAssert(sellWalletType);
        AssertBean toChainAsset = WalletUtil.getMainChainAssert(this, sellWalletType);

        if (null != fromExAsset && null != toExAsset){
            mVBinding.fromAssetCoinIv.setImageResource(fromExAsset.getLogo());
            mVBinding.fromCoinIv.setImageResource(fromChainAsset.getLogo());

            mVBinding.toAssetCoinIv.setImageResource(toExAsset.getLogo());
            mVBinding.toCoinIv.setImageResource(toChainAsset.getLogo());
        }
        mVBinding.fromCoinTv.setText(fromExAsset.getShortNameUpCase()+"-"+fromChainAsset.getShortNameUpCase());
        mVBinding.fromNumTv.setText(AllUtils.getTenDecimalValue(mainOrder.order_amount,18, 4));
        mVBinding.toCoinTv.setText(toExAsset.getShortNameUpCase()+"-"+toChainAsset.getShortNameUpCase());
        mVBinding.toNumTv.setText(AllUtils.getTenDecimalValue(mainOrder.receive_amount, 18, 4));

        
        long timeSeconds = orderDetailData.getAllUseTimeSeconds();
        String useTimeStr = "";
        if(timeSeconds > 0){
            useTimeStr = TimeUtil.getRemainTime(timeSeconds);
        } else {
            useTimeStr = "--";
        }
        mVBinding.timeTV.setText(getString(R.string.all_use_time_title)+useTimeStr);

        
        mTimeLineAdapter.bindDatas(orderDetailData.getTimeLineSteps(ChainBridgeTimeLineActivity.this));

        
        if (mainOrder.isMainOrderExchangeIng()){
            
            mVBinding.scrollView.fullScroll(View.FOCUS_DOWN);
        }

    }
}
