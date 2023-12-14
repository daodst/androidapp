

package com.wallet.ctc.model.blockchain;

import java.io.Serializable;
import java.math.BigDecimal;



public class NewQuotesBean implements Serializable {



    private BigDecimal chg;
    private String cprice;
    private String name;
    private String uprice;
    private String bname;
    private String bprice;
    private String exchange_name;
    private BigDecimal close;
    private String exchange_name_cn;
    private BigDecimal open;
    private String symbol;
    private String time;


    public String getCprice() {
        if(null==cprice){
            return "---";
        }
        return cprice;
    }

    public void setCprice(String cprice) {
        this.cprice = cprice;
    }

    public String getUprice() {
        if(null==uprice){
            return "---";
        }
        return uprice;
    }

    public String getExchange_name() {
        return exchange_name;
    }

    public void setExchange_name(String exchange_name) {
        this.exchange_name = exchange_name;
    }

    public void setUprice(String uprice) {
        this.uprice = uprice;
    }

    public String getChg() {
        BigDecimal bb= chg.multiply(new BigDecimal(100));
        return bb.toPlainString();
    }

    public void setChg(BigDecimal chg) {
        this.chg = chg;
    }

    public String getName() {
        return name.toUpperCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBname() {
        return bname.toUpperCase();
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


    public String getExchange_name_cn() {
        return exchange_name_cn.toUpperCase();
    }

    public void setExchange_name_cn(String exchange_name_cn) {
        this.exchange_name_cn = exchange_name_cn;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {

        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
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
}
