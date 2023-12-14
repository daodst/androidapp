

package com.wallet.ctc.model.blockchain;

import java.util.List;


public class BaseTrxBean {


    

    private boolean success;
    private MetaBean meta;
    private List<Object> data;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static class MetaBean {
        

        private double at;
        private double page_size;
        private String fingerprint;

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public double getAt() {
            return at;
        }

        public void setAt(double at) {
            this.at = at;
        }

        public double getPage_size() {
            return page_size;
        }

        public void setPage_size(double page_size) {
            this.page_size = page_size;
        }
    }
}
