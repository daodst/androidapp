

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.app.utils.TimeUtil;



public class TransactionRecordBean implements Serializable {

    

    private String id;
    @SerializedName("amount")
    private String transaction_number;

    @SerializedName("facc")
    private String fromAddress;

    @SerializedName("tacc")
    private String toAddress;

    private String minerCost;

    @SerializedName("memo")
    private String remarks;

    @SerializedName("time")
    private String transaction;

    @SerializedName("block_num")
    private String block;

    @SerializedName("fee")
    private String spend_eth;

    private String url;
    @SerializedName("token")
    private String coin_name;

    @SerializedName("tx_hash")
    private String transaction_no;
    private String day;
    private String sum;
    private String authority_sign;
    private int type;
    private int status; 
    private int showFlash = 0;
    private int jindu = 0;
    private int flag;

    private long create_time;
    public String trade_type;

    public int getJindu() {
        return jindu;
    }

    public void setJindu(int jindu) {
        this.jindu = jindu;
    }

    private String error;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getShowFlash() {
        return showFlash;
    }

    public void setShowFlash(int showFlash) {
        this.showFlash = showFlash;
    }

    
    public String getBigIntTransferAmount() {
        return getPatternAmount(transaction_number);
    }

    Pattern mPattern = Pattern.compile("\\d+(\\.\\d+)?");

    public String getPatternAmount(String value) {
        try {
            Matcher m = mPattern.matcher(value);
            if (m.find()) {
                return m.group();
            } else {
                
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            return value;
        }
    }


    public void setTransaction_number(String transaction_number) {
        this.transaction_number = transaction_number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromAddress() {
        if (fromAddress == null) {
            return "";
        }
        String address = fromAddress.substring(0, 10);
        int len = fromAddress.length();
        address = address + "..." + fromAddress.substring(len - 10, len);
        return address;
    }



    public String getFromAllAddress() {
        if (fromAddress == null) {
            return "";
        }
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        if (toAddress == null || toAddress.length() < 10) {
            return "";
        }
        String address = toAddress.substring(0, 10);
        int len = toAddress.length();
        address = address + "..." + toAddress.substring(len - 10, len);
        return address;
    }

    public String getToAllAddress() {
        if (toAddress == null) {
            return "";
        }
        return toAddress;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getMinerCost() {
        return minerCost;
    }

    public void setMinerCost(String minerCost) {
        this.minerCost = minerCost;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getAuthority_sign() {
        return authority_sign;
    }

    public void setAuthority_sign(String authority_sign) {
        this.authority_sign = authority_sign;
    }

    public String getTransferTime() {
        return TimeUtil.getYYYYMMddHHMMSS(create_time * 1000);
    }


    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getBigIntFeeAmount() {
        return getPatternAmount(spend_eth);
    }

    public void setSpend_eth(String spend_eth) {
        this.spend_eth = spend_eth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoin_name() {
        if (null == coin_name) {
            return "";
        }
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getTransaction_no() {
        String transactionNo = transaction_no;
        if (transaction_no.length() > 10) {
            transactionNo = transaction_no.substring(0, 8);
            int len = transaction_no.length();
            transactionNo = transactionNo + "..." + transaction_no.substring(len - 8, len);
        }
        return transactionNo;
    }

    public String getAllTransaction_no() {
        return transaction_no;
    }


    public void setTransaction_no(String transaction_no) {
        this.transaction_no = transaction_no;
    }

    public String getDay() {
        if (!TextUtils.isEmpty(day)) {
            return day;
        }
        day = TimeUtil.getYYYYMMdd(create_time * 1000);
        return day;
        
    }



    public void setDay(String day) {
        this.day = day;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
