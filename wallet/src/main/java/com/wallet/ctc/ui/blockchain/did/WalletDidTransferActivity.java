package com.wallet.ctc.ui.blockchain.did;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.databinding.ActivityWalletDidTransferBinding;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.blockchain.addressbook.AddressBookActivity;
import com.wallet.ctc.ui.blockchain.transfer.TransferActivity;
import com.wallet.ctc.util.DecriptUtil;

import common.app.ActivityRouter;
import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;


public class WalletDidTransferActivity extends BaseActivity<WalletDidTransferVM> {

    ActivityWalletDidTransferBinding mViews;
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ADDRESS = "address";
    private String mMobile, mAddr;
    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;
    public static Intent getIntent(Context from, String address, String mobile) {
        Intent intent = new Intent(from, WalletDidTransferActivity.class);
        intent.putExtra(KEY_MOBILE, mobile);
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
        mMobile = getIntent().getStringExtra(KEY_MOBILE);
        if (null == mMobile) {
            mMobile = "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViews = ActivityWalletDidTransferBinding.inflate(getLayoutInflater());
        setContentView(mViews.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);

        mViews.didTransferBack.setOnClickListener(v -> {
            
            finish();
        });
        mViews.didTransferDid.setText(mMobile);
        
        ActivityResultLauncher<Intent> qrLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent data = result.getData();
            int resultCode = result.getResultCode();
            if (resultCode == RESULT_OK && null !=data) {
                String qrcontent = data.getStringExtra("content");
                if (!TextUtils.isEmpty(qrcontent)) {
                    mViews.didTransferAddressEd.setText(qrcontent);
                }
            }
        });
        mViews.didTransferScan.setOnClickListener(v -> {
            
            Intent intent = ActivityRouter.getStringContentIntent(WalletDidTransferActivity.this, ActivityRouter.Common.F_QRCodeFragment, "getScan");
            qrLauncher.launch(intent);
        });

        
        ActivityResultLauncher<Intent> addressBookLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK && null !=data) {
                            String toAddress = data.getStringExtra("toAddress");
                            if (!TextUtils.isEmpty(toAddress)) {
                                mViews.didTransferAddressEd.setText(toAddress);
                            }
                        }
                    }
                });
        mViews.didTransferAddressBook.setOnClickListener(v -> {
            
            Intent intent = new Intent(this, AddressBookActivity.class);
            intent.putExtra("type", 1);
            addressBookLauncher.launch(intent);
        });


        mViews.didTransferBt.setOnClickListener(v -> {
            
            String address = mViews.didTransferAddressEd.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                ToastUtil.showToast(R.string.did_transfer_address_ed_hint);
                return;
            }
            
            String did = mViews.didTransferDid.getText().toString().trim();
            if (TextUtils.isEmpty(did)) {
                ToastUtil.showToast(R.string.did_transfer_did_hint);
                return;
            }
            
            String remark = mViews.didTransferRemark.getText().toString().trim();
            doTransfer(address, did, remark);
        });
    }

    @Override
    public void initData() {
        getViewModel().observe(getViewModel().mTransfResultLD, resultBean -> {
           if (resultBean != null && resultBean.success) {
               showToast(R.string.operate_success);
               finish();
           }
        });
    }

    
    private void doTransfer(String toAddr, String mobile, String remark) {
        
        if (null != mPwdDialog) {
            mPwdDialog.dismiss();
            mPwdDialog = null;
        }
        mPwdDialog = new InputPwdDialog(WalletDidTransferActivity.this, getString(com.wallet.ctc.R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mPwdDialog.dismiss();
                mPwdDialog = null;
                if (!mSelecteWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                    return;
                }
                getViewModel().transferMobile(mAddr, toAddr, mobile, remark, mSelecteWallet, pwd);
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }

    @Override
    protected void onDestroy() {
        getViewModel().onDestroy();
        super.onDestroy();
    }
}




