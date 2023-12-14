

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class TickersBean {
    private List<String> assets;
    private String currency;

    public TickersBean(String currency,List<String> assets){
        this.currency=currency;
        this.assets=assets;
    }


    public List<String> getAssets() {
        return assets;
    }

    public void setAssets(List<String> assets) {
        this.assets = assets;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
