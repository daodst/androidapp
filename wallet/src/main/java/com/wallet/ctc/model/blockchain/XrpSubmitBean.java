

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;



public class XrpSubmitBean {


    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private String engine_result;
        private Integer engine_result_code;
        private String engine_result_message;
        private String status;
        private String tx_blob;
        private TxJsonBean tx_json;

        public String getEngine_result() {
            return engine_result;
        }

        public void setEngine_result(String engine_result) {
            this.engine_result = engine_result;
        }

        public Integer getEngine_result_code() {
            return engine_result_code;
        }

        public void setEngine_result_code(Integer engine_result_code) {
            this.engine_result_code = engine_result_code;
        }

        public String getEngine_result_message() {
            return engine_result_message;
        }

        public void setEngine_result_message(String engine_result_message) {
            this.engine_result_message = engine_result_message;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTx_blob() {
            return tx_blob;
        }

        public void setTx_blob(String tx_blob) {
            this.tx_blob = tx_blob;
        }

        public TxJsonBean getTx_json() {
            return tx_json;
        }

        public void setTx_json(TxJsonBean tx_json) {
            this.tx_json = tx_json;
        }

        public static class TxJsonBean {
            @SerializedName("Account")
            private String account;
            @SerializedName("Amount")
            private AmountBean amount;
            @SerializedName("Destination")
            private String destination;
            @SerializedName("Fee")
            private String fee;
            @SerializedName("Flags")
            private Long flags;
            @SerializedName("Sequence")
            private Integer sequence;
            @SerializedName("SigningPubKey")
            private String signingPubKey;
            @SerializedName("TransactionType")
            private String transactionType;
            @SerializedName("TxnSignature")
            private String txnSignature;
            private String hash;

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public AmountBean getAmount() {
                return amount;
            }

            public void setAmount(AmountBean amount) {
                this.amount = amount;
            }

            public String getDestination() {
                return destination;
            }

            public void setDestination(String destination) {
                this.destination = destination;
            }

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }

            public Long getFlags() {
                return flags;
            }

            public void setFlags(Long flags) {
                this.flags = flags;
            }

            public Integer getSequence() {
                return sequence;
            }

            public void setSequence(Integer sequence) {
                this.sequence = sequence;
            }

            public String getSigningPubKey() {
                return signingPubKey;
            }

            public void setSigningPubKey(String signingPubKey) {
                this.signingPubKey = signingPubKey;
            }

            public String getTransactionType() {
                return transactionType;
            }

            public void setTransactionType(String transactionType) {
                this.transactionType = transactionType;
            }

            public String getTxnSignature() {
                return txnSignature;
            }

            public void setTxnSignature(String txnSignature) {
                this.txnSignature = txnSignature;
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }

            public static class AmountBean {
                private String currency;
                private String issuer;
                private String value;
            }
        }
    }
}
