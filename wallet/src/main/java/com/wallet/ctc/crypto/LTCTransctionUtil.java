

package com.wallet.ctc.crypto;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.LtcApi;
import com.wallet.ctc.model.blockchain.BchEstimateFeeBean;
import com.wallet.ctc.model.blockchain.BchTransResultBean;
import com.wallet.ctc.model.blockchain.BchTxIdBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;

import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.BitcoinScript;
import wallet.core.jni.CoinType;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.proto.Bitcoin;



public class LTCTransctionUtil {
    private static final String TAG = "LTCTRANS";
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private LtcApi mApi = new LtcApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.LtcTransctionListen mListen;

    public LTCTransctionUtil(Context context, WalletTransctionUtil.LtcTransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        this.mContext = context;

    }

    
    public void getLTCestimateFee(TransferBean transferBean) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        mApi.getLTCestimateFee(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BchEstimateFeeBean>(mContext) {
                    @Override
                    public void onNexts(BchEstimateFeeBean baseEntity) {
                        if (baseEntity != null) {
                            if (baseEntity.getResult() != null) {
                                if (mListen != null)
                                    mListen.showGasEstimateFee(baseEntity.getResult());
                            } else if (baseEntity.getMessage() != null) {
                                if (mListen != null) mListen.onFail(baseEntity.getMessage());
                            }
                        }
                    }
                });
    }

    public void getLTCTxId(TransferBean transferBean, String xpubAddr, String pwd) {
        if (!walletDBUtil.getWalletInfoByAddress(transferBean.getPayaddress(), transferBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        if (null != mListen) {
            mListen.showLoading();
        }
        mApi.getLTCTxId(xpubAddr).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<BchTxIdBean>>(mContext) {
                    @Override
                    public void onNexts(List<BchTxIdBean> baseEntity) {
                        if (baseEntity != null) {
                            if (baseEntity != null && baseEntity.size() > 0) {
                                trans(transferBean, baseEntity, pwd);
                            }
                        }
                    }
                });
    }

    public void trans(TransferBean transferBean, List<BchTxIdBean> txid, String pwd) {
        if (null != mListen&&txid!=null) {
            mListen.showLoading();
        }
       
       
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                
                CoinType coinBtc = CoinType.LITECOIN;
                
                String addressBtc = transferBean.getPayaddress();
                byte[] script = BitcoinScript.lockScriptForAddress(addressBtc, coinBtc).data();

                Bitcoin.SigningInput.Builder input = Bitcoin.SigningInput.newBuilder();
                long amount = new BigDecimal(transferBean.getPrice()).multiply(new BigDecimal(Math.pow(10, transferBean.getDecimal()))).toBigInteger().longValue();
                input.setAmount(amount);
                input.setHashType(BitcoinScript.hashTypeForCoin(CoinType.LITECOIN));
                String toAddr = transferBean.getAllAddress();
                input.setToAddress(toAddr);
                String payAddr = transferBean.getPayaddress();
                input.setChangeAddress(payAddr);
                long byteFee = new BigDecimal(transferBean.getKuanggong()).multiply(new BigDecimal(Math.pow(10, transferBean.getDecimal()))).divide(new BigDecimal("1000")).toBigInteger().longValue();
                input.setByteFee(byteFee);
                input.setCoinType(coinBtc.value());
                String p = WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(transferBean.getPayaddress(), transferBean.getType()).getmPrivateKey(), pwd);
                PrivateKey privateKey = new PrivateKey(Numeric.hexStringToByteArray(p));
                input.addPrivateKey(ByteString.copyFrom(privateKey.data()));

                
                for (int i = 0; i < txid.size(); i++) {
                    BchTxIdBean txIdBean=txid.get(i);
                    String utxoTxId = txIdBean.getTxid();
                    Bitcoin.OutPoint.Builder outPoint = Bitcoin.OutPoint.newBuilder();
                    ByteString hashBytes = ByteString.copyFrom(hexStringReverveToByteArray(utxoTxId));
                    outPoint.setHash(hashBytes);
                    outPoint.setIndex(txIdBean.getVout());
                    Bitcoin.UnspentTransaction.Builder utxo = Bitcoin.UnspentTransaction.newBuilder();
                    utxo.setAmount(new BigDecimal(txIdBean.getValue()).toBigInteger().longValue());
                    utxo.setOutPoint(outPoint.build());
                    utxo.setScript(ByteString.copyFrom(script));

                    input.addUtxo(utxo.build());
                }


                
                Bitcoin.TransactionPlan plan = null;
                try {
                    plan = AnySigner.plan(input.build(), coinBtc, Bitcoin.TransactionPlan.parser());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "plan error"+e.getMessage());
                }
                
                if (null != plan) {
                    Log.i(TAG, "plan != null  fee:"+plan.getFee()+",amount:"+plan.getAmount()+", availableAmount:"+plan.getAvailableAmount()+", change:"+plan.getChange());
                    input.setPlan(plan);
                }
                try {
                    Bitcoin.SigningOutput output = AnySigner.sign(input.build(), coinBtc, Bitcoin.SigningOutput.parser());



                    byte[] signByteArray = output.getEncoded().toByteArray();
                    String sign = Numeric.toHexString(signByteArray, 0, signByteArray.length, false);
                    Log.v(TAG, "sign="+sign);
                    sendTrans(sign);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                    if (mListen != null) {
                        mListen.onFail("sign fail :"+e.getMessage());
                    }
                }
            }
        });
    }
    
    private byte[] hexStringReverveToByteArray(String str) {
        int length = str.length();
        String HEX_CHARS = "0123456789ABCDEF";
        int size = length/2;
        byte[] result = new byte[size];
        for (int i =0; i< length; i+=2) {
            int firstIndex = HEX_CHARS.indexOf(String.valueOf(str.charAt(i)).toUpperCase());
            int secondIndex = HEX_CHARS.indexOf(String.valueOf(str.charAt(i+1)).toUpperCase());
            byte octet = (byte)(firstIndex<<4|secondIndex);
            int index = size - (i>>1) - 1;
            result[index] = octet;
          
        }
        return result;
    }

    
    private void sendTrans(String sign) {
        if (TextUtils.isEmpty(sign)) {
            if (mListen != null) {
                mListen.onFail("no find sige");
            }
            return;
        }
        mApi.trans(sign).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BchTransResultBean>(mContext) {
                    @Override
                    public void onNexts(BchTransResultBean baseEntity) {
                        Log.v(TAG, "result="+baseEntity);
                        if (baseEntity != null) {

                            if (baseEntity.getResult() != null) {
                                if (mListen != null) {
                                    mListen.showTransctionSuccess(baseEntity.getResult()+"");
                                }
                            } else if (baseEntity.getMessage() != null) {
                                if (mListen != null) {
                                    mListen.onFail(baseEntity.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null!=mListen){
                            mListen.onFail(e.getMessage());
                        }
                    }
                });
    }


}
