

package com.app.home.ui.vote.create.pay.setp1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.home.net.rpc.IWalletRpcNet;
import com.app.home.net.rpc.WalletRpcImpl;
import com.app.home.pojo.PayVoteStep1Info;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import io.reactivex.disposables.CompositeDisposable;


public class PayVoteStep1VM extends BaseViewModel {
    private final CompositeDisposable mDisposable;
    private final IWalletRpcNet mIWalletRpcNet;

    public PayVoteStep1VM(@NonNull Application application) {
        super(application);
        mIWalletRpcNet = new WalletRpcImpl();
        this.mDisposable = new CompositeDisposable();
    }


    @Override
    protected void onCleared() {
        if (null != this.mDisposable) {
            this.mDisposable.dispose();
        }
        super.onCleared();
    }

    private final MutableLiveData<PayVoteStep1Info> mMutableLiveData = new MutableLiveData<>();
    public LiveData<PayVoteStep1Info> mLiveData = mMutableLiveData;

    void getPayVoteStep1Info() {
        showLoadingDialog("");
        mDisposable.add(mIWalletRpcNet.getPayVoteStep1Info().subscribeWith(new NextSubscriber<PayVoteStep1Info>() {
            @Override
            public void dealData(PayVoteStep1Info value) {
                dismissLoadingDialog();
                mMutableLiveData.setValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                dismissLoadingDialog();
                super.dealError(e);
            }
        }));
    }

}
