

package com.wallet.ctc.nft.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.nft.bean.MetadataBean;
import com.wallet.ctc.nft.http.NftObserver;
import com.wallet.ctc.nft.http.api.NFTApi;

public class NftAssetsDetailBiz extends BaseBiz {

    public WalletEntity currentWallet;

    
    public MutableLiveData<MetadataBean> getMetadataLiveData = null;

    private NftBean nftBean = null;

    
    public NftAssetsDetailBiz(@NonNull Application application) {
        super(application);
        getMetadataLiveData = new MutableLiveData<>();
    }

    public void initData(NftBean data) {
        nftBean = data;
    }

    public void getMetadata(String uri) {
        http(api(NFTApi.class).getMetadata(uri),
                new NftObserver<MetadataBean>() {
                    @Override
                    public void onNext(@NonNull MetadataBean data) {
                        getMetadataLiveData.setValue(data);
                    }
                });
    }
}
