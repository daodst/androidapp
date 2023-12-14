

package com.wallet.ctc.model.blockchain;

import java.io.Serializable;
import java.math.BigDecimal;



public class DiyaBean implements Serializable{

    private String id;
    private String chaincode;
    private String mort_acc;
    private String annual;
    private BigDecimal principle;
    private String period_start;
    private String period_end;
    private BigDecimal draw_earnings;
    private BigDecimal left_earnings;
    private BigDecimal draw_principle;
    private BigDecimal left_principle;

    public String getId() {
        if(id.endsWith(".0")){
            id=id.substring(0,id.length()-2);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChaincode() {
        return chaincode;
    }

    public void setChaincode(String chaincode) {
        this.chaincode = chaincode;
    }

    public String getMort_acc() {
        return mort_acc;
    }

    public void setMort_acc(String mort_acc) {
        this.mort_acc = mort_acc;
    }

    public String getAnnual() {
        return annual;
    }

    public void setAnnual(String annual) {
        this.annual = annual;
    }

    public BigDecimal getPrinciple() {
        return principle;
    }

    public void setPrinciple(BigDecimal principle) {
        this.principle = principle;
    }

    public String getPeriod_start() {
        return period_start;
    }

    public void setPeriod_start(String period_start) {
        this.period_start = period_start;
    }

    public String getPeriod_end() {
        return period_end;
    }

    public void setPeriod_end(String period_end) {
        this.period_end = period_end;
    }

    public BigDecimal getDraw_earnings() {
        return draw_earnings;
    }

    public void setDraw_earnings(BigDecimal draw_earnings) {
        this.draw_earnings = draw_earnings;
    }

    public BigDecimal getLeft_earnings() {
        return left_earnings;
    }

    public void setLeft_earnings(BigDecimal left_earnings) {
        this.left_earnings = left_earnings;
    }

    public BigDecimal getDraw_principle() {
        return draw_principle;
    }

    public void setDraw_principle(BigDecimal draw_principle) {
        this.draw_principle = draw_principle;
    }

    public BigDecimal getLeft_principle() {
        return left_principle;
    }

    public void setLeft_principle(BigDecimal left_principle) {
        this.left_principle = left_principle;
    }
}
