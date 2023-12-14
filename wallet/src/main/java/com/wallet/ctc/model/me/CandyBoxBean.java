

package com.wallet.ctc.model.me;

import java.math.BigDecimal;



public class CandyBoxBean {
    private String candy_box_id;
    private String candy_coin;
    private String candy_coin_logo;
    private String candy_coin_token;
    private String mainline_coin;
    private BigDecimal left;
    private BigDecimal each_number;
    private int stop_time;
    private int received;


    public String getCandy_box_id() {
        if(candy_box_id.endsWith(".0")){
            candy_box_id=candy_box_id.substring(0,candy_box_id.length()-2);
        }
        return candy_box_id;
    }

    public void setCandy_box_id(String candy_box_id) {
        this.candy_box_id = candy_box_id;
    }

    public String getCandy_coin() {
        return candy_coin;
    }

    public void setCandy_coin(String candy_coin) {
        this.candy_coin = candy_coin;
    }

    public String getCandy_coin_logo() {
        return candy_coin_logo;
    }

    public void setCandy_coin_logo(String candy_coin_logo) {
        this.candy_coin_logo = candy_coin_logo;
    }

    public String getCandy_coin_token() {
        return candy_coin_token;
    }

    public void setCandy_coin_token(String candy_coin_token) {
        this.candy_coin_token = candy_coin_token;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public String getMainline_coin() {
        if(null==mainline_coin){
            mainline_coin="";
        }
        return mainline_coin;
    }

    public void setMainline_coin(String mainline_coin) {
        this.mainline_coin = mainline_coin;
    }

    public BigDecimal getLeft() {
        if(null==left){
            left=new BigDecimal("0");
        }
        return left;
    }

    public void setLeft(BigDecimal left) {
        this.left = left;
    }

    public BigDecimal getEach_number() {
        return each_number;
    }

    public void setEach_number(BigDecimal each_number) {
        this.each_number = each_number;
    }

    public int getStop_time() {
        return stop_time;
    }

    public void setStop_time(int stop_time) {
        this.stop_time = stop_time;
    }
}
