

package com.wallet.ctc.model.me;

import java.math.BigDecimal;



public class SendCandyBoxHistoryBean {
    private String candy_box_id	;
    private String candy_coin;
    private String mainline_coin;
    private BigDecimal candy_number;
    private BigDecimal received;
    private BigDecimal left_poundage;
    private int start_time;
    private int stop_time;
    private String from_address	;
    private int status;
    private String status_str;

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

    public String getMainline_coin() {
        return mainline_coin;
    }

    public void setMainline_coin(String mainline_coin) {
        this.mainline_coin = mainline_coin;
    }

    public BigDecimal getCandy_number() {
        return candy_number;
    }

    public void setCandy_number(BigDecimal candy_number) {
        this.candy_number = candy_number;
    }

    public BigDecimal getReceived() {
        return received;
    }

    public void setReceived(BigDecimal received) {
        this.received = received;
    }

    public BigDecimal getLeft_poundage() {
        return left_poundage;
    }

    public void setLeft_poundage(BigDecimal left_poundage) {
        this.left_poundage = left_poundage;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatus_str() {
        return status_str;
    }

    public void setStatus_str(String status_str) {
        this.status_str = status_str;
    }
}
