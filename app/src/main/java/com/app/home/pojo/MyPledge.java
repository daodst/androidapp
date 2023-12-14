package com.app.home.pojo;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyPledge {
    
    private String pledge_amount;
    Pattern mPattern = Pattern.compile("\\d+(\\.\\d+)?");

    public String getPledge_amount() {
        try {
            Matcher m = mPattern.matcher(pledge_amount);
            boolean find = m.find();
            if (find) {
                return m.group();
            } else {
                
                return "-1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            return "-1";
        }
    }

    public String getPledge_amount(int decimal) {
        try {
            String pledgeAmount = getPledge_amount();
            return getBigDecimalValue(new BigDecimal(pledgeAmount).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getPledge_amount();
    }

    
    private String can_withdraw;

    public String getCan_withdraw(int decimal) {
        if (TextUtils.isEmpty(can_withdraw)) {
            return "";
        }
        if (!can_withdraw.contains(",")) {
            
            return getString(can_withdraw, decimal);
        } else {
            StringBuilder builder = new StringBuilder();
            
            String[] split = can_withdraw.split(",");
            for (int i = 0; i < split.length; i++) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(getString(split[i], decimal));
            }
            return builder.toString();
        }
    }

    private String getString(String amount, int decimal) {
        try {
            Matcher m = mPattern.matcher(amount);
            if (m.find()) {
                String data = m.group();
                String unit = amount.substring(data.length());
                if (!TextUtils.isEmpty(unit)) {
                    unit = unit.toUpperCase();
                }
                String result = getBigDecimalValue(new BigDecimal(data).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN));
                return result + " " + unit;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getBigDecimalValue(BigDecimal divide) {
        BigDecimal zero = BigDecimal.ZERO;
        if (divide.compareTo(zero) == 0) {
            return zero.toPlainString();
        } else {
            return divide.stripTrailingZeros().toPlainString();
        }
    }


    
    public String commission;

    public String getCommission(int decimal) {
        if (TextUtils.isEmpty(commission)) {
            return "";
        }
        if (!commission.contains(",")) {
            
            return getString(commission, decimal);
        } else {
            StringBuilder builder = new StringBuilder();
            
            String[] split = commission.split(",");
            for (int i = 0; i < split.length; i++) {
                if (i > 0) {
                    builder.append("\n");
                }
                builder.append(getString(split[i], decimal));
            }
            return builder.toString();
        }
    }
}


