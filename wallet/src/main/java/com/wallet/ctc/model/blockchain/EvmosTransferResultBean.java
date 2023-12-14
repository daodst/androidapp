

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;


public class EvmosTransferResultBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;

    public static class Data {
        public String height;
        public String tx_hash;
        public String code_space;
        public int code;
        public String signed_tx_str;
    }
}
