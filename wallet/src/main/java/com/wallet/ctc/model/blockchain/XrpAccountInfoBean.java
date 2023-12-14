

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.util.List;



public class XrpAccountInfoBean {

    private ResultBean result;
    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        private AccountDataBean account_data;
        private Integer ledger_current_index;
        private QueueDataBean queue_data;
        private String status;
        private Boolean validated;

        public AccountDataBean getAccount_data() {
            return account_data;
        }

        public void setAccount_data(AccountDataBean account_data) {
            this.account_data = account_data;
        }

        public Integer getLedger_current_index() {
            return ledger_current_index;
        }

        public void setLedger_current_index(Integer ledger_current_index) {
            this.ledger_current_index = ledger_current_index;
        }

        public QueueDataBean getQueue_data() {
            return queue_data;
        }

        public void setQueue_data(QueueDataBean queue_data) {
            this.queue_data = queue_data;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Boolean getValidated() {
            return validated;
        }

        public void setValidated(Boolean validated) {
            this.validated = validated;
        }

        public static class AccountDataBean {
            @SerializedName("Account")
            private String account;
            @SerializedName("Balance")
            private String balance;
            @SerializedName("Flags")
            private Integer flags;
            @SerializedName("LedgerEntryType")
            private String ledgerEntryType;
            @SerializedName("OwnerCount")
            private Integer ownerCount;
            @SerializedName("PreviousTxnID")
            private String previousTxnID;
            @SerializedName("PreviousTxnLgrSeq")
            private Integer previousTxnLgrSeq;
            @SerializedName("Sequence")
            private Integer sequence;
            private String index;

            public String getAccount() {
                return account;
            }

            public void setAccount(String account) {
                this.account = account;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }

            public Integer getFlags() {
                return flags;
            }

            public void setFlags(Integer flags) {
                this.flags = flags;
            }

            public String getLedgerEntryType() {
                return ledgerEntryType;
            }

            public void setLedgerEntryType(String ledgerEntryType) {
                this.ledgerEntryType = ledgerEntryType;
            }

            public Integer getOwnerCount() {
                return ownerCount;
            }

            public void setOwnerCount(Integer ownerCount) {
                this.ownerCount = ownerCount;
            }

            public String getPreviousTxnID() {
                return previousTxnID;
            }

            public void setPreviousTxnID(String previousTxnID) {
                this.previousTxnID = previousTxnID;
            }

            public Integer getPreviousTxnLgrSeq() {
                return previousTxnLgrSeq;
            }

            public void setPreviousTxnLgrSeq(Integer previousTxnLgrSeq) {
                this.previousTxnLgrSeq = previousTxnLgrSeq;
            }

            public Integer getSequence() {
                return sequence;
            }

            public void setSequence(Integer sequence) {
                this.sequence = sequence;
            }

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }
        }
        public static class QueueDataBean {
            private Boolean auth_change_queued;
            private Integer highest_sequence;
            private Integer lowest_sequence;
            private String max_spend_drops_total;
            private List<TransactionsBean> transactions;
            private Integer txn_count;

            public Boolean getAuth_change_queued() {
                return auth_change_queued;
            }

            public void setAuth_change_queued(Boolean auth_change_queued) {
                this.auth_change_queued = auth_change_queued;
            }

            public Integer getHighest_sequence() {
                return highest_sequence;
            }

            public void setHighest_sequence(Integer highest_sequence) {
                this.highest_sequence = highest_sequence;
            }

            public Integer getLowest_sequence() {
                return lowest_sequence;
            }

            public void setLowest_sequence(Integer lowest_sequence) {
                this.lowest_sequence = lowest_sequence;
            }

            public String getMax_spend_drops_total() {
                return max_spend_drops_total;
            }

            public void setMax_spend_drops_total(String max_spend_drops_total) {
                this.max_spend_drops_total = max_spend_drops_total;
            }

            public List<TransactionsBean> getTransactions() {
                return transactions;
            }

            public void setTransactions(List<TransactionsBean> transactions) {
                this.transactions = transactions;
            }

            public Integer getTxn_count() {
                return txn_count;
            }

            public void setTxn_count(Integer txn_count) {
                this.txn_count = txn_count;
            }

            public static class TransactionsBean {
                private Boolean auth_change;
                private String fee;
                private String fee_level;
                private String max_spend_drops;
                private Integer seq;
                @SerializedName("LastLedgerSequence")
                private Integer lastLedgerSequence;

                public Boolean getAuth_change() {
                    return auth_change;
                }

                public void setAuth_change(Boolean auth_change) {
                    this.auth_change = auth_change;
                }

                public String getFee() {
                    return fee;
                }

                public void setFee(String fee) {
                    this.fee = fee;
                }

                public String getFee_level() {
                    return fee_level;
                }

                public void setFee_level(String fee_level) {
                    this.fee_level = fee_level;
                }

                public String getMax_spend_drops() {
                    return max_spend_drops;
                }

                public void setMax_spend_drops(String max_spend_drops) {
                    this.max_spend_drops = max_spend_drops;
                }

                public Integer getSeq() {
                    return seq;
                }

                public void setSeq(Integer seq) {
                    this.seq = seq;
                }

                public Integer getLastLedgerSequence() {
                    return lastLedgerSequence;
                }

                public void setLastLedgerSequence(Integer lastLedgerSequence) {
                    this.lastLedgerSequence = lastLedgerSequence;
                }
            }
        }
    }
}
