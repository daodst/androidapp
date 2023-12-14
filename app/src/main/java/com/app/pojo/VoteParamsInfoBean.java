package com.app.pojo;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.wallet.ctc.model.blockchain.EvmosAmountsBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.utils.AllUtils;


public class VoteParamsInfoBean{
    public String subspace;
    public String key;
    public String keyAlias;
    public String value; 
    public String keyDesc;
    public String valueDesc;
    public boolean isBigAmount; 

    public String bigAmount;
    public String denom;

    public String inputValue;

    public VoteParamsInfoBean() {
    }

    public VoteParamsInfoBean(String subspace, String key, String keyAlias, String keyDesc, String valueDesc) {
        this(subspace, key, keyAlias, keyDesc, valueDesc, false);
    }

    public VoteParamsInfoBean(String subspace, String key, String keyAlias, String keyDesc, String valueDesc, boolean isBigAmount) {
        this.subspace = subspace;
        this.key = key;
        this.keyAlias = keyAlias;
        this.keyDesc = keyDesc;
        this.valueDesc = valueDesc;
        this.isBigAmount = isBigAmount;
    }

    
    public void setAmountDenom(String denom, String bigAmount) {
        this.denom = denom;
        this.bigAmount = bigAmount;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNowValue() {
        if (isBigAmount && !TextUtils.isEmpty(bigAmount)) {
            return AllUtils.getTenDecimalValue(bigAmount, 18, 6)+denom;
        } else {
            return value;
        }
    }

    public boolean isChangeSeeting() {
        if (!TextUtils.isEmpty(inputValue)) {
            return true;
        } else {
            return false;
        }
    }

    public String getInputValue() {
        if (isBigAmount && !TextUtils.isEmpty(inputValue)) {
            String amount = AllUtils.getBigDecimalValue(inputValue, 18);
            Map<String,String> prams = new HashMap<>();
            prams.put("denom", denom);
            prams.put("amount", amount);
            String result = new Gson().toJson(prams);
            return result;
        } else {
            return "\""+inputValue+"\"";
        }
    }


    
    public static class GovParams {
        public Map<String,JsonElement> voting_params;
        public GovDeposit deposit_params;
        public Map<String, JsonElement> tally_params;


        public Map<String,JsonElement> getAllMaps() {
            Map<String,JsonElement> maps = new HashMap<>();
            maps.putAll(voting_params);
            if (null != deposit_params) {
                maps.putAll(deposit_params.getMap());
            }
            maps.putAll(tally_params);
            return maps;
        }


    }

    public static class GovDeposit {
        public List<EvmosAmountsBean> min_deposit;
        public String max_deposit_period;

        public Map<String,JsonElement> getMap() {
            Map<String,JsonElement> map = new HashMap<>();
            if (min_deposit != null && min_deposit.size() > 0) {
                String value = "";
                JsonObject jsonObject = new JsonObject();
                EvmosAmountsBean amountsBean = min_deposit.get(0);
                jsonObject.addProperty("denom", amountsBean.denom);
                jsonObject.addProperty("amount", amountsBean.amount);
                map.put("min_deposit", jsonObject);
            }
            JsonPrimitive primitive = new JsonPrimitive(max_deposit_period);
            map.put("max_deposit_period", primitive);
            return map;
        }

    }
}
