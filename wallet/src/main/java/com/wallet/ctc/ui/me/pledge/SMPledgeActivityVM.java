

package com.wallet.ctc.ui.me.pledge;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosChatBurnRatio;
import com.wallet.ctc.model.blockchain.EvmosChatInfoBean;
import com.wallet.ctc.model.blockchain.EvmosChatParamsBean;
import com.wallet.ctc.model.blockchain.EvmosChatToBurn;
import com.wallet.ctc.model.blockchain.EvmosChatUnPledgeAvailable;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.EvmosHxResultBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeConfigBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTotalPledgeBean;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.SmOuterChartInfo;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
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


public class SMPledgeActivityVM extends BaseViewModel {
    private RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    private int mDecimal = 18;

    public MutableLiveData<EvmosPledgeConfigBean> mConfigLD;

    public MutableLiveData<EvmosPledgeResultBean> mResultLD;
    public MutableLiveData<EvmosPledgeResultBean> mPledgeResultLD;
    public MutableLiveData<EvmosPledgeResultBean> mHashPledgeResultLD;
    public MutableLiveData<EvmosPledgeResultBean> mUnPledgeResultLD;
    public MutableLiveData<EvmosPledgeResultBean> mLingQuResultLD;

    public static final int TYPE_REGISTE = 0;
    public static final int TYPE_PLEDGE = 1;
    public static final int TYPE_UN_PLEDGE = 2;
    public static final int TYPE_WITHDRAW = 3;
    public static final int TYPE_HASH_PLEDGE = 4;
    private int mType = TYPE_PLEDGE;


    
    public SMPledgeActivityVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mRpcApi = new RpcApi();
        mConfigLD = new MutableLiveData<>();
        mResultLD = new MutableLiveData<>();
        mUnPledgeResultLD = new MutableLiveData<>();
        mPledgeResultLD = new MutableLiveData<>();
        mHashPledgeResultLD = new MutableLiveData<>();
        mLingQuResultLD = new MutableLiveData<>();

        
        List<AssertBean> assets = WalletDBUtil.getInstent(application).getMustWallet(WalletUtil.MCC_COIN);
        mDecimal = assets.get(0).getDecimal();
        if (mDecimal == 0) {
            mDecimal = 18;
        }
    }

    private MutableLiveData<SmOuterChartInfo> mSmOuterChartInfoMutableLiveData = new MutableLiveData<>();
    public LiveData<SmOuterChartInfo> mSmOuterChartInfoLiveData = mSmOuterChartInfoMutableLiveData;

    public void getChartInfo(String gather_type, String page_size) {
        mDisposable.add(mRpcApi.getChartInfo(gather_type, page_size).subscribeWith(new NextSubscriber<SmOuterChartInfo>() {
            @Override
            public void dealData(SmOuterChartInfo value) {
                mSmOuterChartInfoMutableLiveData.postValue(value);
            }
        }));
    }

    private MutableLiveData<String> mAvailableMutableLiveData = new MutableLiveData<>();
    public LiveData<String> mAvailableLiveData = mAvailableMutableLiveData;

    public void getEvmosChatUnPledgeAvailable(String address) {
        showLoadingDialog("");
        mDisposable.add(mRpcApi.getEvmosChatUnPledgeAvailable(address).subscribeWith(new NextSubscriber<EvmosChatUnPledgeAvailable>() {
            @Override
            public void dealData(EvmosChatUnPledgeAvailable value) {
                dismissLoadingDialog();
                mAvailableMutableLiveData.postValue(value.data);
            }

            @Override
            protected void dealError(Throwable e) {
                dismissLoadingDialog();
                super.dealError(e);
            }
        }));
    }

    private MutableLiveData<String> mHashPldgeNumMutableLiveData = new MutableLiveData<>();
    public LiveData<String> mHashPldgeNumLiveData = mHashPldgeNumMutableLiveData;

    public void getHashPldgeNum(String address, String name) {
        showLoadingDialog("");
        mDisposable.add(mRpcApi.getEvmosOneBalance(address, name).subscribeWith(new NextSubscriber<EvmosOneBalanceBean>() {
            @Override
            public void dealData(EvmosOneBalanceBean value) {
                dismissLoadingDialog();
                if (value.isSuccess() && null != value.data) {
                    String balance = getTenDecimalValue(value.data);
                    mHashPldgeNumMutableLiveData.postValue(balance);
                } else {
                    mHashPldgeNumMutableLiveData.postValue("");
                }
            }

            @Override
            protected void dealError(Throwable e) {
                super.dealError(e);
                dismissLoadingDialog();
            }
        }));
    }

    
    public void getConfig(String address, String phoneStartNum) {
        showLoadingDialog("");
        
        Observable.zip(
                        
                        mRpcApi.getEvmosChatInfo(address),
                        
                        mRpcApi.getEvmosTotalPledge(address),
                        
                        mRpcApi.getEvmosGateway(phoneStartNum),
                        
                        mRpcApi.getEvmosChatParams(),
                        mRpcApi.getToBurn(address),
                        mRpcApi.getBurnRatio(),

                        (chatInfoBean, evmosTotalPledgeBean, evmosGatewayBean, evmosPledgeParamsBean, toBurn, ratio) -> {

                            boolean isSuccess = chatInfoBean.isSuccess() && evmosTotalPledgeBean.isSuccess()
                                    && evmosGatewayBean.isSuccess() && evmosPledgeParamsBean.isSuccess() && toBurn.isSuccess() && ratio.isSuccess() && null != evmosGatewayBean;
                            if (!isSuccess) {
                                String errorInfo = getErrorInfo(chatInfoBean, evmosTotalPledgeBean, evmosGatewayBean, evmosPledgeParamsBean, toBurn, ratio);
                                throw new Exception(errorInfo);
                            }
                            
                            return zipConfig(chatInfoBean, evmosTotalPledgeBean, evmosGatewayBean, evmosPledgeParamsBean, toBurn, ratio);
                        }).concatMap((Function<EvmosPledgeConfigBean, ObservableSource<EvmosPledgeConfigBean>>) evmosPledgeConfigBean ->
                        mRpcApi.getEvmosOneBalance(address, evmosPledgeConfigBean.tokenNameDestory)
                                .map(evmosOneBalanceBean -> {
                                    if (evmosOneBalanceBean != null && evmosOneBalanceBean.isSuccess()) {
                                        String balance = getTenDecimalValue(evmosOneBalanceBean.data);
                                        evmosPledgeConfigBean.tokenBalance = balance;
                                    }
                                    return evmosPledgeConfigBean;
                                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EvmosPledgeConfigBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosPledgeConfigBean data) {
                        if (null != data && data.isSuccess) {
                            mConfigLD.setValue(data);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtil.showToast(e.getMessage());
                        dismissLoadingDialog();
                    }

                    @Override
                    public void onComplete() {
                        dismissLoadingDialog();
                    }
                });
    }


    
    private EvmosPledgeConfigBean zipConfig(EvmosChatInfoBean chatInfoBean, EvmosTotalPledgeBean evmosTotalPledgeBean,
                                            EvmosGatewayBean evmosGatewayBean, EvmosChatParamsBean evmosPledgeParamsBean,
                                            EvmosChatToBurn toBurn, EvmosChatBurnRatio ratio) {
        EvmosPledgeConfigBean config = new EvmosPledgeConfigBean();
        config.ratio = AllUtils.getTenDecimalValue(ratio.data.burn_get_hash, 18, 4);

        
        if (null != chatInfoBean.data && !TextUtils.isEmpty(chatInfoBean.data.from_address)) {
            config.isHasRegisted = true;
            
            config.myMobileList = chatInfoBean.data.mobile;
            config.level = chatInfoBean.data.pledge_level;
        } else {
            config.isHasRegisted = false;
        }


        
        String hasPledgeBigNum = evmosTotalPledgeBean.data.all_pledge_amount.amount;
        BigDecimal hasPledgeNum = new BigDecimal(getTenDecimalValue(hasPledgeBigNum));
        config.totalHasPledgeNum = hasPledgeNum.toPlainString();

        
        String prePledgeBigNum = evmosTotalPledgeBean.data.pre_pledge_amount.amount;
        config.prePledgeNum = getTenDecimalValue(prePledgeBigNum);

        
        String remainPledgeBigNum = evmosTotalPledgeBean.data.remain_pledge_amount.amount;
        config.remainPledgeNum = getTenDecimalValue(remainPledgeBigNum);
        
        config.decimal = mDecimal;
        config.delegations = evmosTotalPledgeBean.data.delegations;

        
        String canWithDrawBigNum = evmosTotalPledgeBean.data.all_can_withdraw.amount;
        config.canWithdrawNum = getTenDecimalValue(canWithDrawBigNum);

        
        config.tokenName = evmosTotalPledgeBean.data.all_pledge_amount.denom;
        config.tokenNameDestory = evmosPledgeParamsBean.data.min_register_burn_amount.denom;


        
        String allFee = evmosPledgeParamsBean.data.getAllFee();
        config.pledgeFee = new BigDecimal(allFee).multiply(new BigDecimal(100)).setScale(2).stripTrailingZeros().toPlainString();

        
        String setMinBigNum = evmosPledgeParamsBean.data.min_register_burn_amount.amount;
        String deMint = toBurn.data.amount;

        
        BigDecimal minNum = new BigDecimal(getTenDecimalValue(setMinBigNum)).subtract(new BigDecimal(getTenDecimalValue(deMint)));
        
        if (minNum.compareTo(new BigDecimal("0")) < 0) {
            config.minPledgeNum = "0";
        } else {
            config.minPledgeNum = minNum.toPlainString();
        }


        
        config.pledgeAddress = evmosGatewayBean.data.gateway_address;
        
        config.phoneStartList = new ArrayList<>();
        for (int i = 0; i < evmosGatewayBean.data.gateway_num.size(); i++) {
            config.phoneStartList.add(evmosGatewayBean.data.gateway_num.get(i).number_index);
        }

        config.isSuccess = true;

        return config;

    }

    
    private String getTenDecimalValue(String bigNum) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        return new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, mDecimal)), 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    
    private String getErrorInfo(EvmosChatInfoBean chatInfoBean, EvmosTotalPledgeBean evmosTotalPledgeBean,
                                EvmosGatewayBean evmosGatewayBean, EvmosChatParamsBean evmosPledgeParamsBean, EvmosChatToBurn toBurn, EvmosChatBurnRatio ratio) {
        String errorInfo = "";
        if (null == evmosGatewayBean) {
            errorInfo = "";
        } else if (!TextUtils.isEmpty(chatInfoBean.getInfo())) {
            errorInfo = chatInfoBean.getInfo();
        } else if (!TextUtils.isEmpty(evmosTotalPledgeBean.getInfo())) {
            errorInfo = evmosTotalPledgeBean.getInfo();
        } else if (!TextUtils.isEmpty(evmosGatewayBean.getInfo())) {
            errorInfo = evmosGatewayBean.getInfo();
        } else if (!TextUtils.isEmpty(evmosPledgeParamsBean.getInfo())) {
            errorInfo = evmosPledgeParamsBean.getInfo();
        } else if (!TextUtils.isEmpty(toBurn.getInfo())) {
            errorInfo = toBurn.getInfo();
        } else if (!TextUtils.isEmpty(ratio.getInfo())) {
            errorInfo = ratio.getInfo();
        }
        return errorInfo;
    }

    private Observable<EvmosSignResult> hashPledgeSign(String address, String number, String nodeAddress,
                                                       EvmosSeqAcountBean.Data seqAccountBean,
                                                       final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd,
                                                       final String tokenName, String delegateAddress) {
        return Observable.create((ObservableOnSubscribe<EvmosSignResult>) emitter -> {
            String amount = new BigDecimal(number).multiply(new BigDecimal(Math.pow(10, mDecimal))).stripTrailingZeros().toPlainString();

            
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
            mType = TYPE_HASH_PLEDGE;
            byte[] signByte = ChatSdk.signChatDelegate(delegateAddress, nodeAddress, amount, coinName);
            
            
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
        }).subscribeOn(Schedulers.io());
    }

    private final MutableLiveData<EvmosSeqGasBean> mHashPledgeGasMutableLiveData = new MutableLiveData<>();
    public LiveData<EvmosSeqGasBean> mHashPledgeGasLiveData = mHashPledgeGasMutableLiveData;

    public void getHashPledgeGas(String address, String num, EvmosPledgeConfigBean config, String delegatorAddr) {
        showLoadingDialog("");
        mDisposable.add(mRpcApi.getEvmosPledgeGas(address, delegatorAddr, config.pledgeAddress, config.tokenName, num)
                .subscribeWith(new NextSubscriber<EvmosSeqGasBean>() {
                    @Override
                    public void dealData(EvmosSeqGasBean value) {
                        dismissLoadingDialog();
                        value.mHashPledgeNum = num;
                        mHashPledgeGasMutableLiveData.postValue(value);
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        dismissLoadingDialog();
                        super.dealError(e);
                    }
                }));
    }

    
    public void doHashPledge(EvmosSeqGasBean bean, String address, String num, EvmosPledgeConfigBean config, WalletEntity wallet,
                             String pwd, String delegatorAddr) {
        showLoadingDialog("");
        hashPledgeSign(address, num, config.pledgeAddress, bean.seqAccount, bean.gas.getGasAmount(),
                bean.gas.getGasLimit(), wallet, pwd, config.tokenName, delegatorAddr)
                .concatMap((Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>) evmosSignResult -> {
                    if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                        return mRpcApi.submitEvmosTransfer(evmosSignResult.Data);
                    } else {
                        throw new Exception(evmosSignResult.getInfo());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
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
                            String errorInfo = data != null ? data.getInfo() : "submit tx hash fail";
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

    
    public void doPledge(String address, String num, final String phonePrex, EvmosPledgeConfigBean config, WalletEntity wallet,
                         String pwd, String delegatorAddr) {
        
        doPledggeRegist(address, num, phonePrex, config, wallet, pwd, delegatorAddr);
    }

    public void doPledge(EvmosSeqGasBean bean, String address, String num, final String phonePrex, EvmosPledgeConfigBean config, WalletEntity wallet,
                         String pwd, String delegatorAddr) {
        
        doPledggeRegist(bean, address, num, phonePrex, config, wallet, pwd, delegatorAddr);
    }

    private final MutableLiveData<EvmosSeqGasBean> mWithdrawGasMutableLiveData = new MutableLiveData<>();
    public LiveData<EvmosSeqGasBean> mWithdrawGasLiveData = mWithdrawGasMutableLiveData;

    public void getWithdrawGas(String address, EvmosPledgeConfigBean config) {
        showLoadingDialog("");
        mDisposable.add(mRpcApi.getEvmosWithdrawGas(address, config.pledgeAddress).subscribeWith(new NextSubscriber<EvmosSeqGasBean>() {
            @Override
            public void dealData(EvmosSeqGasBean value) {
                dismissLoadingDialog();
                mWithdrawGasMutableLiveData.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                dismissLoadingDialog();
                super.dealError(e);
            }
        }));
    }

    
    public void doWithdraw(EvmosSeqGasBean bean, String address, EvmosPledgeConfigBean config, WalletEntity wallet,
                           String pwd) {
        showLoadingDialog("");
        withdrawSign(address, config.pledgeAddress, bean.seqAccount, bean.gas.getGasAmount(),
                bean.gas.getGasLimit(), wallet, pwd, config.tokenName).concatMap((Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>) evmosSignResult -> {
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
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosTransferResultBean data) {
                        if (null != data && data.isSuccess()) {
                            checkTxResult(data, false, 1);
                        } else {
                            dismissLoadingDialog();
                            String errorInfo = data != null ? data.getInfo() : "submit tx hash fail";
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

    private final MutableLiveData<EvmosSeqGasBean> mUnPledgeGasMutableLiveData = new MutableLiveData<>();
    public LiveData<EvmosSeqGasBean> mUnPledgeGasLiveData = mUnPledgeGasMutableLiveData;

    public void getUnPledgeGas(String address, String nodeAddr, String num, EvmosPledgeConfigBean config) {
        showLoadingDialog("");
        String amountBigNum = new BigDecimal(num).multiply(new BigDecimal(Math.pow(10, mDecimal))).stripTrailingZeros().toPlainString();
        mDisposable.add(mRpcApi.getEvmosUnPledgeGas(address, nodeAddr, config.tokenName, amountBigNum).subscribeWith(new NextSubscriber<EvmosSeqGasBean>() {
            @Override
            public void dealData(EvmosSeqGasBean value) {
                dismissLoadingDialog();
                value.mUnPledgeHashNum = num;
                mUnPledgeGasMutableLiveData.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                dismissLoadingDialog();
                super.dealError(e);
            }
        }));

    }

    
    public void doUnPledge(EvmosSeqGasBean bean, String address, String nodeAddr, String num, EvmosPledgeConfigBean config, WalletEntity wallet,
                           String pwd) {
        showLoadingDialog("");
        unPledgeSign(address, num, nodeAddr, bean.seqAccount, bean.gas.getGasAmount(),
                bean.gas.getGasLimit(), wallet, pwd, config.tokenName).concatMap((Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>) evmosSignResult -> {
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
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(EvmosTransferResultBean data) {
                        if (null != data && data.isSuccess()) {
                            checkTxResult(data, false, 1);
                        } else {
                            dismissLoadingDialog();
                            String errorInfo = data != null ? data.getInfo() : "submit tx hash fail";
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


    private final MutableLiveData<EvmosSeqGasBean> mDetialMutableLiveData = new MutableLiveData<>();
    public LiveData<EvmosSeqGasBean> mLiveData = mDetialMutableLiveData;

    public void doPledggeRegistGas(String address, String num, String phonePrex, EvmosPledgeConfigBean config, WalletEntity wallet,
                                   String pwd, String delegatorAddr) {

        showLoadingDialog("");
        mDisposable.add(getEvmosAccountGas(!config.isHasRegisted, address, config.pledgeAddress, config.tokenNameDestory, phonePrex, num, delegatorAddr).subscribeWith(new NextSubscriber<EvmosSeqGasBean>() {
            @Override
            public void dealData(EvmosSeqGasBean value) {
                dismissLoadingDialog();
                mDetialMutableLiveData.postValue(value);
            }

            @Override
            protected void dealError(Throwable e) {
                dismissLoadingDialog();
                super.dealError(e);
            }
        }));
    }

    void doPledggeRegist(EvmosSeqGasBean bean, String address, String num, String phonePrex, EvmosPledgeConfigBean config, WalletEntity wallet,
                         String pwd, String delegatorAddr) {
        showLoadingDialog("");
        registeOrPledgeSign(!config.isHasRegisted, address, num, config.pledgeAddress, bean.seqAccount, bean.gas.getGasAmount(),
                bean.gas.getGasLimit(), wallet, pwd, config.tokenNameDestory, phonePrex, delegatorAddr)
                .concatMap((Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>) evmosSignResult -> {
                    if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                        return mRpcApi.submitEvmosTransfer(evmosSignResult.Data);
                    } else {
                        throw new Exception(evmosSignResult.getInfo());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
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
                            String errorInfo = data != null ? data.getInfo() : "submit tx hash fail";
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


    
    private void doPledggeRegist(String address, String num, String phonePrex, EvmosPledgeConfigBean config, WalletEntity wallet,
                                 String pwd, String delegatorAddr) {
        showLoadingDialog("");
        getEvmosAccountGas(!config.isHasRegisted, address, config.pledgeAddress, config.tokenNameDestory, phonePrex, num, delegatorAddr)

                .concatMap((Function<EvmosSeqGasBean, ObservableSource<EvmosSignResult>>) evmosSeqGasBean -> {
                    if (null != evmosSeqGasBean && evmosSeqGasBean.isSuccess()) {
                        return registeOrPledgeSign(!config.isHasRegisted, address, num, config.pledgeAddress, evmosSeqGasBean.seqAccount, evmosSeqGasBean.gas.getGasAmount(),
                                evmosSeqGasBean.gas.getGasLimit(), wallet, pwd, config.tokenNameDestory, phonePrex, delegatorAddr);
                    } else {
                        throw new Exception(evmosSeqGasBean.getInfo());
                    }
                }).concatMap((Function<EvmosSignResult, ObservableSource<EvmosTransferResultBean>>) evmosSignResult -> {
                    if (evmosSignResult != null && evmosSignResult.isSuccess()) {
                        return mRpcApi.submitEvmosTransfer(evmosSignResult.Data);
                    } else {
                        throw new Exception(evmosSignResult.getInfo());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<>() {
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
                            String errorInfo = data != null ? data.getInfo() : "submit tx hash fail";
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
        Observable.timer(times, TimeUnit.SECONDS).subscribe(time -> {
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
                                
                                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                                result.success = true;
                                postTxReslult(result);
                            } else if (evmosHxResultBean.isTxFail(mUseTime)) {
                                
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
        if (mType == TYPE_HASH_PLEDGE) {
            
            mHashPledgeResultLD.setValue(resultBean);
        } else if (mType == TYPE_PLEDGE) {
            
            mPledgeResultLD.setValue(resultBean);
        } else if (mType == TYPE_REGISTE) {
            
            mResultLD.setValue(resultBean);
        } else if (mType == TYPE_UN_PLEDGE) {
            
            mUnPledgeResultLD.setValue(resultBean);
        } else if (mType == TYPE_WITHDRAW) {
            
            mLingQuResultLD.setValue(resultBean);
        }
    }

    
    private Observable<EvmosSeqGasBean> getEvmosAccountGas(boolean isRegist, String fromAddr, String nodeAddr,
                                                           final String coinName, String phoneStart, String number, String delegatorAddr) {
        String amountBigNum = new BigDecimal(number).multiply(new BigDecimal(Math.pow(10, mDecimal))).stripTrailingZeros().toPlainString();
        if (isRegist) {
            
            return mRpcApi.getEvmosRegistGas(fromAddr, nodeAddr, coinName, phoneStart, amountBigNum);
        } else {
            
            return mRpcApi.getEvmosBurnGas(fromAddr, delegatorAddr, nodeAddr, coinName, amountBigNum);
        }
    }


    
    private Observable<EvmosSignResult> registeOrPledgeSign(boolean isRegsite, String address, String number, String nodeAddress,
                                                            EvmosSeqAcountBean.Data seqAccountBean,
                                                            final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd,
                                                            final String tokenName, String phonePrefix, String delegateAddress) {
        return Observable.create((ObservableOnSubscribe<EvmosSignResult>) emitter -> {
            String amount = new BigDecimal(number).multiply(new BigDecimal(Math.pow(10, mDecimal))).stripTrailingZeros().toPlainString();

            
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
            byte[] signByte = {};
            if (isRegsite) {
                mType = TYPE_REGISTE;
                signByte = ChatSdk.signResgister(address, nodeAddress, amount, coinName, phonePrefix);
            } else {
                mType = TYPE_PLEDGE;
                signByte = ChatSdk.SignBurnGetMedal(address, delegateAddress, amount, coinName, nodeAddress);
            }
            
            
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
        }).subscribeOn(Schedulers.io());
    }


    
    private Observable<EvmosSignResult> unPledgeSign(String address, String number, String nodeAddress,
                                                     EvmosSeqAcountBean.Data seqAccountBean,
                                                     final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd,
                                                     final String tokenName) {
        return Observable.create(new ObservableOnSubscribe<EvmosSignResult>() {
            @Override
            public void subscribe(ObservableEmitter<EvmosSignResult> emitter) throws Exception {
                String amount = new BigDecimal(number).multiply(new BigDecimal(Math.pow(10, mDecimal))).stripTrailingZeros().toPlainString();

                
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
                byte[] signByte = {};
                mType = TYPE_UN_PLEDGE;
                signByte = ChatSdk.signChatUnDelegate(address, amount, coinName);
                
                
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

    
    private Observable<EvmosSignResult> withdrawSign(String address, String nodeAddress,
                                                     EvmosSeqAcountBean.Data seqAccountBean,
                                                     final String gasAmount, final String gasLimit, WalletEntity wallet, String pwd,
                                                     final String tokenName) {
        return Observable.create((ObservableOnSubscribe<EvmosSignResult>) emitter -> {
            
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
            byte[] signByte = {};
            mType = TYPE_WITHDRAW;
            signByte = ChatSdk.signChatWithdraw(address);
            
            
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
        }).subscribeOn(Schedulers.io());
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }


}
