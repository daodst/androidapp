

package com.wallet.ctc.model.blockchain;



public class CreateEthBean {


    

    private String amount;
    private int gaslimit;
    private String gasprice;
    private String privatekey;
    private int nonce;
    private String data;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getGaslimit() {
        return gaslimit;
    }

    public void setGaslimit(int gaslimit) {
        this.gaslimit = gaslimit;
    }

    public String getGasprice() {
        return gasprice;
    }

    public void setGasprice(String gasprice) {
        this.gasprice = gasprice;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
