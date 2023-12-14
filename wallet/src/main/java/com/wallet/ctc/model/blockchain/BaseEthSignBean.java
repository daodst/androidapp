

package com.wallet.ctc.model.blockchain;



public class BaseEthSignBean {


    

    private String result;
    private String error;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "BaseEthSignBean{" +
                "result='" + result + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
