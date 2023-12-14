

package com.wallet.ctc.model.blockchain;

import java.io.Serializable;



public class PostArticleListBean implements Serializable{

    private String id;
    private String title;
    private String cover;
    private String content;
    private int audit;
    private String remark;
    private int w_time;
    private int u_time;
    private int s_time;

    public String getId() {
        if(id.endsWith(".0")){
            id=id.substring(0,id.length()-2);
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getW_time() {
        return w_time;
    }

    public void setW_time(int w_time) {
        this.w_time = w_time;
    }

    public int getU_time() {
        if(s_time>0&&u_time==0){
            return s_time;
        }
        return u_time;
    }

    public void setU_time(int u_time) {
        this.u_time = u_time;
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
}
