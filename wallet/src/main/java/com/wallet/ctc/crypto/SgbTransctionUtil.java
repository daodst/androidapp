

package com.wallet.ctc.crypto;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wallet.ctc.R;
import com.wallet.ctc.api.blockchain.SGBApi;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.SgbSignBean;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.util.DecriptUtil;
import com.wallet.ctc.util.LogUtil;

import common.app.mall.util.ToastUtil;
import common.app.utils.ThreadManager;
import owallet.SignOut;
import owallet.Transfer;



public class SgbTransctionUtil {
    private static final String TAG = "SgbTransctionUtil";
    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping() 
            .create();
    private Context mContext;
    private SGBApi mApi = new SGBApi();
    private WalletDBUtil walletDBUtil;
    private TransferBean mBean;
    private WalletTransctionUtil.SgbTransctionListen mListen;

    public SgbTransctionUtil(Context context, WalletTransctionUtil.SgbTransctionListen mListen) {
        walletDBUtil = WalletDBUtil.getInstent(context);
        this.mListen = mListen;
        this.mContext = context;
    }

    public void getNonce(String pwd, TransferBean transferBean) {
        if (!walletDBUtil.getWalletInfoByAddress(transferBean.getPayaddress(), transferBean.getType()).getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        if (null != mListen) {
            mListen.showLoading();
        }
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                
                String p = WalletUtil.getDecryptionKey(walletDBUtil.getWalletInfoByAddress(transferBean.getPayaddress(), transferBean.getType()).getmPrivateKey(), pwd);
                
                String toAddr = transferBean.getAllAddress();
                
                String amount=transferBean.getPrice();
                try {
                    long nonce= WalletUtil.getDotSdk().getAccount().getAccountInfo(transferBean.getPayaddress()).getNonce();
                    Transfer transfer= WalletUtil.getDotSdk().getTransfer();
                    SignOut signOut =transfer.sign(p, toAddr, amount, nonce);
                    LogUtil.d(""+signOut.getSign());
                    String hash=transfer.submit(signOut.getSign());
                    LogUtil.d(""+hash);
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mListen != null) {
                                LogUtil.d("");
                                mListen.onSuccess(hash);
                            }
                        }
                    });
                }catch (Exception e){
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mListen != null) {
                                LogUtil.d(""+e.getMessage());
                                mListen.onFail("sign fail :"+e.getMessage());
                            }
                        }
                    });

                }
            }
        });
    }


    public void sign(String type,String request,String pwd){
        LogUtil.d(""+type+"        "+request+"       "+pwd);
        SgbSignBean signBean=gson.fromJson(request,SgbSignBean.class);
        WalletEntity walletEntity=walletDBUtil.getWalletInfoByAddress(signBean.getAddress(), WalletUtil.SGB_COIN);
        if (null==walletEntity||!walletEntity.getmPassword().equals(DecriptUtil.MD5(pwd))) {
            if (null != mListen)
                mListen.onFail(mContext.getString(R.string.password_error2));
            return;
        }
        if(null!=walletEntity.getmKeystore()&&walletEntity.getmKeystore().length>4){
            String keystory=WalletUtil.getDecryptionKey(walletEntity.getmKeystore(),pwd);
            WalletUtil.getSgbSign(type,request,pwd,keystory);
        }else {
            String mne=WalletUtil.getDecryptionKey(walletEntity.getmMnemonic(),pwd);
            WalletUtil.getSgbKetstory(mne,pwd);
            ToastUtil.showToast("，");
        }


    }
}
