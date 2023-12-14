

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class EvmosDvmListBean extends EvmosHttpBean{
    @SerializedName(value = "data", alternate = {"Data"})
    public List<Data> data;

    public static class Data {
        public String cluster_chat_id;
        public String cluster_id;
        public String power_reward;
        public String power_dvm;
        public String gas_day_dvm;
        public String auth_contract;
        public long auth_height;
        public String cluster_name;
        public boolean is_owner;

        
        public boolean isHasDvmContract() {
            if (!TextUtils.isEmpty(auth_contract) || is_owner){
                return true;
            } else {
                return false;
            }
        }
    }
}
