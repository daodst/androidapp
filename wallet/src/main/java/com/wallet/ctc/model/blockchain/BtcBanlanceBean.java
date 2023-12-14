

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import java.math.BigDecimal;



public class BtcBanlanceBean {


    

    private String remain;
    private String cprice;
    private String uprice;

    public String getRemain() {
        if(TextUtils.isEmpty(remain)||new BigDecimal(remain).longValue()==0){
            remain="0";
        }
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }

    public String getCprice() {
        if(TextUtils.isEmpty(cprice)){
            cprice="0";
        }
        return cprice;
    }

    public void setCprice(String cprice) {
        this.cprice = cprice;
    }

    public String getUprice() {
        if(TextUtils.isEmpty(uprice)){
            uprice="0";
        }
        return uprice;
    }

    public void setUprice(String uprice) {
        this.uprice = uprice;
    }
}
