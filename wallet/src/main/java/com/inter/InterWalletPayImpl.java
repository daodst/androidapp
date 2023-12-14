package com.inter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.pay.ITransferControlApi;
import com.wallet.ctc.ui.pay.TransferControlApi;
import com.wallet.ctc.ui.pay.TranslationControlDialog;
import com.wallet.ctc.util.AllUtils;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.dialog.TransConfirmDialogBuilder;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.ui.view.InputPwdDialog;
import common.app.ui.view.MyProgressDialog;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
import im.wallet.router.listener.TranslationListener;
import im.wallet.router.util.Consumer;
import im.wallet.router.wallet.IWalletPay;
import im.wallet.router.wallet.pojo.DeviceGroupMember;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import im.wallet.router.wallet.pojo.EvmosGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupList;
import im.wallet.router.wallet.pojo.SignInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class InterWalletPayImpl implements IWalletPay {

    InputPwdDialog mInputPwdDialog;
    private WeakReference<Activity> mActivityRf;
    private SoftReference<Consumer<Boolean>> mCallBackRf;
    private int mDecimal = 18;
    private WalletEntity mSelecteWallet;

    private void init(Activity activity) {
        mActivityRf = new WeakReference<>(activity);
        
        List<AssertBean> assets = WalletDBUtil.getInstent(mActivityRf.get()).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
    }

    private String getTenDecimalValue(String bigNum) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        } else {
            return new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, mDecimal)), 18, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        }
    }

    @Override
    public void showPayDialog(Activity activity, String fromAddrss, String toAddress, String amount, String coinName, Consumer<Boolean> consumer) {
        if (null == activity || TextUtils.isEmpty(fromAddrss) || TextUtils.isEmpty(toAddress) || TextUtils.isEmpty(amount) || null == consumer) {
            ToastUtil.showToast(R.string.data_error);
            return;
        }
        mCallBackRf = new SoftReference<>(consumer);
        init(activity);
        mSelecteWallet = WalletDBUtil.getInstent(activity).getWalletInfoByAddress(fromAddrss, WalletUtil.MCC_COIN);

        getChatPayGas(fromAddrss, toAddress, coinName, amount).subscribe(new NextSubscriber<EvmosSeqGasBean>() {
            @Override
            public void dealData(EvmosSeqGasBean value) {
                if (!value.isSuccess()) {
                    ToastUtil.showToast(value.getInfo());
                    return;
                }
                TransConfirmDialogBuilder.builder(mActivityRf.get(), mSelecteWallet).amount(getTenDecimalValue(amount))
                        
                        .fromAddress(fromAddrss)
                        
                        .toAddress(toAddress)
                        .type(WalletUtil.MCC_COIN)
                        .orderDesc(activity.getString(R.string.trans_send_gifts_tips))
                        
                        .gasFeeWithToken(value.gas.getShowFee(BuildConfig.EVMOS_FAKE_UNINT))
                        
                        .goTransferListener(pwd -> {
                            goChatPay(value, fromAddrss, toAddress, coinName, amount, mSelecteWallet, pwd);
                        }).show();
            }
        });

    }

    @Override
    public void disPay() {
        if (null != mInputPwdDialog) {
            mInputPwdDialog.dismiss();
        }
    }

    @Override
    public void showPayDialog(Activity activity, String address, Consumer<String> consumer) {

        mSelecteWallet = WalletDBUtil.getInstent(activity).getWalletInfoByAddress(address, WalletUtil.MCC_COIN);

        if (null == mSelecteWallet) {
            ToastUtil.showToast(activity.getString(R.string.show_pay_dialog_wallet_error));
            return;
        }
        init(activity);
        if (null != mInputPwdDialog) {
            mInputPwdDialog.dismiss();
            mInputPwdDialog = null;
        }
        
        mInputPwdDialog = new InputPwdDialog(activity, getString(com.wallet.ctc.R.string.place_edit_password));
        mInputPwdDialog.setonclick(new InputPwdDialog.Onclick() {
            @Override
            public void Yes(String pwd) {
                mInputPwdDialog.dismiss();
                mInputPwdDialog = null;
                if (!TextUtils.equals(mSelecteWallet.getmPassword(), DecriptUtil.MD5(pwd))) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.password_error2);
                    return;
                }
                String privateKey = WalletUtil.getDecryptionKey(mSelecteWallet.getmPrivateKey(), pwd);
                consumer.accept(WalletUtil.provideEncryptionPrivateKey(privateKey));
            }

            @Override
            public void No() {
                mInputPwdDialog.dismiss();
            }
        });
        mInputPwdDialog.show();
    }

    @Override
    public SignInfo getSignInfo(Activity activity, String address) {

        String timestamp = System.currentTimeMillis() / 1000 + "";

        WalletEntity walletEntity = WalletDBUtil.getInstent(activity).getWalletInfoByAddress(address, WalletUtil.MCC_COIN);

        String chatPrivateKey = walletEntity.getChatPrivateKey();

        String pub_key = WalletUtil.getCosmosCompressPublickey(chatPrivateKey);

        String query_sign = WalletUtil.cosmosSign(chatPrivateKey, pub_key, address, timestamp, WalletUtil.MCC_COIN);


        SignInfo info = new SignInfo();
        info.timestamp = timestamp;
        info.localpart = address;

        info.pub_key = pub_key;
        info.query_sign = query_sign;

        return info;
    }

    @Override
    public EvmosMyGroupList httpGetMyDeviceGroups(String account) {
        return ChatSdk.httpGetMyDeviceGroups(account);
    }

    @Override
    public EvmosGroupDataBean httpGetDeviceGroupData(String deviceGroupId) {
        return ChatSdk.httpGetDeviceGroupData(deviceGroupId);
    }

    @Override
    public EvmosMyGroupDataBean httpGetSomeGroupData(String account, String deviceGroupId) {
        return ChatSdk.httpGetSomeGroupData(account, deviceGroupId);
    }

    @Override
    public EvmosDaoParams httpGetDaoParams(Context context, String fromAddr, String coinName) {
        if (TextUtils.isEmpty(coinName)) {
            coinName = context.getString(R.string.default_token_name2);
        }
        AssertBean assertBean = WalletDBUtil.getInstent(context).getWalletAssets(WalletUtil.MCC_COIN, coinName);
        int decimal = 18;
        if (null != assertBean) {
            decimal = assertBean.getDecimal();
        }
        EvmosDaoParams daoParams = ChatSdk.httpGetDaoParams();
        if (daoParams == null) {
            daoParams = new EvmosDaoParams();
            daoParams.setStatus(0);
            daoParams.setInfo("get daoParams is null");
        }
        EvmosMyGroupList groupInfo = ChatSdk.httpGetMyDeviceGroups(fromAddr);
        if (groupInfo != null && groupInfo.isSuccess()) {
            daoParams.freezeNum = null != groupInfo.data ? groupInfo.data.getFreezePower() : "0";
        } else {
            daoParams.setStatus(0);
            String errorInfo = groupInfo != null ? groupInfo.getInfo() : "get group info fail";
            daoParams.setInfo(errorInfo);
        }
        EvmosOneBalanceBean result = ChatSdk.getOneBalance(fromAddr, coinName);
        if (result != null && result.isSuccess()) {
            String tenNum = result.getBalance(decimal);
            if (TextUtils.isEmpty(tenNum)) {
                tenNum = "0";
            }
            daoParams.balance = tenNum;
        } else {
            daoParams.setStatus(0);
            String errorInfo = result != null ? result.getInfo() : "get balance fail";
            daoParams.setInfo(errorInfo);
        }
        return daoParams;
    }


    @SuppressLint("CheckResult")
    @Override
    public void createDeviceGroup(Context context, String fromAddr, String groupId, String deviceRatio,
                                  String salaryRatio, String burnAmount, String clusterName, String freezeAmount,
                                  List<DeviceGroupMember> members, TranslationListener callBack) {
        WalletEntity walletEntity = WalletDBUtil.getInstent(context).getWalletInfoByAddress(fromAddr, WalletUtil.MCC_COIN);
        if (null == walletEntity) {
            String errorinfo = context.getString(R.string.no_found_wallet_info);
            ToastUtil.showToast(errorinfo);
            if (null != callBack) {
                callBack.onFail(errorinfo);
            }
            return;
        }
        String dstCoinName = context.getString(R.string.default_token_name2);
        AssertBean assertBean = WalletDBUtil.getInstent(context).getWalletAssets(WalletUtil.MCC_COIN, dstCoinName);
        int decimal = 18;
        if (null != assertBean) {
            decimal = assertBean.getDecimal();
        }
        String bigburnAmount = AllUtils.getBigDecimalValue(burnAmount, decimal);
        String chatAddress = walletEntity.getChatAddress();
        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }
        TranslationControlDialog dialog = new TranslationControlDialog(context, true);
        dialog.showLoadingDialog();
        mRpcApi.getEvmosGateway(SpUtil.getNodeNoSegm()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(evmosGatewayBean -> {
                    if (evmosGatewayBean != null && evmosGatewayBean.isSuccess() &&
                            evmosGatewayBean.data != null && !TextUtils.isEmpty(evmosGatewayBean.data.gateway_address)) {
                        String gateWayAddr = evmosGatewayBean.data.gateway_address;
                        Observable<EvmosSeqGasBean> gasObservable = mRpcApi.getCreateDeviceGroupGas(fromAddr, gateWayAddr, groupId, deviceRatio, salaryRatio, bigburnAmount,
                                chatAddress, clusterName, freezeAmount, members);
                        dialog.doTranslate(walletEntity, context.getString(R.string.wallet_create_device_group_title),
                                burnAmount, "", gasObservable, new TranslationControlDialog.TransferListener() {
                                    @Override
                                    public byte[] signData() {
                                        return ChatSdk.signDaoCreateCluster(gateWayAddr, groupId, chatAddress, clusterName,
                                                deviceRatio, salaryRatio, bigburnAmount, freezeAmount, members);
                                    }

                                    @Override
                                    public void onFail(String errorInfo) {
                                        if (null != callBack) {
                                            callBack.onFail(errorInfo);
                                        }
                                    }

                                    @Override
                                    public void onSubmitSuccess() {
                                        if (null != callBack) {
                                            callBack.onSubmitSuccess();
                                        }
                                    }

                                    @Override
                                    public void onTransSuccess() {
                                        if (null != callBack) {
                                            callBack.onTransSuccess();
                                        }
                                    }
                                });

                    } else {
                        dialog.destroy();
                        String errorInfo = evmosGatewayBean != null ? evmosGatewayBean.getInfo() : "get gateWay info error";
                        ToastUtil.showToast(errorInfo);
                        if (null != callBack) {
                            callBack.onFail(errorInfo);
                        }
                    }
                }, throwable -> {
                    dialog.destroy();
                    throwable.printStackTrace();
                    if (null != callBack) {
                        callBack.onFail(throwable + ":" + throwable.getMessage());
                    }
                });

    }

    @Override
    public void addDeviceMembers(Context context, String fromAddr, String groupId, List<DeviceGroupMember> members, TranslationListener callBack) {
        ITransferControlApi controlApi = new TransferControlApi();
        controlApi.addDeviceMembers(context, fromAddr, groupId, members, callBack);
    }

    @Override
    public void changeGroupId(Context context, String fromAddr, String groupId, String newGroupId, TranslationListener callBack) {
        ITransferControlApi transferControlApi = new TransferControlApi();
        transferControlApi.changeGroupId(context, fromAddr, groupId, newGroupId, callBack);
    }

    @Override
    public String getFingerPayKey(Context context) {
        return AllUtils.getFingerPayKey(context);
    }

    @Override
    public void changeDeviceGroupName(Context context, String fromAddr, String groupId, String newGroupName, TranslationListener callBack) {
        ITransferControlApi transferControlApi = new TransferControlApi();
        transferControlApi.changeDeviceGroupName(context, fromAddr, groupId, newGroupName, callBack);
    }

    private MyProgressDialog mProgressDialog;

    private void showLoadingDialog() {
        Context context = mActivityRf.get();
        if (null == context) {
            return;
        }
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        mProgressDialog = new MyProgressDialog(context, "");
        mProgressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }



    
    
    private RpcApi mRpcApi;


    public Observable<EvmosSeqGasBean> getChatPayGas(String fromAddr, String toAddress, String coinName,
                                                     String amountBigNum) {
        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }
        if (TextUtils.isEmpty(coinName)) {
            coinName = BuildConfig.EVMOS_FAKE_UNINT.toLowerCase();
        }
        return mRpcApi.getEvmosChatPayGas(fromAddr, toAddress, coinName, amountBigNum).compose(RxSchedulers.io_main());
    }

    public void goChatPay(EvmosSeqGasBean bean, String fromAddr, String toAddress, String coinName,
                          String amountBigNum, WalletEntity wallet,
                          String pwd) {
        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }
        if (TextUtils.isEmpty(coinName)) {
            coinName = BuildConfig.EVMOS_FAKE_UNINT.toLowerCase();
        }
        showLoadingDialog();
        String finalCoinName = coinName;
        signChatPay(fromAddr, amountBigNum, toAddress, bean.seqAccount, bean.gas.getGasAmount(),
                bean.gas.getGasLimit(), wallet, pwd, finalCoinName).concatMap((Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>) evmosSignResult -> {
                    if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                        return mRpcApi.submitEvmosTransfer(evmosSignResult.Data);
                    } else {
                        throw new Exception(evmosSignResult.getInfo());
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
                            String errorInfo = data != null ? data.getInfo() : "chat pay fail";
                            ToastUtil.showToast(errorInfo);
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


    
    private Observable<EvmosSignResult> signChatPay(String address, String amountBigNum, String toAddress,
                                                    EvmosSeqAcountBean.Data seqAccountBean,
                                                    final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd,
                                                    final String tokenName) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                String amount = amountBigNum;

                
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

                String coinName = tokenName;
                LogUtil.i("address=" + address + ", " + "toAddress=" + toAddress + ", " + amount + ",coinName=" + coinName);
                byte[] signByte = {};
                signByte = ChatSdk.signChatSendGift(address, toAddress, amount, coinName);
                
                
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


    private int mUseTime;

    private void checkTxResult(EvmosTransferResultBean transferResult, boolean retry, int times) {
        if (retry) {
            mUseTime += times;
        } else {
            showLoadingDialog();
            mUseTime = times;
        }
        Disposable disposable = Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
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
                            } else if (evmosHxResultBean.isTxFail(mUseTime)) {
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = false;
                                result.info = getString(R.string.transfer_fail);
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

    @SuppressLint("ResourceType")
    private String getString(int strResId) {
        Context context = mActivityRf.get();
        if (null == context) {
            return "";
        }
        return context.getString(strResId);
    }


    
    private void postTxReslult(EvmosPledgeResultBean resultBean) {
        dismissLoadingDialog();
        Consumer<Boolean> consumer = mCallBackRf.get();
        if (null == consumer) {
            LogUtil.i("post pay consume = null");
            return;
        }

        if (null != resultBean) {
            LogUtil.i("post pay Result :" + resultBean.success);
            consumer.accept(resultBean.success);
        } else {
            LogUtil.i("post pay fail null");
            consumer.accept(false);
        }
    }


}
