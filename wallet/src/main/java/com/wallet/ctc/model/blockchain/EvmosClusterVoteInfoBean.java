package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class EvmosClusterVoteInfoBean extends EvmosHttpBean implements Serializable {
    @SerializedName(value = "data", alternate = {"Data"})
    public EvmosClusterVoteInfoBean.Data data;

    public static class Data {
        public  FinalTallyResultEntity tally;
        public static class FinalTallyResultEntity implements Serializable {
            

            public String yes_count;
            public String abstain_count;
            public String no_count;
            public String no_with_veto_count;
        }


    }


}
