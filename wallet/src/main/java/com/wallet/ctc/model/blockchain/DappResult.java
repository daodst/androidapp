

package com.wallet.ctc.model.blockchain;



public class DappResult {
    private boolean status;
    private String result;

    public DappResult(){

    }

    
    public DappResult(boolean status, String result){
        this.status=status;
        this.result=result;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
