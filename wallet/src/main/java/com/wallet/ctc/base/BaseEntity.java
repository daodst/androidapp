

package com.wallet.ctc.base;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.wallet.ctc.Constants;



public class BaseEntity {
    @SerializedName("status")
    private int status;
    @SerializedName("info")
    private Object info;
    @SerializedName("data")
    private Object data;
    @SerializedName("message")
    private Object message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInfo() {
        if(null==info||TextUtils.isEmpty(info.toString())){
            if(null!=message||TextUtils.isEmpty(message.toString())){
                return "";
            }
            return message.toString();
        }
        return info.toString();
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getData() {
        if(null==data|| TextUtils.isEmpty(data.toString())){
            return message;
        }
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    
    public boolean isCodeInvalid() {
        return status == Constants.REQUEST_SUCCESS;
    }
}
