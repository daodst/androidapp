

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.wallet.ctc.util.AllUtils;

import java.io.Serializable;
import java.math.BigDecimal;



public class TransactionNewEthRecordBean implements Serializable {


    

    private String ContractAddress;
    private String GasUsed;
    public String Status;
    private int Timestamp;
    private String TokenAddress;
    private int TokenDecimal;
    private String ValueDecimal;
    private String ValueInt;
    private int blockNumber;
    private int digits;
    private String from;
    private String gas;
    private String gasPrice;
    private String hash;
    private String input;
    private String nonce;
    private String to;
    private String coin_name;
    private String remarks;
    private int transactionIndex;
    private BigDecimal spend_eth;

    public String getSpend_eth() {
        if(TextUtils.isEmpty(GasUsed)){
            GasUsed=gas;
        }

        if(TextUtils.isEmpty(GasUsed)||TextUtils.isEmpty(gasPrice)){
            return "--";
        }
        spend_eth=new BigDecimal(GasUsed).multiply(new BigDecimal(gasPrice));
        spend_eth=spend_eth.divide(new BigDecimal("1000000000000000000"));
        return spend_eth.toPlainString();
    }

    public void setSpend_eth(BigDecimal spend_eth) {
        this.spend_eth = spend_eth;
    }

    public String getCoin_name() {
        if(null==coin_name||coin_name.equals("null")|| TextUtils.isEmpty(coin_name)){
            if(null==TokenAddress||TextUtils.isEmpty(TokenAddress)){
                return "ether";
            }
            return "";
        }
        return coin_name;
    }



    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getGasUsed() {
        return GasUsed;
    }

    public void setGasUsed(String gasUsed) {
        GasUsed = gasUsed;
    }

    public String getDay() {
        String time="";
        time= AllUtils.getTimeNYR(Timestamp+"");
        return time;
    }


    public String getContractAddress() {
        return ContractAddress;
    }

    public void setContractAddress(String contractAddress) {
        ContractAddress = contractAddress;
    }

    public int getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(int Timestamp) {
        this.Timestamp = Timestamp;
    }

    public String getTokenAddress() {
        return TokenAddress;
    }

    public void setTokenAddress(String TokenAddress) {
        this.TokenAddress = TokenAddress;
    }

    public int getTokenDecimal() {
        return TokenDecimal;
    }

    public void setTokenDecimal(int TokenDecimal) {
        this.TokenDecimal = TokenDecimal;
    }

    public String getValueDecimal() {
        
        if(TextUtils.isEmpty(ValueDecimal)&&!TextUtils.isEmpty(ValueInt)&&TokenDecimal>0){
            ValueDecimal=new BigDecimal(ValueInt).divide(new BigDecimal(Math.pow(10,TokenDecimal))).toPlainString();
        }
        return ValueDecimal;
    }

    public void setValueDecimal(String ValueDecimal) {
        this.ValueDecimal = ValueDecimal;
    }

    public String getValueInt() {
        return ValueInt;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setValueInt(String ValueInt) {
        this.ValueInt = ValueInt;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getFrom() {
        if(from==null){
            return "";
        }
        String address=from.substring(0,10);
        int len=from.length();
        address=address+"..."+from.substring(len-10,len);
        return address;

    }
    public String getFromAllAddress() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }




    public String getTo() {
        if(to==null){
            return "";
        }
        String address=to.substring(0,10);
        int len=to.length();
        address=address+"..."+to.substring(len-10,len);
        return address;
    }

    public String getToAllAddress() {
        if(to==null){
            return "";
        }
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

