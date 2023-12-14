

package com.app.home.ui.vote.detial;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.app.home.net.http.HomeNetImpl;
import com.app.home.net.http.IHomeNet;
import com.app.home.net.rpc.IWalletRpcNet;
import com.app.home.net.rpc.WalletRpcImpl;
import com.app.home.pojo.VoteDetial;
import com.app.home.pojo.VoteInfoDetialListWapper;
import com.app.home.pojo.rpc.DposVoteParam;
import com.app.home.pojo.rpc.RPCVoteInfo;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;

import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class VoteDetialVM extends BaseViewModel {
    private final CompositeDisposable mDisposable;
    private final IHomeNet mIHomeNet;

    private IWalletRpcNet mIWalletRpcNet;
    public RpcApi mRpcApi;
    
    public MutableLiveData<Boolean> mVoteSuccess;

    public VoteDetialVM(@NonNull Application application) {
        super(application);
        mIHomeNet = new HomeNetImpl();
        mRpcApi = new RpcApi();
        mIWalletRpcNet = new WalletRpcImpl();
        this.mDisposable = new CompositeDisposable();
        mVoteSuccess = new MutableLiveData<>();


    }

    private final MutableLiveData<VoteDetial> mDetialMutableLiveData = new MutableLiveData<>();
    public LiveData<VoteDetial> mLiveData = mDetialMutableLiveData;


    
    void getVoteDetial(String id) {
        this.mDisposable.add(mIHomeNet.getVoteDetial(id).subscribeWith(new NextSubscriber<VoteDetial>() {
            @Override
            public void dealData(VoteDetial value) {
                mDetialMutableLiveData.setValue(value);
            }
        }));
    }

    private final MutableLiveData<VoteInfoDetialListWapper> mDetialListWapperMutableLiveData = new MutableLiveData<>();
    public LiveData<VoteInfoDetialListWapper> mWapperLiveData = mDetialListWapperMutableLiveData;


    void getVoteInfo(String vote_id, VoteInfoDetialListWapper wapper) {
        int page = null == wapper ? 1 : wapper.page + 1;
        this.mDisposable.add(mIHomeNet.getVoteInfo(vote_id, page).subscribeWith(new NextSubscriber<VoteInfoDetialListWapper>() {
            @Override
            public void dealData(VoteInfoDetialListWapper value) {
                if (null != wapper && null != value.result) {
                    value.result.addAll(0, wapper.result);
                }
                if (null == value.result || value.result.size() < IHomeNet.LIMIT) {
                    value.isEnd = true;
                } else {
                    value.isEnd = false;
                }
                mDetialListWapperMutableLiveData.setValue(value);
            }
        }));
    }

    private MutableLiveData<RPCVoteInfo> mGasMutableLive = new MutableLiveData<>();
    public LiveData<RPCVoteInfo> mGasLiveData = mGasMutableLive;

    <T> void dposGas(String type, T param, String consume, WalletEntity walletEntity) {
        showLoadingDialog("");
        RPCVoteParam<String> gasParam = new RPCVoteParam<>();
        gasParam.msg_type = type;
        gasParam.msgs = new Gson().toJson(param);
        mDisposable.add(mIWalletRpcNet.getGas(walletEntity.getAllAddress(), gasParam).subscribeWith(new NextSubscriber<RPCVoteInfo>() {
            @Override
            public void dealData(RPCVoteInfo value) {
                dismissLoadingDialog();
                value.param = param;
                value.consume = consume;
                value.mWalletEntity = walletEntity;
                mGasMutableLive.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
                dismissLoadingDialog();
            }
        }));
    }

    void vote(DposVoteParam msg, WalletEntity entity) {
        showLoadingDialog("");
        dposGas(RPCVoteParam.TYPE_MSGVOTE, msg, "0", entity);
    }

    
    private int mType = -1;
    
    public static final int TYPE_VOTE = 0;


    void vote(RPCVoteInfo<DposVoteParam> info, WalletEntity entity, String pwd) {
        showLoadingDialog("");
        DposVoteParam msg = info.param;

        mDisposable.add(mIWalletRpcNet.sign(entity.getAllAddress(), () -> ChatSdk.signGovVote(msg.proposal_id, msg.option), info.mSeqAcountBean.data, "", info.fee.amount, info.gas_used, entity, pwd).subscribeWith(new NextSubscriber<EvmosTransferResultBean>() {
            @Override
            public void dealData(EvmosTransferResultBean value) {
                mType = TYPE_VOTE;
                if (value.isSuccess()) {
                    checkTxResult(value, false, 1);
                } else {
                    dismissLoadingDialog();
                    String errorInfo = value.getInfo();
                    showToast(errorInfo);
                }
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
                dismissLoadingDialog();
            }
        }));
    }

    
    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        if (mType == TYPE_VOTE) {
            ToastUtil.showToast(getApplication().getString(R.string.vote_success));
            mVoteSuccess.postValue(true);
        }

    }


    private int mUseTime;

    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times) {
        if (retry) {
            mUseTime += times;
        } else {
            showLoadingDialog("");
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
            mRpcApi.getEvmosHxResult(transferResult.data.tx_hash).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<EvmosHxResultBean>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(EvmosHxResultBean evmosHxResultBean) {
                    if (evmosHxResultBean.isTxSuccess()) {
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = true;
                        postTxReslult(result);
                    } else if (evmosHxResultBean.isTxFail(mUseTime)) {
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = false;
                        result.info = getApplication().getString(com.wallet.ctc.R.string.transfer_fail);
                        postTxReslult(result);
                    } else {
                        checkTxResult(transferResult, true, 2);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (mUseTime > 7) {
                        
                        EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                        result.success = false;
                        result.info = "";
                        postTxReslult(result);
                    } else {
                        checkTxResult(transferResult, true, 2);
                    }
                }

                @Override
                public void onComplete() {

                }
            });
        });
        mDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        if (null != this.mDisposable) {
            this.mDisposable.dispose();
        }
        super.onCleared();
    }

}
