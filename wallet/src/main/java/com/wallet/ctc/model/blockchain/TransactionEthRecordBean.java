

package com.wallet.ctc.model.blockchain;

import java.io.Serializable;
import java.math.BigDecimal;



public class TransactionEthRecordBean implements Serializable {

    private String id;
    private String transaction_number;
    private String fromAddress;
    private String toAddress;
    private String minerCost;
    private String remarks;
    private String transaction;
    private String block;
    private String spend_eth;
    private String url;
    private String coin_name;
    private String transaction_no;
    private String day;
    private int status;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransaction_number() {
        if(transaction_number==null){
            return "0";
        }
        BigDecimal bigDecimal=new BigDecimal(transaction_number);
        return bigDecimal.toPlainString();
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
        if(fromAddress==null){
            return "";
        }
        String address=fromAddress.substring(0,10);
        int len=fromAddress.length();
        address=address+"..."+fromAddress.substring(len-10,len);
        return address;
    }

    public String getFromAllAddress() {
        if(fromAddress==null){
            return "";
        }
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        if(toAddress==null){
            return "";
        }
        String address=toAddress.substring(0,10);
        int len=toAddress.length();
        address=address+"..."+toAddress.substring(len-10,len);
        return address;
    }
    public String getToAllAddress() {
        if(toAddress==null){
            return "";
        }
        return toAddress;
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



    public String getTransaction() {
        BigDecimal bigDecimal=new BigDecimal(transaction);
        return bigDecimal.toPlainString();
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

    public String getSpend_eth() {
        if(spend_eth==null){
            spend_eth="0.0";
        }
        return spend_eth;
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
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getTransaction_no() {
        if(null==transaction_no){
            return "";
        }
        if(transaction_no.length()>10){
            String transactionNo=transaction_no.substring(0,8);
            int len=transaction_no.length();
            transaction_no=transactionNo+"..."+transaction_no.substring(len-8,len);
        }
        return transaction_no;
    }

    public String getAllTransaction_no() {
        return transaction_no;
    }


    public void setTransaction_no(String transaction_no) {
        this.transaction_no = transaction_no;
    }

    public String getDay() {
        if(day==null){
            day="";
        }
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

