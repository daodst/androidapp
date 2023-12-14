

package com.wallet.ctc.model.blockchain;

public class FilGasFeeBean {

    

    private String From;
    private int GasLimit;
    private String GasPremium;
    private String To;
    private String Value;

    public String getFrom() {
        return From;
    }

    public void setFrom(String From) {
        this.From = From;
    }

    public int getGasLimit() {
        return GasLimit;
    }

    public void setGasLimit(int GasLimit) {
        this.GasLimit = GasLimit;
    }

    public String getGasPremium() {
        return GasPremium;
    }

    public void setGasPremium(String GasPremium) {
        this.GasPremium = GasPremium;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String To) {
        this.To = To;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }
}
