

package com.app.home.ui.ver.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.home.net.http.HomeNetImpl;
import com.app.home.net.http.IHomeNet;
import com.app.home.pojo.ValidatorListInfo;

import java.util.List;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import io.reactivex.disposables.CompositeDisposable;


public class VWalletVeryListVM extends BaseViewModel {
    private final CompositeDisposable mDisposable;
    private final IHomeNet mIHomeNet;

    public VWalletVeryListVM(@NonNull Application application) {
        super(application);
        mIHomeNet = new HomeNetImpl();
        this.mDisposable = new CompositeDisposable();
    }


    @Override
    protected void onCleared() {
        if (null != this.mDisposable) {
            this.mDisposable.dispose();
        }
        super.onCleared();
    }

    private final MutableLiveData<List<ValidatorListInfo.Result>> mMutableLiveData = new MutableLiveData<>();
    public LiveData<List<ValidatorListInfo.Result>> mLiveData = mMutableLiveData;

    void getValidatorList() {
        mDisposable.add(mIHomeNet.getValidatorList().subscribeWith(new NextSubscriber<List<ValidatorListInfo.Result>>() {
            @Override
            public void dealData(List<ValidatorListInfo.Result> value) {
                mMutableLiveData.setValue(value);
            }
        }));
    }

}
