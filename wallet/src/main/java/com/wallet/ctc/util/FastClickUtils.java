

package com.wallet.ctc.util;


public class FastClickUtils {

    public static final int DELAY = 500;
    private static long lastClickTime = 0;
    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }
}
