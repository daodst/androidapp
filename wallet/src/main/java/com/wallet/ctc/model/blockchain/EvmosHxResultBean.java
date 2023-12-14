

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class EvmosHxResultBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;

    
    public boolean isTxSuccess() {
        if (null != data && data.found && data.code == 0) {
            return true;
        } else {
            return false;
        }
    }

    
    public boolean isTxFail(int useTimeSeconds) {
        if (null != data && data.found && data.code != 0) {
            return true;
        } else if(null != data && !data.found && useTimeSeconds > 7) {
            return true;
        } else {
            return false;
        }
    }

    public static class Data {
        public boolean found;
        public long height;
        public long code;
    }
}
