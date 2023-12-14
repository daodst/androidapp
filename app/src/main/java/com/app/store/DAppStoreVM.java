package com.app.store;

import android.app.Application;
import android.content.res.AssetManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.store.api.AllHostApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.LogUtil;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
import io.reactivex.disposables.Disposable;


public class DAppStoreVM extends BaseViewModel {
    public MutableLiveData<List<DAppStoreEntity>> mLiveData = new MutableLiveData<>();

    public int page = 1;
    public boolean isRefresh = true;
    
    public String searchKeyword = "";

    public final String json_path = "https://appmarket.daodst.com/";
    private final AllHostApi mApi = new AllHostApi(json_path);
    public final Gson mGson = new GsonBuilder().create();

    private List<DAppStoreEntity> mDAppStoreEntities = new ArrayList<>();

    
    public DAppStoreVM(@NonNull Application application) {
        super(application);
    }

    public void getDAppStoreListData() {

        String cache = SpUtil.getAppStoreInfo();
        
        if (TextUtils.isEmpty(cache)) {
            readLocalJson();
            
            getNewDataFromHttp();
        } else {
            
            Type token = new TypeToken<List<DAppStoreEntity>>() {
            }.getType();
            mDAppStoreEntities = mGson.fromJson(cache, token);
            mLiveData.postValue(mDAppStoreEntities);
            
            getNewDataFromHttp();
        }
    }

    public void searchFilter() {
        if (TextUtils.isEmpty(searchKeyword)) {
            mLiveData.postValue(mDAppStoreEntities);
            return;
        }
        List<DAppStoreEntity> list = new ArrayList<>();
        
        for (DAppStoreEntity entity : mDAppStoreEntities) {
            String appName = entity.appName.toLowerCase();
            String[] spilt = searchKeyword.toLowerCase().split("");
            boolean isContainer = true;
            for (String cha : spilt) {
                if (!appName.toLowerCase().contains(cha)) {
                    isContainer = false;
                    break;
                }
                
                appName = appName.replace(cha, "");
            }
            if (isContainer) list.add(entity);
        }
        mLiveData.postValue(list);
    }

    private void readLocalJson() {
        AssetManager assetManager = getApplication().getAssets();
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        try {
            inputStreamReader = new InputStreamReader(assetManager.open("app_store.json"));
            br = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) builder.append(line);

            Type token = new TypeToken<List<DAppStoreEntity>>() {
            }.getType();
            
            mDAppStoreEntities = mGson.fromJson(builder.toString(), token);
            mLiveData.postValue(mDAppStoreEntities);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getNewDataFromHttp() {
        Disposable disposable = mApi.getAppStore().compose(RxSchedulers.io_main()).subscribeWith(new NextSubscriber<>() {
            @Override
            public void dealData(List<DAppStoreEntity> value) {
                mDAppStoreEntities = value;
                String appStoreInfo = mGson.toJson(value);
                LogUtil.d("AppStore Json Api", appStoreInfo);
                SpUtil.setAppStoreInfo(appStoreInfo);
                mLiveData.postValue(value);
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }
}
