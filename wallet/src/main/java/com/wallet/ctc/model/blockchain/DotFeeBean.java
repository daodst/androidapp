

package com.wallet.ctc.model.blockchain;

import com.google.gson.annotations.SerializedName;

public class DotFeeBean {


    

    private String weight;
    @SerializedName("class")
    private String classX;
    private String partialFee;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getClassX() {
        return classX;
    }

    public void setClassX(String classX) {
        this.classX = classX;
    }

    public String getPartialFee() {
        return partialFee;
    }

    public void setPartialFee(String partialFee) {
        this.partialFee = partialFee;
    }
}
