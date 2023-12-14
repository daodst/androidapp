

package com.wallet.ctc.ui.dapp.util;


import android.text.TextUtils;
import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;


public class WebViewCookieJar implements CookieJar {
    private CookieManager webViewCookieManager;

    public WebViewCookieJar()
    {
        try
        {
            this.webViewCookieManager = CookieManager.getInstance();
            return;
        }
        catch (Exception localException) {}
    }

    public List<Cookie> loadForRequest(HttpUrl paramHttpUrl)
    {
        if (this.webViewCookieManager != null)
        {
            Object localObject = paramHttpUrl.toString();
            localObject = this.webViewCookieManager.getCookie((String)localObject);
            if ((localObject != null) && (!TextUtils.isEmpty((CharSequence)localObject)))
            {
                String[] localObjectArray = ((String)localObject).split(";");
                ArrayList localArrayList = new ArrayList();
                int j = localObjectArray.length;
                int i = 0;
                while (i < j)
                {
                    localArrayList.add(Cookie.parse(paramHttpUrl, localObjectArray[i]));
                    i += 1;
                }
                return localArrayList;
            }
        }
        return Collections.emptyList();
    }

    public String cookieHeader(List<Cookie> cookies) {
        StringBuilder cookieHeader = new StringBuilder();
        for (int i = 0, size = cookies.size(); i < size; i++) {
            if (i > 0) {
                cookieHeader.append("; ");
            }
            Cookie cookie = cookies.get(i);
            cookieHeader.append(cookie.name()).append('=').append(cookie.value());
        }
        return cookieHeader.toString();
    }

    public static List<Cookie> parseAll(HttpUrl url, Headers headers) {
        List<String> cookieStrings = headers.values("Set-Cookie");
        List<Cookie> cookies = null;

        for (int i = 0, size = cookieStrings.size(); i < size; i++) {
            Cookie cookie = Cookie.parse(url, cookieStrings.get(i));
            if (cookie == null) continue;
            if (cookies == null) cookies = new ArrayList<>();
            cookies.add(cookie);
        }

        return cookies != null
                ? Collections.unmodifiableList(cookies)
                : Collections.<Cookie>emptyList();
    }

    public static List<Cookie> parseAll(HttpUrl url, List<String> cookieStrings) {
        List<Cookie> cookies = null;

        for (int i = 0, size = cookieStrings.size(); i < size; i++) {
            Cookie cookie = Cookie.parse(url, cookieStrings.get(i));
            if (cookie == null) continue;
            if (cookies == null) cookies = new ArrayList<>();
            cookies.add(cookie);
        }

        return cookies != null
                ? Collections.unmodifiableList(cookies)
                : Collections.<Cookie>emptyList();
    }

    public void saveFromResponse(HttpUrl paramHttpUrl, List<Cookie> paramList) {
        if (this.webViewCookieManager != null) {
            String paramHttpUrl2 = paramHttpUrl.toString();
            Iterator<Cookie> paramList2 = paramList.iterator();
            while (paramList2.hasNext()) {
                Cookie localCookie = (Cookie)paramList2.next();
                this.webViewCookieManager.setCookie(paramHttpUrl2, localCookie.toString());
            }
        }
    }
}
