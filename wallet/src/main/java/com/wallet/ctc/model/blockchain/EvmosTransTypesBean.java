

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import java.util.Map;


public class EvmosTransTypesBean extends EvmosHttpBean{
    public Map<String,String> data;
    public String getTypeStr(String type) {
        if (!TextUtils.isEmpty(type) && data != null && !data.isEmpty() && data.containsKey(type)) {
            return data.get(type);
        } else {
            return type;
        }
    }
}
