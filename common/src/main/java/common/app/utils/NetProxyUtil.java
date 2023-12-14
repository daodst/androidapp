package common.app.utils;

import android.util.Log;
import android.webkit.WebView;

import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import androidx.webkit.WebViewFeature;

import java.util.concurrent.Executor;


public class NetProxyUtil {

    private static final String TAG = "NetProxyUtil";
    private boolean isEnable;

    private static NetProxyUtil INSTANCE;
    private NetProxyUtil() {}

    public synchronized static NetProxyUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NetProxyUtil();
        }
        return INSTANCE;
    }

    private String mUserName, mPwd;
    public void initConfig(String userName, String password) {
        this.mUserName = userName;
        this.mPwd = password;
    }

    public String getUserName() {
        return "xs";
    }

    public String getPassword() {
        return "xs123.";
    }

    public String getVpnIp() {
        return "3.35.42.193"; 
    }

    public int getVpnPort() {
        return 8128;
    }

    public boolean isEnableWebProxy() {
        return isEnable;
    }

    
    public void enableWebProxy(WebView mWebView) {
        if (true) {
            return;
        }
        isEnable = true;
        





        if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {

            ProxyConfig proxyConfig = new ProxyConfig.Builder()
                    .addProxyRule("socks://127.0.0.1:8128")
                    .addDirect().build();
            ProxyController.getInstance().setProxyOverride(proxyConfig, new Executor() {
                @Override
                public void execute(Runnable command) {
                    
                    LogUtil.i(TAG, "eanble webview proxy");
                }
            }, new Runnable() {
                @Override
                public void run() {
                    LogUtil.i(TAG, "webview proxy change");
                }
            });
        } else {
            WebviewSettingProxy.setProxy(mWebView, getVpnIp(), getVpnPort()+"",
                    "com.app.App"
            );
        }

    }

    
    public void disenableWebProxy(WebView mWebView) {
        if (true) {
            return;
        }
        isEnable = false;
        if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            ProxyController.getInstance().clearProxyOverride(new Executor() {
                @Override
                public void execute(Runnable command) {
                    Log.i(TAG, "webview disable proxy");
                }
            }, new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "webview proxy dis change");
                }
            });
        } else {
            WebviewSettingProxy.revertBackProxy(
                    mWebView,
                    "com.imfm.app.App"
            );
        }
    }
}
