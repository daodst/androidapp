package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class EvmosVoteDuringBean implements Serializable {

    

    @SerializedName("voting_params")
    public VotingParamsEntity votingParams;
    @SerializedName("deposit_params")
    public DepositParamsEntity depositParams;
    @SerializedName("tally_params")
    public TallyParamsEntity tallyParams;

    public static class VotingParamsEntity implements Serializable {
        

        @SerializedName("voting_period")
        public String votingPeriod;
    }

    public static class DepositParamsEntity implements Serializable {
        

        @SerializedName("max_deposit_period")
        public String maxDepositPeriod;
        @SerializedName("min_deposit")
        public List<?> minDeposit;
    }

    public static class TallyParamsEntity implements Serializable {
        

        @SerializedName("quorum")
        public String quorum;
        @SerializedName("threshold")
        public String threshold;
        @SerializedName("veto_threshold")
        public String vetoThreshold;
    }
}
