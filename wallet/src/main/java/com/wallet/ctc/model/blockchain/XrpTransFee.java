

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class XrpTransFee {
    private String base_fee;
    private String median_fee;
    private String minimum_fee;
    private String max_fee;
    private String open_ledger_fee;


    public String getBase_fee() {
        return base_fee;
    }

    public void setBase_fee(String base_fee) {
        this.base_fee = base_fee;
    }

    public String getMedian_fee() {
        return median_fee;
    }

    public void setMedian_fee(String median_fee) {
        this.median_fee = median_fee;
    }

    public String getMinimum_fee() {
        return minimum_fee;
    }

    public void setMinimum_fee(String minimum_fee) {
        this.minimum_fee = minimum_fee;
    }

    public String getMax_fee() {
        return max_fee;
    }

    public void setMax_fee(String max_fee) {
        this.max_fee = max_fee;
    }

    public String getOpen_ledger_fee() {
        return open_ledger_fee;
    }

    public void setOpen_ledger_fee(String open_ledger_fee) {
        this.open_ledger_fee = open_ledger_fee;
    }


    public int getDefProgress() {
        if (TextUtils.isEmpty(max_fee)) {
            max_fee = "0.01";
        }
        try {
            BigDecimal min = new BigDecimal(minimum_fee);
            BigDecimal max = new BigDecimal(max_fee);
            BigDecimal medi = new BigDecimal(median_fee);
            BigDecimal sub = max.subtract(min);
            BigDecimal m_sub = medi.subtract(min);
            BigDecimal p = m_sub.multiply(new BigDecimal(100)).divide(sub, min.scale(), RoundingMode.HALF_UP);
            return p.intValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;

    }

    
    public String getFee(int progress) {
        if (TextUtils.isEmpty(max_fee)) {
            max_fee = "0.01";
        }
        try {
            BigDecimal min = new BigDecimal(minimum_fee);
            BigDecimal sub = new BigDecimal(max_fee).subtract(new BigDecimal(minimum_fee));
            BigDecimal num = sub.multiply(new BigDecimal(progress)).divide(new BigDecimal(100), min.scale(), RoundingMode.HALF_UP);
            BigDecimal result = num.add(min);
            return result.stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }


}
