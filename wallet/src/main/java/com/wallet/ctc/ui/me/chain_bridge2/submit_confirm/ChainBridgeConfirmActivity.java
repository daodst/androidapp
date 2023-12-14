package com.wallet.ctc.ui.me.chain_bridge2.submit_confirm;

import static com.wallet.ctc.ui.me.chain_bridge2.ChainBridgeActivity2.ASSET_USDT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityChainSubmitConfirmBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgeConfigBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.model.blockchain.ChainBridgePreOrdersBean;
import com.wallet.ctc.ui.me.chain_bridge2.orders.ChainBridgeOrdersActivity;
import com.wallet.ctc.ui.me.chain_bridge2.timeline.ChainBridgeTimeLineActivity;
import com.wallet.ctc.util.DecriptUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.TitleBarView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class ChainBridgeConfirmActivity extends BaseActivity<ChainBridgeConfirmVM> {

    ActivityChainSubmitConfirmBinding mVBinding;
    ChainBridgePreOrderAdapter mOrderAdapter;
    WalletEntity fromWallet, toWallet;
    AssertBean fromExAssert, fromChainAssert, toExAssert, toChainAssert;
    String mCount, mAssetCoin;
    private ChainBridgePreOrdersBean mOrderData;
    private static final String KEY_FROM_WALLET = "fromWallet";
    private static final String KEY_TO_WALLET = "toWallet";
    private static final String KEY_COUNT = "count";
    private static final String KEY_ASSET_COIN = "assetCoin";
    private String mBalance;


    
    public static Intent getIntent(Context from, WalletEntity fromWallet, WalletEntity toWallet, String count, String exAssetCoin) {
        Intent intent = new Intent(from, ChainBridgeConfirmActivity.class);
        intent.putExtra(KEY_FROM_WALLET, fromWallet);
        intent.putExtra(KEY_TO_WALLET, toWallet);
        intent.putExtra(KEY_COUNT, count);
        intent.putExtra(KEY_ASSET_COIN, exAssetCoin);
        return intent;
    }

    @Override
    public void initParam() {
        fromWallet = getIntent().getParcelableExtra(KEY_FROM_WALLET);
        toWallet = getIntent().getParcelableExtra(KEY_TO_WALLET);
        mCount = getIntent().getStringExtra(KEY_COUNT);
        mAssetCoin = getIntent().getStringExtra(KEY_ASSET_COIN);
        Log.i("testCh", ""+fromWallet+", "+toWallet+", "+mCount+", "+mAssetCoin);
        if (null == fromWallet || null == toWallet || TextUtils.isEmpty(mCount) || TextUtils.isEmpty(mAssetCoin)) {
            setForceIntercept(true);
            showToast(R.string.data_error);
            finish();
            return;
        }

        fromExAssert = getExAssert(fromWallet);
        fromChainAssert = getShowAssert(fromWallet);
        toExAssert = getExAssert(toWallet);
        toChainAssert = getShowAssert(toWallet);
        if(fromExAssert == null || fromChainAssert == null || toExAssert == null || toChainAssert == null){
            setForceIntercept(true);
            showToast(R.string.data_error);
            finish();
            return;
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mVBinding = ActivityChainSubmitConfirmBinding.inflate(getLayoutInflater());
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

        
        mVBinding.refreshIv.setOnClickListener(view1 -> {
            getViewModel().preOrder(fromWallet, toWallet, mCount, getExAssert(fromWallet), getExAssert(toWallet));
        });

        
        mVBinding.confirmBtn.setOnClickListener(view1 -> {
            if (null == mOrderData || !mOrderData.isHasOrders()){
                return;
            }

            if (null != mOrderData.chainBridgeConfig && mOrderData.chainBridgeConfig.isEnabled()){
                
                if (TextUtils.isEmpty(mBalance)){
                    showToast(getString(R.string.req_balance_info));
                    getBalance(true);
                    return;
                }
                BigDecimal needPayNum = new BigDecimal(mCount).multiply(new BigDecimal(mOrderData.chainBridgeConfig.data.ratio));
                if (needPayNum.compareTo(new BigDecimal(mBalance)) > 0) {
                    showToast(R.string.chain_bridge_to_wallet_no_balance);
                    return;
                }
            }
            showAlertDialog(getString(R.string.chain_bridge_auth_prikey_alert), ok->{
                dismissDialog();
                submit();
            }, cancle->{
                dismissDialog();
            });
            mAlertDialog.setTitle(getString(R.string.chain_bridge_auth_prikey_title));
            mAlertDialog.setYesText(getString(R.string.confirm_auth));
            mAlertDialog.setContentGravity(Gravity.LEFT);
            mAlertDialog.setYesTextColor(R.color.default_theme_color);
        });


        
        if (null != toWallet) {
            mVBinding.receiveAddrTv.setText(toWallet.getAllAddress());
            mVBinding.receiveWalletNameTv.setText("("+toWallet.getName()+")");
        }

        
        mVBinding.sendNumTv.setText(mCount);
        mVBinding.exAllNumTv.setText(mCount+" "+getString(R.string.only_unint));
        
        mVBinding.exCoinIv.setImageResource(fromExAssert.getLogo());
        mVBinding.exCoinNameTv.setText(fromExAssert.getShortNameUpCase()+"-"+fromChainAssert.getShortNameUpCase());

        
        mVBinding.receiveCoinTv.setText(toExAssert.getShortNameUpCase()+"-"+toChainAssert.getShortNameUpCase());

        
        mOrderAdapter = new ChainBridgePreOrderAdapter(this);
        mVBinding.listView.setAdapter(mOrderAdapter);
    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mBalanceLD, remain->{
            mBalance = remain;
        });

        
        getViewModel().observe(getViewModel().mPreOrderLD, data->{
            showOrder(data);
        });

        
        getViewModel().observe(getViewModel().mSubmitLD, mainOrderId->{
            if (null != mainOrderId){
                showToast(R.string.submit_success);
                if (mainOrderId > 0){
                    startActivity(ChainBridgeTimeLineActivity.getIntent(ChainBridgeConfirmActivity.this, mainOrderId));
                } else {
                    startActivity(new Intent(ChainBridgeConfirmActivity.this, ChainBridgeOrdersActivity.class));
                }
                finish();
            }
        });


        getPreOrderData();
    }

    
    private void getPreOrderData(){
        getViewModel().preOrder(fromWallet, toWallet, mCount, fromExAssert, toExAssert);
    }


    
    private void showOrder(ChainBridgePreOrdersBean orderData) {
        mOrderData = orderData;
        if (null == mOrderData || !mOrderData.isHasOrders()){
            return;
        }
        mOrderAdapter.bindDatas(mOrderData.getPreOrders());
        
        String receiveAmount = mOrderData.getReceiveAmount(18);
        mVBinding.receiveNumTv.setText(receiveAmount);
        
        mVBinding.willReceiveNumTv.setText(receiveAmount+getString(R.string.only_unint));
        
        mVBinding.gasTipTv.setText(mOrderData.getShowGasRangeStr(fromChainAssert.getDecimal(), fromChainAssert.getShortNameUpCase(),
                toChainAssert.getDecimal(), toChainAssert.getShortNameUpCase()));

        ChainBridgeConfigBean configBean = mOrderData.chainBridgeConfig;
        if (null == configBean || !configBean.isSuccess() || configBean.data == null){
            String errorInfo = (configBean != null && TextUtils.isEmpty(configBean.getInfo())) ? configBean.getInfo() : getString(R.string.chain_bridge_get_bzj_fail);
            showToast(errorInfo);
        } else {
            if (configBean.data.enabled){
                mVBinding.baozhengJinLayout.setVisibility(View.VISIBLE);
                mVBinding.baoZhengJinTipTv.setVisibility(View.VISIBLE);
                String number = new BigDecimal(mCount).multiply(new BigDecimal(configBean.data.ratio)).toPlainString();
                
                mVBinding.baoZhengJinTv.setText(number+toExAssert.getShortNameUpCase()+"-"+toChainAssert.getShortNameUpCase());
                String radioPercent = new BigDecimal(configBean.data.ratio).multiply(new BigDecimal(100)).toPlainString()+"%";
                mVBinding.baoZhengJinTipTv.setText(String.format(getString(R.string.chain_bridge_baozhengjin_tip), radioPercent, configBean.data.days));
                if(TextUtils.isEmpty(mBalance)){
                    getBalance(false);
                }
            }
        }
    }


    private void submit(){
        if (null == mOrderData || !mOrderData.isHasOrders()){
            return;
        }
        InputPwdDialog.show(this, (pwd, dialog) -> {
            if (!fromWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                return;
            }
            dialog.dismiss();
            List<ChainBridgeOrderBean> orders = mOrderData.getPreOrders();
            StringBuilder idsStringBuilder = new StringBuilder();
            for (ChainBridgeOrderBean order : orders) {
                if(TextUtils.isEmpty(idsStringBuilder)){
                    idsStringBuilder.append(order.order_key);
                } else {
                    idsStringBuilder.append(",").append(order.order_key);
                }
            }
            getViewModel().submit(fromWallet, toWallet, mCount, pwd, fromExAssert, toExAssert, idsStringBuilder.toString());
        });
    }


    
    private void getBalance(boolean showLoading) {
        getViewModel().getBalance(toWallet, toExAssert, showLoading);
    }


    
    private AssertBean getShowAssert(WalletEntity wallet) {
        if (null == wallet){
            return null;
        }
        List<AssertBean> mustAsset = WalletDBUtil.getInstent(this).getMustWallet(wallet.getType());
        if (null != mustAsset && mustAsset.size() > 0) {
            return mustAsset.get(0);
        }
        return null;
    }

    
    private AssertBean getExAssert(WalletEntity wallet) {
        AssertBean assertBean = null;
        if (null != wallet) {
            if (ASSET_USDT.equals(mAssetCoin)){
                assertBean = WalletUtil.getUsdtAssert(wallet.getType());
            }
        }
        return assertBean;
    }


    private Disposable mTimeDispose;
    private void startTimer() {
        stopTimer();
        mTimeDispose = Observable.interval(5, 5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tikc->{
                    Log.i("chainBrisu", "tike-"+tikc);
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    private void stopTimer() {
        if (null != mTimeDispose) {
            mTimeDispose.dispose();
            mTimeDispose = null;
        }
    }
}
