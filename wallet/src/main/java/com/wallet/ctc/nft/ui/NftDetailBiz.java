

package com.wallet.ctc.nft.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.nft.bean.NtfPage;
import com.wallet.ctc.nft.http.NftObserver;
import com.wallet.ctc.nft.http.api.NFTApi;

import java.util.List;

import common.app.BuildConfig;

public class NftDetailBiz extends BaseBiz {

    public WalletEntity currentWallet;

    
    public MutableLiveData<List<NftBean>> getNftAssetsLiveData = null;

    private NftBean nftBean = null;

    
    public NftDetailBiz(@NonNull Application application) {
        super(application);
        getNftAssetsLiveData = new MutableLiveData<>();
    }

    public void initData(NftBean data) {
        nftBean = data;
    }

    public void getNftAssets() {

        http(api(NFTApi.class).getNftAssets(nftBean.owner_of, nftBean.token_address, WalletUtil.getNftChain(nftBean.walletType), BuildConfig.NFT_FORMAT),
                new NftObserver<NtfPage<NftBean>>() {
                    @Override
                    public void onNext(@NonNull NtfPage<NftBean> data) {
                        getNftAssetsLiveData.setValue(data.result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        getNftAssetsLiveData.setValue(null);
                    }
                });

    }
}
