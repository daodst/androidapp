package com.wallet.ctc.ui.blockchain.issuance;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityIssuanceCoinBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.did.WalletDidTransferActivity;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceParam;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteParam;
import com.wallet.ctc.ui.blockchain.issuance.rec.IssuanceCoinRecActivity;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;


public class IssuanceCoinActivity extends BaseActivity<IssuanceCoinVM> {


    ActivityIssuanceCoinBinding mViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViews = ActivityIssuanceCoinBinding.inflate(getLayoutInflater());
        setContentView(mViews.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        getViewModel().mBooleanLiveData.observe(this, aBoolean -> {
            mViews.issuanceCoinName.setText("");
            mViews.issuanceCoinShortName.setText("");
            mViews.issuanceCoinDecimal.setText("");
            mViews.issuanceCoinNum.setText("");
            mViews.issuanceCoinLogo.setText("");

        });
        mViews.issuanceCoinBack.setOnClickListener(v -> {
            finish();
        });
        mViews.issuanceCoinRight.setOnClickListener(v -> {
            startActivity(new Intent(this, IssuanceCoinRecActivity.class));
        });
        getViewModel().mBooleanLiveData.observe(this, aBoolean -> {
            ToastUtil.showToast(getApplication().getString(R.string.issuance_coin_success));
            startActivity(IssuanceCoinRecActivity.getIntent(this, true));
        });
        getViewModel().mGasLiveData.observe(this, bean -> {
            WalletEntity entity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
            TransConfirmDialogBuilder.builder(this, entity).amount("")
                    
                    .fromAddress(entity.getAllAddress())
                    
                    .toAddress("")
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.issuance_coin_bt))
                    
                    .gasFeeWithToken(bean.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().issuance(entity, pwd, bean);
                    }).show();
        });
        mViews.issuanceCoinBt.setOnClickListener(v -> {


            
            String tips = getString(R.string.issuance_coin_name_hint);
            
            String name = mViews.issuanceCoinName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.showToast(tips + getString(R.string.issuance_coin_name_tips));
                return;
            }
            String shortName = mViews.issuanceCoinShortName.getText().toString().trim();
            if (TextUtils.isEmpty(shortName)) {
                ToastUtil.showToast(tips + getString(R.string.issuance_coin_short_name_tips));
                return;
            }
            String decimal = mViews.issuanceCoinDecimal.getText().toString().trim();
            if (TextUtils.isEmpty(decimal)) {
                ToastUtil.showToast(tips + getString(R.string.issuance_coin_decimal_tips));
                return;
            }
            String num = mViews.issuanceCoinNum.getText().toString().trim();
            if (TextUtils.isEmpty(num)) {
                ToastUtil.showToast(tips + getString(R.string.issuance_coin_num_tips));
                return;
            }
            String logo = mViews.issuanceCoinLogo.getText().toString().trim();
            if (TextUtils.isEmpty(num)) {
                ToastUtil.showToast(mViews.issuanceCoinLogo.getHint().toString().trim());
                return;
            }
            WalletEntity entity = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
            if (null == entity) {
                ToastUtil.showToast(getString(R.string.get_wallet_address_fail2));
                return;
            }

            IssuanceParam param = new IssuanceParam();
            param.from_address = entity.getAllAddress();
            param.name = name;
            param.symbol = shortName;
            param.pre_mint_amount = num;
            param.decimals = decimal;
            param.logo_url = logo;
            getViewModel().dposGas(WRPCVoteParam.TYPE_APPTOKENISSUE, param, "0", entity);
        });
    }

}
