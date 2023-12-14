package im.wallet.router.wallet.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;


public class EvmosMyGroupDataBean extends BaseResultBean{

    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;

    public static class Data {
        public String power_amount;
        public String burn_amount;
        public boolean is_device;
        public boolean is_amind;
        public boolean is_owner;
        public String burn_ratio;
        public String power_reward;
        public String device_reward;
        public String owner_reward;

        public String auth_contract;
        public String auth_height;
        public String cluster_owner;

        public String gas_day;
        public String cluster_name;

        public String cluster_chat_id;
        public String cluster_id;



        
        public boolean isHasDvm() {
            if (TextUtils.isEmpty(power_amount)) {
                return false;
            }
            try {
                if (new BigDecimal(power_amount).compareTo(new BigDecimal(0)) > 0) {
                    return true;
                } else {
                    return false;
                }
            }catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        
        public boolean isHasDvmContract() {
            if (!TextUtils.isEmpty(auth_contract) || (is_device && is_owner)){
                return true;
            } else {
                return false;
            }
        }


    }
}
