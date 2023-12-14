package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class EvmosClusterPersonVoteBean extends EvmosHttpBean implements Serializable {

    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;

    public static class Data implements Serializable {


        

        @SerializedName("pagination")
        public PaginationEntity pagination;
        @SerializedName("votes")
        public List<VoteEntity> votes;

        public static class PaginationEntity implements Serializable {
            

            @SerializedName("total")
            public int total;
        }

        public static class VoteEntity implements Serializable {
            

            @SerializedName("proposal_id")
            public int proposalId;
            @SerializedName("voter")
            public String voter;
            @SerializedName("option")
            public int option;
            @SerializedName("submit_time")
            public String submitTime;
        }
    }
}
