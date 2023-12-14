package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;


public class ChainMapConfigBean extends EvmosHttpBean{

    public Data data;

    public static class Data {
        public String feePercent;
        public String minMapNum;
    }

    
    public String getGasPercent() {
        if (null != data) {
            if (TextUtils.isEmpty(data.feePercent)) {
                return "0";
            }
            return data.feePercent;
        }
        
        return "";
    }

    
    public String getGasPercentStr() {
        String percent = getGasPercent();
        if (!TextUtils.isEmpty(percent)) {
            return percent+"%";
        }
        return "";
    }

    
    public String getMinNum() {
        if (null != data) {
            if (TextUtils.isEmpty(data.minMapNum)) {
                
                return "0";
            }
            return data.minMapNum;
        }
        
        return "0";
    }
}
