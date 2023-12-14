

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;


public class TrxBanlanceBean {


    
    private BigDecimal tokenPriceInTrx;
    private String tokenId;
    private String balance;
    private String tokenName;
    public int tokenDecimal;
    private String tokenAbbr;
    private int tokenCanShow;
    private String tokenType;
    private boolean vip;
    private String tokenLogo;
    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public double getTokenDecimal() {
        return Math.pow(10,tokenDecimal);
    }

    public void setTokenDecimal(int tokenDecimal) {
        this.tokenDecimal = tokenDecimal;
    }

    public String getTokenAbbr() {
        return tokenAbbr;
    }

    public void setTokenAbbr(String tokenAbbr) {
        this.tokenAbbr = tokenAbbr;
    }

    public int getTokenCanShow() {
        return tokenCanShow;
    }

    public void setTokenCanShow(int tokenCanShow) {
        this.tokenCanShow = tokenCanShow;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getTokenLogo() {
        return tokenLogo;
    }

    public void setTokenLogo(String tokenLogo) {
        this.tokenLogo = tokenLogo;
    }

    public BigDecimal getTokenPriceInTrx() {
        if(null==tokenPriceInTrx){
            tokenPriceInTrx=new BigDecimal("0");
        }
        return tokenPriceInTrx;
    }

    public void setTokenPriceInTrx(BigDecimal tokenPriceInTrx) {
        this.tokenPriceInTrx = tokenPriceInTrx;
    }
}
