package com.app.view.groupinformation.vote.votelist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteBean;
import com.wallet.ctc.ui.pay.TransferControlApi;

import java.util.Collections;
import java.util.List;

import common.app.base.BaseViewModel;
import common.app.utils.LogUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class GovernancePoolsVoteListVM extends BaseViewModel {
    public boolean refresh = false;
    private int page = 1;
    public String groupId;

    public MutableLiveData<List<EvmosClusterVoteBean.Data>> mLiveData = new MutableLiveData<>();

    
    public GovernancePoolsVoteListVM(@NonNull Application application) {
        super(application);
    }

    public void onRefresh() {
        refresh = true;
        page = 1;
        loadData();
    }

    public void onLoadMore() {
        refresh = false;
        page++;
        loadData();
    }

    private void loadData() {
        
        mLiveData.setValue(null);

        TransferControlApi controlApi = new TransferControlApi();
        Observable<EvmosClusterVoteBean> observable = controlApi.getClusterVoteList(groupId);
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pDeviceGroupPageData -> {
                    LogUtil.d("Jues_", new Gson().toJson(pDeviceGroupPageData));
                    
                    Collections.sort(pDeviceGroupPageData.data);
                    mLiveData.postValue(pDeviceGroupPageData.data);
                });
    }
}
