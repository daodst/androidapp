

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class ResponseBodyT<T> {
    @SerializedName("status")
    public int statusCode;

    @SerializedName("info")
    public String info;

    @SerializedName("data")
    public T data;


}
