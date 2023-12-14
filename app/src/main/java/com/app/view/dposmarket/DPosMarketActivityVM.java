package com.app.view.dposmarket;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.home.net.http.HomeNetImpl;
import com.app.home.net.http.IHomeNet;
import com.app.home.pojo.DposInfo;
import com.app.home.pojo.DposListEntity;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import im.vector.app.provide.ChatStatusProvide;
import io.reactivex.disposables.CompositeDisposable;


public class DPosMarketActivityVM extends BaseViewModel {
    private final CompositeDisposable mDisposable;
    private IHomeNet mIHomeNet;

    private int page = 1;

    
    public int isDelegate;
    
    public int sortType;
    
    public String searchKeyword = "";

    
    public boolean zhSortAsc = false;
    public boolean weightSortAsc = false;
    public boolean commissionSortAsc = false;
    public boolean aliveSortAsc = false;

    private final MutableLiveData<DposInfo> mDposInfoMutableLiveData = new MutableLiveData<>();
    public LiveData<DposInfo> mDposInfoLiveData = mDposInfoMutableLiveData;

    public MutableLiveData<DposListEntity> mDposListLiveData = new MutableLiveData<>();

    
    public DPosMarketActivityVM(@NonNull Application application) {
        super(application);
        mIHomeNet = new HomeNetImpl();
        this.mDisposable = new CompositeDisposable();
    }


    
    void getTopInfo() {
        this.mDisposable.add(mIHomeNet.getDposInfo().subscribeWith(new NextSubscriber<DposInfo>() {
            @Override
            public void dealData(DposInfo value) {
                mDposInfoMutableLiveData.setValue(value);
            }
        }));
    }

    public void onRefresh() {
        page = 1;
        getListData();
    }

    public void onLoadMore() {
        page++;
        getListData();
    }

    public void getListData() {
        
        String address = ChatStatusProvide.getAddress(getApplication());
        this.mDisposable.add(mIHomeNet.getDposListData(address, isDelegate, sortType, page, 20, searchKeyword).subscribeWith(new NextSubscriber<DposListEntity>() {
            @Override
            public void dealData(DposListEntity value) {
                mDposListLiveData.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
                if (page > 1) page--;
            }
        }));
    }
}
