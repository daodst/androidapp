

package com.wallet.ctc.ui.dapp.util;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wallet.ctc.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLSocketFactory;

import common.app.base.model.http.DelegatingSocketFactory;
import common.app.utils.NetProxyUtil;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;


public class JsInjectorClient {
    private static final String DEFAULT_CHARSET = "utf-8";
    private static final String DEFAULT_MIME_TYPE = "text/html";
    private static final String JS_TAG_TEMPLATE = "<script type=\"text/javascript\">%1$s</script>";
    private OkHttpClient httpClient = null;
    private String mDAppSDK = "";

    
    private String mSdkJs = "";
    private String mDappJs = "";

    private Request buildRequest(String paramString, String method, Map<String, String> paramMap) {
        HttpUrl httpUrl = HttpUrl.parse(paramString);
        if (httpUrl == null) {
            return null;
        }
        RequestBody body = null;
        Request.Builder builder = null;
        if ("POST".equalsIgnoreCase(method)) {
            body =RequestBody.create(MediaType.parse("application/json"), "{}");
            builder = new Request.Builder().post(body).headers(Headers.of(paramMap)).url(paramString);
        } else {
            builder = new Request.Builder().get().headers(Headers.of(paramMap)).url(paramString);
        }
        return builder.build();
    }

    private JsInjectorResponse buildResponse(Response paramResponse) {
        int i = paramResponse.code();
        Request localRequest = null;
        String responseStr = null;
        InputStream is = null;
        ResponseBody responseBody = paramResponse.body();
        try {
            if (paramResponse.isSuccessful()) {
                is = new BufferedInputStream(responseBody.byteStream());
            }
        } catch (Exception localIOException) {
            Log.d("READ_BODY_ERROR", "Ex", localIOException);
            responseStr = null;
        }
        localRequest = paramResponse.request();
        Response response = paramResponse.priorResponse();
        boolean bool;
        if ((response != null) && (response.isRedirect())) {
            bool = true;
        } else {
            bool = false;
        }
        String contentType = getContentTypeHeader(paramResponse);
        String mimeType = "";
        if (null != responseBody.contentType()) {
            mimeType = responseBody.contentType().type() + "/" + responseBody.contentType().subtype();
        } else {
            mimeType = getMimeType(contentType);
        }
        String chatset = "utf-8";
        if (null != responseBody.contentType() && null != responseBody.contentType().charset()) {
            chatset = responseBody.contentType().charset().displayName();
        } else {
            chatset = getCharset(contentType);
        }


        LogUtil.i("mimeType="+mimeType);
        return new JsInjectorResponse(is, i, localRequest.url().toString(), mimeType, chatset, bool);
    }

    private OkHttpClient createHttpClient() {
        boolean proxyed = NetProxyUtil.getInstance().isEnableWebProxy();
        OkHttpClient.Builder builder = new OkHttpClient.Builder().cookieJar(new WebViewCookieJar());
        LogUtil.i("proxyed="+proxyed);
        if (proxyed) {
            Authenticator.setDefault(
                    new Authenticator() {
                        @Override
                        public PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    NetProxyUtil.getInstance().getUserName(), NetProxyUtil.getInstance().getPassword().toCharArray());
                        }
                    }
            );

            System.setProperty("http.proxyUser", NetProxyUtil.getInstance().getUserName());
            System.setProperty("http.proxyPassword", NetProxyUtil.getInstance().getPassword());
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

            String vpnIp = NetProxyUtil.getInstance().getVpnIp();
            int vpnPort = NetProxyUtil.getInstance().getVpnPort();
            builder.proxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(vpnIp, vpnPort)))
                    .socketFactory(new DelegatingSocketFactory(SSLSocketFactory.getDefault()));
            builder.proxyAuthenticator(new okhttp3.Authenticator() {
                @Nullable
                @Override
                public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
                    
                    String credential = Credentials.basic(NetProxyUtil.getInstance().getUserName(), NetProxyUtil.getInstance().getPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
                }
            });
        }
        return builder.build();
    }

    private String getCharset(String paramString) {
        Matcher matcher = Pattern.compile("charset=([a-zA-Z0-9-]+)").matcher(paramString);
        if ((matcher.find()) && (matcher.groupCount() >= 2)) {
            return matcher.group(1);
        }
        return "utf-8";
    }

    private String getContentTypeHeader(Response paramResponse) {
        String contentType = "";
        Headers headers = paramResponse.headers();
        if (TextUtils.isEmpty(headers.get("Content-Type"))) {
            if (TextUtils.isEmpty(headers.get("content-Type"))) {
                contentType = "text/data; charset=utf-8";
            } else {
                contentType = headers.get("content-Type");
            }
        } else {
            contentType = headers.get("Content-Type");
        }
        String localObject = contentType;
        if (contentType != null) {
            localObject = contentType.trim();
        }
        return localObject;
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

    private String getMimeType(String paramString) {
        Matcher matcher = Pattern.compile("^.*(?=;)").matcher(paramString);
        if (matcher.find()) {
            return matcher.group();
        }
        return "text/html";
    }

    private String injectJS(String paramString1, String paramString2) {
        if (TextUtils.isEmpty(paramString1)) {
            return paramString1;
        }
        int i = getInjectionPosition(paramString1);
        String str = paramString1;
        if (i > 0) {
            str = paramString1.substring(0, i);
            paramString1 = paramString1.substring(i);
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append(str);
            localStringBuilder.append(paramString2);
            localStringBuilder.append(paramString1);
            str = localStringBuilder.toString();
        }
        return str;
    }

    String assembleJs(String paramString) {
        return String.format(paramString, new Object[]{this.mDAppSDK});
    }

    String injectJS(String paramString) {
        return injectJS(paramString, mDAppSDK);
    }


    JsInjectorResponse loadUrl(String paramString, String method, Map<String, String> paramMap) {
        if (this.httpClient == null) {
            this.httpClient = createHttpClient();
        }
        Request request = buildRequest(paramString, method, paramMap);
        try {
            JsInjectorResponse response = buildResponse(this.httpClient.newCall(request).execute());
            return response;
        } catch (Exception e) {
            Log.d("zzz", ""+e.getMessage());
            if ("Connection reset".equalsIgnoreCase(e.getMessage())) {
                return loadUrl(paramString, method, paramMap);
            }
        }
        return null;
    }

    public void setDAppSDK(String paramString) {
        this.mDAppSDK = paramString;
    }

    public String getSdkJs() {
        return mSdkJs;
    }

    public void setSdkJs(String sdkJs) {
        mSdkJs = sdkJs;
    }

    public String getDappJs() {
        return mDappJs;
    }

    public void setDappJs(String dappJs) {
        mDappJs = dappJs;
    }

    public void cleanCall() {
        if (this.httpClient != null) {
            httpClient.dispatcher().cancelAll();
        }
    }
}
