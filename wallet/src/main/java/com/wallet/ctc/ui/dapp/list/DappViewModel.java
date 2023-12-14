package com.wallet.ctc.ui.dapp.list;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.R;
import com.wallet.ctc.db.DBManager;
import com.wallet.ctc.db.DappHistoryEntity;

import java.util.ArrayList;
import java.util.List;

import common.app.base.BaseViewModel;
import io.reactivex.functions.Consumer;


public class DappViewModel extends BaseViewModel {

    MutableLiveData<List<DappHistoryEntity>> mDappsLD;

    
    public DappViewModel(@NonNull Application application) {
        super(application);
        mDappsLD = new MutableLiveData<>();
    }

    
    @SuppressLint("CheckResult")
    public void getMyLikeDapp() {
        DBManager.getInstance(getApplication()).getLikeBrowseHistoryList().subscribe(new Consumer<List<DappHistoryEntity>>() {
            @Override
            public void accept(List<DappHistoryEntity> dappHistoryEntities) throws Exception {
                List<DappHistoryEntity> list = getDefaultList();
                if (null != dappHistoryEntities) {
                    list.addAll(dappHistoryEntities);
                }
                mDappsLD.setValue(list);
            }
        });
    }

    
    private List<DappHistoryEntity> getDefaultList() {
        List<DappHistoryEntity> list = new ArrayList<>();
        DappHistoryEntity entity = new DappHistoryEntity();
        entity.setTitle("PancakeSwap");
        entity.setUrl("https://pancakeswap.finance/");
        entity.setIconRes(R.mipmap.pancakeswap_logo);
        list.add(entity);
        return list;
    }
}
