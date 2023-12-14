

package com.wallet.ctc.ui.dapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wallet.ctc.R;

import common.app.base.BaseActivity;



public class DappWebViewActivity extends BaseActivity {

    private String url, title;
    private boolean isHideTtite;
    private int toWalletType = -1;
    private final String FRAGMENT_TAG = "dappFragment";

    public static void startDappWebViewActivity(Context context, String url, String title, boolean hideTtile, int toWalletType){
        if (null == context) {
            return;
        }
        Intent intent=new Intent(context,DappWebViewActivity.class);
        intent.putExtra(DappWebViewFragment.KEY_URL,url);
        intent.putExtra(DappWebViewFragment.KEY_TITLE,title);
        intent.putExtra(DappWebViewFragment.KEY_HIDE_TITLE,hideTtile);
        intent.putExtra(DappWebViewFragment.KEY_WALLET_TYPE,toWalletType);
        context.startActivity(intent);
    }
    public static void startDappWebViewActivity(Context context, String url, String title){
        startDappWebViewActivity(context, url, title, false, -1);
    }

    public static void startDappWebViewActivity(Context context, String url, String title, int toWalletType){
        startDappWebViewActivity(context, url, title, false, toWalletType);
    }

    public static void startDappWebViewActivity(Context context, String url){
        startDappWebViewActivity(context, url, "", false, -1);
    }

    public static void startDappWebViewActivity(Context context, String url, int toWalleType){
        startDappWebViewActivity(context, url, "", false, toWalleType);
    }

    @Override
    public void initParam() {
       url = getIntent().getStringExtra(DappWebViewFragment.KEY_URL);
       title = getIntent().getStringExtra(DappWebViewFragment.KEY_TITLE);
       isHideTtite = getIntent().getBooleanExtra(DappWebViewFragment.KEY_HIDE_TITLE, false);
       toWalletType = getIntent().getIntExtra(DappWebViewFragment.KEY_WALLET_TYPE, -1);
    }

    @Override
    public void initView(@Nullable View view) {
        setContentView(R.layout.activity_fragment_container);
        Fragment dappFragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (dappFragment == null) {
            dappFragment = DappWebViewFragment.newInstance(url, title, isHideTtite, toWalletType);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, dappFragment, FRAGMENT_TAG).commitAllowingStateLoss();
        }
    }
}
