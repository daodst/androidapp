

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;



public class GasPriceBean {
    private int low;
    private int center;
    private int up;

    public GasPriceBean(){

    }
    public GasPriceBean(String price){
        BigDecimal b=new BigDecimal(price);
        low=b.intValue();
        center=b.multiply(new BigDecimal("2")).intValue();
        up=b.multiply(new BigDecimal("4")).intValue();
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getLow() {

        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getCenter() {
        return center;
    }

    public void setCenter(int center) {
        this.center = center;
    }
}
