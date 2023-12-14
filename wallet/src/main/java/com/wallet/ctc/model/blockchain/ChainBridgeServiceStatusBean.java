

package com.wallet.ctc.model.blockchain;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import common.app.utils.digest.EAICoderUtil;


public class ChainBridgeServiceStatusBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;


    
    public boolean isRunning(){
        if (data != null && data.Status == 1){
            return true;
        } else {
            return false;
        }
    }

    public Object getError(){
        if (data != null) {
            return data.Error;
        } else {
            return null;
        }
    }

    
    public boolean hasNodeConnectError() {
        if (null == data.Error || data.Error.isEmpty()){
            return false;
        }
        for(Map.Entry<Integer, Map<Integer,String>> entry : data.Error.entrySet()){
            Map<Integer,String> errorMap = entry.getValue();
            if(ChainBridgeErrorInfo.hasNodeConnectError(errorMap)){
                return true;
            }
        }
        return false;
    }

    
    public boolean hasMainOrderError() {
        String mainOrderErrorIds = getRunningOrderErrorId();
        if (!TextUtils.isEmpty(mainOrderErrorIds)){
            return true;
        } else {
            return false;
        }
    }

    
    public String getOtherErrorInfo(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        if (data != null && null != data.Error && !data.Error.isEmpty()){
            for(Map.Entry<Integer, Map<Integer,String>> entry : data.Error.entrySet()){
                Integer mainOrderId = entry.getKey();
                if (mainOrderId != null && mainOrderId <= 0 ){
                    
                   String errorInfo= ChainBridgeErrorInfo.getErrorInfo(context, entry.getValue());
                   if (!TextUtils.isEmpty(errorInfo)) {
                       String mainErrorInfo = ChainBridgeErrorInfo.getOtherMainError(context, mainOrderId, errorInfo);
                       stringBuilder.append(mainErrorInfo);
                   }
                }
            }
        }
        return stringBuilder.toString();
    }


    
    public String getMainOrderErrorInfo(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        if (data != null && null != data.Error && !data.Error.isEmpty()){
            for(Map.Entry<Integer, Map<Integer,String>> entry : data.Error.entrySet()){
                Integer mainOrderId = entry.getKey();
                if (mainOrderId != null && mainOrderId > 0){
                    
                    String errorInfo= ChainBridgeErrorInfo.getErrorInfo(context, entry.getValue());
                    if (!TextUtils.isEmpty(errorInfo)) {
                        stringBuilder.append(errorInfo);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }


    
    public String getRunningOrderErrorId() {
        List<String> orderKeys = new ArrayList<>();

        if (data != null && null != data.Error && !data.Error.isEmpty()){
            for(Map.Entry<Integer, Map<Integer,String>> entry : data.Error.entrySet()){
                Integer mainOrderId = entry.getKey();
                if (mainOrderId != null && mainOrderId > 0){
                    
                    orderKeys.add(mainOrderId+"");
                }
            }
        }
        if (orderKeys.size() > 0){
            Collections.sort(orderKeys);
            StringBuilder stringBuilder = new StringBuilder();
            for(String orderKey: orderKeys){
                
                if (!TextUtils.isEmpty(orderKey)){
                    if (!TextUtils.isEmpty(stringBuilder)){
                        stringBuilder.append(",").append(orderKey);
                    } else {
                        stringBuilder.append(orderKey);
                    }
                }
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }

    
    public String getErrorIdKey() {
        String errorId = getRunningOrderErrorId();
        if (!TextUtils.isEmpty(errorId)) {
            return EAICoderUtil.getMD5Code(errorId);
        }
        return "";
    }

    
    public static class Data {
        public int Status;
        public Map<Integer, Map<Integer, String>> Error; 
    }
}
