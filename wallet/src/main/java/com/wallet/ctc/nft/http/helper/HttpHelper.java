

package com.wallet.ctc.nft.http.helper;


import com.wallet.ctc.nft.http.annotation.Host;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import common.app.BuildConfig;
import common.app.base.model.http.NetworkResponseInterceptor;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpHelper {
    public static final String HOST_DEFAULT = BuildConfig.HOST + "api/v1/";
    public static final int DEFAULT_TIMEOUT = 20;
    Map<Class, Object> apiCache = null;
    Map<String, Retrofit> retrofitCache = null;

    public HttpHelper() {
        apiCache = new HashMap<>();
        retrofitCache = new HashMap<>();
    }

    
    public OkHttpClient.Builder createOkHttpBuilder() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(false);
        return builder;
    }

    public synchronized <T> T api(final Class<T> service) {
        T api = (T) apiCache.get(service);
        if (null != api) {
            return api;
        }

        api = getRetrofit(service).create(service);
        apiCache.put(service, api);
        return api;
    }

    public synchronized <T> T api(String url, final Class<T> service) {

        return getRetrofit(url).create(service);
    }

    private Retrofit getRetrofit(final Class api) {
        Host anno = (Host) api.getAnnotation(Host.class);
        String host = HOST_DEFAULT;
        if (null != anno) {
            host = anno.value();
        }

        Retrofit retrofit = retrofitCache.get(host);
        if (null == retrofit) {
            retrofit = createRetrofit(host);
            retrofitCache.put(host, retrofit);
        }

        return retrofit;
    }

    private Retrofit getRetrofit(String url) {
        Retrofit retrofit = createRetrofit(url);
        return retrofit;
    }

    private Retrofit createRetrofit(String baseUrl) {
        OkHttpClient.Builder builder = createOkHttpBuilder();
        builder.addInterceptor(new NetworkResponseInterceptor());
        Retrofit mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return mRetrofit;
    }


    
    public <T> Observable<T> dohttp(Observable<T> observable, final DisposableObserver<T> response) {
        Observable<T> ob = observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        ob.subscribe(response);
        return ob;
    }


}
