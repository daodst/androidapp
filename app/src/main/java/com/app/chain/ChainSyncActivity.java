package com.app.chain;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.R;
import com.app.chain.entity.ChainTabInfo;
import com.app.databinding.ActivityChainSyncBinding;
import com.app.databinding.LayoutItemTabChainSyncBinding;
import com.google.android.material.tabs.TabLayout;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseActivity;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.TitleBarView;
import im.vector.app.provide.ChatStatusProvide;

public class ChainSyncActivity extends BaseActivity<ChainSyncViewModel> {
    public static int CHAIN_SYNC_TYPE_WHITE_LIST = 1;
    public static int CHAIN_SYNC_TYPE_BLACK_LIST = 2;

    public static int CHAIN_SYNC_TYPE_ADDRESS_BOOK = 3;
    public static int CHAIN_SYNC_TYPE_REMARK = 4;
    private ActivityChainSyncBinding vb = null;
    private List<ChainTabInfo> mChainTabInfos = new ArrayList<>();

    private static final String KEY_USERID = "userId";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_MODE = "mode";
    public static final int MODE_WHITE = 1;
    public static final int MODE_BLACK = 2;

    private String mAddr;
    private InputPwdDialog mPwdDialog;
    private WalletEntity mSelecteWallet;
    private EvmosChatFeeBean mNowSetting;
    private int mMode = MODE_WHITE;
    private int mDecimal = 18;

    public static Intent getWhiteIntent(Context from, String userId, String mobile) {
        return getIntent(from, userId, mobile, MODE_WHITE);
    }

    public static Intent getBlackIntent(Context from, String userId, String mobile) {
        return getIntent(from, userId, mobile, MODE_BLACK);
    }

    public static Intent getIntent(Context from, String userId, String mobile, int mode) {
        Intent intent = new Intent(from, ChainSyncActivity.class);
        intent.putExtra(KEY_USERID, userId);
        intent.putExtra(KEY_MOBILE, mobile);
        intent.putExtra(KEY_MODE, mode);
        return intent;
    }

    @Override
    public void initParam() {
        super.initParam();
        String userId = ChatStatusProvide.getUserId(this);
        if (TextUtils.isEmpty(userId)) {
            showToast(com.wallet.ctc.R.string.data_error);
            return;
        }
        mAddr = AllUtils.getAddressByUid(userId);
        mSelecteWallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(mAddr, WalletUtil.MCC_COIN);
        if (null == mSelecteWallet) {
            showToast(com.wallet.ctc.R.string.no_found_wallet_info);
            finish();
            return;
        }
        
        List<AssertBean> assets = WalletDBUtil.getInstent(this).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }

        
        mMode = getIntent().getIntExtra(KEY_MODE, 0);
        if (mMode == MODE_WHITE || mMode == MODE_BLACK) {
            
            String toAddUserId = getIntent().getStringExtra(KEY_USERID);
            String toAddMobile = getIntent().getStringExtra(KEY_MOBILE);
            if (!TextUtils.isEmpty(toAddUserId) && !TextUtils.isEmpty(toAddMobile)) {
                String toAddAddr = AllUtils.getAddressByUid(toAddUserId);
                getViewModel().addLocalUser(toAddAddr, toAddUserId, toAddMobile, mMode);
            }
        }
    }

    @Override
    public View initBindingView(Bundle savedInstanceState) {
        vb = ActivityChainSyncBinding.inflate(getLayoutInflater());
        return vb.getRoot();
    }


    @Override
    public void initView(@Nullable View view) {
        vb.titleBar.setOnTitleBarClickListener(new TitleBarView.TitleBarClickListener() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightClick() {

            }
        });
        
        vb.submitBtn.setOnClickListener(view1 -> {
            if (null == mNowSetting) {
                getViewModel().getSetting(mAddr);
                return;
            }

            
            getViewModel().showGasAlert(mAddr, mNowSetting);

            
        });
        mChainTabInfos.add(new ChainTabInfo(CHAIN_SYNC_TYPE_REMARK, R.string.remark, 0, RemarkListFragment.getInstance()));
        mChainTabInfos.add(new ChainTabInfo(CHAIN_SYNC_TYPE_WHITE_LIST, R.string.sm_whitelist_title, 0, WhiteBlackListFragment.getInstance(MODE_WHITE)));
        mChainTabInfos.add(new ChainTabInfo(CHAIN_SYNC_TYPE_BLACK_LIST, R.string.sm_blacklist_title, 0, WhiteBlackListFragment.getInstance(MODE_BLACK)));

        vb.vpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public int getCount() {
                return mChainTabInfos.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mChainTabInfos.get(position).fragment;
            }

        });
        vb.tab.removeAllTabs();
        vb.tab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vb.vpContent));
        vb.vpContent.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(vb.tab));
        vb.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showTab(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                showTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        vb.vpContent.setOffscreenPageLimit(mChainTabInfos.size());
        for (ChainTabInfo temp : mChainTabInfos) {
            LayoutItemTabChainSyncBinding tib = LayoutItemTabChainSyncBinding.inflate(LayoutInflater.from(this), vb.tab, false);
            tib.tvName.setText(temp.titleId);
            vb.tab.addTab(vb.tab.newTab().setCustomView(tib.getRoot()).setTag(tib));
        }

        
        if (mMode == MODE_WHITE) {
            vb.vpContent.setCurrentItem(1);
        } else if(mMode == MODE_BLACK) {
            vb.vpContent.setCurrentItem(2);
        }
    }



    @Override
    public void initData() {

        
        getViewModel().observe(getViewModel().mTransfResultLD, resultBean->{
            if (resultBean != null && resultBean.success) {
                showToast(com.wallet.ctc.R.string.operate_success);
                finish();
            }
        });

        
        getViewModel().observe(getViewModel().mShowGasDialogLD, evmosSeqGasBean -> {
            TransConfirmDialogBuilder.builder(this,mSelecteWallet).amount("0")
                    
                    .fromAddress(mSelecteWallet.getAllAddress())
                    
                    .toAddress("")
                    .type(WalletUtil.MCC_COIN)
                    .orderDesc(getString(R.string.title_chain_sync))
                    
                    .gasFeeWithToken(evmosSeqGasBean.getShowFee())
                    
                    .goTransferListener(pwd -> {
                        getViewModel().submit(mAddr, mNowSetting, mSelecteWallet, pwd);

                    }).show();
        });

        
        getViewModel().observe(getViewModel().mSettingLD, evmosChatFeeBean -> {
            mNowSetting = evmosChatFeeBean;
        });

        
        getViewModel().observe(getViewModel().mVerfyPwdLD, verify->{
            if (verify) {
                showPwdDialog((wallet, pwd) -> {
                    getViewModel().refreshDatas(mAddr, mNowSetting, wallet, pwd);
                });
            }
        });
        

        getViewModel().getSetting(mAddr);
    }

    private void showTab(TabLayout.Tab tab, boolean isSelect) {
        LayoutItemTabChainSyncBinding tib = (LayoutItemTabChainSyncBinding) tab.getTag();
        if (tib == null) return;
        tib.vBottom.setVisibility(isSelect ? View.VISIBLE : View.GONE);
        tib.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, isSelect ? 15 : 14);
        tib.tvName.setTextColor(ContextCompat.getColor(this, isSelect ? R.color.default_theme_color : R.color.default_text_three_color));
        tib.tvName.setTypeface(Typeface.defaultFromStyle(isSelect ? Typeface.BOLD : Typeface.NORMAL));
    }

    
    interface PwdCallBack{
        void onSuccess(WalletEntity wallet, String pwd);
    }
    private void showPwdDialog(PwdCallBack callBack) {
        
        if (null != mPwdDialog) {
            mPwdDialog.dismiss();
            mPwdDialog = null;
        }
        mPwdDialog = new InputPwdDialog(ChainSyncActivity.this, getString(com.wallet.ctc.R.string.place_edit_password));
        mPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mPwdDialog.dismiss();
                mPwdDialog = null;
                if (!mSelecteWallet.getmPassword().equals(DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                    return;
                }
                if (null != callBack) {
                    callBack.onSuccess(mSelecteWallet, pwd);
                }
            }

            @Override
            public void No() {
                mPwdDialog.dismiss();
            }
        });
        mPwdDialog.show();
    }


    
    public void changeCount(int position, int count) {
        
        TabLayout.Tab tab = vb.tab.getTabAt(position);
        if (null != tab) {
            View view = tab.getCustomView();
            if (null != view) {
                TextView text = view.findViewById(R.id.tv_count);
                text.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                text.setText("" + count);
            }
        }
    }


    
    private void testDeEncode() {
        





        
    }
}
