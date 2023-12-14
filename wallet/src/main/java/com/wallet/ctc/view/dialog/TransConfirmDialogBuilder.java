package com.wallet.ctc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;

import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.TransferBean;

public class TransConfirmDialogBuilder {
    private Context context = null;
    private TransConfirmParams params = null;
    private TransferBean transferBean = null;
    private TransferDialog.goTransfer goTransfer = null;

    public TransConfirmDialogBuilder(Context context, WalletEntity entity) {
        params = new TransConfirmParams();
        params.entity = entity;
        this.context = context;
    }

    public static TransConfirmDialogBuilder builder(Context context, WalletEntity entity) {
        if (null == entity) {
            throw new RuntimeException("WalletEntity can not be null");
        }
        return new TransConfirmDialogBuilder(context, entity);
    }

    public TransConfirmDialogBuilder type(int type) {
        params.type = type;
        return this;
    }

    public TransConfirmDialogBuilder orderDesc(String desc) {
        params.orderDesc = desc;
        return this;
    }

    public TransConfirmDialogBuilder fromAddress(String address) {
        params.fromAddress = address;
        return this;
    }

    public TransConfirmDialogBuilder toAddress(String address) {
        params.toAddress = address;
        return this;
    }

    public TransConfirmDialogBuilder amount(String amount) {
        params.amount = amount;
        return this;
    }

    public TransConfirmDialogBuilder gasFee(String amount) {
        params.gasFee = amount;
        return this;
    }

    public TransConfirmDialogBuilder gasFeeToken(String amount) {
        params.gasFeeToken = amount;
        return this;
    }

    
    public TransConfirmDialogBuilder gasFeeWithToken(String amount) {
        params.gasFeeWithToken = amount;
        return this;
    }

    public TransConfirmDialogBuilder gasPrice(String price) {
        params.gasPrice = price;
        return this;
    }

    public TransConfirmDialogBuilder gasCount(String count) {
        params.gasCount = count;
        return this;
    }

    
    public TransConfirmDialogBuilder gasPriceUnit(String unit) {
        params.gasPriceUnit = unit;
        return this;
    }

    
    public TransConfirmDialogBuilder transferBean(TransferBean bean) {
        this.transferBean = bean;
        return this;
    }

    
    public TransConfirmDialogBuilder goTransferListener(TransferDialog.goTransfer l) {
        this.goTransfer = l;
        return this;
    }

    public Dialog show() {
        TransferDialog transferDialog = new TransferDialog(context);
        if (null == transferBean) {
            if (TextUtils.isEmpty(params.gasFeeWithToken)) {
                if (!TextUtils.isEmpty(params.gasFee)) {
                    params.gasFeeWithToken = params.gasFee + params.gasFeeToken;
                }
            }

            transferBean = new TransferBean(params.entity, params.toAddress, params.fromAddress, params.amount, params.gasCount,
                    params.type, "", params.gasFeeWithToken);
            if (!TextUtils.isEmpty(params.gasPrice)) {
                transferBean.setGasprice(params.gasPrice);
            }
            if (!TextUtils.isEmpty(params.gasCount)) {
                try {
                    transferBean.setGascount(Integer.parseInt(params.gasCount));
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            transferBean.setInfo(params.orderDesc);
        }
        transferDialog.show(transferBean);
        transferDialog.setTrans(goTransfer);
        return transferDialog.getDialog();
    }

    public static class TransConfirmParams {

        public WalletEntity entity;
        public String orderDesc;
        public String fromAddress;
        public String toAddress;
        public String gasPrice;
        public String gasCount;
        public String gasFeeWithToken;
        public String gasFee;
        public int type;
        public String amount;
        public String gasFeeToken;
        public String gasPriceUnit;
    }
}
