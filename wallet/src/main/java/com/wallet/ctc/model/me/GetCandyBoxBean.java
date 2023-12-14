

package com.wallet.ctc.model.me;

import java.math.BigDecimal;



public class GetCandyBoxBean {
    private BigDecimal candy_number;
    private String issue_uid;
    private String mainline_coin;
    private String candy_coin;	
    private String issue_avatar;
    private String issue_nickname;

    public String getIssue_nickname() {
        return issue_nickname;
    }

    public void setIssue_nickname(String issue_nickname) {
        this.issue_nickname = issue_nickname;
    }

    public BigDecimal getCandy_number() {
        return candy_number;
    }

    public void setCandy_number(BigDecimal candy_number) {
        this.candy_number = candy_number;
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

    public String getMainline_coin() {
        return mainline_coin;
    }

    public void setMainline_coin(String mainline_coin) {
        this.mainline_coin = mainline_coin;
    }

    public String getCandy_coin() {
        return candy_coin;
    }

    public void setCandy_coin(String candy_coin) {
        this.candy_coin = candy_coin;
    }

    public String getIssue_avatar() {
        return issue_avatar;
    }

    public void setIssue_avatar(String issue_avatar) {
        this.issue_avatar = issue_avatar;
    }
}
