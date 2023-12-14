package com.app.home.ui.ver.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.R;
import com.app.databinding.ActivityVwalletVeryListBinding;
import com.app.home.pojo.ValidatorListInfo;
import com.app.home.ui.ver.list.adapter.VWalletVeryListAdapter;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.util.Collections;
import java.util.List;

import common.app.base.BaseActivity;


public class VWalletVeryListActivity extends BaseActivity<VWalletVeryListVM> {


    ActivityVwalletVeryListBinding mBinding;


    private static final String PARAM_NUM = "param_num";

    public static final Intent getIntent(Context context, String num) {
        Intent intent = new Intent(context, VWalletVeryListActivity.class);
        intent.putExtra(PARAM_NUM, num);
        return intent;
    }

    private String mNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityVwalletVeryListBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(mBinding.getRoot());
        mNum = getIntent().getStringExtra(PARAM_NUM);
    }


    private VWalletVeryListAdapter mAdapter;
    
    private int mDecimal = 18;

    @Override
    public void initData() {
        List<AssertBean> assets = WalletDBUtil.getInstent(this).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }

        mBinding.vwalletVeryBack.setOnClickListener(v -> {
            finish();
        });


        mAdapter = new VWalletVeryListAdapter(mDecimal);
        mBinding.walletVeryListRv.setAdapter(mAdapter);


        mBinding.vwalletVeryNumParent.setOnClickListener(v -> {
            Boolean tag = getBoolTag(mBinding.vwalletVeryNumArrow);

            if (tag) {
                
                mBinding.vwalletVeryNumArrow.setImageResource(R.drawable.dpos_arrow_up);
            } else {
                
                mBinding.vwalletVeryNumArrow.setImageResource(R.drawable.dpos_arrow_down);
            }

            
            List<ValidatorListInfo.Result> value = getViewModel().mLiveData.getValue();
            if (null != value) {
                if (tag) {
                    Collections.sort(value, new ValidatorListInfo.BalanceComparator());
                } else {
                    
                    Collections.sort(value, new ValidatorListInfo.BalanceComparator());
                    Collections.reverse(value);
                }
                if (null != mAdapter) {
                    mAdapter.setResults(value);
                }
            }
        });
        mBinding.vwalletVeryTimeParent.setOnClickListener(v -> {
            Boolean tag = getBoolTag(mBinding.vwalletVeryTimeArrow);
            if (tag) {
                
                mBinding.vwalletVeryTimeArrow.setImageResource(R.drawable.dpos_arrow_up);
            } else {
                
                mBinding.vwalletVeryTimeArrow.setImageResource(R.drawable.dpos_arrow_down);
            }
            List<ValidatorListInfo.Result> value = getViewModel().mLiveData.getValue();
            if (null != value) {
                if (tag) {
                    Collections.sort(value, new ValidatorListInfo.TimeComparator());
                } else {
                    
                    Collections.sort(value, new ValidatorListInfo.TimeComparator());
                    Collections.reverse(value);
                }
                if (null != mAdapter) {
                    mAdapter.setResults(value);
                }
            }
        });
        mBinding.vwalletVeryShowCount.setText(R.string.vwallet_very_tips);

        getViewModel().mLiveData.observe(this, results -> {
            mBinding.vwalletVeryShowCount.setText(getString(R.string.vwallet_very_tips1) + results.size() + "/" + mNum);
            if (null != mAdapter) {
                mAdapter.setResults(results);
            }
        });
        getViewModel().getValidatorList();
    }

    private Boolean getBoolTag(View view) {
        Object tag = view.getTag();
        boolean boolTag = false;
        if (tag instanceof Boolean) {
            boolTag = (Boolean) tag;
        }
        view.setTag(!boolTag);
        return boolTag;
    }
}
