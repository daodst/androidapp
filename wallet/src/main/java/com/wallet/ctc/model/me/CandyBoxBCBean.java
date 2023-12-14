

package com.wallet.ctc.model.me;

import java.math.BigDecimal;



public class CandyBoxBCBean {

    private String address;;
    private BigDecimal poundage;
    private String mainline_coin;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getPoundage() {
        return poundage;
    }

    public void setPoundage(BigDecimal poundage) {
        this.poundage = poundage;
    }

    public String getMainline_coin() {
        return mainline_coin;
    }

    public void setMainline_coin(String mainline_coin) {
        this.mainline_coin = mainline_coin;
    }
}
