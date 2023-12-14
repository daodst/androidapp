

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;


public class AssetsPriceBean {


    

    private String bname;
    private String bprice;
    private String chg;
    private String close;
    private BigDecimal cprice;
    private String exchange_name;
    private String exchange_name_cn;
    private String name;
    private String open;
    private String symbol;
    private String time;
    private BigDecimal uprice;

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getBprice() {
        return bprice;
    }

    public void setBprice(String bprice) {
        this.bprice = bprice;
    }

    public String getChg() {
        return chg;
    }

    public void setChg(String chg) {
        this.chg = chg;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }



    public String getExchange_name() {
        return exchange_name;
    }

    public void setExchange_name(String exchange_name) {
        this.exchange_name = exchange_name;
    }

    public String getExchange_name_cn() {
        return exchange_name_cn;
    }

    public void setExchange_name_cn(String exchange_name_cn) {
        this.exchange_name_cn = exchange_name_cn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getCprice() {
        return cprice;
    }

    public void setCprice(BigDecimal cprice) {
        this.cprice = cprice;
    }

    public BigDecimal getUprice() {
        return uprice;
    }

    public void setUprice(BigDecimal uprice) {
        this.uprice = uprice;
    }
}
