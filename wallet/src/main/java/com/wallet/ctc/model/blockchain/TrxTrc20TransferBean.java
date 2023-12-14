

package com.wallet.ctc.model.blockchain;


public class TrxTrc20TransferBean {


    

    private String toaddress;
    private String amount;
    private String privatekey;
    private String rpcaddress;
    private String tokenaddress;
    private int feelimit;
    private String data;
    private boolean  notsend;

    public TrxTrc20TransferBean(String toaddress,String tokenaddress, String amount, String privatekey){
        this.toaddress=toaddress;
        this.tokenaddress=tokenaddress;
        this.amount=amount;
        this.privatekey=privatekey;
        feelimit=10000000;
    }

    public String getTokenaddress() {
        return tokenaddress;
    }

    public void setTokenaddress(String tokenaddress) {
        this.tokenaddress = tokenaddress;
    }

    public int getFeelimit() {
        return feelimit;
    }

    public void setFeelimit(int feelimit) {
        this.feelimit = feelimit;
    }

    public String getToaddress() {
        return toaddress;
    }

    public void setToaddress(String toaddress) {
        this.toaddress = toaddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public String getRpcaddress() {
        return rpcaddress;
    }

    public void setRpcaddress(String rpcaddress) {
        this.rpcaddress = rpcaddress;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isNotsend() {
        return notsend;
    }

    public void setNotsend(boolean notsend) {
        this.notsend = notsend;
    }
}
