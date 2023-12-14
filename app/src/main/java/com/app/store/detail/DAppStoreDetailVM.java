package com.app.store.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.store.DAppStoreEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import common.app.base.BaseViewModel;


public class DAppStoreDetailVM extends BaseViewModel {
    public String appInfo;
    public MutableLiveData<DAppStoreEntity> mLiveData = new MutableLiveData<>();

    public final Gson mGson = new GsonBuilder().create();

    
    public DAppStoreDetailVM(@NonNull Application application) {
        super(application);
    }

    public void getDAppDetail() {
        DAppStoreEntity entity = mGson.fromJson(appInfo, DAppStoreEntity.class);
        mLiveData.postValue(entity);
    }
}
