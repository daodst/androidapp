

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.wallet.ctc.util.AllUtils;

import java.io.Serializable;



public class TransactionXrpRecordBean implements Serializable {


    

    private String from_account;
    private String to_account;
    private String tag;
    private String amount;
    public String fee;
    private String hash;
    private String ledger_index;
    private String transaction_type;
    private String sequence;
    private String tx_time;
    private String Coin_name;
    private boolean is_success;
    private String tx_result;



    public String getCoin_name() {
        return Coin_name;
    }

    public void setCoin_name(String coin_name) {
        Coin_name = coin_name;
    }

    public String getFrom_account() {
        return from_account;
    }

    public void setFrom_account(String from_account) {
        this.from_account = from_account;
    }

    public String getTo_account() {
        return to_account;
    }

    public void setTo_account(String to_account) {
        this.to_account = to_account;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getLedger_index() {
        return ledger_index;
    }

    public void setLedger_index(String ledger_index) {
        this.ledger_index = ledger_index;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getDay() {
        if (TextUtils.isEmpty(tx_time)) {
            return "- -";
        }
        String time="";
        time= AllUtils.getTimeByStr(tx_time);
        if (TextUtils.isEmpty(time)) {
            return tx_time;
        }
        return time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTx_time() {
        return tx_time;
    }

    public void setTx_time(String tx_time) {
        this.tx_time = tx_time;
    }

    public boolean isIs_success() {
        return is_success;
    }

    public void setIs_success(boolean is_success) {
        this.is_success = is_success;
    }

    public String getTx_result() {
        return tx_result;
    }

    public void setTx_result(String tx_result) {
        this.tx_result = tx_result;
    }
}

