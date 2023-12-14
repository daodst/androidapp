package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;


public class TransactionInfoBean {
    private String jsonrpc;
    private TransactionData result;
    private int id;
    private ErrorBean error;
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public TransactionData getResult() {
        return result;
    }

    public void setResult(TransactionData result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ErrorBean getError() {
        return error;
    }

    public void setError(ErrorBean error) {
        this.error = error;
    }

    public static class ErrorBean {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class TransactionData {
        public String blockHash;
        public String blockNumber;
        public String from;
        public String gas;
        public String gasPrice;
        public String hash;
        public String input;
        public String nonce;
        public String to;
        public String transactionIndex;
        public String value;
        public String v;
        public String r;
        public String s;

        public String contractAddress;
        public String cumulativeGasUsed;
        public String effectiveGasPrice;
        public String gasUsed;
        public String status;
        public String transactionHash;
        public String type;

        
        public boolean isTransferSuccess() {
            if (!TextUtils.isEmpty(status) && status.equals("0x1")) {
                return true;
            } else {
                return false;
            }
        }

        
        public boolean isTransferFail() {
            if (!TextUtils.isEmpty(status) && status.equals("0x0")) {
                return true;
            } else {
                return false;
            }
        }

    }

    @Override
    public String toString() {
        return "FilBalanceBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", result='" + result + '\'' +
                ", id=" + id +
                ", error=" + error +
                '}';
    }
}
