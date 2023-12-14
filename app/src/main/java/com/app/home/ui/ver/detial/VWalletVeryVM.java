

package com.app.home.ui.ver.detial;

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
import com.app.home.pojo.PledgedTokensDidListEntity;
import com.app.home.pojo.ValidatorDetailNew;
import com.app.home.pojo.rpc.DposPledgeParam;
import com.app.home.pojo.rpc.DposRedeemParam;
import com.app.home.pojo.rpc.RPCVoteInfo;
import com.app.home.pojo.rpc.RPCVoteParam;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosBlockHeightOrRateBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayNumberCountBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RedeemGatewayInfoEntity;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.ValidatorInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class VWalletVeryVM extends BaseViewModel {


    private final CompositeDisposable mDisposable;
    private final IHomeNet mIHomeNet;
    public RpcApi mRpcApi;
    private IWalletRpcNet mIWalletRpcNet;

    public VWalletVeryVM(@NonNull Application application) {
        super(application);
        mIHomeNet = new HomeNetImpl();
        mIWalletRpcNet = new WalletRpcImpl();
        mRpcApi = new RpcApi();
        this.mDisposable = new CompositeDisposable();
    }


    @Override
    protected void onCleared() {
        if (null != this.mDisposable) {
            this.mDisposable.dispose();
        }
        super.onCleared();
    }

    private final MutableLiveData<ValidatorDetailNew> mMutableLiveData = new MutableLiveData<>();
    public LiveData<ValidatorDetailNew> mDetailLiveData = mMutableLiveData;

    void getValidatorDetail(String address, String walletAddress) {

        this.mDisposable.add(mIHomeNet.getValidatorDetail(address, walletAddress).subscribeWith(new NextSubscriber<ValidatorDetailNew>() {
            @Override
            public void dealData(ValidatorDetailNew value) {
                mMutableLiveData.setValue(value);
            }
        }));
    }

    private MutableLiveData<ValidatorInfo> mPageListMutableLiveData = new MutableLiveData<>();
    public LiveData<ValidatorInfo> mLiveData = mPageListMutableLiveData;

    void getValidatorInfo(String address, ValidatorInfo info) {
        int page = null == info ? 1 : info.page + 1;
        int offset = 0;
        if (null == info || null == info.result) offset = 0;
        else offset = info.result.size() - 1;
        this.mDisposable.add(mIHomeNet.getValidatorInfo(address, page, offset).subscribeWith(new NextSubscriber<ValidatorInfo>() {
            @Override
            public void dealData(ValidatorInfo value) {
                if (null != info && null != value.result) {
                    value.result.addAll(0, info.result);
                }
                if (null == value.result || value.result.size() < IHomeNet.LIMIT) {
                    value.isEnd = true;
                } else {
                    value.isEnd = false;
                }
                mPageListMutableLiveData.setValue(value);
            }
        }));
    }


    void dposRedeem(DposRedeemParam param, WalletEntity walletEntity, String pwd, RPCVoteInfo info) {
        showLoadingDialog("");
        StringBuilder builder = new StringBuilder();
        for (String s : param.index_number) {
            if (!TextUtils.isEmpty(builder)) builder.append(",");
            builder.append(s);
        }
        ver(walletEntity, pwd, info, () -> ChatSdk.signDposUnDelagate(param.validator_address, param.amount.amount, param.amount.denom, builder.toString()));
    }


    
    void dposPledge(DposPledgeParam param, WalletEntity walletEntity, String pwd, RPCVoteInfo info) {
        showLoadingDialog("");
        ver(walletEntity, pwd, info, () -> ChatSdk.signDposDelagate(param.validator_address, param.amount.amount, param.amount.denom));
    }

    private void ver(WalletEntity entity, String pwd, RPCVoteInfo info, WalletRpcImpl.IDoSdk doSdk) {
        mDisposable.add(mIWalletRpcNet.sign(entity.getAllAddress(), doSdk, info.mSeqAcountBean.data, "", info.fee.amount, info.gas_used, entity, pwd).subscribeWith(new NextSubscriber<EvmosTransferResultBean>() {
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


    private final MutableLiveData<RPCVoteInfo> mGasMutableLive = new MutableLiveData<>();
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

    
    private void ver(String type, String msg, WalletEntity entity, String pwd, WalletRpcImpl.IDoSdk doSdk) {

        RPCVoteParam<String> param = new RPCVoteParam<>();
        param.msg_type = type;
        param.msgs = msg;
        mDisposable.add(mIWalletRpcNet.getRPCVoteInfo(param, "", entity, pwd, doSdk).subscribeWith(new NextSubscriber<EvmosTransferResultBean>() {
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

    
    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        ToastUtil.showToast(getApplication().getString(R.string.method_success));
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
                        result.info = getApplication().getString(R.string.waiting_trade_result);
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

    public MutableLiveData<Integer> mGatewayNumberCount = new MutableLiveData<>();

    
    public void getGatewayNumberCount(String gatewayAddress, String amount) {
        mDisposable.add(mRpcApi.getGatewayNumberCount(gatewayAddress, amount).subscribeWith(new NextSubscriber<>() {
            @Override
            public void dealData(EvmosGatewayNumberCountBean value) {
                mGatewayNumberCount.postValue(value.data);
            }

            @Override
            protected void dealError(Throwable e) {
                
            }
        }));
    }

    public MutableLiveData<EvmosBlockHeightOrRateBean> mBlockOrRateData = new MutableLiveData<>();

    
    public void getBlockHeightOrRate(String validatorAddress, String walletAddress) {
        mDisposable.add(mRpcApi.getBlockHeightOrRate(validatorAddress, walletAddress).subscribeWith(new NextSubscriber<>() {
            @Override
            public void dealData(EvmosBlockHeightOrRateBean value) {
                mBlockOrRateData.postValue(value);
            }
        }));
    }

    public MutableLiveData<List<PledgedTokensDidListEntity>> mPledgeTokenData = new MutableLiveData<>();
    public MutableLiveData<Boolean> mIsGateway = new MutableLiveData<>();

    
    public void getRedeemToken(String gatewayAddress, String walletAddress) {
        mDisposable.add(mRpcApi.getGatewayAddressInfo(gatewayAddress, walletAddress).subscribeWith(new NextSubscriber<>() {
            @Override
            public void dealData(RedeemGatewayInfoEntity value) {
                List<PledgedTokensDidListEntity> list = new ArrayList<>();
                if (null != value.data) mIsGateway.postValue(value.data.is_gateway);

                if (null == value.data || null == value.data.gateway || null == value.data.gateway.gateway_num) {
                    mPledgeTokenData.postValue(list);
                    return;
                }

                for (RedeemGatewayInfoEntity.Data.GatewayEntity.GatewayNumEntity gatewayNum : value.data.gateway.gateway_num) {
                    PledgedTokensDidListEntity entity = new PledgedTokensDidListEntity(gatewayNum.gateway_address, gatewayNum.number_index);
                    entity.isDefaultSegment = gatewayNum.is_first;
                    list.add(entity);
                }
                mPledgeTokenData.postValue(list);
            }
        }));
    }
}
