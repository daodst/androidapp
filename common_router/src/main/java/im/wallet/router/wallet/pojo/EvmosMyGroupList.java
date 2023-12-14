package im.wallet.router.wallet.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;


public class EvmosMyGroupList extends BaseResultBean{

    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;


    public static class Data {
        public String address;
        public List<DeviceGruop> device;
        public List<String> owner;
        public List<String> be_power;
        public String all_burn;
        public String active_power;
        private String freeze_power;
        public String first_power_cluster;

        public String getFreezePower() {
            if (!TextUtils.isEmpty(freeze_power)) {
                try {
                    return new BigDecimal(freeze_power).stripTrailingZeros().toPlainString();
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
                return freeze_power;
            } else {
                return freeze_power;
            }
        }

        @Override
        public String toString() {
            return "Data{" +
                    "address='" + address + '\'' +
                    ", device=" + device +
                    ", owner=" + owner +
                    ", be_power=" + be_power +
                    ", all_burn='" + all_burn + '\'' +
                    ", active_power='" + active_power + '\'' +
                    ", freeze_power='" + freeze_power + '\'' +
                    ", first_power_cluster='" + first_power_cluster + '\'' +
                    '}';
        }
    }

    public static class DeviceGruop {
        
        @SerializedName("cluster_chat_id")
        public String groupId;
        @SerializedName("cluster_name")
        public String groupName;
        @SerializedName("cluster_level")
        public int groupLevel;

        @SerializedName("cluster_owner")
        public String groupOwnerAddr;

        @Override
        public String toString() {
            return "DeviceGruop{" +
                    "groupId='" + groupId + '\'' +
                    ", groupName='" + groupName + '\'' +
                    ", groupLevel=" + groupLevel +
                    ", groupOwnerAddr='" + groupOwnerAddr + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "EvmosMyGroupList{" +
                "data=" + data +
                '}';
    }
}
