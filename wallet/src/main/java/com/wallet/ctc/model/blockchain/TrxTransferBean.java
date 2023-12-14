

package com.wallet.ctc.model.blockchain;


public class TrxTransferBean {


    

    private String toaddress;
    private String amount;
    private String privatekey;
    private String rpcaddress;
    private String data;
    private boolean  notsend;

    public TrxTransferBean(String toaddress,String amount,String privatekey){
        this.toaddress=toaddress;
        this.amount=amount;
        this.privatekey=privatekey;
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
