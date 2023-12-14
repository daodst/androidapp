

package com.wallet.ctc.ui.dapp;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.GsonBuilder;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.R2;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.dapp.util.DappJsToWeb;
import com.wallet.ctc.ui.dapp.util.DappUtil;
import com.wallet.ctc.ui.dapp.util.DappWebViewClient;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.WalletSpUtil;
import com.wallet.ctc.view.dialog.DappMoreDialog;
import com.wallet.ctc.view.dialog.choosewallet.ChooseWalletDialog;

import butterknife.BindView;
import common.app.ActivityRouter;
import common.app.base.BaseFragment;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.utils.AllUtils;
import common.app.utils.LanguageUtil;
import common.app.utils.NetProxyUtil;



public class DappWebViewFragment extends BaseFragment {

    @BindView(R2.id.title_bar)
    RelativeLayout mTitleBar;
    @BindView(R2.id.iv_back)
    TextView mIvBack;
    @BindView(R2.id.tv_title)
    TextView mTvTitle;
    @BindView(R2.id.dapp_more)
    ImageView mDappMore;
    @BindView(R2.id.tv_back)
    ImageView mBtnLeft;
    @BindView(R2.id.baseweb_webview)
    WebView mWebView;
    @BindView(R2.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R2.id.progressBar)
    ProgressBar mProgressBar;

    
    private DappWebViewClient mDappWebViewClient;
    private WalletDBUtil mWalletDBUtil;
    private WalletEntity mWalletEntity;
    private MyAlertDialog mDialog;
    JsResult mJsResult;
    private String mCacheDirPath;

    private String mUrl, mTitle;
    private boolean isHideTitle;
    private int mToWalletType;

    public static final String KEY_URL = "url";
    public static final String KEY_TITLE = "title";
    public static final String KEY_HIDE_TITLE = "hideTitle";
    public static final String KEY_WALLET_TYPE = "walletType";


    
    private static final String PARAM_COIN_TYPE = "cointype";
    private static final String PARAM_APP_ID = "appid";
    private static final String PARAM_LANG = "lang";
    private static final String PARAM_DEVICE = "device";
    private static final String PARAM_NIGHT = "night";
    private static final String PARAM_TIMESTAMP = "tmp";


    
    public static DappWebViewFragment newInstance(String url, String title, boolean hideTitle, int toWalletType) {
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        args.putString(KEY_TITLE, title);
        args.putBoolean(KEY_HIDE_TITLE, hideTitle);
        args.putInt(KEY_WALLET_TYPE, toWalletType);
        DappWebViewFragment fragment = new DappWebViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DappWebViewFragment newInstance(String url, String title, int toWalletType) {
        return newInstance(url, title, false, toWalletType);
    }

    public static DappWebViewFragment newInstance(String url, String title) {
        return newInstance(url, title, false, -1);
    }

    
    
    
    
    

    @Override
    public void initParam() {
        if (null != getArguments()) {
            mUrl = getArguments().getString(KEY_URL);
            mTitle = getArguments().getString(KEY_TITLE);
            isHideTitle = getArguments().getBoolean(KEY_HIDE_TITLE, false);
            mToWalletType = getArguments().getInt(KEY_WALLET_TYPE, -1);
        }
        if (TextUtils.isEmpty(mUrl)) {
            
            mUrl = WalletSpUtil.getDappUrl();
        }
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.activity_dappwebview;
    }

    @Override
    public void initView(@Nullable View view) {
        mCacheDirPath = getActivity().getFilesDir() + "/webview";
        mWalletDBUtil = WalletDBUtil.getInstent(getActivity());

        if (ActivityRouter.isInstanceof(getActivity(), ActivityRouter.getMainActivityName()) || isHideTitle) {
            
            mTitleBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }

        
        mIvBack.setOnClickListener(view1 -> {
            back();
        });

        
        mBtnLeft.setOnClickListener(view1 -> {
            if (!ActivityRouter.isInstanceof(getActivity(), ActivityRouter.getMainActivityName())) {
                getActivity().finish();
            } else {
                
                loadDappUrl(true);
            }
        });

        
        mDappMore.setOnClickListener(view1 -> {
            DappMoreDialog dialog = new DappMoreDialog(getActivity());
            dialog.SetOnClick(new DappMoreDialog.OnClicks() {
                @Override
                public void reflse() {
                    dialog.dismiss();
                    loadDappUrl(true);
                }

                @Override
                public void onSwitchWallet() {
                    ChooseWalletDialog.showDialog(getActivity(), -1, (address1, walletType) -> {
                        
                        loadDappUrl(true);
                    });
                }
            });
            dialog.show(mUrl);
        });

        
        mDialog = new MyAlertDialog(getActivity(), getString(R.string.dapp_look_address));
        mDialog.setCancelable(false);
        mDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                mDialog.dismiss();

                if (null != mJsResult) {
                    mJsResult.confirm();
                }
            }

            @Override
            public void No() {
                mDialog.dismiss();
                if (null != mJsResult) {
                    mJsResult.confirm();
                }
                back();
            }
        });

        
        mSwipeRefreshLayout.setColorSchemeResources(R.color.default_theme_color);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                
                loadDappUrl(true);
            }
        });
        
        mSwipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> {
            int scrollY = 0;
            if (null != mWebView) {
                scrollY = mWebView.getScrollY();
            }
            return scrollY > 0;
        });
        

    }

    @Override
    public void initData() {
        
        initWebSetting();

        
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 90) {
                    if (null != mSwipeRefreshLayout) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBar.setProgress(newProgress);
                    }
                } else {
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mProgressBar.setProgress(newProgress);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWebTitle();
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if (!isDetached() && null != getContext() && !TextUtils.isEmpty(message) && message.startsWith("dapp://alert")) {
                    mJsResult = result;
                    if (null != mDialog) {
                        String title = url;
                        if (url.startsWith("file://") && url.contains("swap")){
                            title = mTvTitle.getText().toString();
                        }
                        mDialog.setDesc(title + getString(R.string.dapp_link_wallet));
                        mDialog.show();
                    }
                    return true;
                }
                return super.onJsAlert(view, url, message, result);
            }
        });
        
        mWebView.requestFocusFromTouch();

        
        setDappJsInjecte();
        
        NetProxyUtil.getInstance().enableWebProxy(mWebView);

        
        if (BuildConfig.DEBUG) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        
        
        loadDappUrl(false);
    }

    @Override
    protected void onFragmentVisible() {
        
        
        loadDappUrl(false);
    }

    
    private void loadDappUrl(boolean refresh) {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        int loadStatus = 0;
        if (refresh || null == mWalletEntity) {
            mWalletEntity = mWalletDBUtil.getWalletInfo();
            loadStatus = 1;
        } else {
            WalletEntity nowWallet = mWalletDBUtil.getWalletInfo();
            String address = getWalletAddr(mWalletEntity);
            if (null == address) {
                address = "";
            }
            if (null != nowWallet && (nowWallet.getType() != mWalletEntity.getType() ||
                    !address.equals(getWalletAddr(nowWallet)))) {
                
                mWalletEntity = nowWallet;
                loadStatus = 2;
            }
        }
        if (null == mWalletEntity) {
            showToast(getString(R.string.please_create_wallet));
            return;
        }

        if (mToWalletType > -1) {
            if (mToWalletType != mWalletEntity.getType()) {
                
                String toWalletName = WalletDBUtil.getInstent(getContext()).getWalletName(mToWalletType);
                String tip = String.format(getString(R.string.dapp_req_chaintype), toWalletName);
                showInfoDialog(mToWalletType, tip);
                return;
            }
        }


        if (refresh || loadStatus == 1 || loadStatus == 2) {
            if (DappUtil.isSupportDapp(mWalletEntity.getType())) {
                String address = getWalletAddr(mWalletEntity);
                if (null != mDappWebViewClient) {
                    
                    DappUtil.setWallet(mDappWebViewClient.getJsInjectorClient(), address, mWalletEntity.getType());
                }
            } else {
                
                LogUtil.w("this wallet type no support dapp");
                ToastUtil.showToast(getString(R.string.dapp_wallet_error));
            }


            mUrl = processUrl(mUrl);
            mWebView.loadUrl(mUrl);
            if (loadStatus == 2 || refresh) {
                
                mWebView.loadUrl( "javascript:window.location.reload( true )" );
            }
        }
    }

    
    private String processUrl(String url) {
        
        String host = Uri.parse(BuildConfig.HOST).getHost();
        if (!TextUtils.isEmpty(url) && url.contains(host)) {
            
            
            String params =
                    PARAM_LANG + "=" + LanguageUtil.getNowLocalStr(getActivity()) + "&"+
                    PARAM_DEVICE + "=app"+ "&"+
                    PARAM_NIGHT + "=0" + "&"+
                    PARAM_TIMESTAMP + "=" +System.currentTimeMillis()+"&"+
                    PARAM_COIN_TYPE + "="+getWalletCoinType();

            if (url.contains("?")) {
                url = AllUtils.removeParam(url, PARAM_APP_ID, PARAM_LANG, PARAM_DEVICE, PARAM_NIGHT, PARAM_COIN_TYPE, PARAM_TIMESTAMP);
                url = url.replace("?&", "?");
                if (url.endsWith("?")) {
                    url = url + params;
                } else {
                    url = url + "&"+params;
                }
            } else {
                url += "?"+params;
            }
        }
        LogUtil.i("dappUrl="+url);
        return url;
    }

    
    private String getWalletAddr(WalletEntity wallet) {
        return DappUtil.getAddress(wallet);
    }

    
    private String getWalletCoinType() {
        if (null == mWalletEntity) {
            mWalletEntity = mWalletDBUtil.getWalletInfo();
        }
        if (null == mWalletEntity) {
            return "ETH";
        }
        int walletType = mWalletEntity.getType();
        String cointype = "ETH";
        if (walletType == WalletUtil.BTC_COIN) {
            cointype = "BTC";
        } else if (walletType == WalletUtil.ETH_COIN) {
            cointype = "ETH";
        } else if (walletType == WalletUtil.HT_COIN) {
            cointype = "HT";
        } else if (walletType == WalletUtil.TRX_COIN) {
            cointype = "TRX";
        } else if (walletType == WalletUtil.BNB_COIN) {
            cointype = "BNB";
        } else if (walletType == WalletUtil.ETF_COIN) {
            cointype = "OKT";
        } else if(walletType == WalletUtil.MCC_COIN) {
            cointype = getString(R.string.default_token_name).toUpperCase();
        }
        return cointype;
    }

    
    private void setDappJsInjecte() {
        
        DappUtil.initWebDappJsInjecte(getContext(), mWebView, mDappWebViewClient=new DappWebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (null != mProgressBar) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.i("jues_NetP", "\n\n" + new GsonBuilder().create().toJson(request) + "\n\n");
                return super.shouldInterceptRequest(view, request);
            }

            

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
                Log.i("NetP", "onReceivedLoginRequest()");
            }
            }, new DappFuncationImpl(getContext()))
        .setIAlert(new DappJsToWeb.IAlert() {
            @Override
            public void alert(String tip, int walletType) {
                mWebView.post(() -> {
                    String tips = "";
                    if (!TextUtils.isEmpty(tip)) {
                        tips = tip;
                    } else {
                        tips = getString(R.string.dapp_not_support);
                    }
                    showInfoDialog(walletType, tips);
                });
            }
        });
    }

    
    private void showInfoDialog(int walleType, String tips) {
        MyAlertDialog dialog = new MyAlertDialog(getActivity(), tips);
        dialog.setYesText(getString(R.string.select_wallet));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                ChooseWalletDialog.showDialog(getActivity(), walleType, ((address, walletType) -> {
                    
                    loadDappUrl(false);
                }));
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initWebSetting() {
        if (null == mWebView) {
            return;
        }
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setUseWideViewPort(false);
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setBlockNetworkImage(false);
        settings.setBlockNetworkLoads(false);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(false);
        settings.setAppCacheEnabled(false);
        
        settings.setDatabaseEnabled(false);
        settings.setAppCacheMaxSize(1 * 1024 * 1024);   
        settings.setAppCacheEnabled(false);
        settings.setAppCachePath(mCacheDirPath);
        settings.setDatabasePath(mCacheDirPath);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

    }


    @Override
    public boolean onBackPressed() {
        back();
        return true;
    }

    
    private void back() {
        if (null == mWebView) {
            return;
        }
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            if (!ActivityRouter.isInstanceof(getActivity(), ActivityRouter.getMainActivityName())) {
                
                getActivity().finish();
            }
        }
    }

    private void getWebTitle() {
        if (null != mWebView) {
            WebBackForwardList forwardList = mWebView.copyBackForwardList();
            if (null != forwardList) {
                WebHistoryItem item = forwardList.getCurrentItem();
                if (item != null) {
                    mTvTitle.setText(item.getTitle());
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        
        NetProxyUtil.getInstance().disenableWebProxy(mWebView);
        super.onDestroyView();
        mDialog = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mWebView && null != mDappWebViewClient) {
            DappUtil.onDestroy(getActivity(), mDappWebViewClient.getJsInjectorClient(), mWebView);
        }
        if (null != mJsResult) {
            mJsResult.confirm();
            mJsResult = null;
        }
    }
}
