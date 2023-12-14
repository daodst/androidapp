

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class EvmosRpcQueryBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public String data;
}
