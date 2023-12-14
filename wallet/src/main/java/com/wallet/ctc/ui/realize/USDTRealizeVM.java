package com.wallet.ctc.ui.realize;

import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.HexUtils;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.utils.AllUtils;
import im.wallet.router.util.Consumer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class USDTRealizeVM extends BaseViewModel {

    private CompositeDisposable mDisposable;
    private RpcApi mRpcApi;

    
    public USDTRealizeVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mRpcApi = new RpcApi();
    }


    public MutableLiveData<String> dst = new MutableLiveData<>();
    public MutableLiveData<String> eth = new MutableLiveData<>();
    public MutableLiveData<String> dstUsdt = new MutableLiveData<>();
    public MutableLiveData<String> ethUsdt = new MutableLiveData<>();

    public void getData() {
        Context context = getApplication().getBaseContext();

        WalletEntity dstWallet = WalletDBUtil.getInstent(context).getWalletInfo(WalletUtil.MCC_COIN);
        AssertBean dstAssert = WalletDBUtil.getInstent(context).getWalletMainCoin(dstWallet.getType());
        AssertBean dstUsdtAssert = WalletUtil.getUsdtAssert(dstWallet.getType());

        
        getBalance(dstWallet, dstAssert, false, s -> {
            dst.postValue(s);
        });
        
        getBalance(dstWallet, dstUsdtAssert, false, s -> {
            dstUsdt.postValue(s);
        });

        WalletEntity ethWallet = WalletDBUtil.getInstent(context).getWalletInfo(WalletUtil.ETH_COIN);

        AssertBean ethAssert = WalletDBUtil.getInstent(context).getWalletMainCoin(ethWallet.getType());
        AssertBean ethUsdtAssert = WalletUtil.getUsdtAssert(ethWallet.getType());
        
        getBalance(ethWallet, ethAssert, false, s -> {
            eth.postValue(s);
        });
        getBalance(ethWallet, ethUsdtAssert, false, s -> {
            ethUsdt.postValue(s);
        });


    }


    public void getBalance(WalletEntity wallet, AssertBean assertBean, boolean showLoading, Consumer<String> consumer) {
        if (null == wallet || null == assertBean) {
            return;
        }
        int walletType = assertBean.getType();
        if (walletType == MCC_COIN) {
            String contract = assertBean.getContract();
            if (TextUtils.isEmpty(contract)) {
                String address = wallet.getAllAddress();
                getMccBalance(address, assertBean.getShort_name(), assertBean, showLoading, consumer);
            } else {
                String address = wallet.getDefaultAddress();
                getEthBalance(address, walletType, assertBean, showLoading, consumer);
            }
        } else {
            String address = wallet.getAllAddress();
            getEthBalance(address, walletType, assertBean, showLoading, consumer);
        }
    }

    
    private void getMccBalance(String address, String coinName, AssertBean assertBean, boolean showLoading, Consumer<String> consumer) {
        if (showLoading) {
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
                            consumer.accept(remain);
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

    
    private void getEthBalance(String address, int walletType, AssertBean assertBean, boolean showLoading, Consumer<String> consumer) {
        String contract = assertBean.getContract();

        if (showLoading) {
            showLoadingDialog("");
        }
        Disposable disposable = mRpcApi.getEthBanlance(address, contract, walletType)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NextSubscriber<FilBalanceBean>() {
                    @Override
                    public void dealData(FilBalanceBean value) {
                        dismissLoadingDialog();
                        if (null != value && !TextUtils.isEmpty(value.getResult())) {
                            String bigNum = HexUtils.hextoTen(value.getResult());
                            String balance = AllUtils.getTenDecimalValue(bigNum, assertBean.getDecimal(), 6);
                            consumer.accept(balance);
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

    @Override
    protected void onCleared() {
        super.onCleared();
        if (null != mDisposable) {
            mDisposable.clear();
        }
    }
}
