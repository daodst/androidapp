

package com.wallet.ctc.crypto;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.model.blockchain.XrpAccountInfo;
import com.wallet.ctc.model.blockchain.XrpAccountInfoBean;
import com.wallet.ctc.model.blockchain.XrpSubmit;
import com.wallet.ctc.model.blockchain.XrpSubmitBean;
import com.wallet.ctc.model.blockchain.XrpTransBean;
import com.wallet.ctc.model.blockchain.XrpTransFee;
import com.wallet.ctc.model.blockchain.XrpTransSequence;
import com.wallet.ctc.model.blockchain.XrpTrustBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.utils.ThreadManager;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class XrpTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private MeApi mApi = new MeApi();
    private RpcApi rpcApi=new RpcApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.XrpTransctionListen mListen;

    public XrpTransctionUtil(Context context, WalletTransctionUtil.XrpTransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        Log.d("xccTest", "mListen="+mListen);
        this.mContext = context;

    }

    
    public void getNonce(String pwd, TransferBean mBean) {
        this.mBean = mBean;
        if (!walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(),mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        try {
            if (null != mListen)
                mListen.showLoading();
            Map<String, Object> params = new TreeMap<>();
            params.put("method", "account_info");
            params.put("id", MeApi.getETHID());
            List<Object> addr = new ArrayList<>();
            addr.add(new XrpAccountInfo(mBean.getAllAddress()));
            params.put("params", addr);
            rpcApi.getXrpBalance(gson.toJson(params), mBean.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<XrpAccountInfoBean>(mContext) {
                        @Override
                        public void onNexts(XrpAccountInfoBean baseEntity) {
                            if (null == baseEntity.getResult() && baseEntity.getResult().getStatus().equals("success")) {
                                Integer result = baseEntity.getResult().getAccount_data().getSequence();
                                signTransfer(mBean.getAllAddress(),pwd, new BigInteger(result.toString()), mBean.getTokenType(), mBean.getTokenName());
                            } else {
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            LogUtil.d(e.toString());
                        }
                    });
        } catch (Exception e) {
            if (null != mListen) {
                mListen.onFail(mContext.getString(R.string.caozuo_fail));
            }
        }
    }



    public interface TrustTokenCallBack {
        public void onTrustSuccess(String hash);
        public void onFail(String info);
    }
    private TrustTokenCallBack mTrustTokenCallBack;

    
    public void trustToken(String address,String pwd, String issuer, String currency, String amount, TrustTokenCallBack callBack) {
        mTrustTokenCallBack = callBack;
        if (!walletDBUtil.getWalletInfoByAddress(address, WalletUtil.XRP_COIN).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mTrustTokenCallBack) {
                mTrustTokenCallBack.onFail(mContext.getString(R.string.password_error2));
            }
            return;
        }

        
        mApi.getXrpTransFee(new TreeMap()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            XrpTransFee xrpTransFee = gson.fromJson(gson.toJson(baseEntity.getData()), XrpTransFee.class);
                            
                            Map<String, Object> params = new TreeMap();
                            params.put("addr", amount);
                            mApi.getXrpTransSequence(params).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                                        @Override
                                        public void onNexts(BaseEntity baseEntity) {
                                            if (baseEntity.getStatus() == 1) {
                                                XrpTransSequence sequence = gson.fromJson(gson.toJson(baseEntity.getData()), XrpTransSequence.class);
                                                if (null != sequence && !TextUtils.isEmpty(sequence.sequence)) {
                                                    String seq = sequence.sequence;
                                                    trustTokenTransfer(address,pwd, new BigInteger(seq), xrpTransFee.getMedian_fee(), issuer, currency, amount);
                                                } else {
                                                    if (null != mTrustTokenCallBack) {
                                                        mTrustTokenCallBack.onFail(mContext.getString(R.string.xpy_get_seq_error));
                                                    }
                                                }
                                            } else {
                                                if (null != mTrustTokenCallBack) {
                                                    mTrustTokenCallBack.onFail(baseEntity.getInfo());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            if (null != mTrustTokenCallBack) {
                                                mTrustTokenCallBack.onFail("sequence");
                                            }
                                        }
                                    });
                        } else {
                            if (null != mTrustTokenCallBack)
                                mTrustTokenCallBack.onFail(baseEntity.getInfo());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (null != mTrustTokenCallBack) {
                            mTrustTokenCallBack.onFail("");
                        }
                    }
                });
    }

    
    private void trustTokenTransfer(String address,String pwd, BigInteger nonce, String feetr,  String issuer, String currency, String amount) {
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    XrpTrustBean goTransBean = new XrpTrustBean();
                    BigDecimal number = new BigDecimal(amount).multiply(new BigDecimal(1000000));
                    goTransBean.setAmount(number.stripTrailingZeros().toPlainString());
                    BigDecimal fee = new BigDecimal(feetr).multiply(new BigDecimal(1000000));
                    goTransBean.setFee(fee.stripTrailingZeros().toPlainString());
                    goTransBean.setPrivatekey(WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(address, WalletUtil.XRP_COIN).getmPrivateKey(), pwd));
                    goTransBean.setSequence(nonce.longValue());
                    
                    goTransBean.setIssuer(issuer);
                    goTransBean.setCurrency(currency);
                    
                    String hexValue = WalletUtil.trusXrpToken(goTransBean);
                    LogUtil.d("" + hexValue);
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            trustJiaoyiHttp(hexValue);
                        }
                    });

                } catch (Exception e) {
                     e.printStackTrace();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mTrustTokenCallBack)
                                mTrustTokenCallBack.onFail(mContext.getString(R.string.transaction_error));
                        }
                    });

                }
            }
        });
    }



    
    private void signTransfer(String address,String pwd, BigInteger nonce, String issuer, String currency) {
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    XrpTransBean goTransBean = new XrpTransBean();
                    BigDecimal amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(1000000));
                    goTransBean.setAmount(amount.stripTrailingZeros().toPlainString());
                    BigDecimal fee = new BigDecimal(mBean.getFee().trim()).multiply(new BigDecimal(1000000));
                    goTransBean.setFee(fee.stripTrailingZeros().toPlainString());

                    goTransBean.setDestinationtag(mBean.getTag());
                    goTransBean.setPrivatekey(WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(address, WalletUtil.XRP_COIN).getmPrivateKey(), pwd));
                    goTransBean.setSequence(nonce.longValue());
                    goTransBean.setToaddress(mBean.getAllAddress());

                    if (!TextUtils.isEmpty(issuer) && !TextUtils.isEmpty(currency)) {
                        
                        goTransBean.setIssuer(issuer);
                        goTransBean.setCurrency(currency);
                    }

                    
                    String hexValue = WalletUtil.getTrandsSign(gson.toJson(goTransBean), WalletUtil.XRP_COIN);
                    LogUtil.d("" + hexValue);
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
                            if (null != mListen) {
                                mListen.onFail(mContext.getString(R.string.transaction_error));
                            }
                        }
                    });

                }
            }
        });
    }


    
    private void trustJiaoyiHttp(String hexValue) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("method", "submit");
            List<Object> addr = new ArrayList<>();
            addr.add(new XrpSubmit(hexValue));
            params.put("params", addr);
            rpcApi.submitXrp(gson.toJson(params), mBean.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<XrpSubmitBean>(mContext) {
                        @Override
                        public void onNexts(XrpSubmitBean baseEntity) {
                            if (null == baseEntity.getResult() && baseEntity.getResult().getStatus().equals("success")) {
                                if (null != mTrustTokenCallBack) {
                                    mTrustTokenCallBack.onTrustSuccess(baseEntity.getResult().getTx_json().getHash() + "");
                                }
                            } else {
                                if (null != mTrustTokenCallBack) {
                                    mTrustTokenCallBack.onFail(baseEntity.getResult().getStatus());
                                }
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            if (null != mTrustTokenCallBack) {
                                mTrustTokenCallBack.onFail(e.getMessage());
                            }
                            LogUtil.d(e.toString());
                        }
                    });
        } catch (Exception e) {
            if (null != mTrustTokenCallBack)
                mTrustTokenCallBack.onFail(mContext.getString(R.string.transaction_error));
        }
    }

    
    private void jiaoyiHttp(String hexValue) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("method", "submit");
            List<Object> addr = new ArrayList<>();
            addr.add(new XrpSubmit(hexValue));
            params.put("params", addr);
            rpcApi.submitXrp(gson.toJson(params), mBean.getType()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<XrpSubmitBean>(mContext) {
                        @Override
                        public void onNexts(XrpSubmitBean baseEntity) {
                            if (null == baseEntity.getResult() && baseEntity.getResult().getStatus().equals("success")) {
                                if (null != mListen) {
                                    Log.d("xccTest", "click mListen="+mListen +", "+Thread.currentThread());
                                    mListen.showTransctionSuccess(baseEntity.getResult().getTx_json().getHash()  + "");
                                }
                            } else {
                                if (null != mListen) {
                                    mListen.onFail(baseEntity.getResult().getStatus());
                                }
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            if (null != mListen) {
                                mListen.onFail(e.getMessage());
                            }
                            LogUtil.d(e.toString());
                        }
                    });
        } catch (Exception e) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.transaction_error));
        }
    }


    
    public void getDefFee() {
        if(null!=mListen){
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap();
        mApi.getXrpTransFee(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            XrpTransFee xrpTransFee = gson.fromJson(gson.toJson(baseEntity.getData()), XrpTransFee.class);
                            if (null != mListen) {
                                mListen.showDefDee(xrpTransFee);
                            }
                        } else {
                            if (null != mListen)
                                mListen.onFail(baseEntity.getInfo());
                        }
                    }
                });
    }


}
