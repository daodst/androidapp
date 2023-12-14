

package com.wallet.ctc.ui.dapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.DappResult;
import com.wallet.ctc.model.blockchain.SgbDappBean;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.util.LogUtil;

import org.json.JSONObject;

import common.app.ActivityRouter;
import common.app.mall.util.ToastUtil;



public class DappJsToWeb {
    public static final String METHOD_NAME = "trust";
    private static final String TAG = "DappJsToWeb";
    private Context mContext;
    private WebView mWebView;
    private DappFuncation dappFuncation;
    private Handler mHandler;
    private JsInjectorClient jsInjectorClient;

    protected DappJsToWeb(Context context, WebView webView, DappFuncation dappFuncation) {
        this.mWebView = webView;
        this.dappFuncation = dappFuncation;
        this.mContext = context;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    public void setJsInjectorClient(JsInjectorClient jsInjectorClient) {
        this.jsInjectorClient = jsInjectorClient;
    }

    public JsInjectorClient getJsInjectorClient() {
        return jsInjectorClient;
    }

    String backFunction;

    @JavascriptInterface
    public void resultManager(String data, String back) {
        try {
            if (null != dappFuncation) {
                mHandler.post(new Runnable() {
                    public void run() {
                        backFunction = back;
                        dappFuncation.getTrxSign(data);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    
    @JavascriptInterface
    public void signTransaction(String reqid, String to, String value, String nonce, String gasLimit, String gasPrice, String data) {
        if (null != dappFuncation) {
            mHandler.post(new Runnable() {
                public void run() {
                    dappFuncation.sendEthTransaction(reqid, to, value, nonce, gasLimit, gasPrice, data);
                }
            });
        }

    }

    @JavascriptInterface
    public void request(String id, String name, String data) {
        if (null != dappFuncation) {
            mHandler.post(new Runnable() {
                public void run() {
                    dappFuncation.ethRequest(id, name, data);
                }
            });
        }
    }

    @JavascriptInterface
    public void switchEthereumChain(String id, String address, String chainId) {
        if (null != dappFuncation) {
            mHandler.post(new Runnable() {
                public void run() {
                    dappFuncation.switchEthereumChain(id, address, chainId);
                }
            });
        }
    }

    @JavascriptInterface
    public void addEthereumChain(String id, String address, String chainId, String rpcUrls, String symbol) {
        mHandler.post(new Runnable() {
            public void run() {
                dappFuncation.addEthereumChain(id,address,chainId,rpcUrls,symbol);
            }
        });
    }

    @JavascriptInterface
    public void Extension(String data) {
        LogUtil.d("Dappdata" + data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String path = jsonObject.getString("path");
            if (path.equalsIgnoreCase("extensionRequest")) {
                data = jsonObject.getJSONObject("data").toString();
                LogUtil.d("extensionRequest" + data);
                SgbDappBean mBean = new Gson().fromJson(data, SgbDappBean.class);
                if (null != dappFuncation) {
                    mHandler.post(new Runnable() {
                        public void run() {
                            dappFuncation.sgbRequest(mBean);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trxSignResult(DappResult result) {
        if (!result.isStatus()) {
            mWebView.loadUrl("javascript:onerror('" + result.getResult() + "')");
        } else {
            mWebView.loadUrl("javascript:" + backFunction + "('" + result.getResult() + "')");
        }
    }

    public void ethTransactionResult(String reqid, DappResult result) {
        if (result.isStatus()) {
            mWebView.loadUrl("javascript:success(" + reqid + ",'" + result.getResult() + "')");
        } else {
            mWebView.loadUrl("javascript:error(" + reqid + ",'" + result.getResult() + "')");
        }
    }

    
    public void setConfig(String address, String rpcUrl, String chainId) {
        if (null != mWebView && null != mContext) {
            mWebView.post(()->{
                mWebView.loadUrl("javascript:setconfig('" + address + "','"+rpcUrl+"','"+chainId+"')");
            });
        }
    }

    
    public void emitChainChanged(String hexChainId) {
        if (null != mWebView && null != mContext) {
            mWebView.post(()->{
                mWebView.loadUrl("javascript:emitChainChanged('" + hexChainId +"')");
            });
        }
    }

    public void sgbResult(String result) {
        mWebView.loadUrl("javascript:walletExtension.onAppResponse(" + result + ")");
    }

    public void reload() {
        mWebView.reload();
    }

    @JavascriptInterface
    public void back() {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    @JavascriptInterface
    public void toApp1(String name, String value) {
        if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    private IAlert mIAlert;

    public void setIAlert(IAlert IAlert) {
        mIAlert = IAlert;
    }

    public interface IAlert {
        
        void alert(String tip, int walletType);
    }

    @JavascriptInterface
    public void toDapp(String url, String title, String type, String is_open, String alertStr) {
        if (TextUtils.isEmpty(url)) {
            ToastUtil.showToast(R.string.please_dev_wait);
            return;
        }
        if (TextUtils.equals(type, "0")) {
            ToastUtil.showToast(R.string.application_hint);
            DappWebViewActivity.startDappWebViewActivity(mContext, url, title);
        } else {
            if (TextUtils.equals("1", is_open)) {
                ToastUtil.showToast(R.string.application_hint);
                DappWebViewActivity.startDappWebViewActivity(mContext, url, title);
            } else {
                if (null != mIAlert) {
                    mIAlert.alert(alertStr, getChainType(type));
                }

            }
        }
    }

    
    public int getChainType(String type) {
        if (TextUtils.isEmpty(type)) {
            return -1;
        }
        if (type.equals("1")) {
            return WalletUtil.ETH_COIN;
        } else if (type.equals("2")) {
            return WalletUtil.TRX_COIN;
        } else if(type.equals("3")) {
            return WalletUtil.BTC_COIN;
        } else if(type.equals("4")) {
            return WalletUtil.HT_COIN;
        } else if(type.equals("5")) {
            return WalletUtil.BNB_COIN;
        } else if(type.equals("6")) {
            return WalletUtil.ETF_COIN;
        } else {
            return -1;
        }
    }

    @JavascriptInterface
    public void toBanner(String url, String title, String linkIn) {
        DappWebViewActivity.startDappWebViewActivity(mContext, url, title);
    }

    @JavascriptInterface
    public void searchLink(String url) {
        DappWebViewActivity.startDappWebViewActivity(mContext, url, "", 1);
    }

    @JavascriptInterface
    public void callCamera(String content) {
        scanQrcode();
    }

    @JavascriptInterface
    public void scanQrcode() {
        Intent intent = ActivityRouter.getStringContentIntent(mContext, ActivityRouter.Common.F_QRCodeFragment, "toDapp");
        mContext.startActivity(intent);
    }
}
