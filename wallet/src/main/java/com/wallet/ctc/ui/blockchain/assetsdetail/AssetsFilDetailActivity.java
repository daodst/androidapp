

package com.wallet.ctc.ui.blockchain.assetsdetail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.base.BaseWebViewActivity;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityFilassetsdetailBinding;
import com.wallet.ctc.databinding.AssetsBalanceInfoLayoutBinding;
import com.wallet.ctc.databinding.AssetsTokenInfoLayoutBinding;
import com.wallet.ctc.databinding.TitleBarBinding;
import com.wallet.ctc.databinding.TopRecordTypesLayoutBinding;
import com.wallet.ctc.databinding.TransferReceiveBtnLayoutBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.FilTransRecordBean;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.tokendetail.EthTokenDetailActivity;
import com.wallet.ctc.ui.blockchain.tokendetail.TokenDetailActivity;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionFilRecordAdapter;
import com.wallet.ctc.ui.blockchain.transfer.TransfeZecActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferBTCActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferBchActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferDogeActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferDotActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferEtcActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferEthActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferFilActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferLtcActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferSolActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferTrxActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferXrpActivity;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferFilDetailActivity;
import com.wallet.ctc.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.PullToRefreshLayout;



public class AssetsFilDetailActivity extends BaseActivity<AssetsDetailViewModel> {
    private ActivityFilassetsdetailBinding mViewB;
    private TitleBarBinding mTitleBarViewB;
    private AssetsBalanceInfoLayoutBinding mBalanceViewB;
    private AssetsTokenInfoLayoutBinding mTokenInfoViewB;
    private TopRecordTypesLayoutBinding mTypesViewB;
    private TransferReceiveBtnLayoutBinding mBtnViewB;

    private TransactionFilRecordAdapter mAdapter;
    private Intent intent;
    private String coinName = "";
    private String tokenType = "";
    private String gascount = "";
    private int decimal = 18;
    private String DbPrice;
    private String DbNum;
    private String Logo;
    private Gson gson = new Gson();
    private List<FilTransRecordBean.DocsBean> mData = new ArrayList<>();
    private WalletDBUtil walletDBUtil;
    private int walletType;
    private String walletAddress;
    private int mNowPage = 1;
    private boolean refreshFlag = true;

    private static final String KEY_ASSET = "assert";
    private static final String KEY_WALLET_ADDR = "walletAddress";
    private static final String KEY_WALLET_TYPE = "walletType";
    private AssertBean mAssert;
    public static Intent getIntent(Context from, AssertBean assertBean, String walletAddress, int walletType) {
        Intent intent = new Intent(from, AssetsFilDetailActivity.class);
        intent.putExtra(KEY_ASSET, assertBean);
        intent.putExtra(KEY_WALLET_ADDR, walletAddress);
        intent.putExtra(KEY_WALLET_TYPE, walletType);
        return intent;
    }


    @Override
    public void initParam() {
        mAssert = (AssertBean) getIntent().getSerializableExtra(KEY_ASSET);
        if (null == mAssert || TextUtils.isEmpty(mAssert.getShort_name())) {
            setForceIntercept(true);
            showToast(R.string.data_error);
            finish();
            return;
        }
        walletDBUtil = WalletDBUtil.getInstent(this);
        WalletEntity wallet = walletDBUtil.getWalletInfo();
        if (null == wallet) {
            setForceIntercept(true);
            showToast(R.string.no_found_wallet_error);
            finish();
            return;
        }
        walletAddress = getIntent().getStringExtra(KEY_WALLET_ADDR);
        if (TextUtils.isEmpty(walletAddress)) {
            walletAddress = wallet.getAllAddress();
        }
        walletType = getIntent().getIntExtra(KEY_WALLET_TYPE, -1);
        if (walletType < 0) {
            walletType = wallet.getType();
        }
        coinName = mAssert.getShort_name();
        DbPrice = mAssert.getAssertsSumPrice();
        if (TextUtils.isEmpty(DbPrice)) {
            DbPrice = "0";
        }
        DbNum = mAssert.getAssertsNum();
        if (TextUtils.isEmpty(DbNum)) {
            DbNum = "0.00";
        }
        tokenType = mAssert.getContract();
        if (tokenType == null) {
            tokenType = "";
        }
        gascount = mAssert.getGas();
        Logo = mAssert.getImg_path();
        decimal = mAssert.getDecimal();
        if (decimal == 0) {
            decimal = 18;
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityFilassetsdetailBinding.inflate(LayoutInflater.from(this));
        mTitleBarViewB = TitleBarBinding.bind(mViewB.titleInclude.getRoot());
        mBalanceViewB = AssetsBalanceInfoLayoutBinding.bind(mViewB.balanceInclude.getRoot());
        mTokenInfoViewB = AssetsTokenInfoLayoutBinding.bind(mViewB.tokeninfoInclude.getRoot());
        mTypesViewB = TopRecordTypesLayoutBinding.bind(mViewB.typetabsInclude.getRoot());
        mBtnViewB = TransferReceiveBtnLayoutBinding.bind(mViewB.btnsInclude.getRoot());
        return mViewB.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {

        
        mTitleBarViewB.tvBack.setOnClickListener(view1 -> {
            finish();
        });
        mTitleBarViewB.tvTitle.setText(coinName.toUpperCase());

        
        mTitleBarViewB.imgAction.setImageResource(R.mipmap.eth_token_detail);
        mTitleBarViewB.imgAction.setVisibility(View.GONE);

        
        mBalanceViewB.daibNum.setText(DbNum);
        mBalanceViewB.daibPrice.setText("≈ ¥"+DbPrice);

        
        mAdapter = new TransactionFilRecordAdapter(this, coinName, decimal);
        mAdapter.bindAddress(walletAddress);
        mAdapter.bindData(mData);
        mViewB.listview.setAdapter(mAdapter);
        mViewB.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssetsFilDetailActivity.this, TransferFilDetailActivity.class);
                intent.putExtra("type", coinName);
                intent.putExtra("detail", gson.toJson(mData.get(position)));
                startActivity(intent);
                refreshFlag = false;
            }
        });
        
        mViewB.refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
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

        if (walletType == WalletUtil.MCC_COIN) {
            mViewB.goBrowser.setVisibility(View.GONE);
        } else {
            mViewB.goBrowser.setVisibility(View.VISIBLE);
        }

        
        setViewClickListner();
    }

    
    public void setViewClickListner() {
        
        mViewB.goBrowser.setOnClickListener(v -> {
            if (walletType == WalletUtil.ETH_COIN) {
                
                String url = "https://cn.etherscan.com/address/"+walletAddress;
                if (!TextUtils.isEmpty(coinName) && !TextUtils.isEmpty(tokenType)) {
                    
                    AssertBean mustBean=walletDBUtil.getMustWallet(walletType).get(0);
                    if (!coinName.equalsIgnoreCase(mustBean.getShort_name()) || !tokenType.equalsIgnoreCase(mustBean.getContract())) {
                        url = "https://cn.etherscan.com/token/"+tokenType+"?a="+walletAddress;
                    }
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } else if(walletType == WalletUtil.BNB_COIN) {
                
                
                
                String url = "https://bscscan.com/address/"+walletAddress;
                if (!TextUtils.isEmpty(coinName) && !TextUtils.isEmpty(tokenType)) {
                    
                    AssertBean mustBean=walletDBUtil.getMustWallet(walletType).get(0);
                    if (!coinName.equalsIgnoreCase(mustBean.getShort_name()) || !tokenType.equalsIgnoreCase(mustBean.getContract())) {
                        url = "https://bscscan.com/token/"+tokenType+"?a="+walletAddress;
                    }
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        
        mTitleBarViewB.imgAction.setOnClickListener(view1 -> {
            if(walletType == WalletUtil.ETH_COIN || walletType == WalletUtil.HT_COIN ||
                    walletType == WalletUtil.BNB_COIN|| walletType == WalletUtil.ETF_COIN||
                    walletType == WalletUtil.DMF_COIN|| walletType == WalletUtil.DMF_BA_COIN){
                intent = new Intent(this, EthTokenDetailActivity.class);
                intent.putExtra("tokenName",coinName);
                intent.putExtra("logo", Logo);
                intent.putExtra("address",tokenType);
                intent.putExtra("wallettype",walletType);
                startActivity(intent);
            }else if(walletType== WalletUtil.TRX_COIN){
                intent = new Intent(this, BaseWebViewActivity.class);
                intent.putExtra("title", coinName.toUpperCase() + getString(R.string.token_detail2));
                intent.putExtra("url", "https://tronscan.io/#/token20/" + tokenType);
                startActivity(intent);
            }else if(walletType== WalletUtil.DM_COIN||walletType== WalletUtil.MCC_COIN||walletType== WalletUtil.OTHER_COIN){
                intent = new Intent(this, TokenDetailActivity.class);
                intent.putExtra("tokenName", coinName);
                intent.putExtra("logo", Logo);
                intent.putExtra("type", walletType);
                startActivity(intent);
            }
        });

        
        mBtnViewB.transfer.setOnClickListener(view1 -> {
            if(walletDBUtil.getWalletInfo().getLevel()==-1){
                ToastUtil.showToast(getString(R.string.wallet_type_error));
                return;
            }
            if (NetUtils.isNetworkConnected(this)) {
                if(walletType== WalletUtil.ETC_COIN){
                    intent = new Intent(this, TransferEtcActivity.class);
                }else if(walletType== WalletUtil.FIL_COIN){
                    intent = new Intent(this, TransferFilActivity.class);
                }else if(walletType== WalletUtil.DOT_COIN){
                    intent = new Intent(this, TransferDotActivity.class);
                }else if(walletType== WalletUtil.DOGE_COIN){
                    intent = new Intent(this, TransferDogeActivity.class);
                }else if(walletType== WalletUtil.BCH_COIN){
                    intent = new Intent(this, TransferBchActivity.class);
                }else if(walletType== WalletUtil.ZEC_COIN){
                    intent = new Intent(this, TransfeZecActivity.class);
                }else if(walletType== WalletUtil.LTC_COIN){
                    intent = new Intent(this, TransferLtcActivity.class);
                }else if(walletType== WalletUtil.BTC_COIN){
                    intent = new Intent(this, TransferBTCActivity.class);
                }else if(walletType== WalletUtil.TRX_COIN){
                    intent = new Intent(this, TransferTrxActivity.class);
                }else if(walletType== WalletUtil.XRP_COIN){
                    intent = new Intent(this, TransferXrpActivity.class);
                }else if(walletType== WalletUtil.SOL_COIN){
                    intent = new Intent(this, TransferSolActivity.class);
                }else{
                    intent = new Intent(this, TransferEthActivity.class);
                }

                intent.putExtra("amountStr", "0");
                intent.putExtra("tokenType", tokenType);
                intent.putExtra("toAddress", "");
                intent.putExtra("tokenName", coinName);
                intent.putExtra("gasCount", gascount);
                intent.putExtra("decimal", decimal);
                intent.putExtra("from", 1);
                startActivityForResult(intent, 1000);
            } else {
                ToastUtil.showToast(getString(R.string.connect_failuer_toast));
            }
        });

        
        mBtnViewB.collectMoney.setOnClickListener(view1 -> {
            CollectMoneyActivity.startCollectMoneyActivity(this,walletType,tokenType,decimal);
        });
    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mRefreshStatusLD, success->{
            if (mNowPage == 1) {
                if (success) {
                    mViewB.refreshView.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mViewB.refreshView.refreshFinish(PullToRefreshLayout.FAIL);
                }
            } else {
                if (success) {
                    mViewB.refreshView.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mViewB.refreshView.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
        });

        
        getViewModel().observe(getViewModel().mEthRecordsLD, records->{
            if (mNowPage == 1) {
                mData.clear();
            } else {
                if (records == null || records.size() == 0) {
                    ToastUtil.showToast(getString(R.string.nomore));
                    mNowPage--;
                }
            }
            if (records != null) {
                mData.addAll(records);
            }
            mViewB.progressBar.setVisibility(View.GONE);
            if (mData.size() > 0) {
                mViewB.nodata.setVisibility(View.GONE);
            } else {
                mViewB.nodata.setVisibility(View.VISIBLE);
            }
            mAdapter.notifyDataSetChanged();
        });

        
        getViewModel().observe(getViewModel().mBalanceLD, balance->{
            if (!TextUtils.isEmpty(balance)) {
                DbNum = balance;
                mBalanceViewB.daibNum.setText(DbNum);
            }
        });
    }

    
    private void getDatas() {
        getViewModel().getDatas(mNowPage, walletAddress, mAssert);
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (refreshFlag) {
            mNowPage=1;
            getDatas();
        } else {
            refreshFlag = true;
        }
    }
}
