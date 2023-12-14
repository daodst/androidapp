

package com.wallet.ctc.ui.dapp.util;

import static com.wallet.ctc.crypto.WalletUtil.SGB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.getDotSdk;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.gson.Gson;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.SubGameKeyBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;

import owallet.MnemonicOut;



public class SgbJsToWeb {
    private Context mContext;
    private WebView mWebView;

    public SgbJsToWeb(Context mContext, WebView webView) {
        this.mContext = mContext;
        this.mWebView = webView;
    }
    @JavascriptInterface
    public void getKeystory(String data,String pwd) {
        LogUtil.d("  "+data);
        try {
            SubGameKeyBean subGameKeyBean=new Gson().fromJson(data,SubGameKeyBean.class);
            MnemonicOut mnemonicOut = getDotSdk().getAccount().generateByMnemonic(subGameKeyBean.getMnemonic());
            subGameKeyBean.setAddress(mnemonicOut.getAddress());
            WalletEntity walletEntity=WalletDBUtil.getInstent(mContext).getWalletInfoByAddress(subGameKeyBean.getAddress(),SGB_COIN);
            if(null!=walletEntity){
                data=new Gson().toJson(subGameKeyBean);
                walletEntity.setmKeystore(DecriptUtil.Encrypt(data, pwd));
                WalletDBUtil.getInstent(mContext).updateWalletInfoByAddress(walletEntity);
                LogUtil.d("keystory  "+data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @JavascriptInterface
    public void getKeystoryError(String data,String m,String pwd) {
        LogUtil.d("getKeystoryError  "+data);
    }
    @JavascriptInterface
    public void signResult(String data,String type) {
        LogUtil.d("signResult  "+data+"    "+type);
        if(data.equalsIgnoreCase("undefined")){
            return;
        }
        sgbResult("'"+type+"','"+data+"'");
    }
    @JavascriptInterface
    public void signResultError(String data,String type) {
        LogUtil.d("signResultError  "+data);
        if(data.equalsIgnoreCase("undefined")){
            return;
        }
        sgbResult("'"+type+"',null,new Error('Rejected')");
    }
    @JavascriptInterface
    public void initError(String data) {
        LogUtil.d("initError  "+data);
    }
    @JavascriptInterface
    public void initSuccess() {
        LogUtil.d("initSuccess  ");
    }

    public void sgbResult(String result) {
        mWebView.loadUrl("javascript:walletExtension.onAppResponse(" + result + ")");
    }
}
