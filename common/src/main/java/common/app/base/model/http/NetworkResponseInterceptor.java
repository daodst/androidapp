

package common.app.base.model.http;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import common.app.AppApplication;
import common.app.R;
import common.app.RxBus;
import common.app.base.model.OtherDeviceLoginCheck;
import common.app.base.model.http.config.HttpMethods;
import common.app.base.model.http.exception.ExceptionHandle;
import common.app.im.event.AccountError;
import common.app.im.event.Notice;
import common.app.utils.LogUtil;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;



public class NetworkResponseInterceptor implements Interceptor {

    private final String TAG = "NetworkResponse";

    private static final String REDPACKET_LOST = "confirm_redpacket";
    private static final String WEB_STATUS = "web_close";
    private static final String WEB_STATUS_TEXT = "web_close_text";

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final String[] NO_LOG_URLS = {"cosmos/base/tendermint/v1beta1/blocks/latest"};

    public boolean noLogUrl(String url) {
        if (NO_LOG_URLS.length == 0 || TextUtils.isEmpty(url)) {
            return false;
        }
        for(String noLogUrl : NO_LOG_URLS) {
            if (url.contains(noLogUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        
        String agent = getUserAgent();
        String guid = common.app.base.model.http.config.HttpMethods.getGuid();
        Request.Builder requestBuilder = request.newBuilder()
                .header(common.app.base.model.http.config.HttpMethods.KEY_APPAUTH, guid);
        request = requestBuilder.removeHeader("User-Agent")
                .addHeader("User-Agent", agent).build();

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ';
        if (hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }

        
        StringBuffer requestHeaders = new StringBuffer();
        if (hasRequestBody) {
            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                requestHeaders.append(name + ": " + headers.value(i) + "\n");
            }
        }

        
        String arg = "";
        if (null != requestBody) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer)) {
                arg = buffer.readString(charset);
            }
        }

        
        String loginfo = "--> --------Request Info----------" +
                "\nrequestUrl--->" + requestStartMessage +
                "\nrequestHead--->" + requestHeaders +
                "\nrequestArg--->" + arg;
        if (!noLogUrl(requestStartMessage)) {
            LogUtil.d(TAG,loginfo);
        }

        
        long startNs = System.nanoTime();
        Response response;

        
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            try {
                throw ExceptionHandle.handleException(new ConnectException());
            } catch (Exception exceptionManager) {
                throw e;
            }
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        
        String url = response.request().url() + "";

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        

        doCheckHead(response);

        Headers services_headers = response.headers();
        StringBuffer servicesHeases = new StringBuffer();
        for (int i = 0, count = services_headers.size(); i < count; i++) {
            String name = services_headers.name(i);
            servicesHeases.append(name + ": " + services_headers.value(i) + "\n");

        }

        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); 
        Buffer buffer = source.buffer();

        Charset charset = UTF8;
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8);
            } catch (UnsupportedCharsetException e) {
                return response;
            }
        }

        if (!isPlaintext(buffer)) {
            return response;
        }
        String body = "";
        if (contentLength != 0) {
            body = buffer.clone().readString(charset);
            if (!TextUtils.isEmpty(body)) {
                try {
                    JSONObject object = new JSONObject(body);
                    String info = null;
                    if (null != object && object.has("info")) {
                        info = object.getString("info");
                    }
                    if (!TextUtils.isEmpty(info) && OtherDeviceLoginCheck.check(info)) {
                        response.body().toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String info = "url" + url + "\n info！";
                    Log.e(TAG, "" + info);
                }
            }
        }
        
        String logInfo = "<-- --------Response Info----------" + "\n" + "responseHead=" + servicesHeases + "\n" + "url=" + url + "\narg=" + arg + "\ntookMs=" + tookMs + "ms\nrespones=" + body;
        if (!noLogUrl(url)) {
            LogUtil.d(TAG,logInfo);
        }
        return response;
    }

    
    private void doCheckHead(Response response) {
        if (null != response) {
            String redPacketData = response.header(REDPACKET_LOST, "0");
            if ("1".equals(redPacketData)) {
                Log.w(TAG, "has lost redpacket");
                RxBus.getInstance().post(new Notice(Notice.REDPACKET_LOST));
            }

            String host = response.request().url().toString();
            String webCloseStatus = response.header(WEB_STATUS);
            String webCloseText = response.header(WEB_STATUS_TEXT);
            if (!TextUtils.isEmpty(host) && host.startsWith(HttpMethods.BASE_SITE) &&
                    !TextUtils.isEmpty(webCloseStatus) &&
                    "1".equals(webCloseStatus)) {
                
                Log.w(TAG, "has closed web");
                if (TextUtils.isEmpty(webCloseText)) {
                    webCloseText = AppApplication.getInstance().getApplicationContext().getString(R.string.exit);
                }else {
                    if(!webCloseText.startsWith("http")){
                        try {
                            webCloseText= URLDecoder.decode(webCloseText,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    };
                }

                RxBus.getInstance().post(new AccountError(AppApplication.getInstance().getApplicationContext().getString(R.string.prompt),
                        webCloseText, AccountError.ERROR_CODE_HOST_CLOSED));
            }
        }
    }


    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; 
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding);
    }

    
    private static String getUserAgent() {
        String userAgent = "Android";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(AppApplication.getInstance().getApplicationContext());
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
