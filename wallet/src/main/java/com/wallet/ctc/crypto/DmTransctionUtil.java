

package com.wallet.ctc.crypto;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.model.blockchain.EvmosGasBean;
import com.wallet.ctc.model.blockchain.EvmosPledgeResultBean;
import com.wallet.ctc.model.blockchain.EvmosSeqAcountBean;
import com.wallet.ctc.model.blockchain.EvmosSignResult;
import com.wallet.ctc.model.blockchain.EvmosTransferResultBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.im.base.NextSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class DmTransctionUtil {
    private Context mContext;
    private RpcApi mApi;
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private int type;
    private WalletTransctionUtil.DMTransctionListen mListen;
    public DmTransctionUtil(Context context,TransferBean mBean,WalletTransctionUtil.DMTransctionListen mListen){
        walletDBUtil=WalletDBUtil.getInstent(context);
        this.mApi = new RpcApi();
        if (null != mBean) {
            this.type=mBean.getType();
        }
        this.mContext=context;
        this.mBean=mBean;
        this.mListen=mListen;
    }

    interface GetGasCallBack {
        void onGet(EvmosGasBean gasBean);
    }

    
    public void getGas(String fromAddr, String toAddress, String coinName) {
        mApi.getEvmosSeqAccountInfo(fromAddr).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosSeqAcountBean>(mContext) {
                    @Override
                    public void onNexts(EvmosSeqAcountBean baseEntity) {
                        if (baseEntity.isSuccess() && null != baseEntity.data) {
                            getGas(fromAddr, toAddress, "", coinName, baseEntity.data, null);
                        } else {
                            if(null!=mListen) {
                                mListen.onFail(baseEntity.getInfo());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null!=mListen) {
                            mListen.onFail(mContext.getString(R.string.caozuo_fail)+e.getMessage());
                        }

                    }
                });
    }

    
    private void getGas(String fromAddr, String toAddress, String amount,  String coinName,
                        EvmosSeqAcountBean.Data seqAccountInfo, GetGasCallBack callBack) {
        mApi.getEvmosTransferGas(fromAddr, toAddress, amount, coinName, seqAccountInfo).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosGasBean>(mContext) {
                    @Override
                    public void onNexts(EvmosGasBean baseEntity) {
                        if (baseEntity.isSuccess() && null != baseEntity.data) {
                            if (null != callBack) {
                                callBack.onGet(baseEntity);
                            } else if(null != mListen) {
                                mListen.showGasInfo(baseEntity.data.fee.amount, baseEntity.data.gas_used);
                            }
                        } else {
                            if(null!=mListen) {
                                mListen.onFail(baseEntity.getInfo());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null!=mListen) {
                            mListen.onFail(mContext.getString(R.string.caozuo_fail)+e.getMessage());
                        }

                    }
                });
    }

    public void getNonce(String pwd) {
        if (!walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(),mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if(null!=mListen) {
                mListen.onFail(mContext.getString(R.string.password_error2));
            }
            return;
        }
        try {
            if(null!=mListen){
                mListen.showLoading();
            }
            getTxnid(pwd);
        } catch (Exception e) {
            if(null!=mListen) {
                mListen.onFail(mContext.getString(R.string.caozuo_fail));
            }
            e.printStackTrace();
        }
    }

    private void getTxnid(String pwd) {
        mApi.getEvmosSeqAccountInfo(mBean.getPayaddress()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosSeqAcountBean>(mContext) {
                    @Override
                    public void onNexts(EvmosSeqAcountBean baseEntity) {
                        if (baseEntity.isSuccess() && null != baseEntity.data) {
                            if (TextUtils.isEmpty(mBean.getGasprice()) || TextUtils.isEmpty(mBean.getGasFeeCap())) {
                                
                                int decimal = 0;
                                if (mBean.getDecimal() > 0) {
                                    decimal = mBean.getDecimal();
                                } else {
                                    List<AssertBean> assets = walletDBUtil.getMustWallet(mBean.getType());
                                    decimal = assets.get(0).getDecimal();
                                    mBean.setDecimal(decimal);
                                }
                                String payAddress = mBean.getPayaddress();
                                String toAddress = mBean.getAllAddress();
                                String amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, decimal))).toPlainString();
                                String tokenName = mBean.getTokenName();
                                getGas(payAddress, toAddress, amount, tokenName, baseEntity.data, gasBean -> {
                                    String gasAmount = gasBean.data.fee.amount;
                                    String gasLimit = gasBean.data.gas_used;
                                    
                                    signAndPushTransfer(pwd, baseEntity, gasAmount, gasLimit);
                                });
                            } else {
                                String gasLimit = mBean.getGasFeeCap();
                                String gasAmount = mBean.getGasprice();

                                
                                signAndPushTransfer(pwd, baseEntity, gasAmount, gasLimit);
                            }
                        } else {
                            if(null!=mListen) {
                                mListen.onFail(baseEntity.getInfo());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null!=mListen) {
                            mListen.onFail(mContext.getString(R.string.caozuo_fail)+e.getMessage());
                        }

                    }
                });





    }

    
    private void signAndPushTransfer(String pwd, EvmosSeqAcountBean baseEntity, String gasAmount, String gasLimit) {
        EvmosSignResult result = evmosSign(pwd, baseEntity, gasAmount, gasLimit);
        if (null != result && result.isSuccess()) {
            pushTranfer(result.Data);
        } else {
            if(null!=mListen) {
                String errorInfo = result==null ? "sign fail" : result.getInfo();
                mListen.onFail(errorInfo);
            }
        }
    }


    private void pushTranfer(String signHexStr) {
        mApi.submitEvmosTransfer(signHexStr).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<EvmosTransferResultBean>(mContext) {
                    @Override
                    public void onNexts(EvmosTransferResultBean baseEntity) {
                        if (null != baseEntity && baseEntity.isSuccess()) {
                            timerConfirmTxResult(baseEntity);
                            
                        } else {
                            if(null!=mListen) {
                                mListen.onFail(baseEntity.getInfo());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(null!=mListen) {
                            mListen.onFail(mContext.getString(R.string.caozuo_fail)+e.getMessage());
                        }
                    }
                });
    }


    
    private void timerConfirmTxResult(EvmosTransferResultBean result) {
        if (null == mListen || null == result || null == result.data || TextUtils.isEmpty(result.data.tx_hash)) {
            if (null != mListen) {
                mListen.onFail("tx hash data is null return!!");
            }
            return;
        }
        mApi.timerCheckTxResult(result, null, new NextSubscriber<EvmosPledgeResultBean>() {
            @Override
            public void dealData(EvmosPledgeResultBean value) {
                if (null == value || value.success) {
                    if (null != mListen) {
                        mListen.showTransctionSuccess(result.data.tx_hash+"");
                    }
                } else if (null != mListen) {
                    mListen.onFail(value.info+"");
                }
            }

            @Override
            protected void dealError(Throwable e) {
                if (null != mListen) {
                    mListen.showTransctionSuccess(result.data.tx_hash+"");
                }
            }
        });
    }




    
    private EvmosSignResult evmosSign(String pwd, EvmosSeqAcountBean seqAccountBean, String gasAmount, String gasLimit) {
        WalletEntity wallet = walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(),mBean.getType());
        String publickey = new String(wallet.getmPublicKey());
        String privateKey = WalletUtil.getDecryptionKey(wallet.getmPrivateKey(), pwd);
        if (privateKey.startsWith("0x")) {
            privateKey = privateKey.substring(2);
        }

        int decimal = 0;
        if (mBean.getDecimal() > 0) {
            decimal = mBean.getDecimal();
        } else {
            List<AssertBean> assets = walletDBUtil.getMustWallet(mBean.getType());
            decimal = assets.get(0).getDecimal();
            mBean.setDecimal(decimal);
        }

        String payAddress = mBean.getPayaddress();
        String toAddress = mBean.getAllAddress();
        String amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, decimal))).setScale(0, RoundingMode.DOWN).toPlainString();

        
        if (TextUtils.isEmpty(gasAmount)) {
            gasAmount = "100000000000000000";
        }
        if (TextUtils.isEmpty(gasLimit)) {
            gasLimit = "2000000";
        }
        String tokenName = mBean.getTokenName();

        String accountNum = seqAccountBean.data.account_number+"";
        String accountSeq = seqAccountBean.data.sequence+"";
        String memo = mBean.getRemark();

        JSONObject json = new JSONObject();
        try {
            json.put("address", payAddress);
            json.put("publickey", publickey);
            json.put("privatekey", privateKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr = json.toString();
        
        ChatSdk.setupCosmosWallet(jsonStr.getBytes(StandardCharsets.UTF_8));

        
        ChatSdk.setSignTxBase(accountNum, accountSeq, gasLimit, gasAmount, memo);

        byte[] signByte = ChatSdk.signTransfer(toAddress, tokenName, amount);
        
        
        String jsonSignResult = new String(signByte);
        if (TextUtils.isEmpty(jsonSignResult)) {
            return null;
        }
        try {
            EvmosSignResult result = new Gson().fromJson(jsonSignResult, EvmosSignResult.class);
            return result;
        } catch (Exception e){
            e.printStackTrace();
            EvmosSignResult result = new EvmosSignResult();
            result.Status = 0;
            result.Info = e.getMessage();
            return result;
        }
    }

    
}
