

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class CoinPriceCont {

    private static Map<String, String> mCpirceMap = new HashMap<>();
    private static Map<String, String> mUpirceMap = new HashMap<>();

    
    public static void recordPrice(String coinName, String cPrice, String uprice) {
        if (TextUtils.isEmpty(coinName)) {
            return;
        }
        if (!TextUtils.isEmpty(cPrice)) {
            mCpirceMap.put(coinName.toUpperCase(), cPrice);
        }
        if (!TextUtils.isEmpty(uprice)) {
            mUpirceMap.put(coinName.toUpperCase(), uprice);
        }
    }

    public static String getCPrice(String coinName) {
        if (TextUtils.isEmpty(coinName)) {
            return null;
        }
        if (mCpirceMap.containsKey(coinName.toUpperCase())) {
            return mCpirceMap.get(coinName.toUpperCase());
        } else {
            return null;
        }
    }

    public static String getUPrice(String coinName) {
        if (TextUtils.isEmpty(coinName)) {
            return null;
        }
        if (mUpirceMap.containsKey(coinName.toUpperCase())) {
            return mUpirceMap.get(coinName.toUpperCase());
        } else {
            return null;
        }
    }
}
