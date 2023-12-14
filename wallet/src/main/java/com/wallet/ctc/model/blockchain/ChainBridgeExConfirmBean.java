

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class ChainBridgeExConfirmBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public long data;
}
