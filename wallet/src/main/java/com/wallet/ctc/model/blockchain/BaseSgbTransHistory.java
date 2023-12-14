

package com.wallet.ctc.model.blockchain;

import java.util.List;



public class BaseSgbTransHistory {

    private Integer code;
    private DataBean data;
    private String message;
    private Integer ttl;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public static class DataBean {
        private List<SgbTransHistory> transfers;
        private Integer count;

        public List<SgbTransHistory> getTransfers() {
            return transfers;
        }

        public void setTransfers(List<SgbTransHistory> transfers) {
            this.transfers = transfers;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}
