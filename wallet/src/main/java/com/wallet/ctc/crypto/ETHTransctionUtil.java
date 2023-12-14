

package com.wallet.ctc.crypto;

import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.R;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.BasefeeBean;
import com.wallet.ctc.model.blockchain.BlockByNumberBean;
import com.wallet.ctc.model.blockchain.EthEstimateGasBean;
import com.wallet.ctc.model.blockchain.EthTxResultBean;
import com.wallet.ctc.model.blockchain.EvmosOneBalanceBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.Hex16;
import com.wallet.ctc.util.HexUtils;
import com.wallet.ctc.util.LogUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.im.base.NextSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.AllUtils;
import common.app.utils.SpUtil;
import common.app.utils.ThreadManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.CoinType;
import wallet.core.jni.proto.Ethereum;



public class ETHTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private RpcApi rpcApi = new RpcApi();
    private MeApi mApi = new MeApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.TransctionListen mListen;

    public ETHTransctionUtil(Context context, WalletTransctionUtil.TransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        this.mContext = context;
    }

    
    public Observable<String> getBalance(WalletEntity wallet) {
        if (null == wallet) {
            return null;
        }
        
        int walletType = wallet.getType();
        List<AssertBean> asserts = WalletDBUtil.getInstent(mContext).getMustWallet(wallet.getType());
        if (asserts == null || asserts.size() == 0) {
            return null;
        }
        if (walletType == MCC_COIN) {
            String mainToken = BuildConfig.EVMOS_FAKE_UNINT;
            AssertBean main = null;
            for (int i=0; i<asserts.size(); i++) {
                if (mainToken.equalsIgnoreCase(asserts.get(i).getShort_name())) {
                    main = asserts.get(i);
                    break;
                }
            }
            return getBalance(wallet, main);
        } else {

            return getBalance(wallet, asserts.get(0));
        }
    }

    
    public Observable<String> getBalance(WalletEntity wallet, AssertBean assertBean) {
        if (null == wallet || null == assertBean) {
            return null;
        }
        int walletType = assertBean.getType();
        if (walletType == MCC_COIN) {
            String contract = assertBean.getContract();
            if (TextUtils.isEmpty(contract)) {
                String address = wallet.getAllAddress();
                return getMccBalance(address, assertBean);
            } else {
                String address = wallet.getDefaultAddress();
                return getEthBalance(address, assertBean);
            }
        } else {
            String address = wallet.getAllAddress();
            return getEthBalance(address, assertBean);
        }
    }

    
    private Observable<String> getMccBalance(String address, AssertBean assertBean) {
        String coinName = assertBean.getShort_name();
        return rpcApi.getEvmosOneBalance(address, coinName)
                .map(new io.reactivex.functions.Function<EvmosOneBalanceBean, String>() {
                    @Override
                    public String apply(EvmosOneBalanceBean value) throws Exception {
                        if (null != value) {
                            String remain = value.getBalance(assertBean.getDecimal());
                            return remain;
                        } else {
                            throw new Exception("get balance value is null");
                        }
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    private Observable<String> getEthBalance(String address, AssertBean assertBean) {
        String contract = assertBean.getContract();
        int walletType = assertBean.getType();
        return rpcApi.getEthBanlance(address, contract, walletType)
                .map(new io.reactivex.functions.Function<FilBalanceBean, String>() {
                    @Override
                    public String apply(FilBalanceBean value) throws Exception {
                        if (null != value && !TextUtils.isEmpty(value.getResult())) {
                            String bigNum = HexUtils.hextoTen(value.getResult());
                            String balance = AllUtils.getTenDecimalValue(bigNum, assertBean.getDecimal(), 6);
                            return balance;
                        } else {
                            throw new Exception("get eth balance value is null");
                        }
                    }
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public void getNonce(String pwd, TransferBean mBean) {
        this.mBean = mBean;
        
        WalletEntity walletEntity = walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType());
        if (null == walletEntity) {
            ToastUtil.showToast("");
            return;
        }
        if (!walletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        try {
            if (null != mListen)
                mListen.showLoading();
            if (!TextUtils.isEmpty(mBean.getNonce())) {
                getChainId(walletEntity, pwd, mBean.getNonce());
                return;
            }
            Map<String, Object> params = new TreeMap<>();
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_getTransactionCount");
            params.put("id", MeApi.getETHID());
            List<Object> addr = new ArrayList<>();
            addr.add(mBean.getPayaddress());
            addr.add("latest");
            params.put("params", addr);
            rpcApi.getData(new Gson().toJson(params), walletEntity.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                        @Override
                        public void onNexts(FilBalanceBean baseEntity) {
                            if (null == baseEntity.getError()) {
                                getChainId(walletEntity, pwd, HexUtils.hextoTen(baseEntity.getResult()));
                            } else {
                                if (null != mListen)
                                    mListen.onFail(baseEntity.getError().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.caozuo_fail));
        }
    }

    
    public void getChainId(WalletEntity walletEntity, String pwd, String nonce) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_chainId");
            params.put("id", MeApi.getETHID());
            List<Object> addr = new ArrayList<>();
            params.put("params", addr);
            rpcApi.getData(new Gson().toJson(params), walletEntity.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                        @Override
                        public void onNexts(FilBalanceBean baseEntity) {
                            if (null == baseEntity.getError()) {
                                LogUtil.d(nonce + "       " + HexUtils.hextoTen(baseEntity.getResult()));
                                signTransfer(pwd, nonce, HexUtils.hextoTen(baseEntity.getResult()));
                            } else {
                                if (null != mListen)
                                    mListen.onFail(baseEntity.getError().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.caozuo_fail));
        }
    }

    
    private void signTransfer(String pwd, String nonce, String chainId) {
        Log.v("fsefsfsefs", nonce + "");
        Log.v("fsefsfsefs", chainId + "");
        ThreadManager.getNormalPool().execute(new Runnable() {
            @SuppressLint("CheckResult")
            @Override
            public void run() {
                try {
                    String gasPrice = new BigDecimal(mBean.getGasprice()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Math.pow(10, 9))).toBigInteger().toString();
                    BigDecimal amount = new BigDecimal("0");
                    if (!TextUtils.isEmpty(mBean.getPrice())) {
                        amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, mBean.getDecimal())));
                    }
                    String gasLimit = HexUtils.tenToHex((mBean.getGascount() + ""));
                    String price = HexUtils.tenToHex(gasPrice);
                    String at = HexUtils.tenToHex(amount.toPlainString());
                    LogUtil.d(gasLimit + "              " + price + "     " + at);
                    Ethereum.SigningInput.Builder builder = Ethereum.SigningInput.newBuilder();
                    builder.setNonce(ByteString.copyFrom(Numeric.hexStringToByteArray(HexUtils.tenToHex(nonce))));
                    builder.setChainId(ByteString.copyFrom(Numeric.hexStringToByteArray(HexUtils.tenToHex(chainId))));
                    builder.setGasLimit(ByteString.copyFrom(Numeric.hexStringToByteArray(gasLimit)));
                    builder.setGasPrice(ByteString.copyFrom(Numeric.hexStringToByteArray(price)));
                    if (!TextUtils.isEmpty(mBean.getMaxFeePerGas()) && SpUtil.getFeeStatus() == 1 && mBean.getType() == WalletUtil.ETH_COIN) {
                        String maxFeePerGas = new BigDecimal(mBean.getMaxFeePerGas()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Math.pow(10, 9))).toBigInteger().toString();
                        String maxPriorityFeePerGas = new BigDecimal(mBean.getMaxPriorityFeePerGas()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Math.pow(10, 9))).toBigInteger().toString();
                        String maxFee = HexUtils.tenToHex(maxFeePerGas);
                        String maxPriority = HexUtils.tenToHex(maxPriorityFeePerGas);
                        builder.setMaxFeePerGas(ByteString.copyFrom(Numeric.hexStringToByteArray(maxFee)));
                        builder.setMaxInclusionFeePerGas(ByteString.copyFrom(Numeric.hexStringToByteArray(maxPriority)));
                        builder.setTxMode(Ethereum.TransactionMode.Enveloped);
                    }
                    builder.setToAddress(mBean.getAllAddress());
                    String p = WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPrivateKey(), pwd);
                    builder.setPrivateKey(ByteString.copyFrom(Numeric.hexStringToByteArray(p)));
                    Ethereum.Transaction.Builder transaction = Ethereum.Transaction.newBuilder();
                    Log.v("fsefgdfsdfn", mBean.getData() + "==");
                    if (TextUtils.isEmpty(mBean.getData())) {
                        if (TextUtils.isEmpty(mBean.getTokenType())) {
                            LogUtil.d("ETH");
                            Ethereum.Transaction.Transfer.Builder transfer = Ethereum.Transaction.Transfer.newBuilder();
                            transfer.setAmount(ByteString.copyFrom(Numeric.hexStringToByteArray(at)));
                            transaction.setTransfer(transfer);
                        } else {
                            LogUtil.d("");
                            builder.setToAddress(mBean.getTokenType());
                            Ethereum.Transaction.ERC20Transfer.Builder transfer = Ethereum.Transaction.ERC20Transfer.newBuilder();
                            transfer.setAmount(ByteString.copyFrom(Numeric.hexStringToByteArray(at)));
                            transfer.setTo(mBean.getAllAddress());
                            transaction.setErc20Transfer(transfer.build());
                        }
                    } else {
                        LogUtil.d("");
                        builder.setToAddress(mBean.getTokenType());
                        Ethereum.Transaction.Transfer.Builder transfer = Ethereum.Transaction.Transfer.newBuilder();
                        String data = mBean.getData();
                        if (!TextUtils.isEmpty(data) && data.startsWith("0xa9059cbb")) {
                            
                            transfer.setAmount(ByteString.copyFrom(Numeric.hexStringToByteArray("0x0")));
                        } else {
                            
                            transfer.setAmount(ByteString.copyFrom(Numeric.hexStringToByteArray(at)));
                        }
                        transfer.setData(ByteString.copyFrom(Numeric.hexStringToByteArray(mBean.getData())));
                        transaction.setTransfer(transfer.build());
                    }
                    builder.setTransaction(transaction);
                    Ethereum.SigningOutput sign = AnySigner.sign(builder.build(), CoinType.ETHEREUM, Ethereum.SigningOutput.parser());

                    final String hexValue;
                    if (TextUtils.isEmpty(mBean.tokenId)) {
                        hexValue = Hex16.encodeHexStr(sign.getEncoded().toByteArray());
                    } else {
                        
                        String key;
                        key = WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPrivateKey(), pwd);
                        if (key.startsWith("0x")) {
                            key = key.substring(2, key.length());
                        }

                        ECKeyPair pair = ECKeyPair.create(new BigInteger(key, 16));
                        Credentials credentials = Credentials.create(pair);
                        BigInteger tokenId = new BigDecimal(mBean.tokenId).toBigInteger();
                        Log.d("fenghl", nonce + "tokenId = " + tokenId + "  tokenId Uint256 = " + new Uint256(tokenId).toString());
                        Function function = new Function(
                                "transferFrom",
                                Arrays.asList(new org.web3j.abi.datatypes.Address(mBean.getPayaddress()), new org.web3j.abi.datatypes.Address(mBean.getAllAddress()), new Uint256(tokenId)),
                                Arrays.asList(new TypeReference<Type>() {
                                }));
                        String encodedFunction = FunctionEncoder.encode(function);
                        RawTransaction rawTransaction = RawTransaction.createTransaction(new BigInteger(nonce),
                                new BigDecimal(gasPrice).toBigInteger(),
                                new BigDecimal(mBean.getGascount()).toBigInteger(),
                                mBean.getTokenType(), encodedFunction);
                        
                        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                        hexValue = Numeric.toHexString(signMessage);
                    }
                    LogUtil.d("" + hexValue);

                    if (mContext instanceof Activity) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                jiaoyiHttp(hexValue, mBean.getType());
                            }
                        });
                    } else {
                        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(data->{
                            jiaoyiHttp(hexValue, mBean.getType());
                        });
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                    if (null!= mContext && mContext instanceof Activity) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != mListen)
                                    mListen.onFail(mContext.getString(R.string.transaction_error));
                            }
                        });
                    } else {
                        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(data->{
                            if (null != mListen)
                                mListen.onFail(mContext.getString(R.string.transaction_error));
                        });
                    }


                }
            }
        });
    }

    
    private void jiaoyiHttp(String hexValue, int type) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_sendRawTransaction");
            params.put("id", MeApi.getETHID());
            List<Object> data = new ArrayList<>();
            if (!TextUtils.isEmpty(hexValue) && !hexValue.startsWith("0x")) {
                hexValue = "0x" + hexValue;
            }
            data.add(hexValue);
            params.put("params", data);
            rpcApi.getData(new Gson().toJson(params), type).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                        @Override
                        public void onNexts(FilBalanceBean baseEntity) {
                            if (null != baseEntity && null == baseEntity.getError() && !TextUtils.isEmpty(baseEntity.getResult())) {
                                String txHash = baseEntity.getResult();
                                timerConfirmTxResult(txHash, type);
                                
                                LogUtil.d(baseEntity.getResult());
                            } else {
                                if (null != mListen) {
                                    mListen.onFail(baseEntity.getError().getMessage());
                                }

                            }
                        }
                    });
        } catch (Exception e) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.transaction_error));
        }
    }


    
    private void timerConfirmTxResult(String txHash, int walletType) {
        if (null == mListen || TextUtils.isEmpty(txHash)) {
            if (null != mListen) {
                mListen.onFail("tx hash data is null return!!");
            }
            return;
        }
        rpcApi.timerCheckEthTxResult(txHash, walletType, null, new NextSubscriber<EthTxResultBean>() {
            @Override
            public void dealData(EthTxResultBean value) {
                if (null == value || value.isSuccess() || value.isUnknown()) {
                    if (null != mListen) {
                        mListen.showTransctionSuccess(txHash+"");
                    }
                } else if (null != mListen) {
                    mListen.onFail(value.getInfo()+"");
                }
            }

            @Override
            protected void dealError(Throwable e) {
                if (null != mListen) {
                    mListen.showTransctionSuccess(txHash+"");
                }
            }
        });
    }


    
    public void getKGPrice(String fromAddress, String tokenType, int wallettype, String data, String hexValue) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_estimateGas");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        String to = fromAddress;
        if (!TextUtils.isEmpty(tokenType)) {
            to = tokenType;
            if (TextUtils.isEmpty(data)) {
                String address = fromAddress.substring(2);
                data = "0xa9059cbb000000000000000000000000" + address + "0000000000000000000000000000000000000000000000000000000000000000";
            }
        }
        if (TextUtils.isEmpty(hexValue)) {
            hexValue = "0x0";
        } else if (!hexValue.startsWith("0x")) {
            hexValue = "0x" + hexValue;
        }
        addr.add(new EthEstimateGasBean(hexValue, to, data, fromAddress));
        params.put("params", addr);
        rpcApi.getData(new Gson().toJson(params), wallettype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            if (null != mListen) {
                                String gaslimit = HexUtils.hextoTen(baseEntity.getResult());
                                if (!TextUtils.isEmpty(tokenType)) {
                                    gaslimit = new BigDecimal(gaslimit).multiply(new BigDecimal(2)).toPlainString();
                                }
                                mListen.showGasCount(gaslimit);
                            }
                            getDefGasprice(wallettype);
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (null != mListen)
                            mListen.onFail(e.getMessage());
                    }
                });
    }

    
    public void getDefGasprice(int walletype) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_gasPrice");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        params.put("params", addr);
        rpcApi.getData(new Gson().toJson(params), walletype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            String pr = HexUtils.hextoTen(baseEntity.getResult());
                            String gasPrice = new BigDecimal(pr).divide(new BigDecimal(Math.pow(10, 9)), 0, BigDecimal.ROUND_UP).toBigInteger().toString();
                            LogUtil.d("eth_gasPrice       " + gasPrice);
                            if (null != mListen) {
                                mListen.showGasprice(new GasPriceBean(gasPrice));
                                mListen.showDefGasprice(gasPrice + "");
                            }
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null != mListen)
                            mListen.onFail(e.getMessage());
                    }
                });
    }

    
    public void getBlockByNumber(int walletype) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_getBlockByNumber");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        addr.add("latest");
        addr.add(false);
        params.put("params", addr);
        rpcApi.getObjectData(new Gson().toJson(params), walletype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BasefeeBean>(mContext) {
                    @Override
                    public void onNexts(BasefeeBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            BlockByNumberBean blockByNumberBean = gson.fromJson(gson.toJson(baseEntity.getResult()), BlockByNumberBean.class);
                            String baseFeePerGas = blockByNumberBean.getBaseFeePerGas();
                            String baseFee = HexUtils.hextoTen(baseFeePerGas);
                            if (null != mListen) {
                                
                                baseFee = new BigDecimal(baseFee).multiply(new BigDecimal(2)).toPlainString();
                                mListen.showEip1559(baseFee);
                            }
                        } else {

                        }
                    }
                });
    }

    
    public void getMaxPriorityFeePerGas(int walletype) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_maxPriorityFeePerGas");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        params.put("params", addr);
        rpcApi.getData(new Gson().toJson(params), walletype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        String fee = "1";
                        if (null == baseEntity.getError()) {
                            fee = HexUtils.hextoTen(baseEntity.getResult());
                            BigDecimal feeBig = new BigDecimal(fee).divide(new BigDecimal("1000000000"), 0, BigDecimal.ROUND_HALF_UP);
                            fee = feeBig.toPlainString();
                        }
                        GasPriceBean bean = new GasPriceBean();
                        bean.setLow(0);
                        bean.setUp(100);
                        if (null != mListen) {
                            mListen.showGasprice(bean);
                            mListen.showDefGasprice(fee);
                        }
                        getBlockByNumber(walletype);
                    }
                });
    }

    
    public void getGasLimit(String fromAddress, String tokenType, String basefee, int wallettype) {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_estimateGas");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        String to = fromAddress;
        String data = "0x";
        if (!TextUtils.isEmpty(tokenType)) {
            to = tokenType;
            String address = fromAddress.substring(2);
            data = "0xa9059cbb000000000000000000000000" + address + "0000000000000000000000000000000000000000000000000000000000000000";
        }
        EthEstimateGasBean estimateGasBean = new EthEstimateGasBean("0x0", to, data, fromAddress);
        String maxFeePerGas = new BigDecimal(basefee).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(Math.pow(10, 9))).toBigInteger().toString();
        String maxFee = HexUtils.tenToHex(maxFeePerGas);
        estimateGasBean.setMaxFeePerGas(maxFee);
        estimateGasBean.setMaxPriorityFeePerGas("0x0");
        addr.add(estimateGasBean);
        params.put("params", addr);
        rpcApi.getData(new Gson().toJson(params), wallettype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            if (null != mListen)
                                mListen.showGasCount(HexUtils.hextoTen(baseEntity.getResult()));
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }

    
    public void getContractKGPrice(String fromAddress, String contractAddr, int wallettype, String data) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_estimateGas");
        params.put("id", MeApi.getETHID());
        List<Object> addr = new ArrayList<>();
        String to = fromAddress;
        if (!TextUtils.isEmpty(contractAddr)) {
            to = contractAddr;
            if (TextUtils.isEmpty(data)) {
                String address = fromAddress.substring(2);
                data = "0xa9059cbb000000000000000000000000" + address + "0000000000000000000000000000000000000000000000000000000000000000";
            }
        }
        addr.add(new EthEstimateGasBean("0x0", to, data, fromAddress));
        params.put("params", addr);
        rpcApi.getData(new Gson().toJson(params), wallettype).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            if (null != mListen) {
                                String gaslimit = HexUtils.hextoTen(baseEntity.getResult());
                                if (!TextUtils.isEmpty(contractAddr)) {
                                    gaslimit = new BigDecimal(gaslimit).multiply(new BigDecimal(2)).toPlainString();
                                }
                                mListen.showGasCount(gaslimit);
                            }
                            getDefGasprice(wallettype);
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }

}
