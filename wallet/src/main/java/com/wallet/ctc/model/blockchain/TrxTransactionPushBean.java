

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class TrxTransactionPushBean {
    private boolean visible;
    private String txID;
    private TrxRawData raw_data;
    private List<String> signature;
    private String raw_data_hex;
    private TrxPayInfoBean payInfo;

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public TrxRawData getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(TrxRawData raw_data) {
        this.raw_data = raw_data;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<String> getSignature() {
        return signature;
    }

    public void setSignature(List<String> signature) {
        this.signature = signature;
    }

    public String getRaw_data_hex() {
        return raw_data_hex;
    }

    public void setRaw_data_hex(String raw_data_hex) {
        this.raw_data_hex = raw_data_hex;
    }

    public TrxPayInfoBean getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(TrxPayInfoBean payInfo) {
        this.payInfo = payInfo;
    }

    @Override
    public String toString() {
        return "TrxTransactionPushBean{" +
                "visible=" + visible +
                ", txID='" + txID + '\'' +
                ", raw_data=" + raw_data +
                ", signature=" + signature +
                ", raw_data_hex='" + raw_data_hex + '\'' +
                '}';
    }
}
