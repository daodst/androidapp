

package com.wallet.ctc.model.blockchain;

import androidx.annotation.NonNull;



public class ChooseNodeBean implements Comparable<ChooseNodeBean>{


    

    private String district;
    private String inner_ip;
    private String name;
    private String outer_ip;
    private String url;
    private String peerNum;
    private String protocol;
    private String status;
    private int type;
    private int delay;


    public ChooseNodeBean(){

    }

    public ChooseNodeBean(String district,String name,String outer_ip,String url,int type,int delay){
        this.district=district;
        this.name=name;
        this.outer_ip=outer_ip;
        this.url=url;
        this.type=type;
        this.delay=delay;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getInner_ip() {
        return inner_ip;
    }

    public void setInner_ip(String inner_ip) {
        this.inner_ip = inner_ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOuter_ip() {
        String add=outer_ip;
        if(outer_ip.startsWith("http")){
            
            int i=outer_ip.indexOf("/",1)+2;
            add=outer_ip.substring(i,outer_ip.length());
            if(add.indexOf(":")>0){
                add=add.substring(0,add.indexOf(":"));
            }
        }
        return add;
    }

    public String getAllOuter_ip() {
        return outer_ip;
    }

    public void setOuter_ip(String outer_ip) {
        this.outer_ip = outer_ip;
    }

    public String getPeerNum() {
        return peerNum;
    }

    public void setPeerNum(String peerNum) {
        this.peerNum = peerNum;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public int compareTo(@NonNull ChooseNodeBean o) {
        int i = this.getDelay() - o.getDelay();
        return i;
    }

    public String getUrl() {
        return url;
    }

    public String getSmallUrl(){
        String add=url;
        if(url.startsWith("http")){
            
            int i=url.indexOf("/",1)+2;
            add=url.substring(i,url.length());
            if(add.indexOf(":")>0){
                add=add.substring(0,add.indexOf(":"));
            }
        }
        return  add;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
