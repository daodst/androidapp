

package com.wallet.ctc.model.blockchain;



public class BanlanceBean {
    private String cc;
    private String addr;
    private String coin_address;
    private String coin_name;
    private String contract_address;
    public BanlanceBean(){

    }

    public BanlanceBean(String coin_name,String waller_address){
        this.cc=coin_name;
        this.addr=waller_address;
    }

    public BanlanceBean(String coin_name,String coin_address,String contract_address){
        this.coin_name=coin_name;
        this.contract_address=contract_address;
        this.coin_address=coin_address;
    }

    public String getCoin_address() {
        return coin_address;
    }

    public void setCoin_address(String coin_address) {
        this.coin_address = coin_address;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getContract_address() {
        return contract_address;
    }

    public void setContract_address(String contract_address) {
        this.contract_address = contract_address;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
