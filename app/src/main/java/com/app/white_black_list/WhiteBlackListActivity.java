package com.app.white_black_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.databinding.ActivityWhiteBlackListBinding;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.TitleBarView;
import im.vector.app.provide.ChatStatusProvide;

public class WhiteBlackListActivity extends BaseActivity<WhiteBlackListVM> {

    ActivityWhiteBlackListBinding mViews;
    private String mAddr;
    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;
    private EvmosChatFeeBean mNowSetting;

    
    private static final String TYPE_ALL = "any";
    private static final String TYPE_FEE = "fee";
    private static final String TYPE_WHITE_LIST = "list";

    private static final String KEY_MODE = "mode";
    public static final int MODE_WHITE = 1;
    public static final int MODE_BLACK = 2;
    private int mMode = MODE_WHITE;

    private WhiteBlackListAdapter mAdapter;
    private WhiteBlackDelAdapter mDelAdapter;

    private int mDecimal = 18;
    private static final String KEY_USERID = "userId";
    private static final String KEY_MOBILE = "mobile";

    
    public static Intent getWhiteIntent(Context from, String userId, String mobile) {
        Intent intent = new Intent(from, WhiteBlackListActivity.class);
        intent.putExtra(KEY_USERID, userId);
        intent.putExtra(KEY_MOBILE, mobile);
        intent.putExtra(KEY_MODE, MODE_WHITE);
        return intent;
    }

    
    public static Intent getBlackIntent(Context from, String userId, String mobile) {
        Intent intent = new Intent(from, WhiteBlackListActivity.class);
        intent.putExtra(KEY_USERID, userId);
        intent.putExtra(KEY_MOBILE, mobile);
        intent.putExtra(KEY_MODE, MODE_BLACK);
        return intent;
    }

    public static Intent getIntent(Context from, boolean isWhite) {
        Intent intent = new Intent(from, WhiteBlackListActivity.class);
        if (isWhite) {
            intent.putExtra(KEY_MODE, MODE_WHITE);
        } else {
            intent.putExtra(KEY_MODE, MODE_BLACK);
        }

        return intent;
    }

    @Override
    public void initParam() {

        String userId = ChatStatusProvide.getUserId(this);
        if (TextUtils.isEmpty(userId)) {
            showToast(R.string.data_error);
            return;
        }
        mAddr = AllUtils.getAddressByUid(userId);

        mMode = getIntent().getIntExtra(KEY_MODE, MODE_WHITE);

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


        String toAddUserId = getIntent().getStringExtra(KEY_USERID);
        String toAddMobile = getIntent().getStringExtra(KEY_MOBILE);
        if (!TextUtils.isEmpty(toAddUserId) && !TextUtils.isEmpty(toAddMobile)) {
            String toAddAddr = AllUtils.getAddressByUid(toAddUserId);
            getViewModel().addLocalUser(toAddAddr, toAddUserId, toAddMobile, mMode);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mViews = ActivityWhiteBlackListBinding.inflate(getLayoutInflater());
        setContentView(mViews.getRoot());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView(@Nullable View view) {
        super.initView(view);
        mViews.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {
                boolean isEdited = mAdapter.isEdited();
                mAdapter.setEdited(!isEdited);
                if (mAdapter.isEdited()) {
                    mViews.titleBar.setRightText(getString(R.string.delete));
                } else {
                    mViews.titleBar.setRightText(getString(com.app.R.string.sm_manager_title));
                }
            }
        });


        if (mMode == MODE_WHITE) {
            
            mViews.titleBar.setText(getString(com.app.R.string.sm_whitelist_title));
        } else if (mMode == MODE_BLACK) {
            
            mViews.titleBar.setText(getString(com.app.R.string.sm_blacklist_title));
        }

        
        mViews.submitBtn.setOnClickListener(view1 -> {
            if (null == mNowSetting) {
                getViewModel().getUserList(mAddr, mMode);
                return;
            }

            doSetChatInfo();
        });

        mAdapter = new WhiteBlackListAdapter(this, delUser -> {
            MyAlertDialog alertDialog = new MyAlertDialog(WhiteBlackListActivity.this, getString(com.app.R.string.sm_del_user_alert));
            alertDialog.setonclick(new MyAlertDialog.Onclick() {
                @Override
                public void Yes() {
                    alertDialog.dismiss();
                    getViewModel().removeUser(delUser, mMode);
                    if (delUser.isEffect) {
                        showToast(com.app.R.string.after_up_chain_useful);
                    }
                }

                @Override
                public void No() {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        });
        mViews.listview.setAdapter(mAdapter);

        mDelAdapter = new WhiteBlackDelAdapter(this, delUser -> {
            getViewModel().removeDelListUser(delUser, mMode);
        });
        mViews.delRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mViews.delRecyclerview.setAdapter(mDelAdapter);
    }

    @Override
    public void initData() {
        
        getViewModel().observe(getViewModel().mSettingLD, evmosChatFeeBean -> {
            mNowSetting = evmosChatFeeBean;
        });

        
        getViewModel().observe(getViewModel().mListLD, userList -> {
            mAdapter.bindDatas(userList);
            mViews.progressBar.setVisibility(View.GONE);
            if (userList != null && userList.size() > 0) {
                mViews.nodataLayout.setVisibility(View.GONE);
            } else {
                mViews.nodataLayout.setVisibility(View.VISIBLE);
            }
        });

        
        getViewModel().observe(getViewModel().mToRemoveLD, userlist -> {
            mDelAdapter.bindDatas(userlist);
            if (userlist == null || userlist.size() == 0) {
                mViews.delLayout.setVisibility(View.GONE);
            } else {
                mViews.delLayout.setVisibility(View.VISIBLE);
            }
        });

        getViewModel().observe(getViewModel().mTransfResultLD, resultBean -> {
            if (resultBean != null && resultBean.success) {
                showToast(R.string.operate_success);
                finish();
            }
        });

        getViewModel().getUserList(mAddr, mMode);
    }


    
    private void doSetChatInfo() {
        
        if (null != mPwdDialog) {
            mPwdDialog.dismiss();
            mPwdDialog = null;
        }
        mPwdDialog = new InputPwdDialog(WhiteBlackListActivity.this, getString(R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mPwdDialog.dismiss();
                mPwdDialog = null;
                if (!mSelecteWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(R.string.password_error2);
                    return;
                }
                getViewModel().setChatFee(mAddr, mMode, mNowSetting, mSelecteWallet, pwd);
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
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




