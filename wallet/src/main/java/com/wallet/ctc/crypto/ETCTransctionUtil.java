

package com.wallet.ctc.crypto;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.EtcApi;
import com.wallet.ctc.model.blockchain.EtcBalanceBean;
import com.wallet.ctc.model.blockchain.GasBean;
import com.wallet.ctc.model.blockchain.GoTransBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class ETCTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private EtcApi mApi = new EtcApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.EtcTransctionListen mListen;

    public ETCTransctionUtil(Context context, WalletTransctionUtil.EtcTransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        this.mContext = context;

    }

    
    private int lastNonce = -1;

    public void getNonce(String pwd, TransferBean mBean) {

        this.mBean = mBean;
        if (!walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != mListen)
                        mListen.showLoading();
                    Log.v("fsevvfsefsfsef", lastNonce + "---");
                    if (lastNonce != -1) {
                        getChainId(pwd, new BigInteger((lastNonce += 1) + ""));
                    } else {
                        Map<String, Object> params = new TreeMap<>();
                        params.put("jsonrpc", "2.0");
                        params.put("method", "eth_getTransactionCount");
                        params.put("id", 103);
                        List<String> addr = new ArrayList<>();
                        addr.add(mBean.getPayaddress());
                        addr.add("latest");
                        params.put("params", addr);
                        mApi.getBlockNumber(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseSubscriber<EtcBalanceBean>(mContext) {
                                    @Override
                                    public void onNexts(EtcBalanceBean baseEntity) {
                                        if (baseEntity.getId() == 103 && baseEntity.getResult() != null) {
                                            if (baseEntity.getResult().startsWith("0x")) {
                                                String nonce16 = baseEntity.getResult().substring(2, baseEntity.getResult().length());

                                                getChainId(pwd, new BigInteger(nonce16, 16));
                                            }
                                        } else {
                                            if (null != mListen)
                                                mListen.onFail(baseEntity.getError().getMessage());
                                        }
                                    }
                                });
                    }
                } catch (Exception e) {
                    if (null != mListen)
                        mListen.onFail(mContext.getString(R.string.caozuo_fail));
                }
            }
        });
    }

    private void getChainId(String pwd, BigInteger nonce) {
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_chainId");
        params.put("id", 106);
        List<String> addr = new ArrayList<>();
        params.put("params", addr);
        mApi.getBlockNumber(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EtcBalanceBean>(mContext) {
                    @Override
                    public void onNexts(EtcBalanceBean baseEntity) {
                        if (baseEntity.getId() == 106 && baseEntity.getResult() != null) {
                            if (baseEntity.getResult().startsWith("0x")) {
                                String chainId = baseEntity.getResult().substring(2, baseEntity.getResult().length());

                                signTransfer(pwd, nonce, new BigInteger(chainId, 16).toString(10));
                            }
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }

    
    private void signTransfer(String pwd, BigInteger nonce, String chainId) {
        Log.v("fsfsefsfes", nonce.intValue() + "==signTransfer");
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    GoTransBean goTransBean = new GoTransBean();
                    BigDecimal amount = new BigDecimal("0");
                    if (!TextUtils.isEmpty(mBean.getPrice())) {
                        amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, mBean.getDecimal())));
                    }
                    goTransBean.setAmount(amount.toPlainString());
                    goTransBean.setGasprice(new BigDecimal(mBean.getGasprice()).intValue() + "");
                    goTransBean.setGaslimit(mBean.getGascount());
                    goTransBean.setNonce(nonce.intValue());
                    String p = WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPrivateKey(), pwd);
                    
                    goTransBean.setPrivatekey(p);
                    goTransBean.setToaddress(mBean.getAllAddress());
                    goTransBean.setTokenaddress(mBean.getTokenType());
                    goTransBean.setData(mBean.getData());
                    
                    String hexValue = WalletUtil.getTrandsSign(gson.toJson(goTransBean), mBean.getType());



                    

                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jiaoyiHttp(hexValue);
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

    
    private void jiaoyiHttp(String hexValue) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("jsonrpc", "2.0");
            params.put("method", "eth_sendRawTransaction");
            params.put("id", 102);
            List<String> addr = new ArrayList<>();
            addr.add(hexValue);
            params.put("params", addr);
            mApi.toTrans(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<EtcBalanceBean>(mContext) {
                        @Override
                        public void onNexts(EtcBalanceBean baseEntity) {
                            if (baseEntity.getId() == 102 && baseEntity.getResult() != null) {
                                mListen.showTransctionSuccess(baseEntity.getResult());
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

    
    public void getDefGasprice(int wallettype) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_gasPrice");
        params.put("id", 101);
        List<String> addr = new ArrayList<>();
        params.put("params", addr);
        mApi.getEtcGasPrice(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EtcBalanceBean>(mContext) {
                    @Override
                    public void onNexts(EtcBalanceBean baseEntity) {
                        if (baseEntity.getId() == 101 && baseEntity.getResult() != null) {
                            if (null != mListen) {
                                String count = "0";
                                if (baseEntity.getResult().startsWith("0x")) {
                                    count = baseEntity.getResult().substring(2, baseEntity.getResult().length());
                                    mListen.showDefGasprice(new BigInteger(count, 16).toString(10));
                                } else {
                                    mListen.showDefGasprice(baseEntity.getResult() + "");
                                }
                            }
                            
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }


    
    public void getGasCount(TransferBean bean) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "eth_estimateGas");
        params.put("id", 104);
        List<Object> addr = new ArrayList<>();
        GasBean gasBean = new GasBean();
        gasBean.setFrom(bean.getPayaddress());
        gasBean.setTo(bean.getAllAddress());
        String price = new BigDecimal(bean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, bean.getDecimal()))).toPlainString();
        BigInteger b = new BigDecimal(price).toBigInteger();
        gasBean.setValue("0x" + (b.toString(16)));
        
        
        addr.add(gasBean);
        params.put("params", addr);
        mApi.getEtcCount(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EtcBalanceBean>(mContext) {
                    @Override
                    public void onNexts(EtcBalanceBean baseEntity) {
                        if (baseEntity.getId() == 104 && baseEntity.getResult() != null) {
                            if (null != mListen) {
                                if (baseEntity.getResult().startsWith("0x")) {
                                    String count = baseEntity.getResult().substring(2, baseEntity.getResult().length());
                                    mListen.showGasCount(new BigInteger(count, 16).toString(10));
                                } else {
                                    mListen.showGasCount(baseEntity.getResult());
                                }

                            }

                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }
}
