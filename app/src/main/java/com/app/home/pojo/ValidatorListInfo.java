package com.app.home.pojo;

import android.content.Context;
import android.text.TextUtils;

import com.app.R;
import com.app.home.ui.utils.TimeUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class ValidatorListInfo {

    public String height;
    public List<Result> result;


    
    public static class TimeComparator implements Comparator<ValidatorListInfo.Result> {
        @Override
        public int compare(Result o1, Result o2) {
            Long l = TimeUtils.format2Long(o1.unbonding_time);
            Long l1 = TimeUtils.format2Long(o2.unbonding_time);
            return l.compareTo(l1);
        }
    }


    
    public static class BalanceComparator implements Comparator<ValidatorListInfo.Result> {
        @Override
        public int compare(Result o1, Result o2) {
            if (null == o1.description || null == o1.description.moniker) {
                return -1;
            } else if (null == o2.description || null == o2.description.moniker) {
                return 1;
            }
            return o1.description.moniker.compareTo(o2.description.moniker);
        }
    }

    public static class Result {
        
        public String operator_address;
        
        public ConsensusPubKey consensus_pubkey;
        
        private String status;

        public boolean isActive() {
            return TextUtils.equals(status, "3");
        }

        public String getStatus(Context context) {
            if (TextUtils.equals(status, "0")) {
                return context.getString(R.string.validator_status_un);
            } else if (TextUtils.equals(status, "1")) {
                return context.getString(R.string.validator_status_unbind);
            } else if (TextUtils.equals(status, "2")) {
                return context.getString(R.string.validator_status_bind_unbind);
            } else if (TextUtils.equals(status, "3")) {
                return context.getString(R.string.validator_status_bind);
            }
            return "";
        }

        
        public String jailed;
        
        private String tokens;

        public String getTokens(int decimal) {
            try {
                return new BigDecimal(tokens).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tokens;
        }

        
        private String delegator_shares;

        public String getDelegator_shares(int decimal) {
            try {
                return new BigDecimal(delegator_shares).divide(BigDecimal.valueOf(Math.pow(10, decimal)), 6, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return delegator_shares;
        }

        public Description description;

        
        public long start_time;


        
        public String unbonding_time;
        
        public String unbonding_time_format;

        public String getUnbonding_time(Context context) {
            if (TextUtils.isEmpty(unbonding_time_format)) {
                return context.getString(R.string.getunbonding_height_unknow);
            }

            return unbonding_time_format;
        }

        
        public String unbonding_height = "";

        public String getUnbonding_height(Context context) {
            if (TextUtils.isEmpty(unbonding_height)) {
                return context.getString(R.string.getunbonding_height_unknow);
            }
            return unbonding_height;
        }

        
        public Commission commission;
        
        private String min_self_delegation;

        public String getMin_self_delegation(int decimal) {
            try {
                return new BigDecimal(min_self_delegation).divide(BigDecimal.valueOf(Math.pow(10, decimal)), decimal, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return min_self_delegation;
        }
    }

    public static class ConsensusPubKey {

        public String type;
        public String value;
    }

    public static class Description {
        
        private String moniker = "--";
        
        
        private String security_contact = "";

        public String getMoniker() {
            if (TextUtils.isEmpty(moniker)) {
                return "--";
            }
            return moniker;
        }

        public String getSecurity_contact(Context context) {
            if (TextUtils.isEmpty(security_contact)) {
                return context.getString(R.string.getunbonding_height_unknow);
            }
            return security_contact;
        }
    }

    public class Commission {
        
        public CommissionRate commission_rates;
        
        private String update_time;

        public String getUpdate_time() {
            return TimeUtils.format2(update_time);
        }
    }

    public class CommissionRate {
        
        public String rate;

        public String getRate() {
            try {
                String num = new BigDecimal(rate).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                return num + "%";
            } catch (Exception e) {
                e.printStackTrace();
                return "--";
            }
        }


        
        public String max_rate;

        public String getMax_rate() {
            try {
                String num = new BigDecimal(max_rate).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                return num + "%";
            } catch (Exception e) {
                e.printStackTrace();
                return "--";
            }
        }


        
        public String max_change_rate;

        public String getMax_change_rate() {
            try {
                String num = new BigDecimal(max_change_rate).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
                return num + "%";
            } catch (Exception e) {
                e.printStackTrace();
                return "--";
            }
        }
    }
}
