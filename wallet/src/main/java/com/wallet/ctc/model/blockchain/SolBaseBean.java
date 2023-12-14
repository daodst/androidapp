

package com.wallet.ctc.model.blockchain;

import com.google.gson.JsonObject;


public class SolBaseBean {
    private String jsonrpc;
    private JsonObject result;
    private Integer id;
    private ErrorBean error;


    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
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
        private Object message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message.toString();
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
