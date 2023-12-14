

package com.wallet.ctc.model.blockchain;



public class XrpLinesBean {

    private String account;
    private String balance;
    private String currency;
    private String limit;
    private String limit_peer;
    private Integer quality_in;
    private Integer quality_out;
    private Boolean no_ripple;
    private Boolean no_ripple_peer;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getLimit_peer() {
        return limit_peer;
    }

    public void setLimit_peer(String limit_peer) {
        this.limit_peer = limit_peer;
    }

    public Integer getQuality_in() {
        return quality_in;
    }

    public void setQuality_in(Integer quality_in) {
        this.quality_in = quality_in;
    }

    public Integer getQuality_out() {
        return quality_out;
    }

    public void setQuality_out(Integer quality_out) {
        this.quality_out = quality_out;
    }

    public Boolean getNo_ripple() {
        return no_ripple;
    }

    public void setNo_ripple(Boolean no_ripple) {
        this.no_ripple = no_ripple;
    }

    public Boolean getNo_ripple_peer() {
        return no_ripple_peer;
    }

    public void setNo_ripple_peer(Boolean no_ripple_peer) {
        this.no_ripple_peer = no_ripple_peer;
    }

}
