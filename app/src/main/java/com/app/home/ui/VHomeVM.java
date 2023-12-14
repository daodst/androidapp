

package com.app.home.ui;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.app.home.net.http.HomeNetImpl;
import com.app.home.net.http.IHomeNet;
import com.app.home.net.rpc.IWalletRpcNet;
import com.app.home.net.rpc.WalletRpcImpl;
import com.app.home.pojo.DposInfo;
import com.app.home.pojo.MyPledge;
import com.app.home.pojo.VoteInfo;
import com.app.home.pojo.VoteInfoWapper;
import com.app.home.pojo.rpc.DposAwardParam;
import com.app.home.pojo.rpc.RPCVoteInfo;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class VHomeVM extends BaseViewModel {

    private final CompositeDisposable mDisposable;
    private IHomeNet mIHomeNet;
    public RpcApi mRpcApi;
    private IWalletRpcNet mIWalletRpcNet;
    private String TAG = "VHomeVM";

    public VHomeVM(@NonNull Application application) {
        super(application);
        mIHomeNet = new HomeNetImpl();
        mIWalletRpcNet = new WalletRpcImpl();
        mRpcApi = new RpcApi();
        this.mDisposable = new CompositeDisposable();
    }

    private final MutableLiveData<List<VoteInfo>> mPageListMutableLiveData = new MutableLiveData<>();
    public LiveData<List<VoteInfo>> mLiveData = mPageListMutableLiveData;


    void getVoteInfo() {
        this.mDisposable.add(mIHomeNet.getVoteInfo(0).subscribeWith(new NextSubscriber<VoteInfoWapper>() {
            @Override
            public void dealData(VoteInfoWapper value) {
                if (null == value.proposals) {
                    mPageListMutableLiveData.setValue(new ArrayList<>());
                } else {
                    mPageListMutableLiveData.setValue(value.proposals);
                }
            }
        }));
    }

    private final MutableLiveData<DposInfo> mDposInfoMutableLiveData = new MutableLiveData<>();
    public LiveData<DposInfo> mDposInfoLiveData = mDposInfoMutableLiveData;


    void getTopInfo() {
        this.mDisposable.add(mIHomeNet.getDposInfo().subscribeWith(new NextSubscriber<DposInfo>() {
            @Override
            public void dealData(DposInfo value) {
                mDposInfoMutableLiveData.setValue(value);
            }
        }));
    }

    private final MutableLiveData<MyPledge> mMyPledgeMutableLiveData = new MutableLiveData<>();
    public LiveData<MyPledge> mMyPledgeLiveData = mMyPledgeMutableLiveData;


    void getMyPledge(String address) {
        this.mDisposable.add(mIHomeNet.getMyPledge(address).subscribeWith(new NextSubscriber<MyPledge>() {
            @Override
            public void dealData(MyPledge value) {
                mMyPledgeMutableLiveData.setValue(value);
            }
        }));
    }

    void dposaward(WalletEntity walletEntity) {
        showLoadingDialog("");
        mDisposable.add(mRpcApi.getEvmosGateway(SpUtil.getNodeNoSegm()).subscribeWith(new NextSubscriber<EvmosGatewayBean>() {
            @Override
            public void dealData(EvmosGatewayBean value) {
                if (value.isSuccess() && value.data != null) {
                    DposAwardParam param = new DposAwardParam();
                    param.delegator_address = walletEntity.getAllAddress();
                    param.validator_address = value.data.gateway_address;
                    dposGas(RPCVoteParam.TYPE_MSGWITHDRAWDELEGATIONREWARD, param, "0", walletEntity);
                } else if (!TextUtils.isEmpty(value.getInfo())) {
                    showToast(value.getInfo());
                }
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


    public void dposaward(DposAwardParam msg, WalletEntity entity, String pwd, RPCVoteInfo info) {

        mDisposable.add(mIWalletRpcNet.sign(entity.getAllAddress(), () -> ChatSdk.signDposWithdrawReward(msg.validator_address), info.mSeqAcountBean.data, "", info.fee.amount, info.gas_used, entity, pwd).subscribeWith(new NextSubscriber<EvmosTransferResultBean>() {
            @Override
            public void dealData(EvmosTransferResultBean value) {
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


    private MutableLiveData<Boolean> mDposGainMutableLive = new MutableLiveData<>();
    public LiveData<Boolean> mDposGainLive = mDposGainMutableLive;

    
    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        ToastUtil.showToast(getApplication().getString(R.string.dpos_success));
        mDposGainMutableLive.postValue(true);
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
                        result.info = getApplication().getString(R.string.dpos_waiting);
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

}
