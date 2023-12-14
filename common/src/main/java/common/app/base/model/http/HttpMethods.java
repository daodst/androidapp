

package common.app.base.model.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import common.app.AppApplication;
import common.app.BuildConfig;
import common.app.base.model.http.bean.Request;
import common.app.base.pojo.BaseBean;
import common.app.im.model.base.BaseRequst;
import common.app.utils.LanguageUtil;
import common.app.utils.LogUtil;
import common.app.utils.PhoneUtil;
import common.app.utils.SpUtil;
import common.app.utils.digest.EAICoderUtil;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;



public class HttpMethods {
    private final String TAG = "HttpMethods";
    public static final String BASE_SITE = SpUtil.getHostApi();
    public static final String BASE_URL = BASE_SITE + "api/v1/";
    public static final String BASE_URL2 = BASE_SITE + "api/v1offline/";
    private Gson gson = new GsonBuilder()
            
            
            
            
            .disableHtmlEscaping() 
            .create();

    public static final String KEY_ARG_SIGN = "asdf87290oiapso";

    public static final String KEY_APPAUTH = "guid";
    public static final String KEY_TIMESTAP = "TIMESTAMP";
    public static final String KEY_SIGN = "sign";
    public static final String KEY_DEVICE = "device";
    public static final String DEVICE_VALUE = "app";
    public static final String KEY_SYS = "sys";
    public static final String KEY_LANGUAGE = "lang";
    public static String appId=SpUtil.getAppid();
    private static final int DEFAULT_TIMEOUT = 60;
    private static Retrofit mRetrofit;
    private static String sysStr="";

    
    private HttpMethods() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        
        builder.addInterceptor(new NetworkResponseInterceptor());


        mRetrofit = createRetrofit(BASE_URL, builder);


    }
    public Gson getGson() {
        return gson;
    }
    
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    public static synchronized HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    public static String getGuid() {
        String guid = "";
        if (TextUtils.isEmpty(guid)) {
            guid = "";
        }
        return guid;
    }

    
    public  Retrofit getRetrofit() {
        return mRetrofit;
    }

    public  Retrofit getIMRetrofit() {
        return mRetrofit;
    }

    public  Retrofit getRetrofit2() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new NetworkResponseInterceptor());

        Retrofit mRetrofit2 = createRetrofit(BASE_URL2, builder);
        return mRetrofit2;
    }

    public Retrofit getRetrofitAllHost(String host){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new NetworkResponseInterceptor());
        return createRetrofit(host, builder);
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



    
    public Request getRequest(BaseBean param) {
        
        param.setTIMESTAMP(String.valueOf(System.currentTimeMillis() / 1000));
        param.setDevice(DEVICE_VALUE);
        
        String arg = gson.toJson(param);
        
        TreeMap<String,Object> map = gson.fromJson(arg,TreeMap.class);
        return getRequest(map);
    }

    
    public Request getRequest(BaseRequst param) {
        
        param.setTIMESTAMP(String.valueOf(System.currentTimeMillis() / 1000));
        param.setDevice(DEVICE_VALUE);
        String arg = gson.toJson(param);
        
        TreeMap<String,Object> map = gson.fromJson(arg,TreeMap.class);
        return getRequest(map);
    }

    
    public Request getRequest(String[] keys, String[] values) {
        
        TreeMap<String,Object> map = new TreeMap();
        if (keys != null && values != null && keys.length == values.length && keys.length > 0) {
            int count = keys.length;
            for (int i = 0; i<count; i++) {
                if (null != keys[i] && null != values[i]) {
                    map.put(keys[i],values[i]);
                }
            }
        }
        return getRequest(map);
    }

    
    public Request getRequest(Map paraMap) {

        
        TreeMap<String,Object> map;
        if (null != paraMap) {
            map = new TreeMap(paraMap);
        } else {
            map = new TreeMap<>();
        }

        long time = System.currentTimeMillis() / 1000;
        map.put(KEY_TIMESTAP,String.valueOf(time));
        map.put("appid",appId);
        map.put("dt","android");
        map.put("ver", PhoneUtil.getVersionName(AppApplication.getContext()));
        map.put("mac", PhoneUtil.getMac(AppApplication.getContext()));
        map.put(KEY_SYS,sysStr);
        if (!map.containsKey(KEY_APPAUTH)) {
        }
        String language = LanguageUtil.getNowLocalStr(AppApplication.getInstance().getApplicationContext());;
        if (!TextUtils.isEmpty(language) && !map.containsKey(KEY_LANGUAGE)) {
            map.put(KEY_LANGUAGE, language);
        }
        map.put(KEY_DEVICE,DEVICE_VALUE);

        
        for(Map.Entry<String,Object> entry: map.entrySet()){
            if (entry.getValue() instanceof String) {
                map.put(entry.getKey(),entry.getValue());
            } else {
                map.put(entry.getKey(),gson.toJson(entry.getValue()).toString());
            }
        }

        String arg = gson.toJson(map).toString();
        String sign = EAICoderUtil.getMD5Code(arg + KEY_ARG_SIGN);
        if(null==map.get(KEY_SIGN)) {
            map.put(KEY_SIGN, sign);
        }
        if (BuildConfig.DEBUG) {
            LogUtil.d(TAG,"arg="+arg+", sign="+sign);
        }
        Request request = new Request(arg,sign);
        request.map = map;
        return request;
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

        
        public static HttpMethods.ResponseConvertFactory create() {
            return create(new Gson());
        }

        
        public static HttpMethods.ResponseConvertFactory create(Gson gson) {
            return new HttpMethods.ResponseConvertFactory(gson);
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
            return new HttpMethods.GsonResponseBodyConverter<>(gson, type);
        }
    }
    
}
