

package com.wallet.ctc.base;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.ACache;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.PermissionUtils;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import common.app.utils.LanguageUtil;



public abstract class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public Dialog mLoadingDialog;
    protected InputMethodManager manager;
    protected Unbinder unbinder;
    protected ACache mAcache;
    protected WalletDBUtil walletDBUtil;
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            LanguageUtil.initLanguage(this);
            walletDBUtil = WalletDBUtil.getInstent(this);
            manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (initContentView() > 0) {
                setContentView(initContentView());
            }
            unbinder = ButterKnife.bind(this);
            setDialog();
            mAcache = ACache.get(this);
            initUiAndListener();
            initData();

        } catch (Exception e) {
            LogUtil.d("" + e.toString());
             e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)
        {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    public boolean aoutHide(float x, float y) {
        return true;
    }

    
    public abstract int initContentView();

    
    public abstract void initUiAndListener();

    
    public abstract void initData();

    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }

    
    private void setDialog() {
        mLoadingDialog = new Dialog(BaseActivity.this, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.dialog_commom);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView msg = (TextView) mLoadingDialog
                .findViewById(R.id.id_tv_loadingmsg);
        msg.setText(getString(R.string.loading));
    }

    
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }

    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        
        if (event.getAction() == MotionEvent.ACTION_DOWN && aoutHide(event.getX(), event.getY())) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    public PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            getPermission(requestCode);
        }
    };

    public void getPermission(int requestCode) {
    }

    
    public void creatOrInsertWallet(final int type, final String walletaddress) {
        List<AssertBean> must = new ArrayList<>();
        if (type == WalletUtil.DM_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, "DM"));
        } else if (type == WalletUtil.ETH_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, "ETH"));
        } else if (type == WalletUtil.ETF_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, getString(R.string.default_etf).toUpperCase()));
        } else if (type == WalletUtil.DMF_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, getString(R.string.default_dmf_hb).toUpperCase()));
        } else if (type == WalletUtil.DMF_BA_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, getString(R.string.default_dmf_ba).toUpperCase()));
        } else if (type == WalletUtil.MCC_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, getString(R.string.default_token_name).toUpperCase()));
        } else if (type == WalletUtil.OTHER_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, getString(R.string.default_other_token_name).toUpperCase()));
        } else if (type == WalletUtil.TRX_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, "TRX"));
        } else if (type == WalletUtil.XRP_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, "XRP"));
        } else if (type == WalletUtil.HT_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, "HT"));
        } else if (type == WalletUtil.BNB_COIN) {
            must.addAll(SettingPrefUtil.getMustAssets(this, "BNB"));
        }
        for (int i = 0; i < must.size(); i++) {
            must.get(i).setWalletAddress(walletaddress);
            walletDBUtil.addAssets(must.get(i));
        }
    }
}
