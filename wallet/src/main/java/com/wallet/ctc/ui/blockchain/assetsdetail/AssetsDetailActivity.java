

package com.wallet.ctc.ui.blockchain.assetsdetail;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.databinding.ActivityAssetsdetailBinding;
import com.wallet.ctc.databinding.AssetsBalanceInfoLayoutBinding;
import com.wallet.ctc.databinding.AssetsTokenInfoLayoutBinding;
import com.wallet.ctc.databinding.TitleBarBinding;
import com.wallet.ctc.databinding.TopRecordTypesLayoutBinding;
import com.wallet.ctc.databinding.TransferReceiveBtnLayoutBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.TransactionRecordBean;
import com.wallet.ctc.ui.blockchain.choosenode.ChooseNodeActivity;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.income.IncomeActivity;
import com.wallet.ctc.ui.blockchain.tokendetail.TokenDetailActivity;
import com.wallet.ctc.ui.blockchain.transactionrecord.TransactionRecordAdapter;
import com.wallet.ctc.ui.blockchain.transfer.TransferActivity;
import com.wallet.ctc.ui.blockchain.transferdetail.TransferDetailActivity;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.PullToRefreshLayout;



public class AssetsDetailActivity extends BaseActivity<AssetsDetailViewModel> {

    private ActivityAssetsdetailBinding mViewB;
    private TitleBarBinding mTitleBarViewB;
    private AssetsBalanceInfoLayoutBinding mBalanceViewB;
    private AssetsTokenInfoLayoutBinding mTokenInfoViewB;
    private TopRecordTypesLayoutBinding mTypesViewB;
    private TransferReceiveBtnLayoutBinding mBtnViewB;

    private TransactionRecordAdapter mAdapter;
    private Intent intent;
    private String coinName = "";
    private int decimal = 18;
    private String DbNum;
    private String DbPrice;
    private List<TransactionRecordBean> mData = new ArrayList<>();
    private String Logo;
    private int status = 0;
    private int page = 1;
    private int assetType=-1;

    private boolean refreshFlag = true;

    private WalletDBUtil walletDBUtil;

    private static String KEY_ASSET = "assert";
    private AssertBean mAssert;

    
    public static Intent getIntent(Context from, AssertBean assertBean) {
        Intent intent = new Intent(from, AssetsDetailActivity.class);
        intent.putExtra(KEY_ASSET, assertBean);
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
        coinName = mAssert.getShort_name();
        assetType = mAssert.getType();
        DbNum = mAssert.getAssertsNum();
        if (null == DbNum || TextUtils.isEmpty(DbNum)) {
            DbNum = "0";
        }
        DbPrice = mAssert.getAssertsSumPrice();
        if (DbPrice == null || DbPrice.equals("")) {
            DbPrice = "0";
        }
        Logo = mAssert.getImg_path();
        decimal = mAssert.getDecimal();
        if (decimal == 0) {
            decimal = 18;
        }
        walletDBUtil = WalletDBUtil.getInstent(getApplicationContext());
        WalletEntity wallet = walletDBUtil.getWalletInfo();
        if (null == wallet) {
            setForceIntercept(true);
            showToast(R.string.no_found_wallet_error);
            finish();
            return;
        }
        if (assetType == -1 || assetType == 0) {
            assetType = wallet.getType();
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        mViewB = ActivityAssetsdetailBinding.inflate(LayoutInflater.from(this));
        mTitleBarViewB = TitleBarBinding.bind(mViewB.titleInclude.getRoot());
        mBalanceViewB = AssetsBalanceInfoLayoutBinding.bind(mViewB.balanceInclude.getRoot());
        mTokenInfoViewB = AssetsTokenInfoLayoutBinding.bind(mViewB.tokeninfoInclude.getRoot());
        mTypesViewB = TopRecordTypesLayoutBinding.bind(mViewB.typetabsInclude.getRoot());
        mBtnViewB = TransferReceiveBtnLayoutBinding.bind(mViewB.btnsInclude.getRoot());
        return mViewB.getRoot();
    }

    @Override
    public void initView(@Nullable View view) {
        mTitleBarViewB.tvTitle.setText(coinName.toUpperCase());
        mTitleBarViewB.tvBack.setOnClickListener(view1 -> {
            finish();
        });

        mBalanceViewB.daibNum.setText(DbNum);


        mAdapter = new TransactionRecordAdapter(this);
        mAdapter.bindAddress(getNowWalletAddress());
        mAdapter.bindData(mData,assetType);
        mViewB.listview.setAdapter(mAdapter);
        mViewB.listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssetsDetailActivity.this, TransferDetailActivity.class);
                intent.putExtra("detail", mData.get(position));
                intent.putExtra("type", assetType);
                startActivity(intent);
                refreshFlag = false;
            }
        });

        mViewB.refreshView.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                getDatas();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page++;
                getDatas();
            }
        });

        
        setViewClicked();
    }

    
    public void setViewClicked() {

        
        mTokenInfoViewB.tokenDetail.setOnClickListener(view1 -> {
            intent = new Intent(AssetsDetailActivity.this, TokenDetailActivity.class);
            intent.putExtra("tokenName", coinName);
            intent.putExtra("logo", Logo);
            intent.putExtra("type", assetType);
            startActivity(intent);
        });

        
        mTokenInfoViewB.tokenShouyi.setOnClickListener(view1 -> {
            intent = new Intent(AssetsDetailActivity.this, IncomeActivity.class);
            intent.putExtra("tokenName", coinName);
            intent.putExtra("type", assetType);
            startActivity(intent);
        });

        
        mBtnViewB.transfer.setOnClickListener(view1 -> {
            if(walletDBUtil.getWalletInfo().getLevel()==-1){
                ToastUtil.showToast(getString(R.string.wallet_type_error));
                return;
            }
            if (SettingPrefUtil.getNodeType(this) == 1) {
                intent = new Intent(this, TransferActivity.class);
                intent.putExtra("amountStr", "0");
                intent.putExtra("toAddress", "");
                intent.putExtra("type", assetType);
                intent.putExtra("tokenName", coinName);
                startActivity(intent);
            } else {
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setNegativeButton(getString(R.string.cancel), null).setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                intent = new Intent(AssetsDetailActivity.this, ChooseNodeActivity.class);
                                intent.putExtra("type", 1);
                                startActivity(intent);
                            }
                        }).setMessage(getString(R.string.is_normal_not_transefer)).create();
                dialog.show();
            }
        });

        
        mBtnViewB.collectMoney.setOnClickListener(view1 -> {
            CollectMoneyActivity.startCollectMoneyActivity(this,assetType,coinName,decimal);
        });

        
        mTypesViewB.topAll.setOnClickListener(view1 -> {
            changeView(0);
        });

        
        mTypesViewB.topOut.setOnClickListener(view1 -> {
            changeView(1);
        });

        
        mTypesViewB.topIn.setOnClickListener(view1 -> {
            changeView(2);
        });

        
        mTypesViewB.topFail.setOnClickListener(view1 -> {
            changeView(3);
        });
    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mRefreshStatusLD, success->{
            if (page == 1) {
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

        
        getViewModel().observe(getViewModel().mRecordsLD, records->{
            if (page == 1) {
                mData.clear();
            } else {
                if (records == null || records.size() == 0) {
                    ToastUtil.showToast(getString(R.string.nomore));
                    page--;
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
        getViewModel().getDatas(page, getNowWalletAddress(), mAssert);
    }

    
    private String getNowWalletAddress() {
        return walletDBUtil.getWalletInfo().getAllAddress2();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (refreshFlag) {
            page=1;
            getDatas();
        } else {
            refreshFlag = true;
        }
    }

    
    private void changeView(int newStatus) {
        if (status == newStatus) {
            return;
        }
        status = newStatus;
        page = 1;

        mTypesViewB.topAll.setTextColor(ContextCompat.getColor(this, R.color.default_text_three_color));
        mTypesViewB.topAllBottem.setVisibility(View.INVISIBLE);
        mTypesViewB.topOut.setTextColor(ContextCompat.getColor(this, R.color.default_text_three_color));
        mTypesViewB.topOutBottem.setVisibility(View.INVISIBLE);
        mTypesViewB.topIn.setTextColor(ContextCompat.getColor(this, R.color.default_text_three_color));
        mTypesViewB.topInBottem.setVisibility(View.INVISIBLE);
        mTypesViewB.topFail.setTextColor(ContextCompat.getColor(this, R.color.default_text_three_color));
        mTypesViewB.topFailBottem.setVisibility(View.INVISIBLE);

        if (status == 0) {
            mTypesViewB.topAll.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
            mTypesViewB.topAllBottem.setVisibility(View.VISIBLE);
        } else if (status == 1) {
            mTypesViewB.topOut.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
            mTypesViewB.topOutBottem.setVisibility(View.VISIBLE);
        } else if (status == 2) {
            mTypesViewB.topIn.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
            mTypesViewB.topInBottem.setVisibility(View.VISIBLE);
        } else if (status == 3) {
            mTypesViewB.topFail.setTextColor(ContextCompat.getColor(this, R.color.default_text_color));
            mTypesViewB.topFailBottem.setVisibility(View.VISIBLE);
        }

        getDatas();
    }
}
