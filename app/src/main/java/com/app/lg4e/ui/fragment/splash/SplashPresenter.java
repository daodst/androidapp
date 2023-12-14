

package com.app.lg4e.ui.fragment.splash;

import static common.app.utils.FileUtils.SDPATH;

import android.content.Context;
import android.text.TextUtils;

import com.app.view.ChooseUserDialog;
import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseEntity;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.NetUtils;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

;



public class SplashPresenter implements SplashContract.Presenter {


    private static final String TAG = "SplashPresenter";
    private SplashContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private Context mContext;
    private ChooseUserDialog mDialog;

    public SplashPresenter(SplashContract.View view, Context context) {
        this.mView = view;
        this.mView.setPresenter(this);
        mCompositeDisposable = new CompositeDisposable();
        mContext = context;
    }


    @Override
    public void subscribe() {
        
        if (null == mView) {
            return;
        }
        if (!NetUtils.isNetworkConnected(mView.getContext())) {
            toLogIn(false);
            return;
        }
        String data = SpUtil.getJData();
        if (TextUtils.isEmpty(data) || data.length() < 5 || BuildConfig.ENABLE_ONE_CLICK_LOGIN == 0) {
            toLogin();
        } else {
        }

    }

    private void clearAccessToken() {

    }

    @Override
    public void pushLogFile() {
        try {
            File file = new File(SDPATH + "/xsy/log.xml");
        } catch (Exception e) {

        }
    }


    
    private void toLogIn(boolean aBoolean) {
        Observable
                .just(aBoolean)
                .doOnNext(s -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).
                compose(RxSchedulers.io_main())
                .doOnNext(flag -> {
                    
                    if (BuildConfig.ALLOW_VISITOR_MODEL) {

                        if (null != mView) {
                            mView.toMainTab();
                        }

                    } else {
                        if (aBoolean) {
                            if (null != mView) {
                                mView.toMainTab();
                            }

                        } else {
                            if (null != mView) {
                                mView.toLogInFragment();
                            }
                        }
                    }

                }).subscribe();

    }

    @Override
    public void unsubscribe() {
        if (null != mCompositeDisposable) {
            mCompositeDisposable.dispose();
        }
        mView = null;
    }

    private void toLogin() {

    }

    private MeApi mApi;
    private Gson gson = new Gson();

    public void login(String data) {
        if (null == mApi) {
            mApi = new MeApi();
        }
        Map<String, Object> params = new TreeMap();
        params.put("auth", data);
        mApi.checkUserAutk(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {

                        } else {
                            ToastUtil.showToast(baseEntity.getInfo());
                            toLogin();
                        }
                    }

                    private void success(Boolean aBoolean) {
                    }

                    private void failure(Throwable throwable) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        toLogin();

                    }
                });
    }

}
