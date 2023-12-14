

package com.wallet.ctc.crypto;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.wallet.ctc.R;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.BasefeeBean;
import com.wallet.ctc.model.blockchain.BlockByNumberBean;
import com.wallet.ctc.model.blockchain.EthEstimateGasBean;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransctionInitBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.Hex16;
import com.wallet.ctc.util.HexUtils;
import com.wallet.ctc.util.LogUtil;
import com.wallet.ctc.view.dialog.TransferEthDialog;

import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import common.app.utils.SpUtil;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.CoinType;
import wallet.core.jni.proto.Ethereum;



public class EthTransctionImpl implements Transction{
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private RpcApi rpcApi = new RpcApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.TransctionListen mListen;
    public EthTransctionImpl(Context context, WalletTransctionUtil.TransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        this.mContext = context;

    }

    @Override
    public void init(TransctionInitBean initBean) {
        if(null==initBean){
            return;
        }
        if(SpUtil.getFeeStatus()==1&&initBean.getType()== WalletUtil.ETH_COIN) {
            getMaxPriorityFeePerGas(initBean.getType());
        }else {
            getKGPrice(initBean.getFromAddress(), initBean.getTokenType(), initBean.getType(),initBean.getData());
        }
    }

    @Override
    public void doTrans(TransferBean mBean, String pwd) {
        int type=mBean.getType();
        TransferEthDialog transferDialog = new TransferEthDialog(mContext);
        transferDialog.setOnDismiss(new TransferEthDialog.Dismiss() {
            @Override
            public void dismiss() {
                if (null == mContext || null == mListen) {
                    return;
                }
                mListen.onFail("Canceled");
            }
        });
        transferDialog.setTrans(new TransferEthDialog.goTransfer() {
            @Override
            public void goTransfer(String pwd) {
                if (null == mContext || null == mListen) {
                    return;
                }
                if (type == WalletUtil.ETH_COIN || type == WalletUtil.ETF_COIN || type == WalletUtil.DMF_COIN || type == WalletUtil.DMF_BA_COIN || type == WalletUtil.HT_COIN || type == WalletUtil.BNB_COIN) {
                    getNonce(pwd,mBean);
                }
            }
        });
        transferDialog.show(mBean);

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
        ThreadManager.getNormalPool().execute(new Runnable() {
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
                        transfer.setData(ByteString.copyFrom(Numeric.hexStringToByteArray(mBean.getData())));
                        transaction.setTransfer(transfer.build());
                    }
                    builder.setTransaction(transaction);
                    Ethereum.SigningOutput sign = AnySigner.sign(builder.build(), CoinType.ETHEREUM, Ethereum.SigningOutput.parser());
                    String hexValue = Hex16.encodeHexStr(sign.getEncoded().toByteArray());
                    LogUtil.d("" + hexValue);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jiaoyiHttp(hexValue, mBean.getType());
                        }
                    });
                } catch (Exception e) {
                     e.printStackTrace();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mListen)
                                mListen.onFail(mContext.getString(R.string.transaction_error));
                        }
                    });

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
                hexValue = "0x"+hexValue;
            }
            data.add(hexValue);
            params.put("params", data);
            rpcApi.getData(new Gson().toJson(params), type).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                        @Override
                        public void onNexts(FilBalanceBean baseEntity) {
                            if (null == baseEntity.getError()) {
                                if (null != mListen)
                                    mListen.showTransctionSuccess(baseEntity.getResult());
                                LogUtil.d(baseEntity.getResult());
                            } else {
                                if (null != mListen)
                                    mListen.onFail(baseEntity.getError().getMessage());
                            }
                        }
                    });
        } catch (Exception e) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.transaction_error));
        }
    }
    
    public void getKGPrice(String fromAddress, String tokenType, int wallettype, String data) {
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
        addr.add(new EthEstimateGasBean("0x0", to, data, fromAddress));
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


}
