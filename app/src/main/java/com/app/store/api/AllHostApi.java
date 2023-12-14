package com.app.store.api;

import com.app.store.DAppStoreEntity;

import java.util.List;

import common.app.base.model.http.HttpMethods;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class AllHostApi {
    private final AllHostService mHostService;

    public AllHostApi(String allHost) {
        Retrofit retrofit = HttpMethods.getInstance().getRetrofitAllHost(allHost);
        mHostService = retrofit.create(AllHostService.class);
    }

    public Observable<List<DAppStoreEntity>> getAppStore() {
        return mHostService.getData("data.json").subscribeOn(Schedulers.io());
    }
}
