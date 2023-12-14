package com.app.me.destory_group;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.ui.pay.ITransferControlApi;
import com.wallet.ctc.ui.pay.TransferControlApi;
import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import common.app.base.BaseViewModel;
import common.app.pojo.ConsumerData;
import im.wallet.router.listener.TranslationListener;
import im.wallet.router.wallet.pojo.EvmosDaoParams;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class DestroyGroupVM extends BaseViewModel {

    private static final String TAG = "DestroyGroupVM";

    private CompositeDisposable mDisposable;
    private ITransferControlApi mControlApi;
    MutableLiveData<String> mDstPriceLD;
    MutableLiveData<EvmosDaoParams> mDataLD;
    MutableLiveData<ConsumerData> mSuccessLD;


    
    public DestroyGroupVM(@NonNull Application application) {
        super(application);
        mDisposable = new CompositeDisposable();
        mControlApi = new TransferControlApi();
        mDstPriceLD = new MutableLiveData<>();
        mDataLD = new MutableLiveData<>();
        mSuccessLD = new MutableLiveData<>();
    }

    public void getInfo(String fromAddress) {
        Disposable disposable = mControlApi.getDstPancakeSwapPrice()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(price->{
                    String convertPrice = "";
                    if (!TextUtils.isEmpty(price)) {
                        
                        
                        try {
                            convertPrice = new BigDecimal(1).divide(new BigDecimal(price), 4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
                        } catch (NumberFormatException e){
                            e.printStackTrace();
                        }
                    }
                    mDstPriceLD.setValue(convertPrice);
                }, throwable -> {
                    throwable.printStackTrace();
                    Log.e(TAG, throwable+":"+throwable.getMessage());
                });
        mDisposable.add(disposable);

        showLoadingDialog("");
        Disposable disposable2 = mControlApi.getDaoCompositeParams(getApplication(), fromAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    dismissLoadingDialog();
                    if (null != data && data.isSuccess()) {
                        mDataLD.setValue(data);
                    } else {
                        String errorInfo = data != null ? data.getInfo() : "get data is null";
                        showToast(errorInfo);
                    }
                }, throwable -> {
                    dismissLoadingDialog();
                    throwable.printStackTrace();
                    showToast(throwable+":"+throwable.getMessage());
                });
        mDisposable.add(disposable2);
    }


    
    public void doDestroy(Context activityContext, String fromAddr, String toAddr, String groupId,
                          String burnAmount, String useFreezeNum) {
        mControlApi.burnToPower(activityContext, fromAddr, toAddr, groupId, burnAmount, useFreezeNum, new TranslationListener() {
            @Override
            public void onFail(String errorInfo) {
            }
            @Override
            public void onTransSuccess() {
                getNowPowerData(fromAddr, groupId);
            }
        });
    }

    
    @SuppressLint("CheckResult")
    public void getNowPowerData(String fromAddr, String groupId) {
        showLoadingDialog("");
        mControlApi.getMyGroupData(fromAddr, groupId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    dismissLoadingDialog();
                    ConsumerData consumerData = new ConsumerData("", "300%");
                    if ( null != data && data.isSuccess() && null != data.data) {
                       String powerValue = AllUtils.getTenDecimalValue(data.data.power_amount, getWalletDecimal(), 6);
                       consumerData.value1 = powerValue;
                    }
                    mSuccessLD.setValue(consumerData);
                }, throwable -> {
                    dismissLoadingDialog();
                    ConsumerData consumerData = new ConsumerData("", "300%");
                    mSuccessLD.setValue(consumerData);
                });
    }


    public int getWalletDecimal() {
        AssertBean dstAst = WalletDBUtil.getInstent(getApplication()).getWalletAssets(WalletUtil.MCC_COIN, BuildConfig.EVMOS_FAKE_UNINT);
        int decimal = 18;
        if (null != dstAst) {
            decimal = dstAst.getDecimal();
        }
        if (decimal == 0) {
            decimal = 18;
        }
        return decimal;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }
}
