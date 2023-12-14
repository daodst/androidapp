

package com.app.home.pojo.rpc;

import com.google.gson.annotations.SerializedName;


public class RPcResponseBody<T> {
    @SerializedName("status")
    private int statusCode;

    @SerializedName("info")
    private String info;

    @SerializedName("data")
    private T data;

    
    public boolean isOk() {
        return 1 == statusCode && data != null;
    }


    public String getInfo() {
        return info;
    }

    public T getData() {
        return data;
    }


    public String getEmptyData() {
        return 0 == statusCode ? "false" : "true";
    }


    @Override
    public String toString() {
        return "ResponseBody{" +
                "statusCode=" + statusCode +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}
