package com.wallet.ctc.ui.blockchain.issuance;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.blockchain.issuance.net.rpc.ISWalletRpcNet;
import com.wallet.ctc.ui.blockchain.issuance.net.rpc.SWalletRpcImpl;
import com.wallet.ctc.ui.blockchain.issuance.pojo.IssuanceParam;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteInfo;
import com.wallet.ctc.ui.blockchain.issuance.pojo.WRPCVoteParam;

import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.RxSchedulers;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class IssuanceCoinVM extends BaseViewModel {
    public RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<EvmosPledgeResultBean> mTransfResultLD;
    private ISWalletRpcNet mIWalletRpcNet;

    
    public IssuanceCoinVM(@NonNull Application application) {
        super(application);
        mRpcApi = new RpcApi();
        mIWalletRpcNet = new SWalletRpcImpl();
        mDisposable = new CompositeDisposable();
        mTransfResultLD = new MutableLiveData<>();
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }


    void issuance(WalletEntity entity, String pwd, WRPCVoteInfo<IssuanceParam> info) {
        showLoadingDialog("");
        mDisposable.add(mIWalletRpcNet.sign(entity.getAllAddress(), () -> ChatSdk.signUserTokenIssue(info.param), info.mSeqAcountBean.data, "", info.fee.amount, info.gas_used, entity, pwd).subscribeWith(new NextSubscriber<EvmosTransferResultBean>() {
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
                        result.info = getApplication().getString(R.string.wallet_method_waiting_result);
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


    private MutableLiveData<Boolean> mBooleanMutableLive = new MutableLiveData<>();
    public LiveData<Boolean> mBooleanLiveData = mBooleanMutableLive;

    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        mBooleanMutableLive.postValue(true);

    }

    private MutableLiveData<WRPCVoteInfo> mGasMutableLive = new MutableLiveData<>();
    public LiveData<WRPCVoteInfo> mGasLiveData = mGasMutableLive;


    <T> void dposGas(String type, T param, String consume, WalletEntity walletEntity) {
        showLoadingDialog("");
        WRPCVoteParam<String> gasParam = new WRPCVoteParam<>();
        gasParam.msg_type = type;
        gasParam.msgs = new Gson().toJson(param);
        mDisposable.add(mIWalletRpcNet.getGas(walletEntity.getAllAddress(), gasParam).compose(RxSchedulers.io_main()).subscribeWith(new NextSubscriber<WRPCVoteInfo>() {
            @Override
            public void dealData(WRPCVoteInfo value) {
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
}
