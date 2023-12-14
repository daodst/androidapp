

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.util.AllUtils;

import java.math.BigDecimal;



public class IncomeBean {


    

    private String draw_earnings;
    private String draw_time;


    public String getDraw_earnings() {
        return draw_earnings;
    }

    public void setDraw_earnings(String draw_earnings) {
        this.draw_earnings = draw_earnings;
    }

    public String getDraw_time() {
        try{
            if(new BigDecimal(draw_time).doubleValue()>0){
                draw_time= AllUtils.getTimeNYRSF(draw_time);
            }
        }catch (Exception e){

        }
        return draw_time;
    }

    public void setDraw_time(String draw_time) {
        this.draw_time = draw_time;
    }
}
