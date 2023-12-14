

package com.wallet.ctc.model.blockchain;

import java.math.BigDecimal;



public class ArticleInfoBean {

    private  String id;
    private  String author;
    private String avatar;
    private  String title;
    private  String cover;
    private  String content;
    private  String url;
    private  int  look_num;
    private  int  like_num;
    private  int  remark_num;
    private  int  w_time;
    private  int  s_time;

    private BigDecimal up_num;
    private BigDecimal down_num;
    private int is_updown;

    public String getId() {
        if(id.endsWith(".0")){
            id=id.substring(0,id.length()-2);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLook_num() {
        return look_num;
    }

    public void setLook_num(int look_num) {
        this.look_num = look_num;
    }

    public int getLike_num() {
        return like_num;
    }

    public void setLike_num(int like_num) {
        this.like_num = like_num;
    }

    public int getRemark_num() {
        return remark_num;
    }

    public void setRemark_num(int remark_num) {
        this.remark_num = remark_num;
    }

    public int getW_time() {
        return w_time;
    }

    public void setW_time(int w_time) {
        this.w_time = w_time;
    }

    public int getS_time() {
        return s_time;
    }

    public void setS_time(int s_time) {
        this.s_time = s_time;
    }
    public int getIntUp_num() {
        return up_num.toBigInteger().intValue();
    }
    public int getIntDown_num() {
        return down_num.toBigInteger().intValue();
    }

    public BigDecimal getUp_num() {
        return up_num;
    }

    public void setUp_num(BigDecimal up_num) {
        this.up_num = up_num;
    }

    public BigDecimal getDown_num() {
        return down_num;
    }

    public void setDown_num(BigDecimal down_num) {
        this.down_num = down_num;
    }

    public int getIs_updown() {
        return is_updown;
    }

    public void setIs_updown(int is_updown) {
        this.is_updown = is_updown;
    }
}
