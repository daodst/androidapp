package com.app.did_guid;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.R;
import com.app.pojo.BuyDidConfigBean;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.Constants;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.CoinPriceBean;
import com.wallet.ctc.model.blockchain.EvmosChatParamsBean;
import com.wallet.ctc.model.blockchain.EvmosChatToBurn;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.HexUtils;

import java.math.BigDecimal;

import common.app.base.BaseViewModel;
import common.app.base.model.http.exception.ExceptionHandle;
import common.app.base.model.http.exception.ResponseThrowable;
import common.app.im.base.NextSubscriber;
import common.app.utils.AllUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BuyDidGuidVM extends BaseViewModel {
    public RpcApi mRpcApi;
    private CompositeDisposable mDisposable;
    public MutableLiveData<BuyDidConfigBean> mConfigLD;
    public MutableLiveData<String> mBuyPayAmountLD;

    public MutableLiveData<EvmosPledgeResultBean> mResultLD;
    public MutableLiveData<EvmosPledgeResultBean> mApproveResultLD;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;
    public MutableLiveData<EvmosSeqGasBean> mShowApproveDialogLD;
    private WalletTransctionUtil walletTransctionUtil;
    private int mGasCount;
    private String mGasPrice; 
    private boolean isApproveTx = false;
    
    private final String mThirdMapContract;

    
    public BuyDidGuidVM(@NonNull Application application) {
        super(application);
        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mConfigLD = new MutableLiveData<>();
        mBuyPayAmountLD = new MutableLiveData<>();

        mResultLD = new MutableLiveData<>();
        mApproveResultLD = new MutableLiveData<>();
        mShowGasDialogLD = new MutableLiveData<>();
        mShowApproveDialogLD = new MutableLiveData<>();
        mThirdMapContract = Constants.BUY_DST_BSC_CONTRACT;
        walletTransctionUtil = new WalletTransctionUtil(getApplication());
        walletTransctionUtil.setOnTransctionListen(new WalletTransctionUtil.TransctionListen() {
            @Override
            public void showLoading() {
                showLoadingDialog("");
            }

            @Override
            public void showGasCount(String gasCount) {
                dismissLoadingDialog();
                if (!TextUtils.isEmpty(gasCount)) {
                    mGasCount = new BigDecimal(gasCount).intValue();
                }
            }

            @Override
            public void showGasprice(GasPriceBean gasPriceBean) {
            }

            @Override
            public void showEip1559(String baseFeePerGas) {

            }

            @Override
            public void showDefGasprice(String defGasprice) {
                
                dismissLoadingDialog();
                
                if (mGasCount > 0 && !TextUtils.isEmpty(defGasprice)) {
                    BigDecimal gasprice = new BigDecimal(defGasprice).multiply(new BigDecimal("1000000000"));
                    mGasPrice = gasprice.toPlainString();

                    BigDecimal gasCountBig = new BigDecimal(mGasCount);
                    BigDecimal sumWei = gasCountBig.multiply(gasprice);
                    BigDecimal sum = sumWei;
                    BigDecimal jinzhi = new BigDecimal("1000000000000000000");
                    String feiyongStr = sum.divide(jinzhi).toPlainString();
                    EvmosSeqGasBean gasBean = new EvmosSeqGasBean();
                    gasBean.setStatus(1);
                    gasBean.gasFee = feiyongStr;
                    gasBean.gasCount = gasCountBig.intValue();
                    gasBean.gasPrice = defGasprice;
                    if (isApproveTx) {
                        isApproveTx = false;
                        mShowApproveDialogLD.setValue(gasBean);
                    } else {
                        mShowGasDialogLD.setValue(gasBean);
                    }
                }
            }

            @Override
            public void showTransctionSuccess(String hash) {
                dismissLoadingDialog();
                if (isApproveTx) {
                    isApproveTx = false;
                    EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                    result.success = true;
                    mApproveResultLD.setValue(result);
                } else {
                    EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                    result.success = true;
                    mResultLD.setValue(result);
                }
            }

            @Override
            public void onFail(String msg) {
                dismissLoadingDialog();
                EvmosPledgeResultBean result = new EvmosPledgeResultBean();
                result.success = false;
                result.info = msg;
                mResultLD.setValue(result);
            }
        });
    }

    public void getData(String dstAddres) {
        getEvmosData(dstAddres);
    }

    

    
    private void getBscData(BuyDidConfigBean data) {
        showLoadingDialog("");
        Observable.zip(
                        getDstPancakeSwapPrice(),
                        getBscBnbBalance(),
                        getMyBscUsdtBalance(),
                        (dstUsdtPrice, bnbBalance, usdtBalance) -> {
                            
                            data.bscDstUsdtPrice = dstUsdtPrice;
                            data.bnbBalance = bnbBalance;
                            data.bscUsdtBalance = usdtBalance;
                            data.isSuccess = true;
                            data.errorInfo = "";
                            return data;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<BuyDidConfigBean>() {
                    @Override
                    public void dealData(BuyDidConfigBean value) {
                        dismissLoadingDialog();
                        mConfigLD.setValue(value);
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        showErrorInfo("BNB", e);
                        dismissLoadingDialog();
                        mConfigLD.setValue(data);
                    }
                });
    }

    
    private void getEvmosData(String dstAddres) {
        showLoadingDialog("");
        Observable.zip(
                        getMyDstBalance(dstAddres),
                        mRpcApi.getEvmosChatParams(),
                        mRpcApi.getToBurn(dstAddres),
                        (dstBalance, evmosChatParamsBean, evmosChatToBurn) -> {
                            BuyDidConfigBean data = new BuyDidConfigBean();
                            boolean isSuccess = evmosChatParamsBean != null && evmosChatParamsBean.isSuccess() && evmosChatToBurn != null && evmosChatToBurn.isSuccess();
                            String errorInfo = "";
                            if (!isSuccess) {
                                errorInfo = getErrorInfo(evmosChatParamsBean, evmosChatToBurn);
                            } else {
                                
                                data.dstBalance = dstBalance;
                                data.minBurnAmount = AllUtils.getTenDecimalValue(evmosChatParamsBean.data.min_register_burn_amount.amount, 18, 4);
                                data.hasBurnAmount = AllUtils.getTenDecimalValue(evmosChatToBurn.data.amount, 18, 4);
                            }
                            data.isSuccess = isSuccess;
                            data.errorInfo = errorInfo;
                            return data;
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<BuyDidConfigBean>() {
                    @Override
                    public void dealData(BuyDidConfigBean value) {
                        dismissLoadingDialog();
                        getBscData(value);
                    }

                    @Override
                    protected void dealError(Throwable e) {
                        dismissLoadingDialog();
                        showErrorInfo(BuildConfig.EVMOS_FAKE_UNINT, e);
                    }
                });
    }

    private void showErrorInfo(String chainName, Throwable e) {
        if (null == e) {
            return;
        }
        e = e.getCause();
        ResponseThrowable thro = ExceptionHandle.handleException(e);
        if (null != thro) {
            String errorInfo = thro.getErrorInfo();
            String startTip = chainName+getApplication().getString(R.string.chain_node_title);
            if ("BNB".equals(chainName)) {
                String url = "";
                errorInfo = startTip+url+errorInfo+","+getApplication().getString(R.string.please_checknet_or_switch_node);
            } else {
                errorInfo = startTip+errorInfo;
            }
            showToast(errorInfo);
        } else {
            showToast(e.getMessage());
        }
    }


    
    public void showGasAlert(String fromAddr, String toAddress, String dstBigCoinAmount,
                             AssertBean payAssert, String payValue) {
        if ("USDT".equalsIgnoreCase(payAssert.getShort_name())) {
            
            showLoadingDialog("");
            Disposable disposable = mRpcApi.getTokenAllowanceAmount(fromAddr, payAssert.getType(), mThirdMapContract, payAssert.getContract())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new NextSubscriber<String>() {
                        @Override
                        public void dealData(String value) {
                            if (!TextUtils.isEmpty(value)) {
                                
                                String bigPayAmount = AllUtils.getBigDecimalValue(payValue, payAssert.getDecimal());
                                if (new BigDecimal(bigPayAmount).compareTo(new BigDecimal(value)) <= 0) {
                                    
                                    String data = getSwapDstCrossChain(payAssert.getContract(), dstBigCoinAmount, toAddress);
                                    getEthGas(fromAddr, mThirdMapContract, payAssert.getType(), data, "");
                                } else {
                                    
                                    isApproveTx = true;
                                    String data = getApproveData(mThirdMapContract, bigPayAmount);
                                    getEthGas(fromAddr, payAssert.getContract(), payAssert.getType(), data, "");
                                }
                            } else {
                                dismissLoadingDialog();
                                showToast("get allowance admount fail");
                            }
                        }

                        @Override
                        protected void dealError(Throwable e) {
                            super.dealError(e);
                            dismissLoadingDialog();
                        }
                    });
            mDisposable.add(disposable);
        } else {
            
            String contract = payAssert.getContract();
            String data = getSwapDstCrossChain(contract, dstBigCoinAmount, toAddress);
            String bigPayAmount = AllUtils.getBigDecimalValue(payValue, payAssert.getDecimal());
            String hexPayBigAmount = HexUtils.tenToHex(bigPayAmount);
            getEthGas(fromAddr, mThirdMapContract, payAssert.getType(), data, hexPayBigAmount);
        }
    }

    
    public void swapBuy(String fromAddr, String toAddress, String bigDstAmount, String payValue,
                         AssertBean payAssets, WalletEntity wallet, String pwd, boolean isApprove) {
        if (isApprove) {
            
            doTokenAmountApprove(fromAddr, toAddress, bigDstAmount, payAssets.getType(), payAssets.getShort_name(),
                    payAssets.getContract(), "", wallet, pwd);
            return;
        }
        doSwapBuyMap(fromAddr, toAddress, bigDstAmount, payAssets.getType(), payAssets.getShort_name(), payValue,
                payAssets.getContract(), mThirdMapContract, "", wallet, pwd);
    }


    
    private void doSwapBuyMap(String fromAddr, String toAddress, String dstBigCoinAmount, int walletType, String coinName, String bnbValue,
                                           String tokenContract, String mapContract, String remark, WalletEntity wallet, String pwd) {
        String value = "0";
        if ("BNB".equalsIgnoreCase(coinName) || "WBNB".equalsIgnoreCase(coinName)) {
            value = bnbValue;
        }
        String feiyongStr = getFee();
        TransferBean data = new TransferBean(toAddress, fromAddr, value, feiyongStr, walletType, coinName, feiyongStr);
        data.setPrice(value);
        data.setTokenName(coinName);
        if (!TextUtils.isEmpty(remark)) {
            data.setRemark(remark);
        } else {
            data.setRemark(""+coinName+" swapCrosse");
        }
        data.setKuanggong(feiyongStr);
        data.setGascount(mGasCount);
        
        String gasPriceGwei = new BigDecimal(mGasPrice).divide(new BigDecimal("1000000000"), 2, BigDecimal.ROUND_HALF_UP).toPlainString();
        data.setGasprice(gasPriceGwei);
        data.setRuaddress(toAddress);
        data.setTokenType(mapContract);
        data.setDecimal(18);
        String methodData = getSwapDstCrossChain(tokenContract, dstBigCoinAmount, toAddress);
        data.setData(methodData);
        walletTransctionUtil.doTransction(data, pwd);
    }

    
    private void doTokenAmountApprove(String fromAddr, String toAddress, String bigCoinAmount, int walletType, String coinName,
                                      String contract, String remark,  WalletEntity wallet, String pwd) {
        String value = "0";
        String feiyongStr = getFee();
        TransferBean data = new TransferBean(toAddress, fromAddr, value, feiyongStr, walletType, coinName, feiyongStr);
        data.setPrice(value);
        data.setTokenName(coinName);
        if (!TextUtils.isEmpty(remark)) {
            data.setRemark(remark);
        } else {
            data.setRemark("approve "+coinName +" amount");
        }
        data.setKuanggong(feiyongStr);
        data.setGascount(mGasCount);
        
        String gasPriceGwei = new BigDecimal(mGasPrice).divide(new BigDecimal("1000000000"), 2, BigDecimal.ROUND_HALF_UP).toPlainString();
        data.setGasprice(gasPriceGwei);
        data.setRuaddress(toAddress);
        data.setTokenType(contract);
        data.setDecimal(18);
        String methodData = getApproveData(mThirdMapContract, bigCoinAmount);
        data.setData(methodData);
        isApproveTx = true;
        walletTransctionUtil.doTransction(data, pwd);
    }


    
    private String getFee() {
        if (mGasCount > 0 && !TextUtils.isEmpty(mGasPrice)) {
            BigDecimal gasCountBig = new BigDecimal(mGasCount);
            BigDecimal sumWei = gasCountBig.multiply(new BigDecimal(mGasPrice));
            BigDecimal sum = sumWei;
            BigDecimal jinzhi = new BigDecimal("1000000000000000000");
            String feiyongStr = sum.divide(jinzhi).toPlainString();
            return feiyongStr;
        } else {
            return "";
        }
    }

    
    private void getEthGas(String fromAddress, String contract, int walletType, String data, String hexPayBigAmount) {
        walletTransctionUtil.getEthGas(fromAddress, contract, walletType, data, hexPayBigAmount);
    }


    
    public String getApproveData(String spenderContractAddress, String bigAmount) {
        return WalletUtil.getApproveData(spenderContractAddress, bigAmount);
    }

    
    private String getSwapDstCrossChain(String tokenContract, String dstBigAmount, String toAddress) {
        
        return WalletUtil.getSwapDstCrossChain(tokenContract, dstBigAmount, toAddress);
    }


    
    private String getErrorInfo(EvmosChatParamsBean evmosChatParamsBean, EvmosChatToBurn evmosChatToBurn) {
        String errorInfo = "";
        if (evmosChatParamsBean == null) {
            errorInfo = "ChatParams is null";
        } else if (!evmosChatParamsBean.isSuccess()) {
            errorInfo = evmosChatParamsBean.getInfo();
        } else if(evmosChatToBurn == null) {
            errorInfo = "ChatBurn is null";
        } else if(!evmosChatToBurn.isSuccess()) {
            errorInfo = evmosChatToBurn.getInfo();
        }
        return errorInfo;
    }


    
    private Observable<String> getDstPancakeSwapPrice() {
        AssertBean dstAsserts = Constants.getDstBscAssets();
        AssertBean usdtAsserts = Constants.getUSDTBscAssets();
        return mRpcApi.getPancakeSwapAmountsOut("1", dstAsserts, usdtAsserts)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mDisposable.add(disposable);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<CoinPriceBean, String>() {
                    @Override
                    public String apply(CoinPriceBean coinPriceBean) throws Exception {
                        if (coinPriceBean != null) {
                            return coinPriceBean.price;
                        }
                        return "";
                    }
                });
    }


    private String[] amountKey = {""};
    public void calculateBuyPayAmount(String amount, String byCoin) {
        String key = byCoin+amount;
        amountKey[0] = key;
        if (TextUtils.isEmpty(amount)) {
            return;
        }
        AssertBean assertBean = Constants.getDstBscAssets();
        AssertBean toAssert = null;
        AssertBean centerAssert = null;
        if ("USDT".equalsIgnoreCase(byCoin)) {
            toAssert = Constants.getUSDTBscAssets();
        } else if("BNB".equalsIgnoreCase(byCoin)) {
            toAssert = Constants.getWbnbBscAssets();
            centerAssert = Constants.getUSDTBscAssets();
        }
        if (null == toAssert) {
            return;
        }
        mRpcApi.getPancakeSwapAmountsIn(amount, assertBean, toAssert, centerAssert)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> {
                    mDisposable.add(disposable);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<CoinPriceBean>() {
                    @Override
                    public void dealData(CoinPriceBean value) {
                        if (null != value) {
                            String key = byCoin+amount;
                            if (key.equals(amountKey[0])) {
                                
                                String amount = value.toAmount;
                                try {
                                    BigDecimal num = new BigDecimal(amount);
                                    BigDecimal addNum = num.add(num.multiply(new BigDecimal("0.01")));
                                    amount = addNum.toPlainString();
                                } catch (NumberFormatException e){
                                    e.printStackTrace();
                                }
                                mBuyPayAmountLD.setValue(amount);
                            }
                        }
                    }
                });
    }

    
    private Observable<String> getBscBnbBalance() {
        WalletEntity wallet = WalletDBUtil.getInstent(getApplication()).getWalletInfo(WalletUtil.BNB_COIN);
        if (null == wallet) {
            return Observable.just("0.00");
        }
        return mRpcApi.getEthBanlance(wallet.getAllAddress(), "", wallet.getType())
                .doOnSubscribe(disposable -> {
                    mDisposable.add(disposable);
                })
                .map(value->{
                    if (null != value && !TextUtils.isEmpty(value.getResult())) {
                        String bigNum = HexUtils.hextoTen(value.getResult());
                        String balance = AllUtils.getTenDecimalValue(bigNum, 18, 6);
                        return balance;
                    } else {
                        throw new Exception("get eth balance value is null");
                    }
                });
    }

    
    private Observable<String> getMyBscUsdtBalance() {
        WalletEntity wallet = WalletDBUtil.getInstent(getApplication()).getWalletInfo(WalletUtil.BNB_COIN);
        if (null == wallet) {
            return Observable.just("0.00");
        }
        String usdtContract = "0x55d398326f99059ff775485246999027b3197955";
        return mRpcApi.getEthBanlance(wallet.getAllAddress(), usdtContract, wallet.getType())
                .doOnSubscribe(disposable -> {
                    mDisposable.add(disposable);
                })
                .map(value->{
                    if (null != value && !TextUtils.isEmpty(value.getResult())) {
                        String bigNum = HexUtils.hextoTen(value.getResult());
                        String balance = AllUtils.getTenDecimalValue(bigNum, 18, 6);
                        return balance;
                    } else {
                        throw new Exception("get eth balance value is null");
                    }
                });
    }


    
    private Observable<String> getMyDstBalance(String address) {
        if (TextUtils.isEmpty(address)) {
            return Observable.just("0.00");
        }
        
        AssertBean mainCoin = WalletDBUtil.getInstent(getApplication()).getWalletMainCoin(WalletUtil.MCC_COIN);
        String coinName = "";
        int decimal = 18;
        if (mainCoin != null) {
            coinName = mainCoin.getShort_name();
            decimal = mainCoin.getDecimal();
        }
        if (TextUtils.isEmpty(coinName)) {
            coinName = BuildConfig.EVMOS_FAKE_UNINT.toLowerCase();
            decimal = 18;
        }
        int finalDecimal = decimal;
        return mRpcApi.getEvmosOneBalance(address, coinName)
                .doOnSubscribe(disposable -> {
                    mDisposable.add(disposable);
                })
                .map(value -> {
            if (null != value) {
                String remain = value.getBalance(finalDecimal);
                return remain;
            } else {
                throw new Exception("get dst balance value is null");
            }
        });
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}
