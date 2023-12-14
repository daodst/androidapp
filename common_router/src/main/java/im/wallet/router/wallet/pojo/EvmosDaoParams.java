package im.wallet.router.wallet.pojo;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

import im.wallet.router.util.MyUtils;


public class EvmosDaoParams extends BaseResultBean{

    @SerializedName(value = "data", alternate = {"Data"})
    public Data data;


    public String balance;
    public String freezeNum;


    public static class Data {

        private String burn_get_power_ratio;
        public NumRange salary_range;
        public NumRange device_range;
        private String create_cluster_min_burn;
        public String burn_address;
        private String day_burn_reward;

        
        public String getBurnPowerRatio() {
            return MyUtils.getTenDecimalValue(burn_get_power_ratio, 18, 3);
        }

        public String getCreate_cluster_min_burn() {
            return MyUtils.getTenDecimalValue(create_cluster_min_burn, 18, 3);
        }
        
        public String getDayBurnReward() {
            return MyUtils.getTenDecimalValue(day_burn_reward, 18, 3);
        }
    }


    public static class NumRange {
        private String max; 
        private String min; 

        public String getMax() {
            if (TextUtils.isEmpty(max)) {
                return "100";
            }
            try {
                String cvtMax = new BigDecimal(max).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString();
                return cvtMax;
            } catch (NumberFormatException e){
                e.printStackTrace();
                return "100";
            }
        }

        public String getMin() {
            if (TextUtils.isEmpty(min)) {
                return "0";
            }
            try {
                String cvtMax = new BigDecimal(min).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString();
                return cvtMax;
            } catch (NumberFormatException e){
                e.printStackTrace();
                return "0";
            }
        }
    }
}
