

package com.wallet.ctc.model.blockchain;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;



public class BaseIncomeBean {


    
    @SerializedName("undraw_earnings")
    private BigDecimal left_earnings;
    private BigDecimal draw_earnings;
    private String mineral;
    private List<IncomeBean> awardlist;

    public BigDecimal getLeft_earnings() {
        if(null==left_earnings){
            left_earnings=new BigDecimal("0");
        }
        return left_earnings;
    }

    public void setLeft_earnings(BigDecimal left_earnings) {
        this.left_earnings = left_earnings;
    }

    public BigDecimal getDraw_earnings() {
        if(null==draw_earnings){
            draw_earnings=new BigDecimal("0");
        }
        return draw_earnings;
    }

    public void setDraw_earnings(BigDecimal draw_earnings) {
        this.draw_earnings = draw_earnings;
    }

    public List<IncomeBean> getAwardlist() {
        return awardlist;
    }

    public void setAwardlist(List<IncomeBean> awardlist) {
        this.awardlist = awardlist;
    }

    public String getMineral() {
        if(null==mineral|| TextUtils.isEmpty(mineral)){
            mineral="0";
        }
        return mineral;
    }

    public void setMineral(String mineral) {
        this.mineral = mineral;
    }
}
