

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class EvmosHttpDataBean<T> extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public T data;
}
