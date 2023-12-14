

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

public class BchTransResultBean {
    private String result;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @SerializedName("error")
    private String message;

    @Override
    public String toString() {
        return "BchTransResultBean{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
