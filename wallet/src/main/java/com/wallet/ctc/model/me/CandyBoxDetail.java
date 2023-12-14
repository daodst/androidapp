

package com.wallet.ctc.model.me;

import java.math.BigDecimal;



public class CandyBoxDetail {
    private String candy_box_id;
    private String candy_coin;
    private String issue_uid;
    private String issue_avatar;
    private BigDecimal each_number;
    private BigDecimal left;
    private BigDecimal candy_number;
    private int start_time;
    private int stop_time;
    private String from_address	;
    private String candy_describe;
    private String receive_condition;
    private String mainline_coin;
    private String candy_coin_token;
    private String mcc_candy_rate;

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

    public String getIssue_uid() {
        if(issue_uid.endsWith(".0")){
            issue_uid=issue_uid.substring(0,issue_uid.length()-2);
        }
        return issue_uid;
    }

    public void setIssue_uid(String issue_uid) {
        this.issue_uid = issue_uid;
    }

    public String getIssue_avatar() {
        return issue_avatar;
    }

    public void setIssue_avatar(String issue_avatar) {
        this.issue_avatar = issue_avatar;
    }

    public BigDecimal getEach_number() {
        return each_number;
    }

    public void setEach_number(BigDecimal each_number) {
        this.each_number = each_number;
    }

    public BigDecimal getLeft() {
        return left;
    }

    public void setLeft(BigDecimal left) {
        this.left = left;
    }

    public BigDecimal getCandy_number() {
        return candy_number;
    }

    public void setCandy_number(BigDecimal candy_number) {
        this.candy_number = candy_number;
    }

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getStop_time() {
        return stop_time;
    }

    public void setStop_time(int stop_time) {
        this.stop_time = stop_time;
    }

    public String getFrom_address() {
        return from_address;
    }

    public void setFrom_address(String from_address) {
        this.from_address = from_address;
    }

    public String getCandy_describe() {
        return candy_describe;
    }

    public void setCandy_describe(String candy_describe) {
        this.candy_describe = candy_describe;
    }

    public String getReceive_condition() {
        return receive_condition;
    }

    public void setReceive_condition(String receive_condition) {
        this.receive_condition = receive_condition;
    }

    public String getMainline_coin() {
        return mainline_coin;
    }

    public void setMainline_coin(String mainline_coin) {
        this.mainline_coin = mainline_coin;
    }

    public String getMcc_candy_rate() {
        return mcc_candy_rate;
    }

    public void setMcc_candy_rate(String mcc_candy_rate) {
        this.mcc_candy_rate = mcc_candy_rate;
    }

    public String getCandy_coin_token() {
        return candy_coin_token;
    }

    public void setCandy_coin_token(String candy_coin_token) {
        this.candy_coin_token = candy_coin_token;
    }
}
