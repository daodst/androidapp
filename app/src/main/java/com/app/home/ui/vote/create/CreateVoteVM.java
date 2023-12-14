

package com.app.home.ui.vote.create;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.app.pojo.VoteParamsBean;
import com.google.gson.Gson;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.BlockDepositBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;

import java.util.List;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CreateVoteVM extends BaseViewModel {
    private final CompositeDisposable mDisposable;
    private RpcApi mRpcApi;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;
    public MutableLiveData<BlockDepositBean> mBlockInfLD;
    public MutableLiveData<String> mBalanceLD;
    public MutableLiveData<String> mMainBalanceLD;
    public MutableLiveData<EvmosPledgeResultBean> mResultLD;

    private Gson mGson;
    public CreateVoteVM(@NonNull Application application) {
        super(application);
        this.mDisposable = new CompositeDisposable();
        this.mShowGasDialogLD = new MutableLiveData<>();
        this.mBlockInfLD = new MutableLiveData<>();
        this.mResultLD = new MutableLiveData<>();
        this.mBalanceLD = new MutableLiveData<>();
        this.mMainBalanceLD = new MutableLiveData<>();
        this.mRpcApi = new RpcApi();
        this.mGson = new Gson();
    }

    public void getDatas(String address) {
        getBlockInfo(address);
    }

    public interface CheckInputCallBack {
        void onResult(String moneyCoin);
    }


    
    public void showGasAlert(int type, String fromAddr, String title, String desc, String bigMoneyCoin, List<VoteParamsBean> changeParam,
                             String payReceiveAddr, String bigPayNumCoin, String upgradeName, String upgradeInfo, String upgradeHeight) {
        Observable<EvmosSeqGasBean> observable = getGas(type, fromAddr, title, desc, bigMoneyCoin,changeParam,
                payReceiveAddr, bigPayNumCoin, upgradeName, upgradeInfo, upgradeHeight);
        getAndShowGas(observable);
    }

    
    private Observable<EvmosSeqGasBean> getGas(int type, String fromAddr, String title, String desc, String bigMoneyCoin, List<VoteParamsBean> changeParam,
                                               String payReceiveAddr, String bigPayNumCoin, String upgradeName, String upgradeInfo, String upgradeHeight) {
        Observable<EvmosSeqGasBean> observable = null;
        if (type == CreateVoteActivity.TYPE_PARAMS) {
            
            String changeParamsJson = mGson.toJson(changeParam);
            observable = mRpcApi.getEvmosParamsVoteGas(fromAddr, title, desc, bigMoneyCoin, changeParamsJson);
        } else if(type == CreateVoteActivity.TYPE_PAY) {
            
            observable = mRpcApi.getEvmosPayVoteGas(fromAddr, title, desc, bigMoneyCoin, payReceiveAddr, bigPayNumCoin);
        } else if(type == CreateVoteActivity.TYPE_UPGRADE) {
            
            long height = Long.parseLong(upgradeHeight);
            observable = mRpcApi.getEvmosUpgradeVoteGas(fromAddr, title, desc, bigMoneyCoin, upgradeName, upgradeInfo, height);
        }
        return observable;
    }

    
    private void getAndShowGas(Observable<EvmosSeqGasBean> gasObservable) {
        if (null == gasObservable) {
            showToast(getApplication().getString(R.string.get_gas_info_fail));
            return;
        }
        Disposable disposable = gasObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosSeqGasBean>() {
                    @Override
                    public void dealData(EvmosSeqGasBean value) {
                        if (null != value && value.isSuccess()) {
                            mShowGasDialogLD.setValue(value);
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get gas result null";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        showToast(getApplication().getString(R.string.get_gas_info_fail)+e.getMessage());
                    }
                });
        mDisposable.add(disposable);
    }


    
    public void submit(int type, String fromAddr, String title, String desc, String bigMoneyCoin, List<VoteParamsBean> changeParam,
                       String payReceiveAddr, String bigPayNumCoin, String upgradeName, String upgradeInfo, String upgradeHeight,
                       WalletEntity wallet, String pwd) {
        showLoadingDialog("");
        getGas(type, fromAddr, title, desc, bigMoneyCoin,changeParam,
                payReceiveAddr, bigPayNumCoin, upgradeName, upgradeInfo, upgradeHeight)
                .concatMap(evmosSeqGasBean -> {
                    return mRpcApi.submitEvmosTransfer(signVote(type, fromAddr, title, desc, bigMoneyCoin, changeParam,
                            payReceiveAddr, bigPayNumCoin, upgradeName, upgradeInfo, upgradeHeight, evmosSeqGasBean, wallet, pwd).Data);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosTransferResultBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosTransferResultBean data) {
                        if (null != data && data.isSuccess()) {
                            checkTxResult(data);
                        } else {
                            dismissLoadingDialog();
                            String errorInfo = data != null ? data.getInfo() : "set chat fee fail";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dismissLoadingDialog();
                        ToastUtil.showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    
    private EvmosSignResult signVote(int type, String fromAddr, String title, String desc, String bigMoneyCoin, List<VoteParamsBean> changeParam,
                                     String payReceiveAddr, String bigPayNumCoin, String upgradeName, String upgradeInfo, String upgradeHeight,
                                              EvmosSeqGasBean evmosSeqGasBean, WalletEntity wallet, String pwd) throws Exception {
        if (null == evmosSeqGasBean || !evmosSeqGasBean.isSuccess()) {
            String errorInfo = evmosSeqGasBean != null ? evmosSeqGasBean.getInfo() : "evmosSeqGasBean is null";
            throw new Exception(errorInfo);
        }
        
        ChatSdk.resetWalletGasInfo(evmosSeqGasBean, wallet, pwd, "chianMap");
        EvmosSignResult evmosSignResult = null;
        byte[] signData = null;
        if (type == CreateVoteActivity.TYPE_PARAMS) {
            
            String changeParamsJson = mGson.toJson(changeParam);
            signData = ChatSdk.signProposalParam(title, desc,changeParamsJson, bigMoneyCoin);

        } else if(type == CreateVoteActivity.TYPE_PAY) {
            
            signData = ChatSdk.signProposalCommunity(title, desc, payReceiveAddr, bigPayNumCoin, bigMoneyCoin);
        } else if(type == CreateVoteActivity.TYPE_UPGRADE) {
            
            long height = Long.parseLong(upgradeHeight);
            signData = ChatSdk.signProposalUpgrade(title, desc,  upgradeName, upgradeInfo, bigMoneyCoin, height);
        }
        evmosSignResult = ChatSdk.convertSignData(signData);
        if (evmosSignResult == null || !evmosSignResult.isSuccess()) {
            String errorInfo = evmosSignResult != null ? evmosSignResult.getInfo() : "sign result is null";
            throw new Exception(errorInfo);
        }
        return evmosSignResult;
    }

    
    private void checkTxResult(EvmosTransferResultBean transferResult) {
        showLoadingDialog("");
        mRpcApi.timerCheckTxResult(transferResult, mDisposable, new Observer<EvmosPledgeResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onNext(EvmosPledgeResultBean evmosPledgeResultBean) {
                mResultLD.setValue(evmosPledgeResultBean);
                dismissLoadingDialog();
            }

            @Override
            public void onError(Throwable e) {
                dismissLoadingDialog();
            }
            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }
        });
    }

    public void getBlockInfo(String address) {
        Disposable disposable = mRpcApi.getBlockDeposit().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<BlockDepositBean>() {
                    @Override
                    public void dealData(BlockDepositBean value) {
                        if (null != value) {
                            mBlockInfLD.setValue(value);

                            String coinName = "";
                            if (null != value) {
                                coinName = value.getCoinName();
                            }
                            if (TextUtils.isEmpty(coinName)) {
                                coinName = getApplication().getString(R.string.default_token_name);
                            }

                            AssertBean assertBean = WalletDBUtil.getInstent(getApplication()).getWalletAssets(WalletUtil.MCC_COIN, coinName);
                            getMccBalance(address, coinName, assertBean, false);

                            
                        } else {
                            showToast("get block info fail");
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        showToast(e.getMessage());
                    }
                });
        mDisposable.add(disposable);
    }

    
    private void getMccBalance(String address, String coinName, AssertBean assertBean, boolean isMain) {
        Disposable disposable = mRpcApi.getEvmosOneBalance(address, coinName)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosOneBalanceBean>() {
                    @Override
                    public void dealData(EvmosOneBalanceBean value) {
                        int decimal = assertBean != null ? assertBean.getDecimal() : 18;
                        if (null != value && value.isSuccess()) {
                            String remain = value.getBalance(decimal);
                            if (!isMain) {
                                mBalanceLD.setValue(remain);
                            } else {
                                mMainBalanceLD.setValue(remain);
                            }
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get cosmos balance null";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        showToast(e.getMessage());
                    }
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
