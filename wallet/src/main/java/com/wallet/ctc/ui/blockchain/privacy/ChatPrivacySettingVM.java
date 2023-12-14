package com.wallet.ctc.ui.blockchain.privacy;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosChatFeeBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.LogUtil;

import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ChatPrivacySettingVM extends BaseViewModel {
    public RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<EvmosPledgeResultBean> mTransfResultLD;
    public MutableLiveData<EvmosChatFeeBean> mSettingLD;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;
    public MutableLiveData<Boolean> mShowRegitDialog;
    
    public ChatPrivacySettingVM(@NonNull Application application) {
        super(application);
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mTransfResultLD = new MutableLiveData<>();
        mSettingLD = new MutableLiveData<>();
        mShowGasDialogLD = new MutableLiveData<>();
        mShowRegitDialog = new MutableLiveData<>();
    }

    
    public void getSetting(String address) {
        mRpcApi.getEvmosChatFeeSetting(address).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosChatFeeBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosChatFeeBean evmosChatFeeBean) {
                        if (null != evmosChatFeeBean && evmosChatFeeBean.isSuccess() && null != evmosChatFeeBean.data) {
                            mSettingLD.setValue(evmosChatFeeBean);
                        } else {
                            String errorInfo = evmosChatFeeBean != null ? evmosChatFeeBean.getInfo() : "get fee setting fail";
                            showToast(errorInfo);
                        }

                        
                        if (evmosChatFeeBean.data == null || TextUtils.isEmpty(evmosChatFeeBean.data.from_address)) {
                            mShowRegitDialog.setValue(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    
    public void showGasAlert(String fromAddr, EvmosChatFeeBean feeSetting) {
        mRpcApi.getSetPrivacySettingGas(fromAddr, feeSetting.data.chat_restricted_mode, feeSetting.data.getChatFeeAmount(), feeSetting).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<EvmosSeqGasBean>() {
                    @Override
                    public void dealData(EvmosSeqGasBean value) {
                        mShowGasDialogLD.setValue(value);
                    }
                });
    }


    
    public void setChatFee(String fromAddr, String feeMode, String bigAmount, EvmosChatFeeBean feeSetting,  WalletEntity wallet, String pwd) {
        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }
        if (TextUtils.isEmpty(bigAmount)) {
            bigAmount = feeSetting.data.getChatFeeAmount();
        }
        
        
        String finalBigAmount = bigAmount;
        showLoadingDialog("");
        mRpcApi.getSetPrivacySettingGas(fromAddr, feeMode, bigAmount, feeSetting)
                .concatMap(new Function<EvmosSeqGasBean, ObservableSource<EvmosSignResult>>() {
                    @Override
                    public ObservableSource<EvmosSignResult> apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                        if (null != evmosSeqGasBean && evmosSeqGasBean.isSuccess()) {
                            return signSetChatFee(fromAddr, feeMode, finalBigAmount,  feeSetting, evmosSeqGasBean.seqAccount, evmosSeqGasBean.gas.getGasAmount(),
                                    evmosSeqGasBean.gas.getGasLimit(), wallet, pwd);
                        } else {
                            throw new Exception(evmosSeqGasBean.getInfo());
                        }
                    }
                }).concatMap(new Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>() {
                    @Override
                    public ObservableSource<EvmosTransferResultBean> apply(EvmosSignResult evmosSignResult) throws Exception {
                        if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                            return mRpcApi.submitEvmosTransfer(evmosSignResult.Data);
                        } else {
                            throw new Exception(evmosSignResult.getInfo());
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosTransferResultBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(EvmosTransferResultBean data) {
                        if (null != data && data.isSuccess()) {
                            checkTxResult(data, false, 1);
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


    
    private Observable<EvmosSignResult> signSetChatFee(String address, String feeMode, String bigAmount, EvmosChatFeeBean feeSetting,
                                                       EvmosSeqAcountBean.Data seqAccountBean,
                                                       final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                
                String publickey = new String(wallet.getmPublicKey());
                String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
                LogUtil.i("publickey="+publickey+", \nprivateKey="+privateKey+", \naddress="+address);
                ChatSdk.setupCosmosWallet(address, publickey, privateKey);

                
                String gasAmount2 = gasAmount;
                if (TextUtils.isEmpty(gasAmount2)) {
                    gasAmount2 = "100000000000000000";
                }
                String gasLimit2 = gasLimit;
                if (TextUtils.isEmpty(gasLimit2)) {
                    gasLimit2 = "2000000";
                }
                String accountNum = seqAccountBean.account_number+"";
                String accountSeq = seqAccountBean.sequence+"";
                String remark = "";
                LogUtil.i("accountNum="+accountNum+", accountSeq="+accountSeq);
                ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, remark);

                byte[] signByte = {};
                signByte = ChatSdk.signSetPrivacySetting(feeMode, bigAmount, feeSetting);

                
                
                String jsonSignResult = new String(signByte);
                LogUtil.i("jsonSignResult="+jsonSignResult);
                if (TextUtils.isEmpty(jsonSignResult)) {
                    emitter.onNext(new EvmosSignResult());
                } else {
                    try {
                        EvmosSignResult result = new Gson().fromJson(jsonSignResult, EvmosSignResult.class);
                        emitter.onNext(result);
                    } catch (Exception e){
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }


    private int mUseTime;
    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times) {
        if (retry) {
            mUseTime +=times;
        } else {
            showLoadingDialog("");
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time->{
            mRpcApi.getEvmosHxResult(transferResult.data.tx_hash)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EvmosHxResultBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(EvmosHxResultBean evmosHxResultBean) {
                            if (evmosHxResultBean.isTxSuccess()) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = true;
                                postTxReslult(result);
                            } else if(evmosHxResultBean.isTxFail(mUseTime)) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = getApplication().getString(R.string.transfer_fail);
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
    }



    
    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        mTransfResultLD.setValue(resultBean);
    }

    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}
