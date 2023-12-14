

package com.wallet.ctc.model.me;

import java.math.BigDecimal;



public class GetCandyBoxHistoryBean {
    private int status;
    private String candy_box_id;
    private String candy_coin;	
    private String username;
    private String nickname;
    private int receive_time;
    private String mainline_coin;	
    private BigDecimal coin_number;
    private String address;	

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        if(null==nickname){
            return "";
        }
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(int receive_time) {
        this.receive_time = receive_time;
    }

    public String getMainline_coin() {
        return mainline_coin;
    }

    public void setMainline_coin(String mainline_coin) {
        this.mainline_coin = mainline_coin;
    }

    public BigDecimal getCoin_number() {
        return coin_number;
    }

    public void setCoin_number(BigDecimal coin_number) {
        this.coin_number = coin_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
