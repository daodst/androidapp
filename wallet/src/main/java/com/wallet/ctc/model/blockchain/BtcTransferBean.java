

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class BtcTransferBean {

    @SerializedName("utxos")
    private List<UxtosBean> utxos;
    private List<TxoutsBean> txouts;
    private String privatekey;
    private String net;
    

    private TxoutsBean usdtout;


    public List<UxtosBean> getUtxos() {
        return utxos;
    }

    public void setUtxos(List<UxtosBean> utxos) {
        this.utxos = utxos;
    }

    public List<TxoutsBean> getTxouts() {
        return txouts;
    }

    public void setTxouts(List<TxoutsBean> txouts) {
        this.txouts = txouts;
    }

    public String getPrivatekey() {
        return privatekey;
    }

    public void setPrivatekey(String privatekey) {
        this.privatekey = privatekey;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public TxoutsBean getUsdtout() {
        return usdtout;
    }

    public void setUsdtout(TxoutsBean usdtout) {
        this.usdtout = usdtout;
    }

    public static class UxtosBean {
        

        private String txid;
        private int n;

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public int getN() {
            return n;
        }

        public void setN(int n) {
            this.n = n;
        }
    }

    public static class TxoutsBean {
        

        private String address;
        private String amount;

        public TxoutsBean(){

        }
        public TxoutsBean(String a,String am){
            this.address=a;
            this.amount=am;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
