

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.util.AllUtils;

import java.io.Serializable;
import java.math.BigDecimal;



public class TransactionBtcRecordBean implements Serializable {


    

    private BigDecimal blocktime;
    private String from;
    private String to;
    private String txhash;
    private BigDecimal value;
    private String coin_name;
    private int blockheight;
    private String fee;
    private String memo;


    public BigDecimal getBlocktime() {
        return blocktime;
    }

    public void setBlocktime(BigDecimal blocktime) {
        this.blocktime = blocktime;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTxhash() {
        return txhash;
    }

    public void setTxhash(String txhash) {
        this.txhash = txhash;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }
    public String getDay() {
        String time="";
        time= AllUtils.getTimeNYR(blocktime.intValue()+"");
        return time;
    }

    public int getBlockheight() {
        return blockheight;
    }

    public void setBlockheight(int blockheight) {
        this.blockheight = blockheight;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}

