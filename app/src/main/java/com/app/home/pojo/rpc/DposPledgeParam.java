package com.app.home.pojo.rpc;

import java.math.BigDecimal;


public class DposPledgeParam {

    
    public String delegator_address;
    
    public String validator_address;
    public Amount amount;


    public static class Amount {
        
        public String denom;
        
        public String amount;

        public Amount(String amount, int decimal, String coinName) {
            this.denom = coinName;
            this.amount = new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, decimal))).toPlainString();
        }
    }
}
