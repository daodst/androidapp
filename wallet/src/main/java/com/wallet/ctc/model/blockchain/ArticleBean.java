

package com.wallet.ctc.model.blockchain;

import com.wallet.ctc.R;

import java.math.BigDecimal;

import common.app.AppApplication;



public class ArticleBean {



    private String article_id;
    private String url;
    private String title;
    private String w_time;
    private String id;
    private String author;
    private String cover;
    private BigDecimal look_num;
    private BigDecimal up_num;
    private BigDecimal down_num;
    private int is_updown;


    public String getId() {
        if(id!=null&&id.endsWith(".0")){
            id=id.substring(0,id.length()-2);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticle_id() {
        if(article_id!=null&&article_id.endsWith(".0")){
            article_id=article_id.substring(0,article_id.length()-2);
        }
        if(null==article_id){
            return getId();
        }
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getW_time() {
        return w_time;
    }

    public void setW_time(String w_time) {
        this.w_time = w_time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLook_num() {
        if(null==look_num){
            look_num=new BigDecimal("0");
        }
        if(look_num.doubleValue()>10000){
            return look_num.divide(new BigDecimal(10000)).intValue()+ AppApplication.getContext().getString(R.string.wan);
        }
        return look_num.toBigInteger().toString();
    }

    public int getIntLook_num() {
        return look_num.toBigInteger().intValue();
    }

    public void setLook_num(BigDecimal look_num) {
        this.look_num = look_num;
    }

    public BigDecimal getUp_num() {
        return up_num;
    }
    public int getIntUp_num() {
        if(null==up_num){
            up_num=new BigDecimal("0");
        }
        return up_num.toBigInteger().intValue();
    }
    public void setUp_num(BigDecimal up_num) {
        this.up_num = up_num;
    }

    public BigDecimal getDown_num() {
        return down_num;
    }
    public int getIntDown_num() {
        return down_num.toBigInteger().intValue();
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
