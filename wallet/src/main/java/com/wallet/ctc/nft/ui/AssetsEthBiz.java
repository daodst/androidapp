

package com.wallet.ctc.nft.ui;

import android.app.Application;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wallet.ctc.R;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ETHBanlanceBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AssetsEthBiz extends BaseAssetsBiz {
    private CompositeDisposable compositeDisposable;

    
    public AssetsEthBiz(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onLoadAssetsComplete() {
        super.onLoadAssetsComplete();
        compositeDisposable.clear();
        List<Observable<Boolean>> obs = new ArrayList<>();
        for (int i = 0; i < assets.size(); i++) {
            AssertBean assertBean = assets.get(i);
            int type = assertBean.getType();
            Map<String, Object> params2 = new TreeMap();
            params2.put("addr", currentWallet.getAllAddress());
            params2.put("contract_addr", assertBean.getContract());
            Observable<Boolean> ob = meApi.getBanlance(params2, currentWallet.getType()).map(new Function<BaseEntity, Boolean>() {
                @Override
                public Boolean apply(@NonNull BaseEntity baseEntity) throws Exception {
                    if (baseEntity.getStatus() == 1) {
                        Log.d("fenghl","" +  (Looper.getMainLooper().getThread() == Thread.currentThread()));
                        ETHBanlanceBean bean = gson.fromJson(gson.toJson(baseEntity.getData()), ETHBanlanceBean.class);
                        assertBean.setAssertsNum(bean.getRemain().toPlainString());
                        assertBean.setAssertsSumPrice(bean.getSumPrice().toPlainString());
                        if ((assertBean.getShort_name().toUpperCase().equals("ETH") && type == WalletUtil.ETH_COIN)
                                || (assertBean.getShort_name().toUpperCase().equals("BNB") && type == WalletUtil.BNB_COIN)
                                || (assertBean.getShort_name().toUpperCase().equals("HT") && type == WalletUtil.HT_COIN)
                                || (assertBean.getShort_name().toUpperCase().equals(getApplication().getString(R.string.default_dmf_ba).toUpperCase()) && type == WalletUtil.DMF_BA_COIN)
                                || (assertBean.getShort_name().toUpperCase().equals(getApplication().getString(R.string.default_dmf_hb).toUpperCase()) && type == WalletUtil.DMF_COIN)
                                || (assertBean.getShort_name().toUpperCase().equals(getApplication().getString(R.string.default_etf).toUpperCase()) && type == WalletUtil.ETF_COIN)
                        ) {
                            currentWallet.setmBalance(bean.getRemain().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString());
                            currentWallet.setmPrice(bean.getSumPrice().setScale(8, BigDecimal.ROUND_HALF_UP).toPlainString());
                            walletDBUtil.updateWalletInfoByAddress(currentWallet);
                        } else {
                            walletDBUtil.updateWalletAssets(assertBean);
                        }
                    }
                    return true;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            obs.add(ob);
        }

        Observable.mergeDelayError(obs).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@NonNull Boolean isSuccess) {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                showResult();
            }

            @Override
            public void onComplete() {
                showResult();
            }
        });
    }

    
    private void showResult() {
        getAssertsLiveData.setValue(assets);
        
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < assets.size(); i++) {
            sum = sum.add(new BigDecimal(assets.get(i).getAssertsSumPrice()));
        }
        currentWallet.setSumPrice(sum.toPlainString());
        walletDBUtil.updateWalletInfoByAddress(currentWallet);
        onWalletInfoChangeLiveData.setValue(currentWallet);

    }
}
