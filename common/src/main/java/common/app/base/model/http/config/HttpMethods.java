

package common.app.base.model.http.config;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import common.app.AppApplication;
import common.app.BuildConfig;
import common.app.base.model.http.NetworkResponseInterceptor;
import common.app.base.model.http.bean.Request;
import common.app.base.model.http.cookiejar.ClearableCookieJar;
import common.app.base.model.http.cookiejar.PersistentCookieJar;
import common.app.base.model.http.cookiejar.cache.SetCookieCache;
import common.app.base.model.http.cookiejar.persistence.SharedPrefsCookiePersistor;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;
import common.app.utils.PhoneUtil;
import common.app.utils.SpUtil;
import common.app.utils.digest.EAICoderUtil;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;



public class HttpMethods {
    private final String TAG = "HttpMethods";


    public static boolean DEBUG = false;

    
    
    public static final String BASE_SITE = SpUtil.getHostApi();
    public static final String BASE_URL = BASE_SITE + "api/v1/";


    public static final String BASE_URL2 = BASE_SITE + "api/v1offline/";
    

    
    public static final String ARTICLE_BASE_URL = BuildConfig.HOST + "api/v1/";
    

    
    public static final String KEY_ARG_SIGN = "asdf87290oiapso";

    public static final String KEY_APPAUTH = "guid";
    public static final String KEY_TIMESTAP = "TIMESTAMP";
    public static final String KEY_SIGN = "sign";
    public static final String KEY_DEVICE = "device";
    public static final String DEVICE_VALUE = "app";
    public static final String KEY_DT = "dt";
    public static final String KEY_APPID = "appid";
    public static final String KEY_MAC = "mac";
    public static final String KEY_SYS = "sys";

    public static final String APPID_VALUE = SpUtil.getAppid();
    public static final String DT_VALUE = "android";
    public static String MAC_VALUE = "";

    public static final String KEY_LANGUAGE = "lang";

    public static final String TYPE_LANGUAGE_EN = "en";
    public static final String TYPE_LANGUAGE_CHINESE_TRANS = "zh-tw";
    public static final String TYPE_LANGUAGE_CHINESE_SIMPLE = "zh-cn";

    private static final int DEFAULT_TIMEOUT = 20;

    
    private Gson gson = new GsonBuilder()
            
            
            
            
            .disableHtmlEscaping() 
            .create();

    private static Retrofit mRetrofit;
    private static Retrofit mRetrofit2;
    private static Retrofit mBaseRetrofit;
    private static String sysStr;




    
    private static Retrofit mDownUploadRetrofit;


    public Gson getGson() {
        return gson;
    }

    public ClearableCookieJar getCookieJar() {
        return mCookieJar;
    }

    public void setCookieJar(ClearableCookieJar cookieJar) {
        mCookieJar = cookieJar;
    }

    ClearableCookieJar mCookieJar;

    
    private OkHttpClient.Builder createOkHttpBuilder() {

        
        mCookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(AppApplication.getInstance().getApplicationContext()));


        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.cookieJar(mCookieJar);
        
        return builder;
    }

    
    private Retrofit createRetrofit(String baseUrl, OkHttpClient.Builder builder) {
        
        Retrofit retrofit = null;
        try {
            retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(ResponseConvertFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(baseUrl)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retrofit;
    }

    
    public Retrofit createRetrofit(String baseUrl) {
        OkHttpClient.Builder builder = createOkHttpBuilder();
        builder.addInterceptor(new NetworkResponseInterceptor());
        return createRetrofit(baseUrl, builder);
    }

    
    public Retrofit createDownUploadRetrofit(String baseUrl) {
        OkHttpClient.Builder builder = createOkHttpBuilder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                String agent = "Android";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    agent = WebSettings.getDefaultUserAgent(AppApplication.getInstance().getApplicationContext());
                }
                String guid = "";

                okhttp3.Request request = chain.request()
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", agent)
                        .addHeader("guid", guid)
                        .build();
                return chain.proceed(request);
            }
        });

        
        return createRetrofit(baseUrl, builder);
    }


    
    private HttpMethods() {
        MAC_VALUE = PhoneUtil.getMac(AppApplication.getContext());
        sysStr = "" + PhoneUtil.getphoneManufactor() + ",: " + PhoneUtil.getphoneModel() + ",android" + PhoneUtil.getSystemVersion() + ",app: "  + ",ip: " + PhoneUtil.getIPAddress(AppApplication.getContext()) + ",mc: " + MAC_VALUE;
        mBaseRetrofit = createRetrofit(BASE_SITE);

        mRetrofit = createRetrofit(BASE_URL);
        mRetrofit2 = createRetrofit(BASE_URL2);
        mDownUploadRetrofit = createDownUploadRetrofit(BASE_URL);

    }

    
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    public static synchronized HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    
    public Retrofit getRetrofit() {
        return mRetrofit;
    }



    public Retrofit getRetrofit2() {
        return mRetrofit2;
    }


    
    public Retrofit getDownUpRetrofit() {
        return mDownUploadRetrofit;
    }


    
    public Map<String, String> convertToMap(String[] keys, String[] values) {
        
        TreeMap<String, String> map = new TreeMap();
        if (keys != null && values != null && keys.length == values.length && keys.length > 0) {
            int count = keys.length;
            for (int i = 0; i < count; i++) {
                if (null != keys[i] && null != values[i]) {
                    map.put(keys[i], values[i]);
                }
            }
        }
        return map;
    }

    
    public Map<String, Object> convertToMap(Object param) {
        
        JsonObject paramJson = gson.toJsonTree(param).getAsJsonObject();
        TreeMap<String, Object> map = new TreeMap<>();
        if (null != paramJson) {
            for (Map.Entry<String, JsonElement> entry : paramJson.entrySet()) {
                String value = "";
                if (entry.getValue().isJsonObject() || entry.getValue().isJsonArray()) {
                    value = gson.toJson(entry.getValue());
                } else {
                    value = entry.getValue().getAsString();
                }
                map.put(entry.getKey(), value);
            }
        }
        return map;
    }


    
    public Request getRequest(Object param) {
        return getRequest(convertToMap(param));
    }

    
    public Request getRequest(String[] keys, String[] values) {
        return getRequest(convertToMap(keys, values));
    }

    
    public Request getJsRequest(String[] keys, String[] values) {
        return getRequest(convertToMap(keys, values));
    }

    
    public Request getRequest(Map paraMap) {
        
        TreeMap<String, Object> map = getParamsMap(paraMap);
        return createSignRequest(map,"");
    }



    
    private TreeMap<String, Object> getParamsMap(Map paraMap) {
        
        TreeMap<String, Object> map;
        if (null != paraMap) {
            map = new TreeMap(paraMap);
        } else {
            map = new TreeMap<>();
        }

        long time = System.currentTimeMillis() / 1000;
        map.put(KEY_TIMESTAP, String.valueOf(time));

        
        if (!map.containsKey(KEY_APPAUTH)) {
            map.put(KEY_APPAUTH, getGuid());
        }
        String language = getNowSettingLanguage();
        if (!TextUtils.isEmpty(language) && !map.containsKey(KEY_LANGUAGE)) {
            map.put(KEY_LANGUAGE, language);
        }
        map.put(KEY_DEVICE, DEVICE_VALUE);
        map.put(KEY_DT, DT_VALUE);
        map.put(KEY_APPID, APPID_VALUE);
        map.put(KEY_MAC, MAC_VALUE);
        map.put(KEY_SYS, sysStr);
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                map.put(entry.getKey(), entry.getValue());
            } else {
                map.put(entry.getKey(), gson.toJson(entry.getValue()).toString());
            }
        }
        return map;
    }

    
    public static String getGuid() {
        String guid = "";
        if (TextUtils.isEmpty(guid)) {
            guid = "";
        }
        return guid;
    }


    
    private Request createSignRequest(TreeMap<String, Object> paramsMap, String argSign) {
        String arg = gson.toJson(paramsMap);
        String sign = EAICoderUtil.getMD5Code(arg + KEY_ARG_SIGN);
        paramsMap.put(KEY_SIGN, sign);
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "arg=" + arg + ", sign=" + sign);
        }
        Request request = new Request(arg, sign);
        request.map = paramsMap;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arg);
        request.requestBody = requestBody;
        return request;
    }

    public RequestBody getJsonRequestBody(Map<String,Object> paramsMap) {
        String arg = "{}";
        if (null != paramsMap && !paramsMap.isEmpty()) {
            arg = gson.toJson(paramsMap);
        }
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arg);
    }

    public RequestBody getJsonRequestBody(List<String> list) {
        String arg = "[]";
        if (null != list && !list.isEmpty()) {
            arg = gson.toJson(list);
        }
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arg);
    }


    
    private Request createArticleSignRequest(TreeMap<String, Object> paramsMap) {
        String arg = gson.toJson(paramsMap);
        String sign = EAICoderUtil.getMD5Code(arg + KEY_ARG_SIGN);
        paramsMap.put(KEY_SIGN, sign);
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG, "arg=" + arg + ", sign=" + sign);
        }
        Request request = new Request(arg, sign);
        request.map = paramsMap;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), arg);
        request.requestBody = requestBody;
        return request;
    }


    
    public static String getNowSettingLanguage() {
        
        Locale locale = LanguageUtil.getNowLocal(AppApplication.getInstance().getApplicationContext());

        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ((language.equals(Locale.SIMPLIFIED_CHINESE.getLanguage()) || language.equals(Locale.CHINESE.getLanguage())) && "cn".equals(country)) {
            return TYPE_LANGUAGE_CHINESE_SIMPLE;
        } else if (language.equals(Locale.TRADITIONAL_CHINESE.getLanguage()) && "tw".equals(country)) {
            return TYPE_LANGUAGE_CHINESE_TRANS;
        } else {
            return TYPE_LANGUAGE_EN;
        }
    }


    
    
    private static String[] VERIFY_HOST_NAME_ARRAY = new String[]{};
    
    HostnameVerifier mHostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession sslSession) {
            if (TextUtils.isEmpty(hostname)) {
                return false;
            }
            return !Arrays.asList(VERIFY_HOST_NAME_ARRAY).contains(hostname);
        }
    };

    
    public static KeyManager[] getKeyManagers(String keyStr, String keyFilePath) {
        InputStream is = null;
        try {
            if (!TextUtils.isEmpty(keyStr)) {
                is = new okio.Buffer().writeUtf8(keyStr).inputStream();
            } else if (!TextUtils.isEmpty(keyFilePath)) {
                is = new FileInputStream(keyFilePath);
            }
            if (is == null) {
                return null;
            }
            KeyStore clientKeyStore = KeyStore.getInstance("PKCS12");
            clientKeyStore.load(null);
            clientKeyStore.load(is, "".toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientKeyStore, null);
            return keyManagerFactory.getKeyManagers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }


    
    public static TrustManager[] getTrustMangers(String keyStr, String keyFilePath) {
        InputStream is = null;

        
        TrustManager[] defualt = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };
        try {
            if (!TextUtils.isEmpty(keyStr)) {
                is = new okio.Buffer().writeUtf8(keyStr).inputStream();
            } else if (!TextUtils.isEmpty(keyFilePath)) {
                is = new FileInputStream(keyFilePath);
            }
            if (is == null) {
                return defualt;
            }

            KeyStore serverKeyStore = KeyStore.getInstance("PKCS12");
            serverKeyStore.load(null);
            serverKeyStore.load(is, "".toCharArray());
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(serverKeyStore);
            return trustManagerFactory.getTrustManagers();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defualt;
    }

    
    public static SSLSocketFactory getSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = getKeyManagers(null, null);
            TrustManager[] trustManagers = getTrustMangers(null, null);
            Log.d("TLSCert", "" + keyManagers + "," + trustManagers);
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }


    
    public static class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final Type type;

        GsonResponseBodyConverter(Gson gson, Type type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            String response = value.string();
            try {
                T data=gson.fromJson(response, type);
                return data;
            }catch (Exception e){
                e.printStackTrace();
                throw new JsonParseException(""+response);
            }
        }
    }


    public static class ResponseConvertFactory extends Converter.Factory {

        
        public static ResponseConvertFactory create() {
            return create(new Gson());
        }

        
        public static ResponseConvertFactory create(Gson gson) {
            return new ResponseConvertFactory(gson);
        }

        private final Gson gson;

        private ResponseConvertFactory(Gson gson) {
            if (gson == null) {
                throw new NullPointerException("gson == null");
            }
            this.gson = gson;
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
            return new GsonResponseBodyConverter<>(gson, type);
        }
    }
    

}
