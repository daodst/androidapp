

package com.wallet.ctc.nft.ui;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.nft.bean.NtfPage;
import com.wallet.ctc.nft.http.NftObserver;
import com.wallet.ctc.nft.http.api.NFTApi;

import java.util.ArrayList;
import java.util.List;

import common.app.BuildConfig;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;

public class NftBiz extends BaseBiz {

    public WalletEntity currentWallet;

    
    public MutableLiveData<List<NftBean>> getLocalNftLiveData = null;
    
    public MutableLiveData<List<NftBean>> getNftLiveData = null;

    private List<NftBean> nftList = null;

    private WalletDBUtil walletDBUtil = null;

    private DisposableObserver disposableObserver = null;

    
    public NftBiz(@NonNull Application application) {
        super(application);
        getLocalNftLiveData = new MutableLiveData<>();
        getNftLiveData = new MutableLiveData<>();
        walletDBUtil = new WalletDBUtil(application);
    }

    public void loadData(WalletEntity entity) {
        currentWallet = entity;
        getOwnedNft();
    }

    public void getOwnedNft() {
        Observable<NtfPage<NftBean>> ob = Observable.create(new ObservableOnSubscribe<List<NftBean>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<NftBean>> emitter) throws Exception {
                try {
                    nftList = walletDBUtil.getNftListByAddress(currentWallet.getAllAddress(), currentWallet.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("zhao","faill--"+e.getMessage());
                }
                Log.v("zhao","ok--"+nftList);
                emitter.onNext(nftList);
            }
        }).flatMap(new Function<List<NftBean>, ObservableSource<NtfPage<NftBean>>>() {
            @Override
            public ObservableSource<NtfPage<NftBean>> apply(@NonNull List<NftBean> nftBeans) throws Exception {
                return api(NFTApi.class).getOwnedNtf(currentWallet.getAllAddress(), WalletUtil.getNftChain(currentWallet.getType()), BuildConfig.NFT_FORMAT);
            }
        });
        if (null != disposableObserver && !disposableObserver.isDisposed()) {
            disposableObserver.dispose();
        }
        disposableObserver = new NftObserver<NtfPage<NftBean>>() {
            @Override
            public void onNext(@NonNull NtfPage<NftBean> data) {
                List<NftBean> tempList = new ArrayList<>();
                tempList.addAll(nftList);
                
                if (null != data.result && data.result.size() > 0) {
                    for (NftBean bean : data.result) {
                        NftBean temp = findNft(tempList, bean.token_address);
                        if (null == temp) {
                            bean.tokenCount++;
                            tempList.add(bean);
                        } else {
                            temp.tokenCount++;
                        }
                    }
                }
                nftList = tempList;
                getNftLiveData.setValue(nftList);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                getNftLiveData.setValue(nftList);
            }
        };
        http(ob, disposableObserver);
    }

    private NftBean findNft(List<NftBean> list, String tokenAddress) {
        for (NftBean bean : list) {
            if (bean.getToken_address().equals(tokenAddress)) {
                return bean;
            }
        }
        return null;
    }


    
    public void search(String keyword) {
        if (null != nftList && nftList.size() > 0) {
            if (TextUtils.isEmpty(keyword)) {
                getLocalNftLiveData.setValue(nftList);
                return;
            }

            List<NftBean> beans = new ArrayList<>();
            for (int i = 0; null != nftList && i < nftList.size(); i++) {
                NftBean bean = nftList.get(i);
                String name = bean.name;
                if (!TextUtils.isEmpty(name)) {
                    String toUpperCase = name.toUpperCase();
                    if (toUpperCase.contains(keyword.toUpperCase())) {
                        beans.add(bean);
                    }
                }
            }
            getLocalNftLiveData.setValue(beans);
        }
    }

    
    public void deleteNft(NftBean bean) {
        nftList.remove(bean);
        walletDBUtil.deleteNftToken(bean);
        getLocalNftLiveData.setValue(nftList);
    }
}
