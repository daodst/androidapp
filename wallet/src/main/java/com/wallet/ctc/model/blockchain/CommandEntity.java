

package com.wallet.ctc.model.blockchain;



public class CommandEntity {

    private String id;
    private String logo;
    private String nickname;
    private String content;
    private String to_nickname;
    private String to_content;
    private int like_num;
    private int reply_num;
    private int is_ilike;
    private int is_i;
    private int w_time;

    public String getId() {
        if(id.endsWith(".0")){
            id=id.substring(0,id.length()-2);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIs_i() {
        return is_i;
    }

    public void setIs_i(int is_i) {
        this.is_i = is_i;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public int getReply_num() {
        return reply_num;
    }

    public void setReply_num(int reply_num) {
        this.reply_num = reply_num;
    }

    public int getIs_ilike() {
        return is_ilike;
    }

    public void setIs_ilike(int is_ilike) {
        this.is_ilike = is_ilike;
    }

    public int getW_time() {
        return w_time;
    }

    public void setW_time(int w_time) {
        this.w_time = w_time;
    }

    public String getTo_nickname() {
        return to_nickname;
    }

    public void setTo_nickname(String to_nickname) {
        this.to_nickname = to_nickname;
    }

    public String getTo_content() {
        return to_content;
    }

    public void setTo_content(String to_content) {
        this.to_content = to_content;
    }
}
