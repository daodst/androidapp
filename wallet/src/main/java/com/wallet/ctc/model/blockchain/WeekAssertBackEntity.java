

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;



public class WeekAssertBackEntity {


    

    private String timestamp;
    private BigDecimal value;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
