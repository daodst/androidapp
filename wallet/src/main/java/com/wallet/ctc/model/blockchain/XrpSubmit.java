

package com.wallet.ctc.model.blockchain;



public class XrpSubmit {
    private String tx_blob;
    public XrpSubmit(String tx_blob){
        this.tx_blob=tx_blob;
    }

    public String getTx_blob() {
        return tx_blob;
    }

    public void setTx_blob(String tx_blob) {
        this.tx_blob = tx_blob;
    }
}
