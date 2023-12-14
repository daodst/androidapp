

package com.wallet.ctc.nft.ui;

import android.app.Application;

import androidx.annotation.NonNull;

import com.wallet.ctc.nft.http.helper.SkyHelper;

import common.app.base.BaseViewModel;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

public class BaseBiz extends BaseViewModel {
    
    public BaseBiz(@NonNull Application application) {
        super(application);
    }

    
    public <T> Observable<T> http(Observable<T> observable, final DisposableObserver<T> response) {
        return SkyHelper.http().dohttp(observable, response);
    }

    
    public <T> T api(final Class<T> service) {
        return SkyHelper.http().api(service);
    }

}
