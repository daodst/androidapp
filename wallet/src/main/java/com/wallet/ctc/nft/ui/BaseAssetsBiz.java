

package com.wallet.ctc.nft.ui;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseViewModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseAssetsBiz extends BaseViewModel {
    
    public MutableLiveData<List<AssertBean>> getLocalAssertsLiveData = null;
    
    public MutableLiveData<List<AssertBean>> getAssertsLiveData = null;

    
    public MutableLiveData<WalletEntity> onWalletInfoChangeLiveData = null;

    public WalletDBUtil walletDBUtil = null;
    public WalletEntity currentWallet;
    public List<AssertBean> mustAssets;
    public List<AssertBean> assets = new ArrayList<>();
    public Gson gson = new Gson();
    public MeApi meApi = new MeApi();

    
    public BaseAssetsBiz(@NonNull Application application) {
        super(application);
        getAssertsLiveData = new MutableLiveData<>();
        getLocalAssertsLiveData = new MutableLiveData<>();
        onWalletInfoChangeLiveData = new MutableLiveData<>();
        walletDBUtil = new WalletDBUtil(application);
    }

    public void loadData(WalletEntity entity) {
        currentWallet = entity;
        loadAssets();
    }

    public void loadAssets() {
        Observable<Boolean> ob = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    mustAssets = walletDBUtil.getMustWallet(currentWallet.getType());
                    assets.clear();
                    if (assets.size() == 0) {
                        mustAssets.get(0).setAssertsNum(currentWallet.getmBalance());
                        mustAssets.get(0).setAssertsSumPrice(currentWallet.getmPrice());
                        assets.addAll(mustAssets);
                    }
                    assets.addAll(walletDBUtil.getAssetsByWalletType(currentWallet.getAllAddress(), currentWallet.getType()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
        ob.subscribe(aBoolean -> {
            onLoadAssetsComplete();
        });
    }

    public void onLoadAssetsComplete() {
        getLocalAssertsLiveData.setValue(assets);
    }

    
    public void search(String keyword) {
        if (null != assets && assets.size() > 0) {
            if (TextUtils.isEmpty(keyword)) {
                getLocalAssertsLiveData.setValue(assets);
                return;
            }

            List<AssertBean> beans = new ArrayList<>();
            for (int i = 0; null != assets && i < assets.size(); i++) {
                AssertBean bean = assets.get(i);
                String short_name = bean.getShort_name();
                if (!TextUtils.isEmpty(short_name)) {
                    String toUpperCase = short_name.toUpperCase();
                    if (toUpperCase.contains(keyword.toUpperCase())) {
                        beans.add(bean);
                    }
                }
            }
            getLocalAssertsLiveData.setValue(beans);
        }
    }
}
