

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

public class FilGasMsgBean {

    

    private String jsonrpc;
    

    private ResultBean result;
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class ResultBean {
        private int Version;
        private String To;
        private String From;
        private int Nonce;
        private String Value;
        private int GasLimit;
        private String GasFeeCap;
        private String GasPremium;
        private int Method;
        private Object Params;
        

        private CIDBean CID;

        public int getVersion() {
            return Version;
        }

        public void setVersion(int Version) {
            this.Version = Version;
        }

        public String getTo() {
            return To;
        }

        public void setTo(String To) {
            this.To = To;
        }

        public String getFrom() {
            return From;
        }

        public void setFrom(String From) {
            this.From = From;
        }

        public int getNonce() {
            return Nonce;
        }

        public void setNonce(int Nonce) {
            this.Nonce = Nonce;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }

        public int getGasLimit() {
            return GasLimit;
        }

        public void setGasLimit(int GasLimit) {
            this.GasLimit = GasLimit;
        }

        public String getGasFeeCap() {
            return GasFeeCap;
        }

        public void setGasFeeCap(String GasFeeCap) {
            this.GasFeeCap = GasFeeCap;
        }

        public String getGasPremium() {
            return GasPremium;
        }

        public void setGasPremium(String GasPremium) {
            this.GasPremium = GasPremium;
        }

        public int getMethod() {
            return Method;
        }

        public void setMethod(int Method) {
            this.Method = Method;
        }

        public Object getParams() {
            return Params;
        }

        public void setParams(Object Params) {
            this.Params = Params;
        }

        public CIDBean getCID() {
            return CID;
        }

        public void setCID(CIDBean CID) {
            this.CID = CID;
        }

        public static class CIDBean {
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
