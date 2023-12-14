

package com.wallet.ctc.model.blockchain;



public class BtcFeesBean{


    

    private int fastestFee;
    private int halfHourFee;
    private int hourFee;

    public int getFastestFee() {
        return fastestFee;
    }

    public void setFastestFee(int fastestFee) {
        this.fastestFee = fastestFee;
    }

    public int getHalfHourFee() {
        return halfHourFee;
    }

    public void setHalfHourFee(int halfHourFee) {
        this.halfHourFee = halfHourFee;
    }

    public int getHourFee() {
        return hourFee;
    }

    public void setHourFee(int hourFee) {
        this.hourFee = hourFee;
    }
}
