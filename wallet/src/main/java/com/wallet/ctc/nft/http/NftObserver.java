

package com.wallet.ctc.nft.http;

import androidx.annotation.NonNull;

import io.reactivex.observers.DisposableObserver;

public class NftObserver<T> extends DisposableObserver<T> {
    @Override
    public void onNext(@NonNull T data) {

    }

    @Override
    public void onError(@NonNull Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
