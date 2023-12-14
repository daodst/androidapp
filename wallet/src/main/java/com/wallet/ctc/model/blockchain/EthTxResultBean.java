package com.wallet.ctc.model.blockchain;


public class EthTxResultBean {

    public String hash;
    private String info;
    private int status = 0;

    
    public static EthTxResultBean newSuccessResult(String hash) {
        EthTxResultBean result = new EthTxResultBean();
        result.hash = hash;
        result.setSuccess();
        return  result;
    }

    
    public static EthTxResultBean newFailResult(String hash, String errorInfo) {
        EthTxResultBean result = new EthTxResultBean();
        result.hash = hash;
        result.setFail(errorInfo);
        return  result;
    }

    
    public static EthTxResultBean newUnknownResult(String hash) {
        EthTxResultBean result = new EthTxResultBean();
        result.hash = hash;
        result.setUnkown();
        return  result;
    }


    public String getInfo() {
        return info;
    }

    public void setSuccess() {
        setStatus(1);
    }

    public void setFail(String errorInfo) {
        setStatus(2);
        info = errorInfo;
    }

    public void setUnkown() {
        setStatus(0);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    
    public boolean isSuccess() {
        return status == 1;
    }

    
    public boolean isFail() {
        return status == 2;
    }

    
    public boolean isUnknown() {
        return status == 0;
    }
}
