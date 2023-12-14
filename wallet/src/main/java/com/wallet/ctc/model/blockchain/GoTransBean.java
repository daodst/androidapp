

package com.wallet.ctc.model.blockchain;



public class GoTransBean {


    

    private String toaddress;
    private String tokenaddress;
    private String amount;
    private int gaslimit;
    private String gasprice;
    private String privatekey;
    private int nonce;
    private String data;
    private String gasfeecap;
    private String gastipcap;
    private int chainid;

    public String getToaddress() {
        return toaddress;
    }

    public void setToaddress(String toaddress) {
        this.toaddress = toaddress;
    }

    public String getTokenaddress() {
        return tokenaddress;
    }

    public void setTokenaddress(String tokenaddress) {
        this.tokenaddress = tokenaddress;
    }

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

    public String getGasfeecap() {
        return gasfeecap;
    }

    public void setGasfeecap(String gasfeecap) {
        this.gasfeecap = gasfeecap;
    }

    public String getGastipcap() {
        return gastipcap;
    }

    public void setGastipcap(String gastipcap) {
        this.gastipcap = gastipcap;
    }

    public int getChainid() {
        return chainid;
    }

    public void setChainid(int chainid) {
        this.chainid = chainid;
    }

    @Override
    public String toString() {
        return "GoTransBean{" +
                "toaddress='" + toaddress + '\'' +
                ", tokenaddress='" + tokenaddress + '\'' +
                ", amount='" + amount + '\'' +
                ", gaslimit=" + gaslimit +
                ", gasprice='" + gasprice + '\'' +
                ", privatekey='" + privatekey + '\'' +
                ", nonce=" + nonce +
                ", data=" + data +
                '}';
    }
}
