package com.app.home.pojo;

import android.content.Context;
import android.text.TextUtils;

import com.app.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class ValidatorDetailNew implements Serializable {

    

    @SerializedName("val_addr")
    public String valAddr;
    @SerializedName("name")
    public String name;
    @SerializedName("val_delegate_amount")
    public String valDelegateAmount;
    @SerializedName("self_delegate_rate")
    public String selfDelegateRate;
    @SerializedName("min_self_delegate")
    public String minSelfDelegate;
    @SerializedName("identify")
    public String identify;
    @SerializedName("status")
    public int status;
    @SerializedName("unbond_height")
    public int unbondHeight;
    @SerializedName("unbond_time")
    public int unbondTime;
    @SerializedName("jail")
    public boolean jail;
    @SerializedName("contact")
    public String contact;
    
    @SerializedName("delegated")
    public String delegated;
    @SerializedName("update_time")
    public int updateTime;
    @SerializedName("commission_rate")
    public String commissionRate;
    @SerializedName("max_commission_rate")
    public String maxCommissionRate;
    @SerializedName("max_change_rate")
    public String maxChangeRate;
    
    
    @SerializedName(value = "balance", alternate = {"balance_amount"})
    public String balance = "0";


    public String getValidator_self_delagate(int decimal) {
        try {
            return new BigDecimal(selfDelegateRate).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selfDelegateRate;
    }

    public String getTenAmount(String amount, int decimal) {
        try {
            return new BigDecimal(amount).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

    public String getHasOrNone(String data, Context context) {
        if (TextUtils.isEmpty(data)) {
            return context.getString(R.string.getunbonding_height_unknow);
        }
        return data;
    }

    public String getRate(String rate) {
        try {
            String num = new BigDecimal(rate).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            return num + "%";
        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }
}
