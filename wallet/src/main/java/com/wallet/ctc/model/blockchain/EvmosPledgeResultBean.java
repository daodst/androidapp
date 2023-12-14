package com.wallet.ctc.model.blockchain;


public class EvmosPledgeResultBean {

    public boolean success;
    public String info;

    public EvmosPledgeResultBean() {

    }

    public EvmosPledgeResultBean(boolean success, String errorInfo) {
        this.success = success;
        this.info = errorInfo;
    }

}
