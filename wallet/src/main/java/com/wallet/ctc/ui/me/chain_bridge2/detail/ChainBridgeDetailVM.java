package com.wallet.ctc.ui.me.chain_bridge2.detail;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderDetailBean;

import java.util.List;

import common.app.base.BaseViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChainBridgeDetailVM extends BaseViewModel {
    public MutableLiveData<List<ChainBridgeOrderBean>> mOrdersLD;
    CompositeDisposable mDisposable;

    
    public ChainBridgeDetailVM(@NonNull Application application) {
        super(application);
        mOrdersLD = new MutableLiveData<>();
        mDisposable = new CompositeDisposable();
    }

    public void getData(long mainOrderId, boolean showLoading) {
        if(showLoading){
            showLoadingDialog("");
        }
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
                        mOrdersLD.setValue(data.getOrders());
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
}
