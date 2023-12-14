package com.wallet.ctc.ui.blockchain.issuance.rec;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.blockchain.issuance.net.rpc.ISWalletRpcNet;
import com.wallet.ctc.ui.blockchain.issuance.net.rpc.SWalletRpcImpl;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceCoinPageInfo;

import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.RxSchedulers;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class IssuanceCoinRecVM extends BaseViewModel {
    public RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<EvmosPledgeResultBean> mTransfResultLD;


    private ISWalletRpcNet mISWalletRpcNet;

    public IssuanceCoinRecVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mTransfResultLD = new MutableLiveData<>();
        mISWalletRpcNet = new SWalletRpcImpl();
    }

    private MutableLiveData<IssuanceCoinPageInfo> mMutableLiveData = new MutableLiveData<>();
    MutableLiveData<IssuanceCoinPageInfo> mLiveData = mMutableLiveData;

    void getIssuanceCoinPageInfoMore(IssuanceCoinPageInfo info, String owner) {
        mDisposable.add(Observable.interval(0, 3, TimeUnit.SECONDS).take(3).compose(RxSchedulers.io_main()).subscribeWith(new NextSubscriber<Long>() {
            @Override
            public void dealData(Long value) {
                getIssuanceCoinPageInfo(info, owner);
            }
        }));

    }

    void getIssuanceCoinPageInfo(IssuanceCoinPageInfo info, String owner) {
        showLoadingDialog("");
        mDisposable.add(mISWalletRpcNet.getIssuanceCoinPageInfo(info, owner).subscribeWith(new NextSubscriber<IssuanceCoinPageInfo>() {
            @Override
            public void dealData(IssuanceCoinPageInfo value) {
                dismissLoadingDialog();
                mMutableLiveData.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                dismissLoadingDialog();
                super.dealError(e);
            }
        }));
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}
