package com.app.home.pojo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DposInfo {


    public String getSupplyNum(int delimal) {

        try {
            String num1 = getBigDecimalValue(new BigDecimal(all_staking).divide(BigDecimal.valueOf(Math.pow(10, delimal)), 18, RoundingMode.DOWN).divide(new BigDecimal(10000), 2, RoundingMode.DOWN));
            String num2 = getBigDecimalValue(new BigDecimal(getAll_supply()).divide(BigDecimal.valueOf(Math.pow(10, delimal)), 18, RoundingMode.DOWN).divide(new BigDecimal(10000), 2, RoundingMode.DOWN));
            return num1 + "W /" + num2 + " W";
        } catch (Exception e) {
            return "--";
        }
    }

    public static String getBigDecimalValue(BigDecimal divide) {
        BigDecimal zero = BigDecimal.ZERO;
        if (divide.compareTo(zero) == 0) {
            return zero.toPlainString();
        } else {
            return divide.stripTrailingZeros().toPlainString();
        }
    }

    public String getSupplyRate() {
        try {
            BigDecimal num1 = new BigDecimal(all_staking).divide(new BigDecimal(10000), 0, RoundingMode.DOWN);
            BigDecimal num2 = new BigDecimal(getAll_supply()).divide(new BigDecimal(10000), 0, RoundingMode.DOWN);
            return getBigDecimalValue(num1.multiply(new BigDecimal("100")).divide(num2, 1, RoundingMode.DOWN)) + "%";
        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }

    
    public String getMyWalletRate(String myPledgeBigNum) {
        try {
            BigDecimal num1 = new BigDecimal(all_staking).divide(new BigDecimal(10000), 0, RoundingMode.DOWN);
            BigDecimal num2 = new BigDecimal(myPledgeBigNum).divide(new BigDecimal(10000), 0, RoundingMode.DOWN);
            return getBigDecimalValue(num2.multiply(new BigDecimal("100")).divide(num1, 6, RoundingMode.DOWN)) + "%";
        } catch (Exception e) {
            return "--";
        }
    }

    
    public String all_staking;
    
    private AllSupply all_supply;

    public static class AllSupply{

        

        public String denom;
        public String amount;
    }

    Pattern mPattern = Pattern.compile("\\d+(\\.\\d+)?");

    public String getAll_supply() {
        try {
            Matcher m = mPattern.matcher(all_supply.amount);
            if (m.find()) {
                return m.group();
            } else {
                
                return "-1";
            }
        } catch (Exception e) {
            
            return "-1";
        }
    }

    public Staking staking_params;
    public Distribution distribution_params;
    public Slashing slashing_params;
    
    public String mint_inflation;

    
    public static class Staking {
        
        public String max_entries;
        
        public long unbonding_time;
        
        public String max_validators;
    }

    
    public static class Distribution {
        
        private String community_tax;

        public String getCommunity_tax() {
            try {
                String num = getBigDecimalValue(new BigDecimal(community_tax).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN));
                return num + "%";
            } catch (Exception e) {
                return "--";
            }
        }


        
        private String base_proposer_reward;

        public String getBase_proposer_reward() {
            try {
                String num = getBigDecimalValue(new BigDecimal(base_proposer_reward).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN));
                return num + "%";
            } catch (Exception e) {
                return "--";
            }
        }


        
        private String bonus_proposer_reward;

        public String getBonus_proposer_reward() {
            try {
                String num = getBigDecimalValue(new BigDecimal(bonus_proposer_reward).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN));
                return num + "%";
            } catch (Exception e) {
                return "--";
            }
        }

    }


    
    public static class Slashing {
        
        public String signed_blocks_window;
        
        public String min_signed_per_window;


        public String getMin_signed_per_window() {
            try {
                String num = getBigDecimalValue(new BigDecimal(min_signed_per_window).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN));
                return num + "%";
            } catch (Exception e) {
                return "--";
            }
        }


        
        public long downtime_jail_duration;
        
        public String slash_fraction_double_sign;

        public String getSlash_fraction_double_sign() {
            try {
                String num = getBigDecimalValue(new BigDecimal(slash_fraction_double_sign).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN));
                return num + "%";
            } catch (Exception e) {
                return "--%";
            }
        }


        
        public String slash_fraction_downtime;

        public String getSlash_fraction_downtime() {
            try {
                String num = getBigDecimalValue(new BigDecimal(slash_fraction_downtime).multiply(new BigDecimal(100)).setScale(1, RoundingMode.DOWN));
                return num + "%";
            } catch (Exception e) {
                return "--%";
            }
        }
    }

}
