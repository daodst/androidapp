

package com.wallet.ctc.model.me;

import android.text.TextUtils;



public class DefValueBean {
    private String id ;
    private String key;
    private String logo;
    private String name;
    private String symbol;
    private int is_defshow;
    private String url;
    private String address;
    private String limit;
    private String amount;
    private int decimal; 

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        if (TextUtils.isEmpty(key)) {
            return key;
        }
        return key.toLowerCase();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return (name+"").toLowerCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getIs_defshow() {
        return is_defshow;
    }

    public void setIs_defshow(int is_defshow) {
        this.is_defshow = is_defshow;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }
}
