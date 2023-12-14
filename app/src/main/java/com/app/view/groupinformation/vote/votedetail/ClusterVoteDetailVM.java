package com.app.view.groupinformation.vote.votedetail;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.app.home.pojo.rpc.DposVoteParam;
import com.google.gson.Gson;
import com.wallet.ctc.model.blockchain.EvmosClusterPersonVoteBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteDetailBean;
import com.wallet.ctc.model.blockchain.EvmosClusterVoteInfoBean;
import com.wallet.ctc.ui.pay.TransferControlApi;

import java.util.List;

import common.app.base.BaseViewModel;
import common.app.mall.util.ToastUtil;
import common.app.utils.LogUtil;
import im.wallet.router.listener.TranslationListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ClusterVoteDetailVM extends BaseViewModel {

    public MutableLiveData<EvmosClusterVoteDetailBean.Data> mHeaderLiveData = new MutableLiveData<>();
    public MutableLiveData<EvmosClusterVoteInfoBean.Data.FinalTallyResultEntity> mutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<EvmosClusterPersonVoteBean.Data.VoteEntity>> mLiveData = new MutableLiveData<>();

    
    public int proposalId;

    
    public ClusterVoteDetailVM(@NonNull Application application) {
        super(application);
    }

    
    public void loadDetailData() {
        TransferControlApi controlApi = new TransferControlApi();
        Observable<EvmosClusterVoteDetailBean> observable = controlApi.getClusterVoteDetail(proposalId);
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pDeviceGroupPageData -> {
                    LogUtil.d("Jues_", new Gson().toJson(pDeviceGroupPageData));
                    
                    mHeaderLiveData.postValue(pDeviceGroupPageData.data);
                });
    }

    
    public void loadVoteData() {
        TransferControlApi controlApi = new TransferControlApi();
        Observable<EvmosClusterVoteInfoBean> observable = controlApi.getVoteDetail(proposalId);
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pDeviceGroupPageData -> {
                    
                    mutableLiveData.postValue(pDeviceGroupPageData.data.tally);
                });
    }

    
    public void getClusterVoteAllPersonDetail() {
        
        TransferControlApi controlApi = new TransferControlApi();
        Observable<EvmosClusterPersonVoteBean> observable = controlApi.getClusterVoteAllPersonDetail(proposalId);
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pDeviceGroupPageData -> {
                    LogUtil.d("Jues_", new Gson().toJson(pDeviceGroupPageData));
                    
                    mLiveData.postValue(pDeviceGroupPageData.data.votes);
                });
    }

    void vote(Context context, DposVoteParam msg) {
        
        TransferControlApi controlApi = new TransferControlApi();
        controlApi.groupProposalVote(context, msg.voter, msg.proposal_id, msg.option, msg.option2, new TranslationListener() {
            @Override
            public void onFail(String errorInfo) {

            }

            @Override
            public void onTransSuccess() {
                
                ToastUtil.showToast(getApplication().getString(R.string.vote_success));
                getClusterVoteAllPersonDetail();
                loadVoteData();

            }
        });
        
    }
}
