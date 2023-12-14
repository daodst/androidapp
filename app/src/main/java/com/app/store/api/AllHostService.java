package com.app.store.api;

import com.app.store.DAppStoreEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface AllHostService {
    @GET
    Observable<List<DAppStoreEntity>> getData(@Url String url);
}
