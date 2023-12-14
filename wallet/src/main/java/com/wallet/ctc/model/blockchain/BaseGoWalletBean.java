

package com.wallet.ctc.model.blockchain;



public class BaseGoWalletBean {


    

    private GoWalletBean result;
    private String error;

    public GoWalletBean getResult() {
        return result;
    }

    public void setResult(GoWalletBean result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
