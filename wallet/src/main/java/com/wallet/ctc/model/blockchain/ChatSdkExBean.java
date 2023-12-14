

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class ChatSdkExBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Object data;
}
