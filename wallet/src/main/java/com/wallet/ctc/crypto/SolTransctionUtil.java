

package com.wallet.ctc.crypto;

import static wallet.core.jni.CoinType.SOLANA;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.SolApi;
import com.wallet.ctc.api.me.MeApi;
import com.wallet.ctc.model.blockchain.SolBaseBean;
import com.wallet.ctc.model.blockchain.SolHashBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import common.app.base.fragment.mall.catcherror.BaseSubscriber;
import common.app.mall.util.ToastUtil;
import io.reactivex.android.schedulers.AndroidSchedulers;
import wallet.core.java.AnySigner;
import wallet.core.jni.Base58;
import wallet.core.jni.proto.Solana;



public class SolTransctionUtil {
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private SolApi mApi = new SolApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.SolTransctionListen mListen;
    public SolTransctionUtil(Context context, WalletTransctionUtil.SolTransctionListen mListen){
        walletDBUtil=WalletDBUtil.getInstent(context);
        this.mContext=context;
        this.mListen=mListen;
    }
    
    public void getDefFee(String pwd, TransferBean mBean) {
        this.mBean=mBean;
        if (!walletDBUtil.getWalletInfoByAddress(mBean.getPayaddress(),mBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            ToastUtil.showToast(mContext.getResources().getString(R.string.password_error2));
            return;
        }
        Map<String, Object> params = new TreeMap<>();
        params.put("jsonrpc", "2.0");
        params.put("method", "getRecentBlockhash");
        params.put("id", MeApi.getETHID());
        mApi.getRecentBlockhash(gson.toJson(params)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<SolBaseBean>(mContext) {
                    @Override
                    public void onNexts(SolBaseBean baseEntity) {
                        if (null == baseEntity.getError()) {
                            SolHashBean solHashBean=gson.fromJson(baseEntity.getResult(),SolHashBean.class);
                            signTransfer(pwd,solHashBean.getValue().getBlockhash());
                        } else {
                            if(null!=mListen){
                                mListen.onFail(baseEntity.getError().getMessage());
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

    private void signTransfer(String pwd,String blockhash) {
        BigDecimal amount = new BigDecimal("0");
        if (!TextUtils.isEmpty(mBean.getPrice())) {
            amount = new BigDecimal(mBean.getPrice().trim()).multiply(new BigDecimal(Math.pow(10, mBean.getDecimal())));
        }
        Solana.Transfer.Builder transaction = Solana.Transfer.newBuilder();
        transaction.setValue(amount.longValue());
        transaction.setRecipient(mBean.getAllAddress());
        Solana.SigningInput.Builder builder = Solana.SigningInput.newBuilder();
        byte[] base58=Base58.decodeNoCheck(mBean.getPayaddress());
        builder.setPrivateKey(ByteString.copyFrom(base58));
        builder.setRecentBlockhash(blockhash);
        builder.setTransferTransaction(transaction);
        try {
            Solana.SigningOutput  sign= AnySigner.sign(builder.build(), SOLANA, Solana.SigningOutput.parser());
            LogUtil.d("" + sign.getEncoded());
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    jiaoyiHttp(sign.getEncoded());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void jiaoyiHttp(String hexValue) {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put("jsonrpc", "2.0");
            params.put("method", "sendTransaction");
            params.put("id", MeApi.getETHID());
            List<Object> data = new ArrayList<>();
            data.add(hexValue);
            params.put("params", data);
            mApi.toTrans(new Gson().toJson(params)).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<SolBaseBean>(mContext) {
                        @Override
                        public void onNexts(SolBaseBean baseEntity) {
                            if (null == baseEntity.getError()) {
                                if (null != mListen)
                                    mListen.showTransctionSuccess(baseEntity.getResult().toString());
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

}
