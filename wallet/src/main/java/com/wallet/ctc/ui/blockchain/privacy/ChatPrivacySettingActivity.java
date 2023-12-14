package com.wallet.ctc.ui.blockchain.privacy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityChatPrivacySettingBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.ui.blockchain.transfer.TransferActivity;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import common.app.ActivityRouter;
import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;


public class ChatPrivacySettingActivity extends BaseActivity<ChatPrivacySettingVM> {

    ActivityChatPrivacySettingBinding mViews;
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ADDRESS = "address";
    private String mAddr;
    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;
    private EvmosChatFeeBean mNowSetting;

    
    private static final String TYPE_ALL = "any";
    private static final String TYPE_FEE = "fee";
    private static final String TYPE_WHITE_LIST = "list";

    private int mDecimal = 18;
    public static Intent getIntent(Context from, String address) {
        Intent intent = new Intent(from, ChatPrivacySettingActivity.class);
        intent.putExtra(KEY_ADDRESS, address);
        return intent;
    }

    @Override
    public void initParam() {
        mAddr = getIntent().getStringExtra(KEY_ADDRESS);
        if (TextUtils.isEmpty(mAddr)) {
            showToast(R.string.data_error);
            finish();
            return;
        }
        mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(mAddr, WalletUtil.MCC_COIN);
        if (null == mSelecteWallet) {
            showToast(R.string.no_found_wallet_info);
            finish();
            return;
        }
        
        List<AssertBean> assets = WalletDBUtil.getInstent(this).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViews = ActivityChatPrivacySettingBinding.inflate(getLayoutInflater());
        setContentView(mViews.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        mViews.didTransferBack.setOnClickListener(view1 -> {
            finish();
        });

        
        mViews.roomPrivacyBt.setOnClickListener(view1 -> {
            if (null == mNowSetting) {
                getViewModel().getSetting(mAddr);
                return;
            }
            getViewModel().showGasAlert(mAddr, mNowSetting);
        });

        
        mViews.whiteListLayout.setOnClickListener(view1 -> {
            boolean isChecked = mViews.roomPrivacyCkWhite.isChecked();
            if (isChecked) {
                setFeeModeViewState(TYPE_WHITE_LIST);
            }
        });
        mViews.roomPrivacyCkWhite.setOnClickListener(view1 -> {
            boolean isChecked = mViews.roomPrivacyCkWhite.isChecked();
            if (isChecked) {
                setFeeModeViewState(TYPE_WHITE_LIST);
            }
        });
        
        mViews.needPayTipTv.setText(getString(R.string.room_privacy_ck_stranger_tips2)+" "+ BuildConfig.EVMOS_FAKE_UNINT);
        mViews.feeLayout.setOnClickListener(view1 -> {
            boolean isChecked = mViews.roomPrivacyCkStranger.isChecked();
            if (isChecked) {
                setFeeModeViewState(TYPE_FEE);
            }
        });
        mViews.roomPrivacyCkStranger.setOnClickListener(view1 -> {
            boolean isChecked = mViews.roomPrivacyCkStranger.isChecked();
            if (isChecked) {
                setFeeModeViewState(TYPE_FEE);
            }
        });
        
        mViews.unPayTipTv.setText(getString(R.string.room_privacy_ck_accept_all_tips_sub)+" "+BuildConfig.EVMOS_FAKE_UNINT);
        mViews.allLayout.setOnClickListener(view1 -> {
            boolean isChecked = mViews.roomPrivacyCkAcceptAll.isChecked();
            if (isChecked) {
                setFeeModeViewState(TYPE_ALL);
            }
        });
        mViews.roomPrivacyCkAcceptAll.setOnClickListener(view1 -> {
            boolean isChecked = mViews.roomPrivacyCkAcceptAll.isChecked();
            if (isChecked) {
                setFeeModeViewState(TYPE_ALL);
            }
        });

    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mSettingLD, evmosChatFeeBean -> {
            mNowSetting = evmosChatFeeBean;

            EvmosChatFeeBean.Data setting = evmosChatFeeBean.data;
            
            setFeeModeViewState(setting.chat_restricted_mode);
            String bigAmount = evmosChatFeeBean.data.getChatFeeAmount();
            if (!TextUtils.isEmpty(bigAmount)) {
                String amount = getTenDecimalValue(bigAmount);
                mViews.roomPrivacyStrangerConsume.setText(amount);
            }
        });

        getViewModel().observe(getViewModel().mShowRegitDialog, aBoolean -> {
            if (aBoolean) {
                showRgistDialog();
            }
        });

        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            TransConfirmDialogBuilder.builder(this,mSelecteWallet).amount("0")
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress("")
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(com.wallet.ctc.R.string.room_privacy_setting_sub_title))
                    
                    .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                    
                    .goTransferListener(pwd -> {
                        
                        submit(pwd);
                    }).show();
        });

        getViewModel().observe(getViewModel().mTransfResultLD, resultBean -> {
           if (resultBean != null && resultBean.success) {
               showToast(R.string.operate_success);
               finish();
           }
        });

        getViewModel().getSetting(mAddr);
    }

    
    private void setFeeModeViewState(String feeMode) {
        switch (feeMode) {
            case TYPE_ALL:
                mViews.roomPrivacyCkAcceptAll.setChecked(true);
                mViews.roomPrivacyCkStranger.setChecked(false);
                mViews.roomPrivacyCkWhite.setChecked(false);
                break;
            case TYPE_FEE:
                mViews.roomPrivacyCkAcceptAll.setChecked(false);
                mViews.roomPrivacyCkStranger.setChecked(true);
                mViews.roomPrivacyCkWhite.setChecked(false);
                break;
            case TYPE_WHITE_LIST:
                mViews.roomPrivacyCkAcceptAll.setChecked(false);
                mViews.roomPrivacyCkStranger.setChecked(false);
                mViews.roomPrivacyCkWhite.setChecked(true);
                break;
        }
    }


    
    private void submit(String pwd) {
        if (null == mNowSetting) {
            getViewModel().getSetting(mAddr);
            return;
        }
        
        String feeMode = "";
        String bigAmount = "";
        if (mViews.roomPrivacyCkAcceptAll.isChecked()) {
            feeMode = "any";
        } else if(mViews.roomPrivacyCkStranger.isChecked()) {
            feeMode = "fee";
            String amount = mViews.roomPrivacyStrangerConsume.getText().toString().trim();
            if (TextUtils.isEmpty(amount)) {
                showToast(R.string.room_privacy_stranger_consume);
                return;
            }
            bigAmount = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, mDecimal))).stripTrailingZeros().toPlainString();
        } else if(mViews.roomPrivacyCkWhite.isChecked()){
            feeMode = "list";
        }
        getViewModel().setChatFee(mAddr, feeMode, bigAmount, mNowSetting, mSelecteWallet, pwd);
    }

    
    private void doSetChatInfo(String feeMode, String bigAmount) {
        
        if (null != mPwdDialog) {
            mPwdDialog.dismiss();
            mPwdDialog = null;
        }
        mPwdDialog = new InputPwdDialog(ChatPrivacySettingActivity.this, getString(R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mPwdDialog.dismiss();
                mPwdDialog = null;
                if (!mSelecteWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(R.string.password_error2);
                    return;
                }
                getViewModel().setChatFee(mAddr, feeMode, bigAmount, mNowSetting, mSelecteWallet, pwd);
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }

    private AlertDialog mAlertDialog;

    
    private void showRgistDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.prompt)).setMessage(R.string.set_private_chat_error).setCancelable(false).setNegativeButton(getString(R.string.cancel), (dialog, which) -> finish()).setPositiveButton(getString(R.string.register), (dialog, which) -> {
                
                finish();
                Intent intent = ActivityRouter.getIntent(this, ActivityRouter.Wallet.SMPledgeActivity);
                intent.putExtra("address", mAddr);
                intent.putExtra("nikeName", "");
                startActivity(intent);
            }).create();
        }
        if (!mAlertDialog.isShowing()) {
            mAlertDialog.show();
        }
    }

    
    private String getTenDecimalValue(String bigNum) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        return new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, mDecimal)), 2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    protected void onDestroy() {
        getViewModel().onDestroy();
        super.onDestroy();
    }
}




