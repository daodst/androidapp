

package com.wallet.ctc.ui.dapp.util;

import java.io.InputStream;


public class JsInjectorResponse {
    final String charset;
    final InputStream data;
    final boolean isRedirect;
    final String mime;
    final String url;

    JsInjectorResponse(InputStream data, int responseCode, String url, String mime, String charset, boolean isRedirect)
    {
        this.data = data;
        this.url = url;
        this.mime = mime;
        this.charset = charset;
        this.isRedirect = isRedirect;
    }
}
