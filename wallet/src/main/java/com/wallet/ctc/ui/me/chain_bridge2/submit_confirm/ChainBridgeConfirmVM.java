package com.wallet.ctc.ui.me.chain_bridge2.submit_confirm;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainBridgeConfigBean;
import com.wallet.ctc.model.blockchain.ChainBridgeExConfirmBean;
import com.wallet.ctc.model.blockchain.ChainBridgePreOrdersBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.HexUtils;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.AllUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChainBridgeConfirmVM extends BaseViewModel {

    CompositeDisposable mDisposable;
    MutableLiveData<ChainBridgePreOrdersBean> mPreOrderLD;
    MutableLiveData<Long> mSubmitLD;
    MutableLiveData<String> mErrorLD;
    MutableLiveData<String> mBalanceLD;
    private RpcApi mRpcApi;
    
    public ChainBridgeConfirmVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mPreOrderLD = new MutableLiveData<>();
        mSubmitLD = new MutableLiveData<>();
        mErrorLD = new MutableLiveData<>();
        mBalanceLD = new MutableLiveData<>();
        mRpcApi = new RpcApi();
    }


    
    public void preOrder(WalletEntity fromWallet, WalletEntity toWallet, String amount, AssertBean fromExAsset, AssertBean toExAsset) {
        showLoadingDialog("");
        Disposable disposable = Observable.create(new ObservableOnSubscribe<ChainBridgePreOrdersBean>() {
                    @Override
                    public void subscribe(ObservableEmitter<ChainBridgePreOrdersBean> emitter) throws Exception {
                        String buyChain = ChatSdk.typeToChainName(fromWallet.getType());
                        String sellChain = ChatSdk.typeToChainName(toWallet.getType());
                        String bigAmount = AllUtils.getBigDecimalValue(amount, 18);
                        ChainBridgePreOrdersBean preOrder = ChatSdk.crossOrderPreview(sellChain, buyChain, bigAmount);
                        ChainBridgeConfigBean chainBridgeConfig = ChatSdk.depositInfo(ChatSdk.typeToChainName(toWallet.getType()));
                        preOrder.chainBridgeConfig = chainBridgeConfig;
                        emitter.onNext(preOrder);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    dismissLoadingDialog();
                    if (null != data && data.isSuccess() && data.isHasOrders()){
                        
                        mPreOrderLD.setValue(data);
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


    
    public void submit(WalletEntity fromWallet, WalletEntity toWallet, String amount, String pwd,
                       AssertBean fromExAsset, AssertBean toExAsset, String orderIds) {
        showLoadingDialog("");
        String fromPrivateKey = fromWallet.decodePrivateKey(pwd, true);
        String toPrivateKey = toWallet.decodePrivateKey(pwd, true);
        if (TextUtils.isEmpty(fromPrivateKey) || TextUtils.isEmpty(toPrivateKey)){
            showToast(getApplication().getString(R.string.get_private_key_fail));
            return;
        }
        String toChainName = ChatSdk.typeToChainName(toWallet.getType());
        String fromChainName = ChatSdk.typeToChainName(fromWallet.getType());
        String depositAddr = fromWallet.getDefaultAddress();
        String withdrawAddr = toWallet.getDefaultAddress();
        String bigAmount = AllUtils.getBigDecimalValue(amount, 18);
        Disposable disposable = Observable.create(new ObservableOnSubscribe<ChainBridgeExConfirmBean>() {
            @Override
            public void subscribe(ObservableEmitter<ChainBridgeExConfirmBean> emitter) throws Exception {
                ChatSdk.addCrossWalletPir(fromPrivateKey);
                ChatSdk.addCrossWalletPir(toPrivateKey);
                ChainBridgeExConfirmBean result = ChatSdk.crossConfirm(toChainName, fromChainName,  depositAddr, withdrawAddr, bigAmount, orderIds);
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(data->{
            dismissLoadingDialog();
            if(data != null && data.isSuccess()){
                long mainOrderId = data.data;
                if (mainOrderId > 0){
                    mSubmitLD.setValue(mainOrderId);
                } else {
                    mSubmitLD.setValue(0L);
                }
            } else {
                String errorInfo = data != null ? data.getInfo() : getApplication().getString(R.string.get_data_fail);
                showToast(errorInfo);
            }
        }, throwable -> {
            dismissLoadingDialog();
            throwable.printStackTrace();
            showToast(throwable+":"+throwable.getMessage());
        });
        mDisposable.add(disposable);
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
        } else if(walletType == BNB_COIN) {
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


    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}
