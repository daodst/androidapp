

package com.wallet.ctc;

import android.text.TextUtils;



public class AppHolder {

    public static final int STATU_YES = 1;
    public static final int STATU_NO = 0;
    public static final int CHOSE_CITY = 1000;
    public static final int CHOSE_ADDRESS = 1001;
    public static final int SEACH_SHOP = 1002;
    public static final int SEACH_PRODUCT=2000;
    public static final int PAGE=1;
    public static final int ZERO=0;
    public static final int ONE=1;
    public static final int TWO=2;

    
    public static String getLogoByAddress(String address) {
        String logo = "file:
        return logo;
    }

    
    public static String getLogoAssetsPath(String address) {
        return "avatar/"+getLogoIndexByAddress(address)+".png";
    }

    
    public static int getLogoIndexByAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return 0;
        }
        int hashCode = Math.abs(address.hashCode());
        int index = hashCode % 20 + 1;
        return index;
    }
    

    




}
