package com.wallet.ctc.ui.me.chain_bridge2.orders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.model.blockchain.ChainBridgeMainOrdersBean;
import com.wallet.ctc.model.blockchain.ChainBridgeOrderBean;
import com.wallet.ctc.model.blockchain.ChainBridgeServiceStatusBean;

import java.util.List;

import common.app.base.BaseViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChainBridgeOrdersVM extends BaseViewModel {

    MutableLiveData<List<ChainBridgeOrderBean>> mOrdersLD;
    MutableLiveData<Boolean> mRefreshStatusLD;
    CompositeDisposable mDisposable;


    
    public ChainBridgeOrdersVM(@NonNull Application application) {
        super(application);
        mOrdersLD = new MutableLiveData<>();
        mRefreshStatusLD = new MutableLiveData<>();
        mDisposable = new CompositeDisposable();
    }


    public void getOrders(int page, int type, String filterAddr, int filterWType){
        showLoadingDialog("");
       Disposable disposable = Observable.create(new ObservableOnSubscribe<ChainBridgeMainOrdersBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChainBridgeMainOrdersBean> emitter) throws Exception {
                
                ChainBridgeServiceStatusBean serviceStatus  = ChatSdk.serviceStatus();
                if (null != serviceStatus && serviceStatus.isRunning()){
                    ChatSdk.setChainBridgeClientAddr(getApplication(), false);
                }
                ChainBridgeMainOrdersBean orders = ChatSdk.mainOrderList(filterAddr, type, page, 10);
                emitter.onNext(orders);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(orders->{
            dismissLoadingDialog();
            if(orders != null && orders.isSuccess()){
                mRefreshStatusLD.setValue(true);
                mOrdersLD.setValue(orders.getMainOrderList());
            } else {
                String errorInfo = orders != null ? orders.getInfo() : getApplication().getString(R.string.get_data_fail);
                showToast(errorInfo);
                mRefreshStatusLD.setValue(false);
            }
        }, throwable -> {
            dismissLoadingDialog();
            throwable.printStackTrace();
            showToast(throwable+":"+throwable.getMessage());
            mRefreshStatusLD.setValue(false);
        });
       mDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != mDisposable){
            mDisposable.clear();
        }
    }
}
