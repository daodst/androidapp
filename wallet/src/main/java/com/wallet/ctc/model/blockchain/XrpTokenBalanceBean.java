

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import java.math.BigDecimal;


public class XrpTokenBalanceBean {
    
    public String issuer;
    public String currency;
    public String balance;

    private String cprice;
    private String uprice;

    public BigDecimal getSumPrice() {
        if (TextUtils.isEmpty(cprice) || TextUtils.isEmpty(balance)) {
            return new BigDecimal("0.00");
        }
        try {
            BigDecimal price = new BigDecimal(cprice);
            BigDecimal ba = new BigDecimal(balance);
            if (null != price && null != ba) {
                return price.multiply(ba).setScale(2,BigDecimal.ROUND_HALF_UP);
            } else {
                return new BigDecimal("0.00");
            }
        } catch (NumberFormatException e) {
            return new BigDecimal("0.00");
        }
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCprice() {
        return cprice;
    }

    public void setCprice(String cprice) {
        this.cprice = cprice;
    }

    public String getUprice() {
        return uprice;
    }

    public void setUprice(String uprice) {
        this.uprice = uprice;
    }
}
