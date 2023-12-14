

package com.wallet.ctc.nft.ui;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.wallet.ctc.api.blockchain.TrxApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.AssetsPriceBean;
import com.wallet.ctc.model.blockchain.BandWidthBean;
import com.wallet.ctc.model.blockchain.BaseTrxBanlanceBean;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class AssetsTrxBiz extends BaseAssetsBiz {
    private CompositeDisposable compositeDisposable;
    private BigDecimal trxprice = new BigDecimal("0");
    private BigDecimal trxUsdtprice = new BigDecimal("0");
    private TrxApi mTApi = new TrxApi();

    
    public AssetsTrxBiz(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onLoadAssetsComplete() {
        super.onLoadAssetsComplete();
        compositeDisposable.clear();
        gettrxPrice();
    }

    
    private void getTrxBanlance() {
        Map<String, Object> params = new TreeMap<>();
        params.put("address", currentWallet.getAllAddress());
        mTApi.getAccounts(params).map(new Function<BaseTrxBanlanceBean, Boolean>() {
            @Override
            public Boolean apply(@NonNull BaseTrxBanlanceBean baseEntity) throws Exception {
                if (null != baseEntity.getBandwidth()) {
                    BandWidthBean mBean = baseEntity.getBandwidth();
                    currentWallet.setEnergy(mBean.getEnergyRemaining().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                    currentWallet.setBroadband(mBean.getFreeNetRemaining().toPlainString());
                    currentWallet.setEnergyMax(mBean.getEnergyLimit().setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                    currentWallet.setBroadbandMax(mBean.getFreeNetLimit().toPlainString());
                }
                if (null == baseEntity.getTokens() || baseEntity.getTokens().size() < 1) {
                    for (int i = 0; i < assets.size(); i++) {
                        assets.get(i).setAssertsNum("0.00");
                        assets.get(i).setAssertsSumPrice("0.00");
                        if (!assets.get(i).getShort_name().equals("TRX")) {
                            walletDBUtil.updateWalletAssets(assets.get(i));
                        }
                    }
                    currentWallet.setmBalance("0.00");
                    currentWallet.setmPrice("0.00");
                    walletDBUtil.updateWalletInfoByAddress(currentWallet);
                } else {
                    BigDecimal sum = new BigDecimal("0");
                    for (int position = 0; position < assets.size(); position++) {
                        assets.get(position).setAssertsNum("0.00");
                        assets.get(position).setAssertsSumPrice("0.00");

                        for (int j = 0; j < baseEntity.getTokens().size(); j++) {
                            String tokenaddress = baseEntity.getTokens().get(j).getTokenId();
                            String tokenName = baseEntity.getTokens().get(j).getTokenAbbr().toLowerCase();
                            BigDecimal tokenBalance = new BigDecimal(baseEntity.getTokens().get(j).getBalance());
                            tokenBalance = tokenBalance.divide(new BigDecimal(baseEntity.getTokens().get(j).getTokenDecimal()), 2, BigDecimal.ROUND_HALF_UP);
                            
                            if ((TextUtils.isEmpty(tokenaddress) || tokenaddress.length() < 5) && tokenName.equals(assets.get(position).getShort_name().toLowerCase())) {
                                assets.get(position).setAssertsNum(tokenBalance.toPlainString());
                                assets.get(position).setAssertsSumPrice(tokenBalance.multiply(trxprice).toPlainString());
                                currentWallet.setmBalance(tokenBalance.toPlainString());
                                BigDecimal bigDecimal = tokenBalance.multiply(trxprice);
                                currentWallet.setmPrice(bigDecimal.toPlainString());
                                sum = sum.add(bigDecimal);
                                break;
                            } else if (tokenName.equalsIgnoreCase(assets.get(position).getShort_name()) && tokenaddress.equalsIgnoreCase(assets.get(position).getContract())) {
                                assets.get(position).setAssertsNum(tokenBalance.toPlainString());
                                BigDecimal tokenPrice = trxprice.multiply(baseEntity.getTokens().get(1).getTokenPriceInTrx());
                                BigDecimal amount = tokenPrice.multiply(tokenBalance);
                                assets.get(position).setAssertsSumPrice(amount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
                                currentWallet.setmPrice(amount.toPlainString());
                                sum = sum.add(amount);
                                walletDBUtil.updateWalletAssets(assets.get(position));
                            }
                        }
                    }
                    currentWallet.setSumPrice(sum.toPlainString());
                    walletDBUtil.updateWalletInfoByAddress(currentWallet);
                }
                return true;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Boolean baseEntity) {
                        showResult();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    
    private void gettrxPrice() {
        Map<String, Object> params2 = new TreeMap();
        params2.put("keys", "trxusdt");
        params2.put("tag", "huobi");
        meApi.getMarketPrice(params2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseEntity>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            List<AssetsPriceBean> initPriceBean = gson.fromJson(gson.toJson(baseEntity.getData()), new TypeToken<List<AssetsPriceBean>>() {
                            }.getType());
                            if (null != initPriceBean && initPriceBean.size() > 0) {
                                trxprice = initPriceBean.get(0).getUprice();
                                trxUsdtprice = trxprice.divide(initPriceBean.get(0).getUprice(), 4, BigDecimal.ROUND_HALF_UP);
                            }

                        } else {
                            LogUtil.d(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        getTrxBanlance();
                    }

                    @Override
                    public void onComplete() {
                        getTrxBanlance();
                    }
                });
    }

    
    private void showResult() {
        getAssertsLiveData.setValue(assets);
        
        onWalletInfoChangeLiveData.setValue(currentWallet);
    }
}
