

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import common.app.utils.AllUtils;


public class ChainBridgePreOrdersBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;


    
    public ChainBridgeConfigBean chainBridgeConfig;


    public List<ChainBridgeOrderBean> getPreOrders(){
        if (null != data){
            return data.order;
        } else {
            return null;
        }
    }

    public boolean isHasOrders(){
        List<ChainBridgeOrderBean> orders = getPreOrders();
        if (orders != null && orders.size() > 0){
            return true;
        } else {
            return false;
        }
    }

    
    public String getSuggestNumStrs(int decimal){
        if (data != null && data.match_amount != null && data.match_amount.size() > 0){
            StringBuilder tipStrBuilder = new StringBuilder();
            for(String num: data.match_amount){
                if ("0".equals(num)){
                    continue;
                }
                String numStr = AllUtils.getTenDecimalValue(num, decimal, 4);
                if (!TextUtils.isEmpty(numStr)){
                    if(TextUtils.isEmpty(tipStrBuilder)){
                        tipStrBuilder.append(numStr);
                    } else {
                        tipStrBuilder.append(",").append(numStr);
                    }
                }
            }
            return tipStrBuilder.toString();
        } else {
            return "";
        }
    }

    
    public String getReceiveAmount(int decimal){
        if (data != null){
            return AllUtils.getTenDecimalValue(data.receive_amount, decimal, 6);
        }
        return "";
    }

    
    public String getShowGasRangeStr(int sourceDecimal, String sourceCoin, int targetDecimal, String targetCoin){
        StringBuilder stringBuilder = new StringBuilder();

        String targetGasRange = getTargetGasRange(targetDecimal);
        if(!TextUtils.isEmpty(targetGasRange)){
            stringBuilder.append(targetGasRange).append(targetCoin);
        }

        String sourceGasRange = getSourceGasRange(sourceDecimal);
        if(!TextUtils.isEmpty(sourceGasRange)){
            if(!TextUtils.isEmpty(stringBuilder)){
                stringBuilder.append("\n");
            }
            stringBuilder.append(sourceGasRange).append(sourceCoin);
        }
        if (TextUtils.isEmpty(stringBuilder)){
            return "- -";
        } else {
            return stringBuilder.toString();
        }
    }

    public String getSourceGasRange(int decimal){
        if(null != data && null != data.gas_info && null != data.gas_info.buy_chain){

            StringBuilder stringBuilder = new StringBuilder();
            String minGas = data.gas_info.buy_chain.min_gas;
            if (!TextUtils.isEmpty(minGas)){
                
                String minGasNum = AllUtils.getTenDecimalValue(minGas, decimal, 18);
                stringBuilder.append(minGasNum+"");
            }

            String maxGas = data.gas_info.buy_chain.max_gas;
            if(!TextUtils.isEmpty(maxGas) && !maxGas.equals(minGas)){
                
                String maxGasNum = AllUtils.getTenDecimalValue(maxGas, decimal, 18);
                if(!TextUtils.isEmpty(stringBuilder)){
                    stringBuilder.append("-").append(maxGasNum);
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }

    
    public String getTargetGasRange(int decimal) {
        if(null != data && null != data.gas_info && null != data.gas_info.sell_chain){
            StringBuilder stringBuilder = new StringBuilder();
            String minGas = data.gas_info.sell_chain.min_gas;
            if (!TextUtils.isEmpty(minGas)){
                
                String minGasNum = AllUtils.getTenDecimalValue(minGas, decimal, 18);
                stringBuilder.append(minGasNum+"");
            }

            String maxGas = data.gas_info.sell_chain.max_gas;
            if(!TextUtils.isEmpty(maxGas) && !maxGas.equals(minGas)){
                
                String maxGasNum = AllUtils.getTenDecimalValue(maxGas, decimal, 18);
                if(!TextUtils.isEmpty(stringBuilder)){
                    stringBuilder.append("-").append(maxGasNum);
                }
            }
            return stringBuilder.toString();
        }
        return "";
    }

    public static class Data {
        public List<ChainBridgeOrderBean> order;
        public List<String> match_amount;
        public String receive_amount; 
        public GasInfo gas_info;
    }

    public static class GasInfo{
        public GasRange buy_chain; 
        public GasRange sell_chain; 
    }

    public static class GasRange {
        public String min_gas;
        public String max_gas;
    }
}
