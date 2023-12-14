

package com.wallet.ctc.crypto;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.DotApi;
import com.wallet.ctc.model.blockchain.DotBalanceBean;
import com.wallet.ctc.model.blockchain.DotFeeBean;
import com.wallet.ctc.model.blockchain.DotMaterialBean;
import com.wallet.ctc.model.blockchain.DotTransResultBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;

import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.CoinType;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.proto.Polkadot;



public class DotTransctionUtil {
    private static final String TAG = "DotTransctionUtil";
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private DotApi mApi = new DotApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.DotTransctionListen mListen;

    public DotTransctionUtil(Context context, WalletTransctionUtil.DotTransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        this.mContext = context;

    }

    
    private int lastNonce = -1;

    public void getNonce(String pwd, TransferBean transferBean) {
        if (!walletDBUtil.getWalletInfoByAddress(transferBean.getPayaddress(), transferBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        if (null != mListen) {
            mListen.showLoading();
        }
        mApi.getDotBalance(transferBean.getPayaddress()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DotBalanceBean>(mContext) {
                    @Override
                    public void onNexts(DotBalanceBean baseEntity) {
                        if (baseEntity != null) {
                            if (!TextUtils.isEmpty(baseEntity.getNonce())) {
                                getMateria(transferBean, pwd, baseEntity.getNonce());
                            } else {
                                if (mListen != null) mListen.onFail("fail get nonce");
                            }
                        } else {
                            if (mListen != null) mListen.onFail("fail get balance info");
                        }
                    }
                });

    }


    
    public void getDotestimateFee(TransferBean transferBean, String tx) {
        if (null != mListen) {
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("tx", tx);
        mApi.getDotestimateFee(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DotFeeBean>(mContext) {
                    @Override
                    public void onNexts(DotFeeBean baseEntity) {
                        if (baseEntity != null) {
                            if (baseEntity.getPartialFee() != null) {
                                if (mListen != null)
                                    mListen.showGasEstimateFee(baseEntity.getPartialFee());
                            } else if (baseEntity.getMessage() != null) {
                                if (mListen != null) mListen.onFail(baseEntity.getMessage());
                            }
                        }
                    }
                });
    }


    
    public void getMateria(TransferBean transferBean, String pwd, String nonce) {
        if (null != mListen) {
            mListen.showLoading();
        }
        mApi.getDotMateria("").observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DotMaterialBean>(mContext) {
                    @Override
                    public void onNexts(DotMaterialBean baseEntity) {
                        if (baseEntity != null) {
                            trans(transferBean, baseEntity, pwd, nonce);
                        } else {
                            if (mListen != null) mListen.onFail("no find materia");
                        }
                    }
                });
    }


    
    
    
    
    

    public void trans(TransferBean transferBean, DotMaterialBean material, String pwd, String nonceStr) {
        if (null != mListen&& material!=null) {
            mListen.showLoading();
        }
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {

                String p = WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(transferBean.getPayaddress(), transferBean.getType()).getmPrivateKey(), pwd);
                PrivateKey privateKey = new PrivateKey(Numeric.hexStringToByteArray(p));


                int nonce = new BigInteger(nonceStr).intValue();
                Log.i(TAG, "nonce = "+nonce);

                
                String toAddr = transferBean.getAllAddress();
                Log.i(TAG, "toAddr"+toAddr);

                
                long amount = new BigDecimal(transferBean.getPrice()).multiply(new BigDecimal(Math.pow(10, transferBean.getDecimal()))).toBigInteger().longValue();
                Log.i(TAG, "amaount="+amount+", price="+transferBean.getPrice()+", "+transferBean.getDecimal());
                BigInteger bigInteger = new BigInteger(amount+"");
                String valueHexStr = Numeric.toHexStringNoPrefix(bigInteger);
                Log.i(TAG, "amountHexStr="+valueHexStr);
                ByteString value = ByteString.copyFrom(Numeric.hexStringToByteArray(valueHexStr));
                Polkadot.Balance balance = Polkadot.Balance.newBuilder()
                        .setTransfer(Polkadot.Balance.Transfer.newBuilder().setToAddress(toAddr).setValue(value).build()).build();

                ByteString genesisHashStr = ByteString.copyFrom(Numeric.hexStringToByteArray(material.getGenesisHash()));
                ByteString blockHash = ByteString.copyFrom(Numeric.hexStringToByteArray(material.getAt().getHash()));
                long blockNum = Long.parseLong(material.getAt().getHeight());
                Polkadot.SigningInput input = Polkadot.SigningInput.newBuilder()
                        .setGenesisHash(genesisHashStr)
                        .setBlockHash(blockHash)
                        .setNonce(nonce)
                        .setSpecVersion(Integer.parseInt(material.getSpecVersion()))
                        .setNetwork(Polkadot.Network.POLKADOT)
                        .setTransactionVersion(Integer.parseInt(material.getTxVersion()))
                        .setPrivateKey(ByteString.copyFrom(privateKey.data()))
                        .setBalanceCall(balance)
                        .setEra(Polkadot.Era.newBuilder().setBlockNumber(blockNum).setPeriod(64))
                        .build();

                try {
                    Polkadot.SigningOutput output = AnySigner.sign(input, CoinType.POLKADOT, Polkadot.SigningOutput.parser());
                    String sign  = Numeric.toHexString(output.getEncoded().toByteArray());
                    Log.i(TAG, "sign="+sign);
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

    
    private void sendTrans(String sign) {
        if (TextUtils.isEmpty(sign)) {
            if (mListen != null) {
                mListen.onFail("no find sige");
            }
            return;
        }
        Map<String,String> params = new HashMap<>();
        params.put("tx", sign);
        mApi.trans(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DotTransResultBean>(mContext) {
                    @Override
                    public void onNexts(DotTransResultBean baseEntity) {
                        if (baseEntity != null) {
                            if (baseEntity.getHash() != null) {
                                if (mListen != null) {
                                    mListen.showTransctionSuccess(baseEntity.getHash()+"");
                                }
                            } else if (baseEntity.getHash() != null) {
                                if (mListen != null) {
                                    mListen.onFail(baseEntity.getHash());
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
