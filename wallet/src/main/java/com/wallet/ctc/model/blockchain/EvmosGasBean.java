

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.wallet.ctc.BuildConfig;

import common.app.utils.AllUtils;


public class EvmosGasBean extends EvmosHttpBean {
    public Data data;

    
    public String getShowFee(String units) {
        if (null == data || null == data.fee) {
            return "";
        }
        String unit = data.fee.denom;
        if (TextUtils.isEmpty(unit)) {
            unit = units;
        }
        if (null != unit) {
            unit = unit.toUpperCase();
        }
        String amount = AllUtils.getTenDecimalValue(data.fee.amount, 18, 6);
        return amount + " " + unit;
    }

    public String getShowFeeAdd() {
        if (null == data || null == data.fee) {
            return "0";
        }
        String amount = AllUtils.getTenDecimalValue(data.fee.amount, 18, 18);
        return amount;
    }

    
    public String getShowFee() {
        return getShowFee(BuildConfig.EVMOS_FAKE_UNINT);
    }


    public String getGasAmount() {
        if (null != data && null != data.fee) {
            return data.fee.amount;
        }
        return "";
    }

    public String getGasLimit() {
        
        if (null != data) {
            return data.gas_used;
        } else {
            return "";
        }
    }


    public static class Data {
        public Amount fee;
        public String gas_used;
        public Amount gas_price;
        public Amount first_gas_price;
    }


    public static class Amount {
        public String denom;
        public String amount;
    }

}
