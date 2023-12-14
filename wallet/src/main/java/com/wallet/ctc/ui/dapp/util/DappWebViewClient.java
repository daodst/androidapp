

package com.wallet.ctc.ui.dapp.util;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wallet.ctc.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.utils.NetProxyUtil;
import okhttp3.Cookie;
import okhttp3.HttpUrl;


public class DappWebViewClient extends WebViewClient {
    private static final String TAG = "DappTest";
    private final JsInjectorClient jsInjectorClient;
    private final Object lock = new Object();
    private WebViewCookieJar mCookieJar = new WebViewCookieJar();

    public DappWebViewClient() {
        this.jsInjectorClient = new JsInjectorClient();
    }

    public JsInjectorClient getJsInjectorClient() {
        return jsInjectorClient;
    }

    
    public void setDAppSDK(String dappJsSdk) {
        if (null != jsInjectorClient) {
            jsInjectorClient.setDAppSDK(dappJsSdk);
        }
    }

    
    private synchronized WebResourceResponse proxyRequest(WebView view, WebResourceRequest request) {

        String urlString = request.getUrl().toString().split("#")[0];
        String method = request.getMethod();
        HttpUrl httpUrl = HttpUrl.parse(urlString);

        int statusCode = 204;
        HttpURLConnection connection = null;
        try {

            boolean proxied = NetProxyUtil.getInstance().isEnableWebProxy();
            if (proxied) {
                LogUtil.w("proxied="+proxied);
                String proxyHost = NetProxyUtil.getInstance().getVpnIp();
                int proxyPort = NetProxyUtil.getInstance().getVpnPort();
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
                connection = (HttpURLConnection) new URL(urlString).openConnection(proxy);

                String userName = NetProxyUtil.getInstance().getUserName();
                String pwd = NetProxyUtil.getInstance().getPassword();
                String headerKey = "Proxy-Authorization";
                String userPwd = userName+":"+pwd;
                try {
                    String headerValue = "Basic " + new String(Base64.encode(userPwd.getBytes(), Base64.DEFAULT), "utf-8");
                    connection.setRequestProperty(headerKey, headerValue);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Authenticator.setDefault(
                        new Authenticator() {
                            @Override
                            public PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(
                                        userName, pwd.toCharArray());
                            }
                        });
            } else {
                connection = (HttpURLConnection) new URL(urlString).openConnection();
            }

            LogUtil.i("requestMethod="+request.getMethod()+", "+urlString);
            connection.setRequestMethod(request.getMethod());
            StringBuilder headerStr = new StringBuilder();
            for (Map.Entry<String, String> requestHeader : request.getRequestHeaders().entrySet()) {
                headerStr.append("\n"+requestHeader.getKey()+":"+requestHeader.getValue());
                connection.setRequestProperty(requestHeader.getKey(), requestHeader.getValue());
            }
            LogUtil.i("headerStr="+urlString+headerStr);
            List<Cookie> cookieList = mCookieJar.loadForRequest(httpUrl);
            if (null != cookieList && !cookieList.isEmpty()) {
                connection.setRequestProperty("Cookie", mCookieJar.cookieHeader(cookieList));
            }


            try {
                statusCode = connection.getResponseCode();
            } catch (SocketException ex) {
                ex.printStackTrace();
                LogUtil.e("fail ---"+ex.getMessage());
                if ("Connection reset".equalsIgnoreCase(ex.getMessage())) {
                    return proxyRequest(view, request);
                }
            }

            LogUtil.i("con statusCode ="+statusCode);

            
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String encoding = connection.getContentEncoding();
            Map<String, String> responseHeaders = new HashMap<>();
            for (String key : connection.getHeaderFields().keySet()) {
                responseHeaders.put(key, connection.getHeaderField(key));
            }
            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
            if (null != cookies && !cookies.isEmpty()) {
                mCookieJar.saveFromResponse(httpUrl, mCookieJar.parseAll(httpUrl, cookies));
            }

            String mimeType = "text/plain";
            if (connection.getContentType() != null && !connection.getContentType().isEmpty()) {
                mimeType = connection.getContentType().split("; ")[0];
            }
            String responseMessage = connection.getResponseMessage();
            if (responseMessage == null) {
                responseMessage = "";
            }
            return new WebResourceResponse(mimeType, encoding, statusCode, responseMessage, responseHeaders, in);
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LogUtil.e("fail="+method+"-----"+urlString+"--"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("fail="+method+"-----"+urlString+"--"+e.getMessage());
        }
        LogUtil.w("no get web content :" + statusCode);
        
        return new WebResourceResponse("text/plain", "UTF-8", statusCode, "No Content", new HashMap<String, String>(), new ByteArrayInputStream(new byte[]{}));
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (true) {
            return super.shouldInterceptRequest(view, request);
        }

        if (false) {
                WebResourceResponse webResourceResponse = proxyRequest(view, request);
                synchronized (this.lock) {
                    return webResourceResponse;
                }

        }

        WebResourceResponse webResourceResponse = null;
        if (webResourceResponse != null) {

            int statusCode = webResourceResponse.getStatusCode();
            LogUtil.i("statusCode="+statusCode);
            if (statusCode >= 300 && statusCode < 400) {
                
                String redirectUrl = webResourceResponse.getResponseHeaders().get("location");
                LogUtil.w("redirect url ="+redirectUrl);
                if(!TextUtils.isEmpty(redirectUrl) && null!=view) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl(redirectUrl+"?"+ System.currentTimeMillis());
                        }
                    });
                }
                return null;
            }
            LogUtil.d("webResourceResponse ！= null");
            InputStream inputStream = webResourceResponse.getData();
            try {
                byte[] arrayOfByte = new byte[inputStream.available()];
                if (inputStream.read(arrayOfByte) != 0) {
                    LogUtil.i("inject js");
                    webResourceResponse.setData(new ByteArrayInputStream(this.jsInjectorClient.injectJS(new String(arrayOfByte)).getBytes()));
                    return webResourceResponse;
                } else {
                    LogUtil.w("inject js null");
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e("inject js error : "+e.getMessage());
                return null;
            }

        } else {
            String url = request.getUrl().toString();
            

            String method = request.getMethod();
            boolean isForMainFrame = request.isForMainFrame();
            LogUtil.i("method:"+method+"---"+isForMainFrame+"---"+url);
            String scheme = request.getUrl().getScheme();

                HttpUrl httpUrl = HttpUrl.parse(url);
                if (httpUrl == null) {
                    LogUtil.e("httpurl is null return");
                    return null;
                }
                Map<String,String> requestHeaders = request.getRequestHeaders();
                JsInjectorResponse jsInjectorResponse = this.jsInjectorClient.loadUrl(url, method, requestHeaders);
                if ((jsInjectorResponse != null) && (jsInjectorResponse.data != null)){
                    if (jsInjectorResponse.isRedirect) {
                        LogUtil.i("url redirect ->"+jsInjectorResponse.url);
                        if(!TextUtils.isEmpty(jsInjectorResponse.url)&&null!=view) {
                            view.post(new Runnable() {
                                @Override
                                public void run() {
                                    view.loadUrl(jsInjectorResponse.url+"?"+ System.currentTimeMillis());
                                }
                            });
                        }
                        return null;
                    }
                    WebResourceResponse webResourceResponse1 = new WebResourceResponse(jsInjectorResponse.mime, jsInjectorResponse.charset, jsInjectorResponse.data);
                    synchronized (this.lock) {
                        return webResourceResponse1;
                    }

                } else {
                    return null;
                }

        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        LogUtil.d("onPageStarted" + url);
        
        if (null != view && null != jsInjectorClient && !TextUtils.isEmpty(jsInjectorClient.getSdkJs())) {
            view.evaluateJavascript(jsInjectorClient.getSdkJs(), null);
            view.evaluateJavascript(jsInjectorClient.getDappJs(), null);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.d("onPageFinished()" + url);
        
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtil.d("onReceivedError()" + error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        LogUtil.e("onReceivedHttpError()"+errorResponse.getStatusCode()+", "+errorResponse.getData());
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
        LogUtil.e("onReceivedSslError()" + error);
    }
}
