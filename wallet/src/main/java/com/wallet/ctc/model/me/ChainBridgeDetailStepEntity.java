package com.wallet.ctc.model.me;


public class ChainBridgeDetailStepEntity {
    public boolean current;
    public String title;
    public String content;
    public long countdown; 
    public String withdrawCode;
    public boolean isAlert;

    public ChainBridgeDetailStepEntity(){
    }

    public ChainBridgeDetailStepEntity(boolean completed, String title, String content, String withdrawCode){
        this.current = completed;
        this.title = title;
        this.content = content;
        this.withdrawCode = withdrawCode;
    }

    
    public void setCountdown(long seconds){
        this.countdown = seconds;
    }

    public void setWithdrawCode(String code){
        this.withdrawCode = code;
    }

    public void setCurrent(boolean current){
        this.current = current;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
        isAlert = alert;
    }
}
