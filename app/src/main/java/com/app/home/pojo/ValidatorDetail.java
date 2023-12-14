package com.app.home.pojo;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ValidatorDetail {


    private String validator_self_delagate;

    public String getValidator_self_delagate(int decimal) {
        try {
            return new BigDecimal(validator_self_delagate).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return validator_self_delagate;
    }

    
    public String can_undelegate_amount;
    public String validator_detail_json;
    private ValidatorDetailSub mResult;

    private Gson mGson = new Gson();

    public ValidatorDetailSub getResult() {
        if (null == mResult) {
            
            mResult = mGson.fromJson(validator_detail_json, ValidatorDetailSub.class);
        }
        return mResult;
    }

    public class ValidatorDetailSub {
        public String height;
        public ValidatorListInfo.Result result;
    }


    public Amount balance;

    public static class Amount {
        
        public String denom;
        
        public String amount;
    }

}
