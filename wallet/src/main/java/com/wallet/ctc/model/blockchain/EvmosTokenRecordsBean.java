

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EvmosTokenRecordsBean extends EvmosHttpBean {
    private List<Data> data;
    Pattern mPattern;

    
    private String getStrNum(String amountStr) {
        if (null == mPattern) {
            mPattern = Pattern.compile("\\d+(\\.\\d+)?");
        }
        try {
            Matcher m = mPattern.matcher(amountStr);
            boolean find = m.find();
            if (find) {
                return m.group();
            } else {
                
                return amountStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            return amountStr;
        }
    }

    
    public List<Data> convertData(String address, String tokenName, int tokenDecimal) {
        if (null != data && data.size() > 0 && !TextUtils.isEmpty(address)) {
            for (int i = 0; i < data.size(); i++) {
                
                if (address.equalsIgnoreCase(data.get(i).Facc)) {
                    
                    data.get(i).direction = "outgoing";
                } else {
                    
                    data.get(i).direction = "incoming";
                }

                
                String fee = data.get(i).Fee;
                if (!TextUtils.isEmpty(fee)) {
                    String[] fees = fee.split(",");
                    List<Fee> feelist = new ArrayList<>();
                    for (String feeStr : fees) {
                        
                        if (!TextUtils.isEmpty(feeStr)) {
                            String amount = getStrNum(feeStr);
                            String coinName = feeStr.substring(amount.length());
                            feelist.add(new Fee(coinName, amount));
                        }
                    }
                    data.get(i).feeList = feelist;
                }

                
                data.get(i).tokenDecimal = tokenDecimal;
                data.get(i).tokenName = tokenName;
            }
        }
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Data {
        public String Uid;

        @SerializedName(value = "BlockNum", alternate = "block_num")
        public long BlockNum;
        @SerializedName(value = "Txhash", alternate = "txhash")
        public String Txhash;
        @SerializedName(value = "LogIndex", alternate = "log_index")
        public String LogIndex;
        @SerializedName(value = "Facc", alternate = "facc")
        public String Facc;
        @SerializedName(value = "Tacc", alternate = "tacc")
        public String Tacc;
        @SerializedName(value = "Token", alternate = "token")
        public String Token;
        @SerializedName(value = "Amount", alternate = "amount")
        public String Amount;
        @SerializedName(value = "TradeType", alternate = "trade_type")
        public String TradeType;
        @SerializedName(value = "Status", alternate = "status")
        public int Status; 
        @SerializedName(value = "Errmsg", alternate = "errmsg")
        public String Errmsg;
        @SerializedName(value = "CreateTime", alternate = "create_time")
        public long CreateTime;
        @SerializedName(value = "Fee", alternate = "fee")
        public String Fee;
        @SerializedName(value = "ToBalance", alternate = "to_balance")
        public String ToBalance;
        @SerializedName(value = "FromBalance", alternate = "from_balance")
        public String FromBalance;
        @SerializedName(value = "Memo", alternate = "memo")
        public String Memo; 

        public String direction;
        public List<Fee> fees;
        public String tokenName;
        public int tokenDecimal;
        public List<Fee> feeList;
    }

    public static class Fee {
        public String coinname;
        public String value;

        public Fee(String coinname, String value) {
            this.coinname = coinname;
            this.value = value;
        }
    }
}
