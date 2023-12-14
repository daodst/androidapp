

package com.wallet.ctc.crypto;

import android.content.Context;

import com.wallet.ctc.R;
import com.wallet.ctc.model.blockchain.GasPriceBean;
import com.wallet.ctc.view.dialog.TransferDialog;

import java.math.BigDecimal;

import common.app.ui.view.InputPwdDialog;



public class NewWalletTransctionUtil {
    private Context mContext;
    private TransferDialog transferDialog;
    private InputPwdDialog mDialog;
    private TransctionListen transctionListen;

    public NewWalletTransctionUtil(Context context) {
        this.mContext = context;
        mDialog = new InputPwdDialog(mContext, mContext.getString(R.string.place_edit_password));
    }

    
    public void DoTransction(int walletType,String fromAddress,String toAddress,String amount,String assetsAddress,int decimal,double gasCount,double gasprice,boolean showConfirm){
        amount = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10,decimal))).toPlainString();
    }

    
    public void getServiceFee(int walletType,String fromAddress, String tokenType){

    }

    
    public String changeEthGasPrice(BigDecimal gasPrice,BigDecimal gasCount){
        String feiyongStr="";
        if (gasCount == null||gasPrice==null) {
            return "0";
        }
        BigDecimal sumWei = gasCount.multiply(gasPrice);
        BigDecimal sum = sumWei;
        BigDecimal jinzhi = new BigDecimal("1000000000000000000");
        feiyongStr = sum.divide(jinzhi).toPlainString();
        return feiyongStr;
    }


    
    public interface TransctionListen {
        void showLoading();

        void showGasCount(String gasCount);

        void showGasprice(GasPriceBean gasPriceBean);

        void showDefGasprice(String defGasprice);

        void showTransctionSuccess(String hash);

        void onFail(String msg);
    }
}
