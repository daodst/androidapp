

package com.wallet.ctc.nft.http.helper;

public class SkyHelper {
    private static HttpHelper http = null;

    public static HttpHelper http() {
        synchronized (HttpHelper.class) {
            if (null == http) {
                http = new HttpHelper();
            }
        }
        return http;
    }
}
