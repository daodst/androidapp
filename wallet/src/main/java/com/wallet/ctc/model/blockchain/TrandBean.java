

package com.wallet.ctc.model.blockchain;



public class TrandBean {


    

    private Float balance;
    private String blockNum;
    private String lastTxid;
    private String readBlock;
    private String time;
    private String timeDay;
    private String token;

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getBlockNum() {
        return blockNum;
    }

    public void setBlockNum(String blockNum) {
        this.blockNum = blockNum;
    }

    public String getLastTxid() {
        return lastTxid;
    }

    public void setLastTxid(String lastTxid) {
        this.lastTxid = lastTxid;
    }

    public String getReadBlock() {
        return readBlock;
    }

    public void setReadBlock(String readBlock) {
        this.readBlock = readBlock;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeDay() {
        return timeDay;
    }

    public void setTimeDay(String timeDay) {
        this.timeDay = timeDay;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
