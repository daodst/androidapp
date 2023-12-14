package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;



public class EvmosClusterVoteBean extends EvmosHttpBean implements Serializable {
    @SerializedName(value = "data", alternate = {"Data"})
    public List<EvmosClusterVoteBean.Data> data;

    public static class Data implements Comparable<Data> {
        

        public int id;
        public String group_policy_address;
        public String metadata;
        public String submit_time;

        public Long time() {
            return format2Long(submit_time);
        }

        public static long format2Long(String timeStr) {
            
            
            try {
                
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date time = null;
                try {
                    time = dateFormat.parse(timeStr);
                } catch (Exception e) {
                    time = dateFormat2.parse(timeStr);
                }
                if (null == time) {
                    return 0;
                }
                return time.getTime();
            } catch (Exception e) {
            }
            return 0;
        }

        
        public int status;
        public int group_version;
        public int group_policy_version;
        public FinalTallyResultEntity final_tally_result;
        public String voting_period_end;
        
        public int executor_result;
        public List<MessageEntity> messages;
        public List<String> proposers;

        @Override
        public int compareTo(Data o) {
            return o.time().compareTo(time());
        }

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
        }
    }


}
