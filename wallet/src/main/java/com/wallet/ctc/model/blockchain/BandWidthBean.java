

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;


public class BandWidthBean {


    

    private BigDecimal energyRemaining;
    private BigDecimal freeNetLimit;
    private BigDecimal freeNetRemaining;
    private BigDecimal energyLimit;

    public BigDecimal getEnergyRemaining() {
        return energyRemaining;
    }

    public void setEnergyRemaining(BigDecimal energyRemaining) {
        this.energyRemaining = energyRemaining;
    }

    public BigDecimal getFreeNetLimit() {
        return freeNetLimit;
    }

    public void setFreeNetLimit(BigDecimal freeNetLimit) {
        this.freeNetLimit = freeNetLimit;
    }

    public BigDecimal getFreeNetRemaining() {
        return freeNetRemaining;
    }

    public void setFreeNetRemaining(BigDecimal freeNetRemaining) {
        this.freeNetRemaining = freeNetRemaining;
    }

    public BigDecimal getEnergyLimit() {
        return energyLimit;
    }

    public void setEnergyLimit(BigDecimal energyLimit) {
        this.energyLimit = energyLimit;
    }
}
