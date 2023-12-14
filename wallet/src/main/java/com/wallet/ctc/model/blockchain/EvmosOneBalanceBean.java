

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class EvmosOneBalanceBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public String data;

    public String getBalance(int decimal) {
        String remain = "";
        if (!TextUtils.isEmpty(data)) {
            remain = new BigDecimal(data).divide(new BigDecimal(Math.pow(10, decimal)), 3, RoundingMode.HALF_UP).toPlainString();
        }
        return remain;
    }
}
