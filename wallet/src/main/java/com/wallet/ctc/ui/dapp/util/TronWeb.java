

package com.wallet.ctc.ui.dapp.util;

import static com.wallet.ctc.crypto.WalletUtil.TRX_COIN;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletTransctionUtil;
import com.wallet.ctc.model.blockchain.TransferBean;
import com.wallet.ctc.model.blockchain.TrxDappBean;
import com.wallet.ctc.model.blockchain.TrxDappValueBean;
import com.wallet.ctc.model.blockchain.TrxRawData;
import com.wallet.ctc.model.blockchain.TrxTransSignBean;
import com.wallet.ctc.model.blockchain.TrxTransactionPushBean;

import java.math.BigDecimal;

import common.app.mall.util.ToastUtil;
import common.app.utils.LogUtil;
import owallet.Owallet;

public class TronWeb {
    private Context mContext;
    private WebView web;
    public Dialog mLoadingDialog;
    private WalletTransctionUtil walletTransctionUtil;

    public TronWeb(Context mContext, WebView webView) {
        this.web = webView;
        this.mContext = mContext;
        mLoadingDialog = new Dialog(mContext, R.style.progress_dialog);
        mLoadingDialog.setContentView(R.layout.dialog_commom);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        TextView msg = (TextView) mLoadingDialog
                .findViewById(R.id.id_tv_loadingmsg);
        msg.setText(mContext.getString(R.string.loading));
        walletTransctionUtil = new WalletTransctionUtil(mContext);
        walletTransctionUtil.setTrxTransctionListen(new WalletTransctionUtil.TrxTransctionListen() {
            @Override
            public void showLoading() {
                mLoadingDialog.show();
            }

            @Override
            public void showTransctionSuccess(String hash) {
                mLoadingDialog.dismiss();
                doNext(hash);
            }

            @Override
            public void onFail(String msg) {
                mLoadingDialog.dismiss();
                ToastUtil.showToast(msg);
                doDef();
            }
        });
    }

    
    @JavascriptInterface
    public void resultManager(String data,String back) {
        backFun=back;
        ((Activity) mContext).runOnUiThread(new Runnable() {
            public void run() {
                doTrans(data);
            }
        });
    }
    String transData;
    String backFun;
    private void doTrans(String data) {
        this.transData=data;
        LogUtil.d("zzz", data);

        TrxDappBean trxDappBean = new Gson().fromJson(data, TrxDappBean.class);
        try {
            TrxDappValueBean valueBean;
            TrxRawData.ContractBean contractBean;
            TrxRawData rawData;
            long time;
            long expiration;
            String rowdata;
            if (null != trxDappBean && null != trxDappBean.getTransaction()) {
                rawData=trxDappBean.getTransaction().getRaw_data();
                contractBean = rawData.getContract().get(0);
                time=trxDappBean.getTransaction().getRaw_data().getTimestamp();
                rowdata=new Gson().toJson(trxDappBean.getTransaction().getRaw_data());
                expiration=trxDappBean.getTransaction().getRaw_data().getExpiration();
            } else {
                TrxTransactionPushBean transactionBean = new Gson().fromJson(data, TrxTransactionPushBean.class);
                rawData=transactionBean.getRaw_data();
                contractBean = rawData.getContract().get(0);
                time=transactionBean.getRaw_data().getTimestamp();
                rowdata=new Gson().toJson(transactionBean.getRaw_data());
                expiration=transactionBean.getRaw_data().getExpiration();
            }
            valueBean = contractBean.getParameter().getValue();
            String toAddress = toBase58(valueBean.getTo_address());
            String fromAddress = toBase58(valueBean.getOwner_address());
            String contract =toBase58(valueBean.getContract_address());

            if (contractBean.getType().equalsIgnoreCase("TransferContract")) {
                String amountStr = "";
                int decmail = 18;
                if (TextUtils.isEmpty(valueBean.getContract_address())) {
                    decmail = 6;
                    amountStr = new BigDecimal(valueBean.getAmount()).divide(new BigDecimal(Math.pow(10, 6))).toPlainString();
                }
                TransferBean mBeam = new TransferBean(toAddress, fromAddress, amountStr, TRX_COIN, "", contract, decmail);
                mBeam.setTimestamp(time);
                mBeam.setExpiration(expiration);
                walletTransctionUtil.DoTrxTransctionDapp(mBeam, TRX_COIN);
            } else if (contractBean.getType().equalsIgnoreCase("TriggerSmartContract") && !TextUtils.isEmpty(valueBean.getData())) {
                    TransferBean mBeam = new TransferBean(toAddress, fromAddress, valueBean.getAmount() + "", TRX_COIN, "", contract, 18);
                    mBeam.setData(valueBean.getData());
                    mBeam.setTimestamp(time);
                    mBeam.setInfo(contractBean.getType());
                    mBeam.setExpiration(expiration);
                    walletTransctionUtil.DoTrxTransctionDapp(mBeam, TRX_COIN);
            } else {
                doDef();
            }
        } catch (Exception e) {
            LogUtil.d("zzz", e.getMessage());
        }
    }

    private void doDef() {
        web.loadUrl("javascript:onerror('cancle')");
    }

    private void doNext(String data) {
        web.loadUrl("javascript:"+backFun+"('" + data + "')");
    }

    private String toBase58(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        String data = Owallet.tronHex2Addr(address);
        try {
            if (data.startsWith("{")) {
                TrxTransSignBean signBean = new Gson().fromJson(data, TrxTransSignBean.class);
                if (new Gson().toJson(signBean.getError()).length() > 4) {
                    return "";
                } else {
                    return signBean.getResult() + "";
                }
            } else {
                return data;
            }
        } catch (Exception e) {

        }
        return "";
    }

}
