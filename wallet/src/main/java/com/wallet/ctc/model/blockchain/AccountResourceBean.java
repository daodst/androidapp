

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;


public class AccountResourceBean {


    

    private BigDecimal freeNetUsed;
    private BigDecimal freeNetLimit;
    private BigDecimal TotalNetLimit;
    private BigDecimal TotalNetWeight;
    private BigDecimal EnergyLimit;
    private BigDecimal TotalEnergyLimit;
    private BigDecimal TotalEnergyWeight;

    public BigDecimal getFreeNetUsed() {
        if(null==freeNetUsed){
            return new BigDecimal("0");
        }
        return freeNetUsed;
    }

    public void setFreeNetUsed(BigDecimal freeNetUsed) {
        this.freeNetUsed = freeNetUsed;
    }

    public BigDecimal getFreeNetLimit() {
        if(null==freeNetLimit){
            return new BigDecimal("0");
        }
        return freeNetLimit;
    }

    public void setFreeNetLimit(BigDecimal freeNetLimit) {
        this.freeNetLimit = freeNetLimit;
    }

    public BigDecimal getTotalNetLimit() {
        return TotalNetLimit;
    }

    public void setTotalNetLimit(BigDecimal totalNetLimit) {
        TotalNetLimit = totalNetLimit;
    }

    public BigDecimal getTotalNetWeight() {
        return TotalNetWeight;
    }

    public void setTotalNetWeight(BigDecimal totalNetWeight) {
        TotalNetWeight = totalNetWeight;
    }

    public BigDecimal getEnergyLimit() {
        if(null==EnergyLimit){
            return new BigDecimal("0");
        }
        return EnergyLimit;
    }

    public void setEnergyLimit(BigDecimal energyLimit) {
        EnergyLimit = energyLimit;
    }

    public BigDecimal getTotalEnergyLimit() {
        return TotalEnergyLimit;
    }

    public void setTotalEnergyLimit(BigDecimal totalEnergyLimit) {
        TotalEnergyLimit = totalEnergyLimit;
    }

    public BigDecimal getTotalEnergyWeight() {
        return TotalEnergyWeight;
    }

    public void setTotalEnergyWeight(BigDecimal totalEnergyWeight) {
        TotalEnergyWeight = totalEnergyWeight;
    }
}
