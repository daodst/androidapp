package com.wallet.ctc.ui.me.chain_bridge2;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgePreOrdersBean;
import com.wallet.ctc.model.blockchain.ChainBridgeServiceStatusBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.HexUtils;

import java.util.concurrent.TimeUnit;

import common.app.RxBus;
import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.my.RxNotice;
import common.app.pojo.NameIdBean;
import common.app.utils.AllUtils;
import common.app.utils.NetWorkUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChainBridgeVM2 extends BaseViewModel {

    private CompositeDisposable mDisposable;
    public MutableLiveData<String> mBalanceLD;
    public MutableLiveData<String> mErrorLD;
    public MutableLiveData<NameIdBean> mErrorTipLD;
    public MutableLiveData<Object> mSuccessLD;
    private RpcApi mRpcApi;


    
    public ChainBridgeVM2(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mRpcApi = new RpcApi();
        mBalanceLD = new MutableLiveData<>();
        mSuccessLD = new MutableLiveData<>();
        mErrorLD = new MutableLiveData<>();
        mErrorTipLD = new MutableLiveData<>();
    }

    
    private String[] balanceKey = {""}; 
    public void getBalance(WalletEntity wallet, AssertBean assertBean, boolean showLoading) {
        if (null == wallet || null == assertBean) {
            return;
        }
        int walletType = assertBean.getType();
        if (walletType == MCC_COIN) {
            String contract = assertBean.getContract();
            if (TextUtils.isEmpty(contract)) {
                String address = wallet.getAllAddress();
                getMccBalance(address, assertBean.getShort_name(), assertBean, showLoading);
            } else {
                String address = wallet.getDefaultAddress();
                getEthBalance(address, walletType, assertBean, showLoading);
            }
        } else {
            String address = wallet.getAllAddress();
            getEthBalance(address, walletType, assertBean, showLoading);
        }
    }

    private String getKey(String address, String contract, int walletType) {
        return address+contract+walletType;
    }

    
    private void getMccBalance(String address, String coinName, AssertBean assertBean, boolean showLoading) {
        balanceKey[0] = getKey(address, coinName, assertBean.getType());
        if(showLoading){
            showLoadingDialog("");
        }
        Disposable disposable = mRpcApi.getEvmosOneBalance(address, coinName)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<EvmosOneBalanceBean>() {
                    @Override
                    public void dealData(EvmosOneBalanceBean value) {
                        dismissLoadingDialog();
                        if (null != value) {
                            String remain = value.getBalance(assertBean.getDecimal());
                            postBalance(remain, getKey(address, coinName, assertBean.getType()));
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        dismissLoadingDialog();
                        showToast(e.getMessage());
                    }
                });
        mDisposable.add(disposable);
    }

    
    private void getEthBalance(String address, int walletType, AssertBean assertBean, boolean showLoading) {
        String contract = assertBean.getContract();
        balanceKey[0] = getKey(address,contract,walletType);
        if(showLoading){
            showLoadingDialog("");
        }
        Disposable disposable =mRpcApi.getEthBanlance(address, contract, walletType)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<FilBalanceBean>() {
                    @Override
                    public void dealData(FilBalanceBean value) {
                        dismissLoadingDialog();
                        if (null != value && !TextUtils.isEmpty(value.getResult())) {
                            String bigNum = HexUtils.hextoTen(value.getResult());
                            String balance = AllUtils.getTenDecimalValue(bigNum, assertBean.getDecimal(), 6);
                            postBalance(balance, getKey(address,contract,walletType));
                        }
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        super.dealError(e);
                        dismissLoadingDialog();
                        showToast(e.getMessage());
                    }
                });
        mDisposable.add(disposable);
    }

    
    private void postBalance(String balance, String key) {
        if (!TextUtils.isEmpty(balanceKey[0]) && balanceKey[0].equals(key)) {
            mBalanceLD.setValue(balance);
        }
    }


    
    public void chainMap(WalletEntity fromWallet, WalletEntity toWallet, String amount, AssertBean fromAsset, AssertBean toAsset) {
        showLoadingDialog("");
        Disposable disposable = Observable.create(new ObservableOnSubscribe<ChainBridgePreOrdersBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChainBridgePreOrdersBean> emitter) throws Exception {
                String buyChain = ChatSdk.typeToChainName(fromWallet.getType());
                String sellChain = ChatSdk.typeToChainName(toWallet.getType());
                String bigAmount = AllUtils.getBigDecimalValue(amount, 18);
                ChainBridgePreOrdersBean preOrder = ChatSdk.crossOrderPreview(sellChain, buyChain, bigAmount);
                emitter.onNext(preOrder);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data->{
            dismissLoadingDialog();
            if (null != data && data.isSuccess() && data.isHasOrders()){
                
                mSuccessLD.setValue("1");
            } else {
                if (data == null){
                    showToast(getApplication().getString(R.string.get_data_fail));
                    return;
                }
                String suggestTip = data.getSuggestNumStrs(18);
                String errorInfo = getApplication().getString(R.string.chain_bridge_pre_order_error);
                if (!TextUtils.isEmpty(suggestTip)){
                    errorInfo = getApplication().getString(R.string.chain_bridge_preorder_suggest)+suggestTip;
                }
                mErrorLD.setValue(errorInfo);
            }
        }, throwable -> {
            dismissLoadingDialog();
            throwable.printStackTrace();
            showToast(throwable+":"+throwable.getMessage());
        });
        mDisposable.add(disposable);
    }

    
    private String checkNodeConnect() {
        StringBuilder stringBuilder = new StringBuilder();
        
        String mccNodeUrl = WalletUtil.getSmartRpcUrl(MCC_COIN);
        int netStatus = NetWorkUtils.checkUrlReachable(getApplication(), mccNodeUrl);
        if (netStatus == NetWorkUtils.HOST_UN_REACHABLE){
            
            stringBuilder.append(BuildConfig.EVMOS_FAKE_UNINT);
        }

        
        String bnbNodeUrl = WalletUtil.getSmartRpcUrl(BNB_COIN);
        netStatus =  NetWorkUtils.checkUrlReachable(getApplication(), bnbNodeUrl);
        if(netStatus == NetWorkUtils.HOST_UN_REACHABLE){
            if(TextUtils.isEmpty(stringBuilder)){
                stringBuilder.append("BSC");
            } else {
                stringBuilder.append(",").append("BSC");
            }
        }

        
        String ethNodeUrl = WalletUtil.getSmartRpcUrl(ETH_COIN);
        netStatus =  NetWorkUtils.checkUrlReachable(getApplication(), ethNodeUrl);
        if(netStatus == NetWorkUtils.HOST_UN_REACHABLE){
            if(TextUtils.isEmpty(stringBuilder)){
                stringBuilder.append("ETH");
            } else {
                stringBuilder.append(",").append("ETH");
            }
        }

        if (!TextUtils.isEmpty(stringBuilder)) {
            stringBuilder.append(getApplication().getString(R.string.rpc_node_unconnect_tip));
        }
        return stringBuilder.toString();
    }


    
    private void toOpenService() {
        RxNotice notice = new RxNotice(RxNotice.MSG_START_CHAIN_BRIDGE_TASK);
        notice.setData("1");
        RxBus.getInstance().post(notice);
    }

    private  Disposable timerDisposable;
    private void stopTimer(){
        if (null != timerDisposable){
            timerDisposable.dispose();
            timerDisposable = null;
        }
    }
    
    public void timerStartService() {
        stopTimer();
        showLoadingDialog(getApplication().getString(R.string.starting_please_wait));
        long startTime = System.currentTimeMillis();
        
        toOpenService();
        timerDisposable = Observable.interval(2, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(aLong -> {
                    ChainBridgeServiceStatusBean serviceStatus  = ChatSdk.serviceStatus();
                    if(serviceStatus != null && serviceStatus.isRunning()){
                        
                        dismissLoadingDialog();
                        showToast(getApplication().getString(R.string.start_success));
                        stopTimer();
                    } else {
                        
                        toOpenService();
                        if(System.currentTimeMillis() - startTime > 10000) {
                            dismissLoadingDialog();
                            
                            showToast(getApplication().getString(R.string.start_fail));
                            mErrorTipLD.postValue(new NameIdBean("2", getApplication().getString(R.string.chain_b_retry_start_alert), ""));
                            stopTimer();
                        }
                    }
                }, throwable -> {
                    dismissLoadingDialog();
                    throwable.printStackTrace();
                    stopTimer();
                });
        mDisposable.add(timerDisposable);
    }

    
    public void checkServiceStatus() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<NameIdBean>() {
            @Override
            public void subscribe(ObservableEmitter<NameIdBean> emitter) throws Exception {
                String errorCode = "0";
                String errorMsg = "";
                ChainBridgeServiceStatusBean serviceStatus  = ChatSdk.serviceStatus();
                if (serviceStatus == null || !serviceStatus.isRunning()){
                    
                    String errorInfo = checkNodeConnect();
                    if (!TextUtils.isEmpty(errorInfo)){
                        errorCode = "1";
                        errorMsg = errorInfo;
                    } else {
                        
                        errorCode = "2";
                        errorMsg = getApplication().getString(R.string.chain_b_service_no_run_alert);
                    }
                } else {
                    
                    ChatSdk.setChainBridgeClientAddr(getApplication(), false);

                    
                    if(serviceStatus.hasNodeConnectError()) {
                        
                        String errorInfo = checkNodeConnect();
                        if (!TextUtils.isEmpty(errorInfo)){
                            errorCode = "1";
                            errorMsg = errorInfo;
                        }
                    } else if(serviceStatus.hasMainOrderError()){
                        errorMsg = getApplication().getString(R.string.chain_b_order_has_error_alert);
                        errorCode = "3";
                    } else {
                        String otherError = serviceStatus.getOtherErrorInfo(getApplication());
                        if (!TextUtils.isEmpty(otherError)) {
                            errorCode = "4";
                            errorMsg = otherError;
                        }
                    }
                }
                emitter.onNext(new NameIdBean(errorCode, errorMsg, ""));
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(error->{
                    mErrorTipLD.setValue(error);
                }, throwable -> {
                    throwable.printStackTrace();
                });
        mDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}
