

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;


public class SolBalanceBean {
    private ContextBean context;
    private BigDecimal value;
    public static class ContextBean {
        private Integer slot;
    }


    public ContextBean getContext() {
        return context;
    }

    public void setContext(ContextBean context) {
        this.context = context;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
