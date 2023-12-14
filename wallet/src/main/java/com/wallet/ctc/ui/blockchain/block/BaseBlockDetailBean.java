

package com.wallet.ctc.ui.blockchain.block;

import java.util.List;



public class BaseBlockDetailBean {

    

    private String Number;
    private String TxId;
    private String Timestamp;
    private List<BlockDetailBean> Ledgers;
    public String getNumber() {
        return Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }

    public String getTxId() {
        return TxId;
    }

    public void setTxId(String TxId) {
        this.TxId = TxId;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String Timestamp) {
        this.Timestamp = Timestamp;
    }

    public List<BlockDetailBean> getLedgers() {
        return Ledgers;
    }

    public void setLedgers(List<BlockDetailBean> ledgers) {
        Ledgers = ledgers;
    }
}
