package com.wallet.ctc.ui.me.chain_bridge2.timeline;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderDetailBean;

import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChainBridgeTimeLineVM extends BaseViewModel {

    public MutableLiveData<ChainBridgeOrderDetailBean> mOrdersLD;
    CompositeDisposable mDisposable;

    
    public ChainBridgeTimeLineVM(@NonNull Application application) {
        super(application);
        mOrdersLD = new MutableLiveData<>();
        mDisposable = new CompositeDisposable();
    }


    
    public void getData(long mainOrderId){
        Disposable disposable = Observable.create(new ObservableOnSubscribe<ChainBridgeOrderDetailBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<ChainBridgeOrderDetailBean> emitter) throws Exception {
                        ChainBridgeOrderDetailBean orderDetail =  ChatSdk.orderDetail(mainOrderId);
                        emitter.onNext(orderDetail);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    dismissLoadingDialog();
                    if (data != null && data.isSuccess()){
                        mOrdersLD.setValue(data);
                        if(!data.isMainOrderExchangeIng()){
                            
                            stopTimer();
                        }
                    } else {
                        String errorInfo = (data != null && !TextUtils.isEmpty(data.getInfo())) ? data.getInfo() : getApplication().getString(R.string.get_data_fail);
                        showToast(errorInfo);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    dismissLoadingDialog();
                    showToast(throwable+":"+throwable.getMessage());
                });
        mDisposable.add(disposable);
    }



    private Disposable mTimeDispose;
    private long mMainOrderId;
    public void startTimer(long mainOrderId) {
        this.mMainOrderId = mainOrderId;
        stopTimer();
        mTimeDispose = Observable.interval(0, 5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tikc->{
                    Log.i("chainBrisu", "tike-"+tikc);
                    getData(mMainOrderId);
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    private void stopTimer() {
        if (null != mTimeDispose) {
            mTimeDispose.dispose();
            mTimeDispose = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTimer();
        if (mDisposable != null){
            mDisposable.clear();
        }
    }
}
