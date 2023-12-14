

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FilChainHeadBean {

    

    private String jsonrpc;
    private ResultBean result;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EtcBalanceBean.ErrorBean getError() {
        return error;
    }

    public void setError(EtcBalanceBean.ErrorBean error) {
        this.error = error;
    }

    private EtcBalanceBean.ErrorBean error;
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        

        private List<CidsBean> Cids;

        public List<CidsBean> getCids() {
            return Cids;
        }

        public void setCids(List<CidsBean> Cids) {
            this.Cids = Cids;
        }

        public static class CidsBean {
            @SerializedName("/")
            private String a;

            public String getA() {
                return a;
            }

            public void setA(String a) {
                this.a = a;
            }
        }
    }
}
