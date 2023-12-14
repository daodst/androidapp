

package com.wallet.ctc.ui.me.virtualphone;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosChatInfoBean;
import com.wallet.ctc.model.blockchain.EvmosChatParamsBean;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.me.SMVirtualPhoneEntity;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;
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


public class SMVirtualPhoneActivityVM extends BaseViewModel {
    public MutableLiveData<List<SMVirtualPhoneEntity>> mVirtualPhone;
    public MutableLiveData<EvmosGatewayBean> mGateWayLD;
    public MutableLiveData<EvmosPledgeResultBean> mResultLD;
    public MutableLiveData<EvmosChatParamsBean> mChatParamsLD;
    public MutableLiveData<Boolean> mHasPledge;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;
    private CompositeDisposable mDisposable;
    private RpcApi mRpcApi;
    private int mDecimal = 18;

    
    public SMVirtualPhoneActivityVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mVirtualPhone = new MutableLiveData<>();
        mResultLD = new MutableLiveData<>();
        mChatParamsLD = new MutableLiveData<>();
        mHasPledge = new MutableLiveData<>();
        mShowGasDialogLD = new MutableLiveData<>();
        mGateWayLD = new MutableLiveData<>();

        mRpcApi = new RpcApi();

        
        List<AssertBean> assets = WalletDBUtil.getInstent(application).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
    }

    public String getGateWayAddr() {
        EvmosGatewayBean gateWayData =  mGateWayLD.getValue();
        if (null != gateWayData && null != gateWayData.data) {
            return gateWayData.data.gateway_address;
        } else {
            return "";
        }
    }

    
    public void getHoldPhoneList(String address) {
        showLoadingDialog("");
        mRpcApi.getEvmosChatInfo(address).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosChatInfoBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosChatInfoBean chatInfoBean) {
                        dismissLoadingDialog();
                        if (null != chatInfoBean && chatInfoBean.isSuccess()) {
                            
                            mHasPledge.setValue(null == chatInfoBean.data || TextUtils.isEmpty(chatInfoBean.data.from_address));
                            if (null == chatInfoBean.data) {
                                mVirtualPhone.setValue(new ArrayList<>());
                                return;
                            }
                            List<String> phoneList = chatInfoBean.data.mobile;
                            List<SMVirtualPhoneEntity> datas = new ArrayList<>();
                            String nowPhone = SpUtil.getNowPhone(address);
                            boolean hasSelected = false;
                            if (phoneList != null && phoneList.size() > 0) {
                                for (int i = 0; i < phoneList.size(); i++) {
                                    String phoneNum = phoneList.get(i);
                                    SMVirtualPhoneEntity entity = new SMVirtualPhoneEntity(phoneNum);
                                    if (phoneNum.equalsIgnoreCase(nowPhone)) {
                                        entity.checked = true;
                                        hasSelected = true;
                                    }
                                    datas.add(entity);
                                }
                            }
                            if (!hasSelected && datas.size() > 0) {
                                datas.get(0).checked = true;
                            }
                            mVirtualPhone.setValue(datas);
                        } else {
                            String errorInfo = chatInfoBean != null ? chatInfoBean.getInfo() : "get fail";
                            showToast(errorInfo);
                        }
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

    

    public MutableLiveData<List<String>> phoneNumberList = new MutableLiveData<>();

    
    public void getPhoneList(String noSegment) {
        mRpcApi.getEvmosGateway(noSegment).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosGatewayBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosGatewayBean gateWayBean) {
                        if (null != gateWayBean && gateWayBean.isSuccess() && null != gateWayBean.data) {
                            mGateWayLD.setValue(gateWayBean);

                            List<EvmosGatewayBean.GatewayNum> list = gateWayBean.data.gateway_num;
                            List<String> numList = new ArrayList<>();
                            if (null != list && list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    numList.add(list.get(i).number_index + "XXXX");
                                }
                            }
                            phoneNumberList.setValue(numList);
                        } else {
                            String errorInfo = gateWayBean != null ? gateWayBean.getInfo() : "get gateWay fail";
                            showToast(errorInfo);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    
    public void getChatAndBalance(String address) {
        showLoadingDialog("");
        mRpcApi.getEvmosChatParams()
                .concatMap(new Function<EvmosChatParamsBean, ObservableSource<EvmosChatParamsBean>>() {
                    @Override
                    public ObservableSource<EvmosChatParamsBean> apply(EvmosChatParamsBean evmosChatParamsBean) throws Exception {
                        return mRpcApi.getEvmosOneBalance(address, evmosChatParamsBean.data.destroyPhoneNumberCoin.denom).map(new Function<EvmosOneBalanceBean, EvmosChatParamsBean>() {
                            @Override
                            public EvmosChatParamsBean apply(EvmosOneBalanceBean evmosOneBalanceBean) throws Exception {
                                if (evmosOneBalanceBean != null && evmosOneBalanceBean.isSuccess()) {
                                    String balance = getTenDecimalValue(evmosOneBalanceBean.data);
                                    evmosChatParamsBean.tokenBalance = balance;
                                }
                                return evmosChatParamsBean;
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosChatParamsBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosChatParamsBean evmosChatParamsBean) {
                        dismissLoadingDialog();
                        if (null != evmosChatParamsBean && evmosChatParamsBean.isSuccess()) {
                            mChatParamsLD.setValue(evmosChatParamsBean);
                        }
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


    public void showGasAlert(String address, String phonePrex, String chatAddr) {
        mRpcApi.getEvmosBurnPhoneGas(address, phonePrex, getGateWayAddr(), chatAddr).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<EvmosSeqGasBean>() {
                    @Override
                    public void dealData(EvmosSeqGasBean value) {
                        if (value != null && value.isSuccess()) {
                            mShowGasDialogLD.setValue(value);
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get gas result is null";
                            showToast(errorInfo);
                        }
                    }
                });
    }


    
    public void burnGetMobile(String address, String phonePrex, WalletEntity wallet, String pwd) {
        
        doBurnGetMobile(address, phonePrex, wallet, pwd);
    }


    
    private void doBurnGetMobile(String address, String phonePrex, WalletEntity wallet, String pwd) {
        showLoadingDialog("");
        mRpcApi.getEvmosBurnPhoneGas(address, phonePrex, getGateWayAddr(), wallet.getChatAddress())
                .concatMap(new Function<EvmosSeqGasBean, ObservableSource<EvmosSignResult>>() {
                    @Override
                    public ObservableSource<EvmosSignResult> apply(EvmosSeqGasBean evmosSeqGasBean) throws Exception {
                        if (null != evmosSeqGasBean && evmosSeqGasBean.isSuccess()) {
                            return signBurnGetMobile(address, evmosSeqGasBean.seqAccount, evmosSeqGasBean.gas.getGasAmount(),
                                    evmosSeqGasBean.gas.getGasLimit(), wallet, pwd, phonePrex, wallet.getChatAddress());
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
                        mDisposable.add(d);
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

    private int mUseTime;

    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times) {
        if (retry) {
            mUseTime += times;
        } else {
            showLoadingDialog("");
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
            mRpcApi.getEvmosHxResult(transferResult.data.tx_hash)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<EvmosHxResultBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable.add(d);
                        }

                        @Override
                        public void onNext(EvmosHxResultBean evmosHxResultBean) {
                            if (evmosHxResultBean.isTxSuccess()) {
                                dismissLoadingDialog();
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = true;
                                mResultLD.setValue(result);
                            } else if (evmosHxResultBean.isTxFail(mUseTime)) {
                                dismissLoadingDialog();
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = getApplication().getString(R.string.transfer_fail);
                                mResultLD.setValue(result);
                            } else {
                                checkTxResult(transferResult, true, 2);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mUseTime > 7) {
                                dismissLoadingDialog();
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = "";
                                mResultLD.setValue(result);
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

    
    private Observable<EvmosSignResult> signBurnGetMobile(String address, EvmosSeqAcountBean.Data seqAccountBean,
                                                          final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd,
                                                          String phonePrefix, String chatAddr) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                
                String publickey = new String(wallet.getmPublicKey());
                String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
                LogUtil.i("publickey=" + publickey + ", \nprivateKey=" + privateKey + ", \naddress=" + address);
                ChatSdk.setupCosmosWallet(address, publickey, privateKey);

                
                String gasAmount2 = gasAmount;
                if (TextUtils.isEmpty(gasAmount2)) {
                    gasAmount2 = "100000000000000000";
                }
                String gasLimit2 = gasLimit;
                if (TextUtils.isEmpty(gasLimit2)) {
                    gasLimit2 = "2000000";
                }
                String accountNum = seqAccountBean.account_number + "";
                String accountSeq = seqAccountBean.sequence + "";
                String memo = "";
                LogUtil.i("accountNum=" + accountNum + ", accountSeq=" + accountSeq);
                ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit2, gasAmount2, memo);

                byte[] signByte = {};
                signByte = ChatSdk.signBurnGetMobile(phonePrefix, getGateWayAddr(), chatAddr);
                
                
                String jsonSignResult = new String(signByte);
                LogUtil.i("jsonSignResult=" + jsonSignResult);
                if (TextUtils.isEmpty(jsonSignResult)) {
                    emitter.onNext(new EvmosSignResult());
                } else {
                    try {
                        EvmosSignResult result = new Gson().fromJson(jsonSignResult, EvmosSignResult.class);
                        emitter.onNext(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }

    
    public String getTenDecimalValue(String bigNum) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        return new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, mDecimal)), 18, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}

