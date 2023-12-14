package com.app.pojo;

import android.text.TextUtils;

import java.math.BigDecimal;


public class BuyDidConfigBean {
    public boolean isSuccess; 
    public String errorInfo; 

    public String bnbBalance;
    public String bscUsdtBalance;



    public String bscDstUsdtPrice;

    public String dstBalance;
    public String minBurnAmount;
    public String hasBurnAmount;

    
    public String getMinUsdtBurnAmount() {
        if (!TextUtils.isEmpty(bscDstUsdtPrice) && !TextUtils.isEmpty(minBurnAmount)) {
            return new BigDecimal(minBurnAmount).multiply(new BigDecimal(bscDstUsdtPrice)).setScale(2).stripTrailingZeros().toPlainString();
        } else {
            return "";
        }
    }

    public String getBscDstUsdtPrice() {
        if (TextUtils.isEmpty(bscDstUsdtPrice)) {
            return "";
        }
        return bscDstUsdtPrice;
    }




    
    public boolean isUsdtEnough() {
        String minUsdAmount = getMinUsdtBurnAmount();
        if (!TextUtils.isEmpty(minUsdAmount) && !TextUtils.isEmpty(bscUsdtBalance)) {
            if (new BigDecimal(bscUsdtBalance).compareTo(new BigDecimal(minUsdAmount)) >= 0) {
                return true;
            }
        }
        return false;
    }

    
    public boolean isDstEnough() {
        if (!TextUtils.isEmpty(dstBalance) && !TextUtils.isEmpty(minBurnAmount)) {
            if (new BigDecimal(dstBalance).compareTo(new BigDecimal(minBurnAmount)) >= 0) {
                return true;
            }
        }
        return false;
    }

    
    public String getSubDstNum() {
        if (!TextUtils.isEmpty(dstBalance) && !TextUtils.isEmpty(minBurnAmount)) {
            BigDecimal subV = new BigDecimal(dstBalance).subtract(new BigDecimal(minBurnAmount));
            if (subV.compareTo(new BigDecimal(0)) >= 0) {
                return "0.00";
            } else {
                return subV.abs().toPlainString();
            }
        }
        return "0.00";
    }


    
    public boolean isBurnEnough() {
        if (!TextUtils.isEmpty(hasBurnAmount) && !TextUtils.isEmpty(minBurnAmount)) {
            if (new BigDecimal(hasBurnAmount).compareTo(new BigDecimal(minBurnAmount)) >= 0) {
                return true;
            }
        }
        return false;
    }

    
    public String getSubBurnNum() {
        if (!TextUtils.isEmpty(hasBurnAmount) && !TextUtils.isEmpty(minBurnAmount)) {
            BigDecimal subV = new BigDecimal(hasBurnAmount).subtract(new BigDecimal(minBurnAmount));
            if (subV.compareTo(new BigDecimal(0)) >= 0) {
                return "0.00";
            } else {
                return subV.abs().toPlainString();
            }
        }
        return "0.00";
    }


}
