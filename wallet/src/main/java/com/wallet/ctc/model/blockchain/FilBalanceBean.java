

package com.wallet.ctc.model.blockchain;


public class FilBalanceBean {

    

    private String jsonrpc;
    private String result;
    private int id;
    private EtcBalanceBean.ErrorBean error;
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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
