

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class ChainBridgeConfigBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;


    public boolean isEnabled(){
        if (data != null){
            return data.enabled;
        } else {
            return false;
        }
    }

    public static class Data {
        public boolean enabled;
        public String ratio;
        public String days;
    }
}
