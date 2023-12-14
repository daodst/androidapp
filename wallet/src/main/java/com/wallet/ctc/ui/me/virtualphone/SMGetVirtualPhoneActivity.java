

package com.wallet.ctc.ui.me.virtualphone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.util.XPopupUtils;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.SmActivityGetVirtualPhoneBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosChatParamsBean;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import common.app.ActivityRouter;
import common.app.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.ui.view.InputPwdDialog;
import common.app.utils.SpUtil;


public class SMGetVirtualPhoneActivity extends BaseActivity<SMVirtualPhoneActivityVM> implements SwipeRefreshLayout.OnRefreshListener {
    private SmActivityGetVirtualPhoneBinding mBinding;

    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;

    private List<String> mPhoneNumbers = new ArrayList<String>();
    private static final String KEY_ADDR = "address";
    private String mAddress;
    
    private Boolean mPledgeLevel;
    private AlertDialog mAlertDialog;
    private EvmosChatParamsBean mConfigData;

    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, SMGetVirtualPhoneActivity.class);
        intent.putExtra(KEY_ADDR, address);
        return intent;
    }

    @Override
    public void initParam() {
        mAddress = getIntent().getStringExtra(KEY_ADDR);
        if (TextUtils.isEmpty(mAddress)) {
            showToast(R.string.data_error);
            finish();
            return;
        }
        mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(mAddress, WalletUtil.MCC_COIN);
        if (null == mSelecteWallet) {
            showToast(R.string.no_found_wallet_info);
            finish();
            return;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = SmActivityGetVirtualPhoneBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.LOLLIPOP_MR1) {
            Eyes.setTranslucent(this);
            mBinding.titleBar.setPadding(0, Eyes.getStatusBarHeight(this), 0, 0);
        }
        mBinding.refreshView.setOnRefreshListener(this);
        mBinding.ivBack.setOnClickListener(v -> finish());
        
        mBinding.tvChooseNumber.setOnClickListener(v -> {
            new XPopup.Builder(this).maxHeight((int) (XPopupUtils.getScreenHeight(this) * 0.5)).asBottomList("", mPhoneNumbers.toArray(new String[]{}), (position, text) -> {
                mBinding.tvChooseNumber.setText(text);
            }).show();
        });

        mBinding.btnMint.setOnClickListener(v -> {

            if (null == mConfigData) {
                getViewModel().getChatAndBalance(mAddress);
                return;
            }

            String phoneNumStart = mBinding.tvChooseNumber.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNumStart)) {
                phoneNumStart = phoneNumStart.replaceAll("XXXX", "");

                
                getViewModel().showGasAlert(mAddress, phoneNumStart, mSelecteWallet.getChatAddress());

                
            }
        });
    }

    public String getBigDecimalValue(BigDecimal divide) {
        BigDecimal zero = BigDecimal.ZERO;
        if (divide.compareTo(zero) == 0) {
            return zero.toPlainString();
        } else {
            return divide.stripTrailingZeros().toPlainString();
        }
    }

    @Override
    public void initData() {
        super.initData();
        
        getViewModel().observe(viewModel.phoneNumberList, strings -> {
            mPhoneNumbers = strings;
            if (strings.size() > 0) {
                mBinding.tvChooseNumber.setText(strings.get(0));
            }
        });

        
        getViewModel().observe(getViewModel().mChatParamsLD, evmosChatParamsBean -> {

            mConfigData = evmosChatParamsBean;

            String showData = "0";
            try {
                BigDecimal decimal = new BigDecimal(evmosChatParamsBean.tokenBalance);
                showData = getBigDecimalValue(decimal);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
            String wakuangyue = getString(R.string.sm_pledge_string_17) + "<font color=\"#111111\">" + showData + "</font>";
            mBinding.tvPledgeWakuangYue.setText(Html.fromHtml(wakuangyue));

            
            if (null != evmosChatParamsBean && null != evmosChatParamsBean.data && null != evmosChatParamsBean.data.destroyPhoneNumberCoin) {
                String mintNum = getMintNum();
                String tokenName = getMintCoinName();
                mBinding.btnMint.setText(getString(R.string.phone_destory) + mintNum + tokenName.toUpperCase());

                String maxNum = evmosChatParamsBean.data.maxPhoneNumber + "";
                String remakStr = String.format(getString(R.string.mint_phone_alert), mintNum, tokenName.toUpperCase(), maxNum);
                mBinding.tvRemark.setText(remakStr);
            }
        });

        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            String mintNum = getMintNum();
            TransConfirmDialogBuilder.builder(this,mSelecteWallet).amount(mintNum)
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress("")
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(com.wallet.ctc.R.string.sm_virtual_phone_title_2))
                    
                    .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                    
                    .goTransferListener(pwd -> {
                        
                        String phoneNumStart = mBinding.tvChooseNumber.getText().toString().trim();
                        if (!TextUtils.isEmpty(phoneNumStart)) {
                            phoneNumStart = phoneNumStart.replaceAll("XXXX", "");
                            getViewModel().burnGetMobile(mAddress, phoneNumStart, mSelecteWallet, pwd);
                        }
                    }).show();
        });


        
        getViewModel().observe(getViewModel().mResultLD, evmosPledgeResultBean -> {
            if (evmosPledgeResultBean.success) {
                showToast(R.string.operate_success);
                finish();
            } else {
                showToast(evmosPledgeResultBean.info);
            }
        });

        
        getViewModel().observe(viewModel.mHasPledge, pledge -> {
            mPledgeLevel = pledge;
            if (pledge) showDialog();
        });

        getData();
    }

    
    private String getMintNum() {
        if (null == mConfigData) {
            return "";
        }
        String mintNum = getViewModel().getTenDecimalValue(mConfigData.data.destroyPhoneNumberCoin.amount);
        return mintNum;

    }

    
    private String getMintCoinName() {
        if (null == mConfigData) {
            return "";
        }
        String tokenName = mConfigData.data.destroyPhoneNumberCoin.denom;
        return tokenName;
    }

    private void getData() {
        
        String noSegment = SpUtil.getNodeNoSegm();
        getViewModel().getPhoneList(noSegment);

        
        getViewModel().getChatAndBalance(mAddress);
    }


    @Override
    public void onRefresh() {
        mBinding.refreshView.setRefreshing(false);
        getViewModel().getHoldPhoneList(mAddress);
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().getHoldPhoneList(mAddress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getViewModel().onDestroy();
    }

    private void showDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.prompt)).setMessage(getString(R.string.sm_pledge_string_21)).setCancelable(false).setNegativeButton(getString(R.string.cancel), (dialog, which) -> finish()).setPositiveButton(getString(R.string.register), (dialog, which) -> {
                
                finish();
                Intent intent = ActivityRouter.getIntent(this, ActivityRouter.Wallet.SMPledgeActivity);
                intent.putExtra("address", mAddress);
                intent.putExtra("nikeName", "");
                startActivity(intent);
            }).create();
        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }
}
