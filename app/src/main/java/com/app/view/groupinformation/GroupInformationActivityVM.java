package com.app.view.groupinformation;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.google.gson.Gson;
import com.wallet.ctc.model.blockchain.DeviceGroupPageData;
import com.wallet.ctc.ui.pay.TransferControlApi;

import java.util.ArrayList;

import common.app.base.BaseViewModel;
import common.app.utils.LogUtil;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.listener.TranslationListener;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class GroupInformationActivityVM extends BaseViewModel {
    public MutableLiveData<EvmosGroupDataBean> mLiveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<EvmosMyGroupDataBean>> mListData = new MutableLiveData<>();
    public MutableLiveData<EvmosDaoParams> mLimitData = new MutableLiveData<>();
    private CompositeDisposable mDisposable;

    private final TransferControlApi mApi = new TransferControlApi();

    
    public GroupInformationActivityVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
    }

    
    public void getInfoData(String groupId) {
        
        String address = ChatStatusProvide.getAddress(getApplication());
        Observable<DeviceGroupPageData> observable = mApi.getDeviceGroupInfo(address, groupId);
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pDeviceGroupPageData -> {
                    LogUtil.d("Jues_", new Gson().toJson(pDeviceGroupPageData));
                    
                    mLiveData.postValue(pDeviceGroupPageData.groupData);
                    ArrayList<EvmosMyGroupDataBean> list = new ArrayList<>();
                    list.add(pDeviceGroupPageData.myData);
                    mListData.postValue(list);
                });
        mDisposable.add(disposable);
    }

    
    @SuppressLint("CheckResult")
    public void getInputLimit() {
        Observable<EvmosDaoParams> observable = mApi.getDaoParams();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pEvmosDaoParams -> {
                    
                    mLimitData.setValue(pEvmosDaoParams);
                });
    }

    
    public void myDvmApprove(Context context, String approveAddress, String clusterId, String approveEndBlock) {
        
        String address = ChatStatusProvide.getAddress(context);
        TransferControlApi controlApi = new TransferControlApi();
        controlApi.signPersonDvmApprove(context, address, approveAddress, clusterId, approveEndBlock, new TranslationListener() {
            @Override
            public void onTransSuccess() {
                showToast(context.getString(R.string.success));
                
                getInfoData(clusterId);
            }

            @Override
            public void onFail(String errorInfo) {

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != mDisposable){
            mDisposable.clear();
        }
    }
}
