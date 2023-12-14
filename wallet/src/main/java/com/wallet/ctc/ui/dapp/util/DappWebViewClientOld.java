

package com.wallet.ctc.ui.dapp.util;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wallet.ctc.util.LogUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DappWebViewClientOld extends WebViewClient {
    private static final String TAG = "DappTest";
    private final Object lock = new Object();
    private String url;
    private String guid;
    private String js;
    public static String html;

    public DappWebViewClientOld(String url, String guid, String js) {
        this.url = url;
        this.guid = guid;
        this.js = js;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String address) {
        if(!url.equals(address)) {
            return super.shouldInterceptRequest(view,address);
        }
        return getWeb(url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String address) {
        
        
        if (!url.contains("guid")) {
            url = address + guid;
        }
        LogUtil.d("" + url);
        view.loadUrl(url);
        return true;
    }

    private WebResourceResponse getWeb(final String address) {
        Log.d(TAG, "" + address);
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            URL url = new URL(address);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10 * 1000);
            httpURLConnection.setReadTimeout(40 * 1000);
            reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line+"\n");
            }
        } catch (Exception e) {
            Log.d(TAG, " " + e.toString());
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException ioe) {

                }
            }
        }
        html=stringBuffer.toString();
        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html", "utf-8", new ByteArrayInputStream(injectJS(stringBuffer.toString(), js).getBytes()));
        return webResourceResponse;
    }

    private int getInjectionPosition(String paramString) {
        paramString = paramString.toLowerCase();
        int j = paramString.indexOf("<!--");
        int i = paramString.indexOf("<script");
        if (j >= 0) {
            i = Math.min(i, j);
        }
        j = i;
        if (i < 0) {
            j = paramString.indexOf("</head");
        }
        return j;
    }

    private String injectJS(String paramString1, String paramString2) {
        if (TextUtils.isEmpty(paramString1)) {
            return paramString1;
        }
        int i = getInjectionPosition(paramString1);
        String str = paramString1;
        if (i > 0){
            str = paramString1.substring(0, i);
            paramString1 = paramString1.substring(i);
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(str);
            localStringBuilder.append(paramString2);
            localStringBuilder.append(paramString1);
            str = localStringBuilder.toString();
        }
        LogUtil.d(str);
        return str;
    }
}
