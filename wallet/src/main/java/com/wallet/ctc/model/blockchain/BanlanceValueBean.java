

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.ui.blockchain.home.AssetsPrice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;



public class BanlanceValueBean {


    

    private Map<String, AssetsPrice> list;
    private BigDecimal sumPrice;
    private String sumNumber;

    public Map<String, AssetsPrice> getList() {
        if(null==list){
            list=new HashMap<>();
        }
        return list;
    }

    public void setList(Map<String, AssetsPrice> list) {
        this.list = list;
    }

    public BigDecimal getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(BigDecimal sumPrice) {
        this.sumPrice = sumPrice;
    }

    public String getSumNumber() {
        return sumNumber;
    }

    public void setSumNumber(String sumNumber) {
        this.sumNumber = sumNumber;
    }

    public static class ListBean {
    }
}
