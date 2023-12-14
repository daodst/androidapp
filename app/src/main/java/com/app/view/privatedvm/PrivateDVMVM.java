package com.app.view.privatedvm;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.google.gson.Gson;
import com.wallet.ctc.model.blockchain.EvmosDvmListBean;
import com.wallet.ctc.ui.pay.TransferControlApi;

import java.util.List;

import common.app.base.BaseViewModel;
import common.app.utils.LogUtil;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.listener.TranslationListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class PrivateDVMVM extends BaseViewModel {
    public boolean isRefresh = true;
    private int page = 1;

    public MutableLiveData<List<EvmosDvmListBean.Data>> mLiveData;
    private final TransferControlApi mApi = new TransferControlApi();


    
    public PrivateDVMVM(@NonNull Application application) {
        super(application);
        mLiveData = new MutableLiveData<>();

        loadData();
    }

    public void onRefresh() {
        isRefresh = true;
        page = 1;
        loadData();
    }

    public void onLoadMore() {
        isRefresh = false;
        page++;
        loadData();
    }

    public void loadData() {
        
        String address = ChatStatusProvide.getAddress(getApplication());
        Observable<EvmosDvmListBean> observable = mApi.getMyDvmList(address);
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pDeviceGroupPageData -> {
                    LogUtil.d("Jues_", new Gson().toJson(pDeviceGroupPageData));
                    
                    mLiveData.postValue(pDeviceGroupPageData.data);
                });
    }

    public void myDvmApprove(Context context, String approveAddress, String clusterId, String approveEndBlock) {
        
        String address = ChatStatusProvide.getAddress(context);
        TransferControlApi controlApi = new TransferControlApi();
        controlApi.signPersonDvmApprove(context, address, approveAddress, clusterId, approveEndBlock, new TranslationListener() {
            @Override
            public void onTransSuccess() {
                showToast(context.getString(R.string.success));
                
                onRefresh();
            }

            @Override
            public void onFail(String errorInfo) {

            }
        });
    }
}
