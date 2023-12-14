package com.app.me.computing;


import static common.app.utils.LanguageUtil.TYPE_LAGUAGE_ENGLISH;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.app.R;
import com.app.databinding.ActivityComputingBinding;
import com.app.me.destory.DestoryPledgeVM;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosPledgeConfigBean;
import com.wallet.ctc.model.blockchain.EvmosTotalPledgeBean;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import java.math.BigDecimal;

import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.utils.DisplayUtils;
import common.app.utils.LanguageUtil;
import common.app.utils.SpUtil;


public class ComputingActivity extends BaseActivity<DestoryPledgeVM> {

    private ActivityComputingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mBinding = ActivityComputingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        
        Eyes.setTranslucent(this);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBinding.computingBackNested.getLayoutParams();
        ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) mBinding.computingBackTop.getLayoutParams();
        params2.topMargin = Eyes.getStatusBarHeight(this);
        params.topMargin = Eyes.getStatusBarHeight(this) + DisplayUtils.dp2px(this, 45);


        super.onCreate(savedInstanceState);
    }

    private static final String KEY_ADDRESS = "address";

    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, ComputingActivity.class);
        intent.putExtra(KEY_ADDRESS, address);
        return intent;
    }

    private String mAddress;
    private WalletEntity mSelecteWallet;

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mBinding.computingBack.setOnClickListener(v -> {
            finish();
        });

        mAddress = getIntent().getStringExtra(KEY_ADDRESS);

        if (!TextUtils.isEmpty(mAddress)) {
            mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(mAddress, WalletUtil.MCC_COIN);
        } else {
            mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfo(WalletUtil.MCC_COIN);
        }

        if (null == mSelecteWallet) {
            showToast(com.wallet.ctc.R.string.no_found_wallet_info);
            finish();
            return;
        }
        mAddress = mSelecteWallet.getAllAddress();

        String local = LanguageUtil.getNowLocalStr(this);

        if (TYPE_LAGUAGE_ENGLISH.equals(local)) {
            
            mBinding.computingTopLabel1.setTextSize(30);
            mBinding.computingTopLabel1.setText(new Spanny("YOU  WILL  RECEIVE \n")
                    
                    .append("--", new ForegroundColorSpan(Color.parseColor("#14BF9D")))
                    .append(" FOR  EVERY\n")
                    .append("--  DESTROYED."));
            mBinding.computingTopLabel2.setText(new Spanny("The reward for every ")
                    
                    .append("--,\n")
                    .append("with an interval of 14400 blocks is ")
                    
                    .append("--.", new AbsoluteSizeSpan(18, true), new ForegroundColorSpan(Color.parseColor("#FF9800"))));

        } else {
            
            mBinding.computingTopLabel1.setTextSize(26);
            mBinding.computingTopLabel1.setText(new Spanny("1")
                    
                    .append("--,\n")
                    .append("")
                    
                    .append("--", new ForegroundColorSpan(Color.parseColor("#14BF9D")), new AbsoluteSizeSpan(36, true)));


            mBinding.computingTopLabel2.setText(new Spanny(" ")
                    
                    .append("--", new AbsoluteSizeSpan(18, true))
                    .append("\n")
                    .append("14400", new AbsoluteSizeSpan(18, true))
                    .append(" ")
                    
                    .append("--.", new AbsoluteSizeSpan(18, true), new ForegroundColorSpan(Color.parseColor("#FF9800"))));
        }


        mBinding.computingWalletAddress.setOnClickListener(v -> {
            
            ChooseWalletDialog.showDialog(this, WalletUtil.MCC_COIN, ((address1, walletType) -> {
                
                mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfo();
                mAddress = mSelecteWallet.getAllAddress();
                getData();
            }));
        });


        
        mBinding.computingPledgeBt.setOnClickListener(v -> {
            
            Uri httpUri = Uri.parse("http://water-tap.daodst.com/");
            startActivity(new Intent(Intent.ACTION_VIEW).setData(httpUri));
        });


        getViewModel().mConfigLD.observe(this, this::showInfo);

        getViewModel().mWithdrawGasLiveData.observe(this, bean -> {
            EvmosPledgeConfigBean value = getViewModel().mConfigLD.getValue();
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(value.canWithdrawNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(value.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(com.wallet.ctc.R.string.sm_pledge_string_16))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().doWithdraw(bean, mAddress, value, mSelecteWallet, pwd);
                    }).show();
        });

        getViewModel().mHashPledgeGasLiveData.observe(this, bean -> {
            EvmosPledgeConfigBean value = getViewModel().mConfigLD.getValue();
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(bean.mHashPledgeNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(value.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(com.wallet.ctc.R.string.sm_pledge_string_1))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        getViewModel().doHashPledge(bean, mSelecteWallet.getAllAddress(), bean.mHashPledgeNum, value, mSelecteWallet, pwd, mSelecteWallet.getAllAddress());
                    }).show();
        });

        getViewModel().mAvailableLiveData.observe(this, s -> {
            EvmosPledgeConfigBean value = getViewModel().mConfigLD.getValue();
            value.available = s;
            UnPledgeDialog dialog = new UnPledgeDialog(this, mSelecteWallet, getViewModel());
            dialog.show(value);
        });
        getViewModel().mUnPledgeGasLiveData.observe(this, bean -> {
            EvmosPledgeConfigBean value = getViewModel().mConfigLD.getValue();
            TransConfirmDialogBuilder.builder(this, mSelecteWallet).amount(bean.mUnPledgeHashNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress(value.pledgeAddress)
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(com.wallet.ctc.R.string.sm_pledge_string_14))
                    
                    .gasFeeWithToken(bean.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                    
                    .goTransferListener(pwd -> {
                        EvmosTotalPledgeBean.Delegation delegation = value.delegations.get(0);
                        getViewModel().doUnPledge(bean, delegation.delegation.delegator_address, delegation.delegation.validator_address, bean.mUnPledgeHashNum, value, mSelecteWallet, pwd);
                    }).show();
        });


        getViewModel().mHashPldgeNumLiveData.observe(this, num -> {
            EvmosPledgeConfigBean value = getViewModel().mConfigLD.getValue();
            String gainTokenName = value.tokenName.toUpperCase();
            mBinding.computingPledgePowerBalance.setText(new Spanny(getString(R.string.surplus) + " ").append(num + " " + gainTokenName, new ForegroundColorSpan(Color.parseColor("#14BF9D"))));
        });


        getViewModel().observe(getViewModel().mHashPledgeResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mUnPledgeResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        
        getViewModel().observe(getViewModel().mLingQuResultLD, bean -> {
            if (bean.success) {
                showToast(R.string.operate_success);
                getData();
            } else {
                showToast(bean.info);
            }
        });

        getData();
    }

    private void getData() {
        mBinding.computingWalletName.setText(mSelecteWallet.getName() + BuildConfig.EVMOS_FAKE_UNINT + getString(R.string.dst_destory_wallet));

        mBinding.computingWalletAddress.setText(mSelecteWallet.getAllAddress());
        
        String noSegment = SpUtil.getNodeNoSegm();
        getViewModel().getConfig(mAddress, noSegment, "hash_pledge", LanguageUtil.getNowLocalStr(this));
    }

    public void showInfo(EvmosPledgeConfigBean configBean) {
        if (null == configBean) {
            return;
        }
        
        mBinding.computingPledgePower.setText("");
        String local = LanguageUtil.getNowLocalStr(this);

        
        getViewModel().getHashPldgeNum(mAddress, configBean.tokenName);

        String comsumeTokenName = configBean.tokenNameDestory.toUpperCase();
        String gainTokenName = configBean.tokenName.toUpperCase();


        if (TYPE_LAGUAGE_ENGLISH.equals(local)) {
            
            mBinding.computingTopLabel1.setTextSize(30);
            mBinding.computingTopLabel1.setText(new Spanny("YOU  WILL  RECEIVE \n")
                    
                    .append(configBean.ratio + " " + gainTokenName, new ForegroundColorSpan(Color.parseColor("#14BF9D")))
                    .append(" FOR  EVERY\n")
                    .append(comsumeTokenName + "  DESTROYED."));
            mBinding.computingTopLabel2.setText(new Spanny("The reward for every ")
                    
                    .append(configBean.ratio + " " + gainTokenName + ",\n")
                    .append("with an interval of 14400 blocks is ")
                    
                    .append(configBean.pledge_hash_get + comsumeTokenName, new AbsoluteSizeSpan(18, true), new ForegroundColorSpan(Color.parseColor("#FF9800"))));

        } else {
            
            mBinding.computingTopLabel1.setTextSize(26);
            mBinding.computingTopLabel1.setText(new Spanny("1")
                    
                    .append(comsumeTokenName + ",\n")
                    .append("")
                    
                    .append(configBean.ratio + " " + gainTokenName, new ForegroundColorSpan(Color.parseColor("#14BF9D")), new AbsoluteSizeSpan(36, true)));


            mBinding.computingTopLabel2.setText(new Spanny(" ")
                    
                    .append(configBean.ratio + " " + gainTokenName, new AbsoluteSizeSpan(18, true))
                    .append("\n")
                    .append("14400", new AbsoluteSizeSpan(18, true))
                    .append(" ")
                    
                    .append(configBean.pledge_hash_get + comsumeTokenName + ".", new AbsoluteSizeSpan(18, true), new ForegroundColorSpan(Color.parseColor("#FF9800"))));
        }
        mBinding.computingNum.setText(new Spanny(getString(R.string.quantity)).append(0 + " " + gainTokenName, new ForegroundColorSpan(Color.parseColor("#14BF9D"))));

        
        if (configBean.level < configBean.undelegate_level) {
            mBinding.computingPledgeHashBt.setVisibility(View.GONE);
        } else {
            mBinding.computingPledgeHashBt.setVisibility(View.VISIBLE);
        }

        
        mBinding.computingBt.setOnClickListener(v -> {
            String inputNum = mBinding.computingPledgePower.getText().toString().trim();
            if (TextUtils.isEmpty(inputNum)) {
                showToast(mBinding.computingPledgePower.getHint().toString());
                return;
            }
            String hashBalance = getViewModel().mHashPldgeNumLiveData.getValue();
            if (null == hashBalance) {
                ToastUtil.showToast("" + gainTokenName + "");
                getViewModel().getHashPldgeNum(mAddress, configBean.tokenName);
                return;
            }
            if (new BigDecimal(inputNum).compareTo(new BigDecimal(hashBalance)) > 0) {
                showToast(com.wallet.ctc.R.string.balance_no_enaful);
                return;
            }
            getViewModel().getHashPledgeGas(mSelecteWallet.getAllAddress(), inputNum, configBean, mSelecteWallet.getAllAddress());
        });

        mBinding.computingTips.setText(Html.fromHtml(configBean.hash_pledge));
        mBinding.computingPledgeHash.setText(configBean.totalHasPledgeNum + " " + gainTokenName);

        
        mBinding.computingPledgeHashBt.setOnClickListener(v -> {
            if (null == configBean || !configBean.isSuccess) {
                return;
            }
            if (!TextUtils.isEmpty(configBean.canWithdrawNum)) {
                BigDecimal remainPledgeNum = new BigDecimal(configBean.remainPledgeNum);
                if (remainPledgeNum.compareTo(new BigDecimal(0)) <= 0) {
                    return;
                }
            }
            if (configBean.level < configBean.undelegate_level) {
                ToastUtil.showToast(getString(com.app.R.string.shuhui_rank_tips));
                return;
            }
            getViewModel().getEvmosChatUnPledgeAvailable(mSelecteWallet.getAllAddress());
        });
        
        mBinding.computingAward.setText(configBean.canWithdrawNum + " " + comsumeTokenName);
        mBinding.computingAwardBt.setOnClickListener(v -> {
            
            if (!configBean.isSuccess) {
                return;
            }
            if (!TextUtils.isEmpty(configBean.canWithdrawNum)) {
                BigDecimal canWithdrawNum = new BigDecimal(configBean.canWithdrawNum);
                if (canWithdrawNum.compareTo(new BigDecimal(0)) <= 0) {
                    return;
                }
                getViewModel().getWithdrawGas(mAddress, configBean);
            }
        });

        mBinding.computingPledgePower.addTextChangedListener(new CusTextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    trim = "0";
                }
                mBinding.computingNum.setText(new Spanny(getString(R.string.quantity)).append(trim + " " + gainTokenName, new ForegroundColorSpan(Color.parseColor("#14BF9D"))));
            }
        });


    }


}
