

package com.wallet.ctc.ui.me.chain_bridge;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.wallet.ctc.Constants;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.ChainMapConfigBean;
import com.wallet.ctc.model.blockchain.DaodstTipsBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqGasBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.HexUtils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import common.app.base.BaseViewModel;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChainBridgeActivityVM extends BaseViewModel {

    public MutableLiveData<EvmosPledgeResultBean> mResultLD;
    public MutableLiveData<EvmosPledgeResultBean> mApproveResultLD;
    public MutableLiveData<EvmosSeqGasBean> mShowGasDialogLD;
    public MutableLiveData<EvmosSeqGasBean> mShowApproveDialogLD;
    public MutableLiveData<List<Map<String,AssertBean>>> mMapAssetsLD;
    public MutableLiveData<String> mBalanceLD;
    public MutableLiveData<ChainMapConfigBean> mMapConfigLD;

    private CompositeDisposable mDisposable;
    private RpcApi mRpcApi;
    private WalletTransctionUtil walletTransctionUtil;
    private int mGasCount;
    private String mGasPrice; 

    private boolean isApproveTx = false;

    
    
    private final String mThirdMapContract;


    
    public ChainBridgeActivityVM(@NonNull Application application) {
        super(application);
        mThirdMapContract = Constants.THIRD_TOKEN_MAP_BSC_CONTRACT;

        mRpcApi = new RpcApi();
        mDisposable = new CompositeDisposable();
        mResultLD = new MutableLiveData<>();
        mShowGasDialogLD = new MutableLiveData<>();
        mMapAssetsLD = new MutableLiveData<>();
        mBalanceLD = new MutableLiveData<>();
        mShowApproveDialogLD = new MutableLiveData<>();
        mApproveResultLD = new MutableLiveData<>();
        mMapConfigLD = new MutableLiveData<>();

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

    
    private List<AssertBean> getMapDstCoins() {
        
        List<AssertBean> dstAsserts = new ArrayList<>();
        
        dstAsserts.addAll(WalletDBUtil.getInstent(getApplication()).getMustWallet(WalletUtil.MCC_COIN));
        
        AssertBean usdtAsserts = Constants.getUSDTEvmosAssets();
        if (null != usdtAsserts) {
            dstAsserts.add(usdtAsserts);
        }
        return dstAsserts;
    }

    
    private Map<String, AssertBean> getDstBscMap(AssertBean dstCoin) {
        if (null == dstCoin || TextUtils.isEmpty(dstCoin.getShort_name())) {
            return null;
        }
        String symbol = dstCoin.getShort_name();
        AssertBean bscAsset = null;
        if (symbol.equalsIgnoreCase("nxn")) {
            
            bscAsset = Constants.getFmBscAssets();
        } else if(symbol.equalsIgnoreCase("dst")) {
            
            bscAsset = Constants.getDstBscAssets();
        } else if(symbol.equalsIgnoreCase("usdt")) {
            
            bscAsset = Constants.getUSDTBscAssets();
        }
        if (bscAsset != null) {
            Map<String,AssertBean> map = new HashMap<>();
            map.put(ChainBridgeActivity.CHAIN_TO_MCC, dstCoin);
            map.put(ChainBridgeActivity.CHAIN_TO_BSC, bscAsset);
            return map;
        }
        return null;
    }

    
    public boolean isThirdBscToken(AssertBean assertBean) {
        String contract = assertBean.getContract();
        return isThirdBscToken(contract);
    }

    
    public boolean isThirdBscToken(String tokenContract) {
        if (!TextUtils.isEmpty(tokenContract) && tokenContract.equalsIgnoreCase(Constants.USDT_BSC_CONTRACT)) {
            return true;
        } else {
            return false;
        }
    }



    
    public void getAssets() {
        List<Map<String,AssertBean>> mapList = new ArrayList<>();
        List<AssertBean> dstAsserts = getMapDstCoins();
        
        for (int i=0; i<dstAsserts.size(); i++) {
            Map<String,AssertBean> map = getDstBscMap(dstAsserts.get(i));
            if (null != map) {
                mapList.add(map);
            }
        }
        mMapAssetsLD.setValue(mapList);
    }

    
    public void getMapConfig() {
        
        mRpcApi.getDaodstTips("", "CHC").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextSubscriber<DaodstTipsBean>() {
                    @Override
                    public void dealData(DaodstTipsBean value) {
                        if (null != value && value.isSuccess() && null != value.data) {

                            ChainMapConfigBean config = new ChainMapConfigBean();
                            config.setStatus(1);
                            config.data = new ChainMapConfigBean.Data();
                            config.data.feePercent = value.data.cross_chain_fee;
                            config.data.minMapNum = value.data.cross_chain_min;
                            mMapConfigLD.setValue(config);
                        } else {
                            String errorInfo = value != null ? value.getInfo() : "get config is null";
                            showToast(errorInfo);
                        }
                    }
                });



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


    
    public void showGasAlert(String fromAddr, String toAddress, String bigCoinAmount,
                             AssertBean from, String chainType) {
        if (ChainBridgeActivity.CHAIN_TO_BSC.equals(chainType)) {
            
            String coinSymbol = from.getShort_name();
            mRpcApi.getEvmosChainEthsGas(fromAddr, toAddress, bigCoinAmount, coinSymbol, chainType, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NextSubscriber<EvmosSeqGasBean>() {
                        @Override
                        public void dealData(EvmosSeqGasBean value) {
                            mShowGasDialogLD.setValue(value);
                        }
                    });

        } else if(ChainBridgeActivity.CHAIN_TO_MCC.equals(chainType)) {
            
            if (isThirdBscToken(from)) {
                
                showLoadingDialog("");
                Disposable disposable = mRpcApi.getTokenAllowanceAmount(fromAddr, from.getType(), mThirdMapContract, from.getContract())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new NextSubscriber<String>() {
                            @Override
                            public void dealData(String value) {
                                if (!TextUtils.isEmpty(value)) {
                                    
                                    if (new BigDecimal(bigCoinAmount).compareTo(new BigDecimal(value)) <= 0) {
                                        
                                        String data = getTokenCrossChain(from.getContract(), bigCoinAmount, toAddress);
                                        getEthGas(fromAddr, mThirdMapContract, from.getType(), data);
                                    } else {
                                        
                                        isApproveTx = true;
                                        String data = getApproveData(mThirdMapContract, bigCoinAmount);
                                        getEthGas(fromAddr, from.getContract(), from.getType(), data);
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
                
                String contract = from.getContract();
                String data = getBurnTokenData(toAddress, bigCoinAmount, chainType);
                getEthGas(fromAddr, contract, from.getType(), data);
            }
        }
    }


    
    public void chainMap(String fromAddr, String toAddress, String bigCoinAmount,
                         AssertBean from, String chainType, WalletEntity wallet, String pwd, boolean isApprove) {
        if (ChainBridgeActivity.CHAIN_TO_BSC.equals(chainType)) {
            
            String coinSymbol = from.getShort_name();
            doEvmosToBscMap(fromAddr, toAddress, bigCoinAmount, coinSymbol, chainType, "", wallet, pwd);
        } else if(ChainBridgeActivity.CHAIN_TO_MCC.equals(chainType)) {
            if (isApprove) {
                
                doTokenAmountApprove(fromAddr, toAddress, bigCoinAmount, from.getType(), from.getShort_name(),
                        from.getContract(), "", wallet, pwd);
                return;
            }

            
            if (isThirdBscToken(from)) {
                
                doThirdBscTokenToEvmosMap(fromAddr, toAddress, bigCoinAmount, from.getType(), from.getShort_name(),
                        from.getContract(), mThirdMapContract, chainType, "", wallet, pwd);
            } else {
                doBscToEvmosMap(fromAddr, toAddress, bigCoinAmount, from.getType(), from.getShort_name(),
                        from.getContract(), chainType, "", wallet, pwd);
            }

        }
    }


    

    
    private void doEvmosToBscMap(String fromAddr, String toAddress, String bigCoinAmount,
                                 String coinSymbol, String chainType, String remark, WalletEntity wallet, String pwd) {
        showLoadingDialog("");
        mRpcApi.getEvmosChainEthsGas(fromAddr, toAddress, bigCoinAmount, coinSymbol, chainType, remark)
                .concatMap(evmosSeqGasBean -> {
                    return mRpcApi.submitEvmosTransfer(signCrossChainOut(toAddress, bigCoinAmount, coinSymbol,
                            chainType, remark, evmosSeqGasBean, wallet, pwd).Data);
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
                            checkTxResult(data);
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


    
    private void checkTxResult(EvmosTransferResultBean transferResult) {
        showLoadingDialog("");
        mRpcApi.timerCheckTxResult(transferResult, mDisposable, new Observer<EvmosPledgeResultBean>() {
            @Override
            public void onSubscribe(Disposable d) {}
            @Override
            public void onNext(EvmosPledgeResultBean evmosPledgeResultBean) {
                mResultLD.setValue(evmosPledgeResultBean);
                dismissLoadingDialog();
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

    
    private EvmosSignResult signCrossChainOut(String toAddress, String bigCoinAmount,
                                              String coinSymbol, String chainType, String remark,
                                              EvmosSeqGasBean evmosSeqGasBean, WalletEntity wallet, String pwd) throws Exception {
        if (null == evmosSeqGasBean || !evmosSeqGasBean.isSuccess()) {
            String errorInfo = evmosSeqGasBean != null ? evmosSeqGasBean.getInfo() : "evmosSeqGasBean is null";
            throw new Exception(errorInfo);
        }
        
        ChatSdk.resetWalletGasInfo(evmosSeqGasBean, wallet, pwd, "chianMap");
        EvmosSignResult evmosSignResult = ChatSdk.convertSignData(ChatSdk.signCrossChainOut(toAddress, bigCoinAmount, coinSymbol, chainType, remark));
        if (evmosSignResult == null || !evmosSignResult.isSuccess()) {
            String errorInfo = evmosSignResult != null ? evmosSignResult.getInfo() : "sign result is null";
            throw new Exception(errorInfo);
        }
        return evmosSignResult;
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



    
    private void doBscToEvmosMap(String fromAddr, String toAddress, String bigCoinAmount, int walletType, String coinName,
                                 String contract, String chainType, String remark, WalletEntity wallet, String pwd) {
        String value = "0";
        String feiyongStr = getFee();
        TransferBean data = new TransferBean(toAddress, fromAddr, value, feiyongStr, walletType, coinName, feiyongStr);
        data.setPrice(value);
        data.setTokenName(coinName);
        if (!TextUtils.isEmpty(remark)) {
            data.setRemark(remark);
        } else {
            data.setRemark(""+coinName+" map");
        }
        data.setKuanggong(feiyongStr);
        data.setGascount(mGasCount);
        
        String gasPriceGwei = new BigDecimal(mGasPrice).divide(new BigDecimal("1000000000"), 2, BigDecimal.ROUND_HALF_UP).toPlainString();
        data.setGasprice(gasPriceGwei);
        data.setRuaddress(toAddress);
        data.setTokenType(contract);
        data.setDecimal(18);
        String methodData = getBurnTokenData(toAddress, bigCoinAmount, chainType);
        data.setData(methodData);
        walletTransctionUtil.doTransction(data, pwd);
    }

    
    private void doThirdBscTokenToEvmosMap(String fromAddr, String toAddress, String bigCoinAmount, int walletType, String coinName,
                                 String tokenContract, String mapContract, String chainType, String remark, WalletEntity wallet, String pwd) {
        String value = "0";
        String feiyongStr = getFee();
        TransferBean data = new TransferBean(toAddress, fromAddr, value, feiyongStr, walletType, coinName, feiyongStr);
        data.setPrice(value);
        data.setTokenName(coinName);
        if (!TextUtils.isEmpty(remark)) {
            data.setRemark(remark);
        } else {
            data.setRemark(""+coinName+" map");
        }
        data.setKuanggong(feiyongStr);
        data.setGascount(mGasCount);
        
        String gasPriceGwei = new BigDecimal(mGasPrice).divide(new BigDecimal("1000000000"), 2, BigDecimal.ROUND_HALF_UP).toPlainString();
        data.setGasprice(gasPriceGwei);
        data.setRuaddress(toAddress);
        data.setTokenType(mapContract);
        data.setDecimal(18);
        String methodData = getTokenCrossChain(tokenContract, bigCoinAmount, toAddress);
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


    
    private String getBurnTokenData(String toAddress, String bigCoinAmount, String chainType) {
        
        BigInteger _amount = new BigInteger(bigCoinAmount);
        String FUNC_BURNTOKEN = "burnToken"; 
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNTOKEN,
                Arrays.<Type>asList(new Uint256(_amount), 
                        new Utf8String(toAddress),
                        new Utf8String(chainType)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        return data;
    }


    
    private String getApproveData(String spenderContractAddress, String bigAmount) {
        
        
        BigInteger _amount = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
        String FUNC_BURNTOKEN = "approve"; 
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNTOKEN,
                Arrays.<Type>asList(new Address(spenderContractAddress), new Uint256(_amount)), 
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        return data;
    }


    
    private String getTokenCrossChain(String tokenContract, String bigAmount, String toAddress) {
        
        BigInteger _amount = new BigInteger(bigAmount);
        String FUNC_BURNTOKEN = "crossChain"; 
        byte[] key32Array = getByte32Key();
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNTOKEN,
                Arrays.<Type>asList(new Address(tokenContract), new Uint256(_amount), new Utf8String(toAddress), new Bytes32(key32Array)), 
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        return data;
    }


    private byte[] getByte32Key() {
        Random random = new Random();
        byte[] array = new byte[32];
        random.nextBytes(array);
        return array;
    }

    
    private void getEthGas(String fromAddress, String contract, int walletType, String data) {
        walletTransctionUtil.getEthGas(fromAddress, contract, walletType, data, "");
    }


    public void onDestroy() {
        if (null != mDisposable) {
            mDisposable.dispose();
        }
    }
}

