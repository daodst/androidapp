package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class ValidatorInfo extends EvmosHttpBean{

    public int page = 1;

    public boolean isEnd = false;

    
    @Deprecated
    public String height;
    @SerializedName("delegation_responses")
    public List<ValidatorInfoResult> result;


    public class ValidatorInfoResult {
        
        public Delegation delegation;
        
        public Balance balance;

    }

    public class Delegation {

        
        public String delegator_address;
        
        public String validator_address;
        
        public String shares;
    }

    public class Balance {

        
        public String denom;
        
        private String amount;

        public String getAmount(int decimal) {
            try {
                return new BigDecimal(amount).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return amount;
        }
    }
}
