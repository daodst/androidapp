

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;


public class EvmosChatParamsBean extends EvmosHttpBean {
    @SerializedName(value = "data", alternate = {"params"})
    public Data data;

    public String tokenBalance;

    @Override
    public boolean isSuccess() {
        if (status == 1 || null != data) {
            return true;
        }
        return false;
    }

    public static class Data {
        public EvmosAmountsBean minMortgageCoin;
        @SerializedName("max_phone_number")
        public int maxPhoneNumber;

        @SerializedName("destroy_phone_number_coin")
        public EvmosAmountsBean destroyPhoneNumberCoin;
        public EvmosAmountsBean preAttCoin;
        public String preAttAccount;
        public String attDestroyPercent="0";
        public String attGatewayPercent="0";
        public String attDposPercent="0";

        @SerializedName("min_register_burn_amount")
        public EvmosAmountsBean min_register_burn_amount;

        @SerializedName("min_burn_amount")
        public EvmosAmountsBean min_burn_amount;

        
        public String getAllFee() {
            return new BigDecimal(attDestroyPercent).add(new BigDecimal(attGatewayPercent)).add(new BigDecimal(attDposPercent)).stripTrailingZeros().toPlainString();
        }
    }
}
