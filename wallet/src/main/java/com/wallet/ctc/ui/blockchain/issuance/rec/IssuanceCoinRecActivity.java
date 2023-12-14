package com.wallet.ctc.ui.blockchain.issuance.rec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityIssuanceCoinRecBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinPageInfo;
import com.wallet.ctc.ui.blockchain.issuance.rec.adapter.IssuanceCoinAdapter;
import com.wallet.ctc.util.SettingPrefUtil;
import com.wallet.ctc.view.view.LoadMoreFooter;

import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;


public class IssuanceCoinRecActivity extends BaseActivity<IssuanceCoinRecVM> {


    private static final String TAG = "IssuanceCoinRecActivity";
    ActivityIssuanceCoinRecBinding mViews;


    private static final String PARAM_REFERSH_FLAG = "param_refersh_flag";

    public static Intent getIntent(Context context, boolean refersh) {
        Intent intent = new Intent(context, IssuanceCoinRecActivity.class);
        intent.putExtra(PARAM_REFERSH_FLAG, refersh);
        return intent;
    }

    private boolean mRefersh = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViews = ActivityIssuanceCoinRecBinding.inflate(getLayoutInflater());
        setContentView(mViews.getRoot());
        Intent intent = getIntent();
        if (null != intent) {
            mRefersh = intent.getBooleanExtra(PARAM_REFERSH_FLAG, false);
        }
        super.onCreate(savedInstanceState);
    }

    private IssuanceCoinAdapter mCoinAdapter;

    private LoadMoreFooter mLoadMoreFooter;
    private IssuanceCoinPageInfo mInfo = null;

    private WalletDBUtil walletDBUtil;

    
    @Override
    public void initView(@Nullable View view) {
        super.initView(view);


        WalletEntity entity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        if (null == entity) {
            ToastUtil.showToast(getString(R.string.get_wallet_address_fail2));
            return;
        }

        mCoinAdapter = new IssuanceCoinAdapter(this);


        walletDBUtil = WalletDBUtil.getInstent(this);
        
        List<AssertBean> canAdd = walletDBUtil.getMustWallet(WalletUtil.MCC_COIN);
        canAdd.addAll(walletDBUtil.getMustAssets(WalletUtil.MCC_COIN));
        canAdd.addAll(walletDBUtil.canChooseWallet(WalletUtil.MCC_COIN));


        List<AssertBean> chooseList = walletDBUtil.getAssetsByWalletType(SettingPrefUtil.getWalletAddress(this), WalletUtil.MCC_COIN);
        chooseList.addAll(walletDBUtil.getMustWallet(WalletUtil.MCC_COIN));

        mCoinAdapter.setAddList(canAdd, chooseList);

        mViews.issuanceRecRefersh.setOnRefreshListener(() -> getViewModel().getIssuanceCoinPageInfo(null, entity.getAllAddress()));
        mViews.issuanceRecBack.setOnClickListener(v -> {
            finish();
        });

        mViews.issuanceRecRv.setLayoutManager(new LinearLayoutManager(this));
        mViews.issuanceRecRv.setAdapter(mCoinAdapter);

        getViewModel().mLiveData.observe(this, info -> {

            mViews.issuanceRecRefersh.setRefreshing(false);

            if (null != mCoinAdapter) {
                mInfo = info;
                mCoinAdapter.setCoinItems(mInfo.result);
                mLoadMoreFooter.setState(mInfo.isEnd ? LoadMoreFooter.STATE_FINISHED : LoadMoreFooter.STATE_ENDLESS);
                if (null == mInfo.result || mInfo.result.size() == 0) {
                    mViews.issuanceRecEmpty.setVisibility(View.VISIBLE);
                } else {
                    mViews.issuanceRecEmpty.setVisibility(View.GONE);
                }
            }
        });
        if (mRefersh) {
            getViewModel().getIssuanceCoinPageInfoMore(null, entity.getAllAddress());
        } else {
            getViewModel().getIssuanceCoinPageInfo(null, entity.getAllAddress());
        }

        mLoadMoreFooter = new LoadMoreFooter(this, mViews.issuanceRecRv, () -> {
            getViewModel().getIssuanceCoinPageInfo(mInfo, entity.getAllAddress());
        });


    }
}
