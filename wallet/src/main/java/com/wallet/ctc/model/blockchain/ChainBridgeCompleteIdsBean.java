

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

import common.app.utils.digest.EAICoderUtil;


public class ChainBridgeCompleteIdsBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public List<String> data;


    public String getOrderIds() {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != data && data.size() > 0){
            Collections.sort(data);
            for(String id: data){
                
                if (!TextUtils.isEmpty(id)){
                    if (!TextUtils.isEmpty(stringBuilder)){
                        stringBuilder.append(",").append(id);
                    } else {
                        stringBuilder.append(id);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public String getOrderIdKey() {
        String orderIds = getOrderIds();
        if (!TextUtils.isEmpty(orderIds)){
            return EAICoderUtil.getMD5Code(orderIds);
        }
        return orderIds;
    }
}
