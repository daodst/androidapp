package com.app.home.pojo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class DposListEntity implements Serializable {

    

    @SerializedName("validator_amount")
    public int validatorAmount;
    @SerializedName("validator_amount_max")
    public int validatorAmountMax;
    @SerializedName("validator_list")
    public List<ValidatorListEntity> validatorList;

    public static class ValidatorListEntity implements Serializable {
        

        @SerializedName("validator_addr")
        public String validatorAddr;
        @SerializedName("validator_name")
        public String validatorName;
        @SerializedName("validator_delegate_amount")
        public String validatorDelegateAmount;
        @SerializedName("validator_online_ratio")
        public String validatorOnlineRatio;
        @SerializedName("commision")
        public String commision;
        @SerializedName("persion_delegate_amount")
        public String persionDelegateAmount;
    }
}
