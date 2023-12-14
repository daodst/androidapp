

package com.wallet.ctc.crypto;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.FilApi;
import com.wallet.ctc.model.blockchain.FilBalanceBean;
import com.wallet.ctc.model.blockchain.FilChainHeadBean;
import com.wallet.ctc.model.blockchain.FilGasFeeBean;
import com.wallet.ctc.model.blockchain.FilGasMsgBean;
import com.wallet.ctc.model.blockchain.FilTransResultBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.Hex16;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.CoinType;
import wallet.core.jni.proto.Filecoin;



public class FILTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private FilApi mApi = new FilApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.FilTransctionListen mListen;

    public FILTransctionUtil(Context context, WalletTransctionUtil.FilTransctionListen mListen) {
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
                    if (lastNonce != -1) {
                        signTransfer(pwd, new BigInteger((lastNonce += 1) + ""));
                    } else {
                        if (null != mListen) {
                            mListen.showLoading();
                        }
                        Map<String, Object> params = new TreeMap<>();
                        params.put("jsonrpc", "2.0");
                        params.put("method", "Filecoin.MpoolGetNonce");
                        params.put("id", 203);
                        List<Object> addr = new ArrayList<>();
                        addr.add(mBean.getPayaddress());
                        params.put("params", addr);
                        mApi.getNonce(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                                    @Override
                                    public void onNexts(FilBalanceBean baseEntity) {
                                        if (baseEntity.getId() == 203 && baseEntity.getResult() != null) {
                                            lastNonce=new BigInteger(baseEntity.getResult()).intValue();
                                            signTransfer(pwd, new BigInteger(baseEntity.getResult()));
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

    
    private void signTransfer(String pwd, BigInteger nonce) {

        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BigDecimal amount = new BigDecimal("0");
                    if (!TextUtils.isEmpty(mBean.getPrice())) {
                        amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, mBean.getDecimal())));
                    }
                    Filecoin.SigningInput.Builder builder = Filecoin.SigningInput.newBuilder();
                    builder.setNonce(nonce.longValue());
                    builder.setGasFeeCap(ByteString.copyFrom(new BigInteger(mBean.getGasFeeCap()).toByteArray()));
                    builder.setGasLimit(mBean.getGascount());
                    builder.setGasPremium(ByteString.copyFrom("0".getBytes()));
                    builder.setTo(mBean.getAllAddress());
                    builder.setValue(ByteString.copyFrom(new BigInteger(amount.toBigInteger().toString()).toByteArray()));
                   String p= WalletUtil.getDecryptionKey( walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(), mBean.getType()).getmPrivateKey(),pwd);
                    builder.setPrivateKey(ByteString.copyFrom(Hex16.hexStringToByteArray(p)));
                   Filecoin.SigningOutput sign= AnySigner.sign(builder.build(), CoinType.FILECOIN, Filecoin.SigningOutput.parser());



                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            jiaoyiHttp(sign.getJson());
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
    public static String toHexString(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder("");
        if (byteArray == null || byteArray.length <= 0)
            return null;
        for (int i = 0; i < byteArray.length; i++) {
            int v = byteArray[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hexString.append(0);
            }
            hexString.append(hv);
        }
        return hexString.toString().toLowerCase();
    }
    
    private void jiaoyiHttp(String hexValue) {
        Log.v("xuccnn",hexValue);

        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("jsonrpc", "2.0");
            params.put("method", "Filecoin.MpoolPush");
            params.put("id", 208);
            List<Object> data = new ArrayList<>();
            data.add(new Gson().fromJson(hexValue,new TypeToken<JsonObject>(){}.getType()));
            params.put("params", data);


            mApi.toTrans(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<FilTransResultBean>(mContext) {
                        @Override
                        public void onNexts(FilTransResultBean baseEntity) {
                            if (baseEntity.getId() == 208 && baseEntity.getResult() != null) {
                               mListen.showTransctionSuccess(baseEntity.getResult().getA());
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

    
    public void getDefGasprice(TransferBean transferBean) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "Filecoin.GasEstimateFeeCap");
        params.put("id", 201);
        List<Object> addr = new ArrayList<>();
        FilGasFeeBean filGasFeeBean = new FilGasFeeBean();
        filGasFeeBean.setFrom(transferBean.getPayaddress());
        filGasFeeBean.setTo(transferBean.getAllAddress());
        filGasFeeBean.setGasLimit(transferBean.getGascount());
        filGasFeeBean.setValue(new BigDecimal(transferBean.getPrice()).multiply(new BigDecimal(Math.pow(10, transferBean.getDecimal()))).toBigInteger().toString());
        filGasFeeBean.setGasPremium("0");
        addr.add(filGasFeeBean);
        addr.add(0);
        addr.add(new ArrayList<>());
        params.put("params", addr);
        mApi.getFilGasPrice(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilBalanceBean>(mContext) {
                    @Override
                    public void onNexts(FilBalanceBean baseEntity) {
                        if (baseEntity.getId() == 201 && baseEntity.getResult() != null) {
                            if (null != mListen) {
                                String count = "0";
                                if (baseEntity.getResult().startsWith("0x")) {
                                    count = baseEntity.getResult().substring(2, baseEntity.getResult().length());
                                    mListen.showGasEstimateFeeCap(new BigInteger(count, 16).toString(10));
                                } else {
                                    mListen.showGasEstimateFeeCap(baseEntity.getResult() + "");
                                }
                            }
                            
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }

    public void getChainHead(TransferBean transferBean) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "Filecoin.ChainHead");
        params.put("id", 205);
        List<Object> addr = new ArrayList<>();
        params.put("params", addr);
        mApi.getChainHead(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilChainHeadBean>(mContext) {
                    @Override
                    public void onNexts(FilChainHeadBean baseEntity) {
                        if (baseEntity.getId() == 205 && baseEntity.getResult() != null) {

                        } else {
                        }
                    }
                });
    }

    public void getGasEstimateMessageGas(TransferBean transferBean) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "Filecoin.GasEstimateMessageGas");
        params.put("id", 204);
        List<Object> addr = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("From", transferBean.getPayaddress());
        jsonObject.addProperty("To", transferBean.getAllAddress());
        jsonObject.addProperty("Value", new BigDecimal(transferBean.getPrice()).multiply(new BigDecimal(Math.pow(10, transferBean.getDecimal()))).toBigInteger().toString());
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("TokenAmount", "0");
        addr.add(jsonObject);
        addr.add(jsonObject1);
        addr.add(new ArrayList<>());
        params.put("params", addr);
        mApi.getGasEstimateMessageGas(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<FilGasMsgBean>(mContext) {
                    @Override
                    public void onNexts(FilGasMsgBean baseEntity) {
                        if (baseEntity.getId() == 204 && baseEntity.getResult() != null) {
                            if (mListen != null) {
                                mListen.showGasmsg(baseEntity.getResult());
                            }
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getError().getMessage());
                        }
                    }
                });
    }

}
