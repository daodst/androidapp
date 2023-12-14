package im.wallet.router.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MyUtils {

    
    public static String getTenDecimalValue(String bigNum, int decimal, int scale) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        try {
            String amount = new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, decimal)), scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            return amount;
        } catch (Exception e){
            e.printStackTrace();
        }
        return bigNum;
    }
}
