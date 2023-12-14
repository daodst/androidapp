package im.wallet.router.wallet.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;


public class EvmosGroupDataBean extends BaseResultBean{

    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;


    public static class Data {
        public String online_ratio;
        public String cluster_active_device;
        public String cluster_device_amount;
        public String device_connectivity_rate;
        public String cluster_device_ratio;
        public String cluster_salary_ratio;
        public String cluster_day_free_gas;
        public String cluster_dao_pool_power;
        public String dao_pool_day_free_gas;
        public String dao_pool_available_amount;
        public String dao_licensing_contract;
        public String dao_licensing_height;

        public LevelInfo level_info;

        public String cluster_name;
        public String cluster_chat_id;
        public String cluster_all_power;
        public String cluster_all_burn;
        public String cluster_owner;
        public String cluster_vote_policy;

        
        public boolean isDaoHasContact() {
            if (TextUtils.isEmpty(dao_licensing_contract)) {
                return false;
            } else {
                return true;
            }
        }
    }


    public static class LevelInfo{
        public int level;

        public String active_amount_next_level;

        public String burn_amount_next_level;

    }

}
