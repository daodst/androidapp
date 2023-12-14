

package com.wallet.ctc.crypto;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.base.BaseEntity;
import com.wallet.ctc.model.blockchain.BtcFeesBean;
import com.wallet.ctc.model.blockchain.BtcTransferBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;

import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;



public class BtcTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private MeApi mApi = new MeApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.BtcTransctionListen mListen;
    public BtcTransctionUtil(Context context, WalletTransctionUtil.BtcTransctionListen mListen){
        walletDBUtil=WalletDBUtil.getInstent(context);
        this.mContext=context;
        this.mListen=mListen;
    }
    public void getNonce(String pwd, TransferBean mBean,@Nullable BtcTransferBean.TxoutsBean txoutsBean) {
        this.mBean=mBean;
        if (!walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(),mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            ToastUtil.showToast(mContext.getResources().getString(R.string.password_error2));
            return;
        }
        try {
            getTransData(pwd,txoutsBean);
        } catch (Exception e) {
             e.printStackTrace();
        }
    }



    
    private void getTransData(String pwd,@Nullable BtcTransferBean.TxoutsBean usdtout) {
        if(null!=mListen){
            mListen.showLoading();
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("address",mBean.getPayaddress());
        params.put("target",mBean.getAllAddress());
        if(null!=usdtout){
            params.put("amount", "0.00000546");
        }else {
            params.put("amount", mBean.getPrice());
        }
        params.put("fee_base",mBean.getFee());
        mApi.getBtcData(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            BtcTransferBean bean = gson.fromJson(gson.toJson(baseEntity.getData()), BtcTransferBean.class);
                            bean.setPrivatekey(WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(),mBean.getType()).getmPrivateKey(), pwd));
                            if (null!=usdtout){
                                bean.setUsdtout(usdtout);
                            }
                            String sign = WalletUtil.getTrandsSign(gson.toJson(bean), WalletUtil.BTC_COIN);
                            getSendTrans(sign);
                        } else {
                            if(null!=mListen){
                                mListen.onFail(baseEntity.getInfo());
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

    
    private void getSendTrans(String data) {
        Map<String, Object> params = new TreeMap<>();
        params.put("data", data);
        params.put("memo",mBean.getRemark());
        mApi.getBtcTransfer(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            if(null!=mListen){
                                mListen.showTransctionSuccess(baseEntity.getData()+"");
                            }
                        } else {
                            if(null!=mListen){
                                mListen.onFail(baseEntity.getInfo());
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

    
    public void getDefFee() {
        Map<String, Object> params = new TreeMap<>();
        params.put("cnt", "1");
        mApi.getBtcFees(params).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<BaseEntity>(mContext) {
                    @Override
                    public void onNexts(BaseEntity baseEntity) {
                        if (baseEntity.getStatus() == 1) {
                            if(null!=mListen){
                                mListen.showDefDee(gson.fromJson(gson.toJson(baseEntity.getData()), BtcFeesBean.class));
                            }
                        } else {
                            if(null!=mListen){
                                mListen.onFail(baseEntity.getInfo());
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
