

package com.wallet.ctc.model.blockchain;


public class SgbDappWalletBean {

    private String address;
    private String name;
    private String genesisHash;

    public SgbDappWalletBean(){

    }
    public SgbDappWalletBean(String address,String name){
        this.address=address;
        this.name=name;
        this.genesisHash="";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenesisHash() {
        return genesisHash;
    }

    public void setGenesisHash(String genesisHash) {
        this.genesisHash = genesisHash;
    }
}
