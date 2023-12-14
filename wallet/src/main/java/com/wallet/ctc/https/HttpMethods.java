

package com.wallet.ctc.https;

import com.wallet.ctc.Constants;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.util.SettingPrefUtil;

import java.util.concurrent.TimeUnit;

import common.app.AppApplication;
import common.app.base.model.http.NetworkResponseInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;



public class HttpMethods {
    private static final int DEFAULT_TIMEOUT = 60;
    private static OkHttpClient.Builder builder;
    private static Retrofit mGoRetrofit;
    public static Retrofit mMccRetrofit = null;
    private static Retrofit mDmRetrofit = null;
    private static Retrofit mOtherRetrofit = null;
    private static Retrofit mPhpRetrofit = null;
    private static Retrofit mTrxRetrofit = null;
    private static Retrofit mFilRetrofit = null;
    private static Retrofit mTrustWalletRetrofit = null;
    private static Retrofit mMarketPriceRetrofit = null;
    private static Retrofit mDogeRetrofit = null;
    private static Retrofit mDotRetrofit = null;
    private static Retrofit mLtcRetrofit = null;
    private static Retrofit mBchRetrofit = null;
    private static Retrofit mZecRetrofit = null;
    private static Retrofit mEtcRetrofit = null;
    private static Retrofit mEtfRetrofit = null;
    private static Retrofit mDmfRetrofit = null;
    private static Retrofit mRetrofitError = null;
    private static Retrofit mSgbRetrofit = null;
    private static Retrofit mSolRetrofit = null;
    private static Retrofit mMaticRetrofit = null;


    
    public static final String ERROR_REPORT_APP_NAME = "dm";
    
    public static final String ERROR_REPORT_SIGN = "dmklfds22s8d5dew";

    
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    public static synchronized HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    
    private HttpMethods() {
        builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new NetworkResponseInterceptor());
        builder.retryOnConnectionFailure(true);
    }

    
    public Retrofit getRetrofit() {
        mGoRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constants.API_URL)
                .build();
        return mGoRetrofit;
    }

    public static Retrofit getMccRetrofit() {
        String url = SettingPrefUtil.getMCCHostApi(AppApplication.getContext());
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        if (mMccRetrofit == null) {
            mMccRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mMccRetrofit;
    }

    public static Retrofit getDmRetrofit() {
        String url2 = SettingPrefUtil.getDMHostApi(AppApplication.getContext());
        if (!url2.endsWith("/")) {
            url2 = url2 + "/";
        }
        if (mDmRetrofit == null) {
            mDmRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url2)
                    .build();
        }
        return mDmRetrofit;
    }

    public static Retrofit getOtherRetrofit() {
        String url3 = SettingPrefUtil.getOtherHostApi(AppApplication.getContext());
        if (!url3.endsWith("/")) {
            url3 = url3 + "/";
        }
        if (mOtherRetrofit == null) {
            mOtherRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url3)
                    .build();
        }
        return mOtherRetrofit;
    }

    public static Retrofit getPhpRetrofit(String Url) {
        if (mPhpRetrofit == null) {
            mPhpRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Url)
                    .build();
        }
        return mPhpRetrofit;
    }

    public static Retrofit getTrxRetrofit() {
        String url = SettingPrefUtil.getTrxHostApi(AppApplication.getContext());
        if (!url.endsWith("/")) {
            url = url + "/";
        }

        if (mTrxRetrofit == null) {
            mTrxRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mTrxRetrofit;
    }

    public static Retrofit getFilRetrofit() {
        String url = SettingPrefUtil.getFilHostApi(AppApplication.getContext());
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        if (mFilRetrofit == null) {
            mFilRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mFilRetrofit;
    }

    public static Retrofit getTrustWalletRetrofit(String Url) {
        if (mTrustWalletRetrofit == null) {
            mTrustWalletRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Url)
                    .build();
        }
        return mTrustWalletRetrofit;
    }

    public static Retrofit getmMarketPriceRetrofit() {
        String url= SettingPrefUtil.getMarketPriceHost(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }

        if (mMarketPriceRetrofit == null) {
            mMarketPriceRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mMarketPriceRetrofit;
    }

    public static Retrofit getDogeRetrofit() {
        String url= SettingPrefUtil.getDogeHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mDogeRetrofit == null) {
            mDogeRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mDogeRetrofit;
    }

    public static Retrofit getDotRetrofit() {
        String url= SettingPrefUtil.getDotHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mDotRetrofit == null) {
            mDotRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mDotRetrofit;
    }

    public static Retrofit getmLtcRetrofit() {
        String url= SettingPrefUtil.getLtcHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mLtcRetrofit == null) {
            mLtcRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mLtcRetrofit;
    }

    public static Retrofit getBchRetrofit() {
        String url= SettingPrefUtil.getBchHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mBchRetrofit == null) {
            mBchRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mBchRetrofit;
    }

    public static Retrofit getZecRetrofit() {
        String url= SettingPrefUtil.getZecHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mZecRetrofit == null) {
            mZecRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mZecRetrofit;
    }

    public static Retrofit getmEtcRetrofit() {
        String url= SettingPrefUtil.getEtcHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mEtcRetrofit == null) {
            mEtcRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mEtcRetrofit;
    }
    public static Retrofit getmSolRetrofit() {
        String url= SettingPrefUtil.getSolHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        if (mSolRetrofit == null) {
            mSolRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mSolRetrofit;
    }
    public static Retrofit getmSgbRetrofit() {
        String url= SettingPrefUtil.getSgbHostApi(AppApplication.getContext());
        if(!url.endsWith("/")){
            url=url+"/";
        }
        LogUtil.d("SGB "+url);
        if (mSgbRetrofit == null) {
            mSgbRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(url)
                    .build();
        }
        return mSgbRetrofit;
    }
    public static Retrofit getEtfRetrofit(String Url) {
        if (mEtfRetrofit == null) {
            mEtfRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Url)
                    .build();
        }
        return mEtfRetrofit;
    }

    public static Retrofit getDmfRetrofit(String Url) {
        if (mDmfRetrofit == null) {
            mDmfRetrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Url)
                    .build();
        }
        return mDmfRetrofit;
    }


    public static Retrofit getRetrofitError(String Url) {
        if (mRetrofitError == null) {
            mRetrofitError = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Url)
                    .build();
        }
        return mRetrofitError;
    }
}
