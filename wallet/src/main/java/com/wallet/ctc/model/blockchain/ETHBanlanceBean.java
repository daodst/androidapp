

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;



public class ETHBanlanceBean {


    

    private BigDecimal cprice;
    private BigDecimal remain;
    private BigDecimal uprice;

    public BigDecimal getSumPrice() {
        return cprice.multiply(remain).setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getCprice() {
        return cprice;
    }

    public void setCprice(BigDecimal cprice) {
        this.cprice = cprice;
    }

    public BigDecimal getRemain() {
        if(remain.longValue()==0){
            remain=new BigDecimal("0");
        }
        return remain;
    }

    public void setRemain(BigDecimal remain) {
        this.remain = remain;
    }

    public BigDecimal getUprice() {
        return uprice;
    }

    public void setUprice(BigDecimal uprice) {
        this.uprice = uprice;
    }
}
