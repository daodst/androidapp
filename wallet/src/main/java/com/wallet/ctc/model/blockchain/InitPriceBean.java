

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;



public class InitPriceBean {
    private BigDecimal dm;
    private BigDecimal mcc;

    public BigDecimal getMcc() {
        return mcc;
    }

    public void setMcc(BigDecimal mcc) {
        this.mcc = mcc;
    }

    public BigDecimal getDm() {
        return dm;
    }

    public void setDm(BigDecimal dm) {
        this.dm = dm;
    }
}
