package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class EvmosClusterVoteDetailBean extends EvmosHttpBean implements Serializable {
    @SerializedName(value = "data", alternate = {"Data"})
    public EvmosClusterVoteDetailBean.Data data;

    public static class Data {
        

        public int id;
        public String group_policy_address;
        public String metadata;
        public String submit_time;

        
        public int status;
        public int group_version;
        public int group_policy_version;
        public FinalTallyResultEntity final_tally_result;
        public String voting_period_end;
        
        public int executor_result;
        public List<MessageEntity> messages;
        public List<String> proposers;

        public static class Metadata implements Serializable {
            
            public String title;
            public String description;
        }

        public static class FinalTallyResultEntity implements Serializable {
            

            public String yes_count;
            public String abstain_count;
            public String no_count;
            public String no_with_veto_count;
        }

        public static class MessageEntity implements Serializable {

            

            @SerializedName("cluster_id")
            public String clusterId;
            @SerializedName("approve_address")
            public String approveAddress;
            @SerializedName("approve_end_block")
            public String approveEndBlock;
            @SerializedName("from_address")
            public String fromAddress;


            public String to_address;
            public List<Amount> amount;

            public String device_ratio;

            


            
            


            public String salary_ratio;

        }

        public static class Amount implements Serializable {
            
            

            public String denom;
            public String amount;
        }
    }


}
