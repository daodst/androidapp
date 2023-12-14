

package com.wallet.ctc.ui.dapp.util;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETF_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.HT_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;
import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChainsRpcsUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.CoinType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import common.app.AppApplication;
import common.app.utils.FileUtils;


public class DappUtil {

    
    public static DappJsToWeb initWebDappJsInjecte(Context context, WebView webView, DappWebViewClient webViewClient, DappFuncation dappFuncation) {
        DappJsToWeb dappJsToWeb=new DappJsToWeb(context, webView, dappFuncation);
        dappFuncation.setJsToWeb(dappJsToWeb);
        
        webView.addJavascriptInterface(dappJsToWeb,DappJsToWeb.METHOD_NAME);
        webView.setWebViewClient(webViewClient);
        dappJsToWeb.setJsInjectorClient(webViewClient.getJsInjectorClient());
        return dappJsToWeb;
    }

    
    public static boolean isSupportDapp(int coinType) {
        if (coinType == TRX_COIN || coinType == ETF_COIN || coinType == ETH_COIN || coinType == BNB_COIN || coinType == HT_COIN ||
            coinType == MCC_COIN) {
            return true;
        } else {
            return false;
        }
    }

    
    public static void setWallet(JsInjectorClient client, String address, int walletType) {
        if (null == client) {
            Log.w("dapp", "client is null");
            return;
        }
        if (!isSupportDapp(walletType)) {
            Log.w("dapp", walletType+" chaintype is no support");
            return;
        }
        
        if (walletType == TRX_COIN) {
            setWallet(client, address, CoinType.TRON, "", "",false);
        } else {
            String chainId = ChainsRpcsUtil.walletTypeToChainId(walletType)+"";
            String rpcUrl = ChainsRpcsUtil.getRpcUrlByWalletType(walletType);
            setWallet(client,address, CoinType.ETH,rpcUrl, chainId,false);
        }

    }

    
    public static String getAddress(WalletEntity wallet) {
        if (null == wallet) {
            return "";
        }
        if (wallet.getType() == MCC_COIN) {
            return wallet.getDefaultAddress();
        } else {
            return wallet.getAllAddress();
        }
    }



    
    public static void setWallet(JsInjectorClient client, String walletAddress, @CoinType int CoinType, String jsonrpcAddress, String chainId,boolean isDebug){
        String js=getJS(AppApplication.getContext(), client, CoinType,walletAddress,jsonrpcAddress,chainId,isDebug);
        client.setDAppSDK(js);
    }

    
    public static String getJS(Context context, JsInjectorClient client, @CoinType int type, String address, String rpcadress, String port,boolean isDebug){
        String dappSdk="";

        String ethJS = "";
        String sgbJS = "";
        String trxJS = "";
        String dappJS ="";

        if(type== CoinType.ETH){
            if(TextUtils.isEmpty(ethJS)){
                ethJS=getRawFile(context,R.raw.trust);
            }
            if(TextUtils.isEmpty(sgbJS)){
                sgbJS=getRawFile(context,R.raw.polka_extension);
            }
            if(TextUtils.isEmpty(rpcadress)){
                Toast.makeText(context,"RPC",Toast.LENGTH_SHORT).show();
                return dappSdk;
            }
            if(TextUtils.isEmpty(port)){
                Toast.makeText(context,"RPC",Toast.LENGTH_SHORT).show();
                return dappSdk;
            }
            String jappjs  =getRawFile(context,R.raw.trust_init);
            dappJS = String.format(jappjs, address,rpcadress,port);
            dappSdk= String.format("<script type=\"text/javascript\">%1$s</script><script type=\"text/javascript\">%2$s</script><script type=\"text/javascript\">%3$s</script>", ethJS, dappJS,sgbJS);
        }else if(type== CoinType.TRON){
            if(TextUtils.isEmpty(trxJS)){
                trxJS=getRawFile(context,R.raw.simplify_tronweb);;
            }
            String jappjs =getRawFile(context,R.raw.app);
            if(isDebug){
                dappJS=String.format(jappjs, address,1);
            }else {
                dappJS=String.format(jappjs, address,0);
            }
            dappSdk= String.format("<script type=\"text/javascript\">%1$s</script><script type=\"text/javascript\">%2$s</script>", dappJS, trxJS);
        }
        
        client.setDappJs(dappJS);
        client.setSdkJs(ethJS);
        if (!TextUtils.isEmpty(trxJS)) {
            client.setSdkJs(trxJS);
        }

        ethJS = null;
        sgbJS = null;
        trxJS = null;
        dappJS = null;
        return dappSdk;
    }

    
    private static String getRawFile(Context context,int area) {
        InputStream is =context.getResources().openRawResource(area);
        InputStreamReader isr = new InputStreamReader(is);
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(isr);
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line+"\n");
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void onDestroy(Context context, JsInjectorClient client,  WebView webView){
        if(null!=client) {
            client.cleanCall();
        }
        System.gc();
        cleanCun(webView, context);
    }
    private static void cleanCun(WebView mWebView, Context mContext){
        try {
            if(null!=mWebView) {
                mWebView.clearFormData();
                mWebView.clearMatches();
                mWebView.stopLoading();
                mWebView.removeAllViews();
                mWebView.loadUrl("about:blank");
                mWebView.clearHistory();
                mWebView.clearCache(true);
                mWebView.setVisibility(View.GONE);
                mWebView.destroy();
                mWebView=null;
            }
            if(null!=mContext) {
                mContext.deleteDatabase("webview.db");
                mContext.deleteDatabase("webviewCache.db");
                File cacheDir = new File(mContext.getFilesDir() + "/webcache");
                File hws = new File(mContext.getCacheDir() + "/WebView/hws_webview/index-dir/the-real-index");
                if (hws.exists()) {
                    try {
                        FileOutputStream fos = new FileOutputStream(hws);
                        
                        byte[] bytes = " ".getBytes();
                        fos.write(bytes);
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File real = new File(mContext.getCacheDir() + "/WebView/hws_webview/index-dir");
                int re = FileUtils.deleteAllFiles(real);
                
                int ca = FileUtils.deleteAllFiles(mContext.getCacheDir());
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    int da = FileUtils.delWebview(mContext.getDataDir());
                }
                
                int fi = FileUtils.deleteAllFiles(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
