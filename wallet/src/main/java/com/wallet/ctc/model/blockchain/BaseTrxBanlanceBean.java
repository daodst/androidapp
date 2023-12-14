

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class BaseTrxBanlanceBean {
    private BandWidthBean bandwidth;
    private List<TrxBanlanceBean> tokens;

    public List<TrxBanlanceBean> getTokens() {
        return tokens;
    }

    public void setTokens(List<TrxBanlanceBean> tokens) {
        this.tokens = tokens;
    }

    public BandWidthBean getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(BandWidthBean bandwidth) {
        this.bandwidth = bandwidth;
    }
}
