

package com.wallet.ctc.ui.blockchain.issuance.pojo;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.wallet.ctc.R;

import common.app.AppApplication;


public class WRPcResponseBody<T> {
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
        Context context = AppApplication.getInstance().getApplicationContext();
        if (null != context && !TextUtils.isEmpty(info)) {
            if (info.contains("insufficient funds")) {
                return context.getString(R.string.insufficient_balance);
            } else if (info.contains("insufficient level to transfer")) {
                int strid = context.getResources().getIdentifier("error_insufficient_level_to_transfer", "string", context.getPackageName());
                return context.getResources().getString(strid);
            }
        }
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
