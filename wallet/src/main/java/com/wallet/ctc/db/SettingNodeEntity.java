

package com.wallet.ctc.db;

import android.net.Uri;
import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;


@Entity
public class SettingNodeEntity {
    @Id(autoincrement = true)
    private Long id;  
    private String nodeName;
    private String nodeUrl;
    private int type;
    private boolean choose=false;
    private int isDef;
    private String mainPhoneIndex;
    private String tokenNum;
    private long onLineTime;
    private String gateWayAddr;

    @Transient
    private long pingTime = 0; 
    @Transient
    private String hostUrl;

    public SettingNodeEntity(){

    }
    public SettingNodeEntity(String nodeName, String nodeUrl,int type,boolean choose,int isdef){
        this.nodeName=nodeName;
        this.nodeUrl=nodeUrl;
        this.type=type;
        this.choose=choose;
        this.isDef=isdef;
    }
    public SettingNodeEntity(String nodeName, String nodeUrl,int type,boolean choose,int isdef, String mainPhoneIndex, String tokenNum, long onLineTime, String gateWayAddr){
        this.nodeName=nodeName;
        this.nodeUrl=nodeUrl;
        this.type=type;
        this.choose=choose;
        this.isDef=isdef;
        this.mainPhoneIndex = mainPhoneIndex;
        this.tokenNum = tokenNum;
        this.onLineTime = onLineTime;
        this.gateWayAddr =gateWayAddr;
    }

    @Generated(hash = 1262881895)
    public SettingNodeEntity(Long id, String nodeName, String nodeUrl, int type, boolean choose, int isDef, String mainPhoneIndex, String tokenNum, long onLineTime,
            String gateWayAddr) {
        this.id = id;
        this.nodeName = nodeName;
        this.nodeUrl = nodeUrl;
        this.type = type;
        this.choose = choose;
        this.isDef = isDef;
        this.mainPhoneIndex = mainPhoneIndex;
        this.tokenNum = tokenNum;
        this.onLineTime = onLineTime;
        this.gateWayAddr = gateWayAddr;
    }
    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public String getHost() {
        if (!TextUtils.isEmpty(hostUrl)) {
            return hostUrl;
        }
        String host = "";
        if (!TextUtils.isEmpty(nodeUrl)) {
            String setUrl = nodeUrl;
            if (!nodeUrl.contains("://")) {
                setUrl = "http://"+nodeUrl;
            }
            try {
                host = Uri.parse(setUrl).getHost();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        hostUrl = host;
        return host;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public boolean getChoose() {
        return this.choose;
    }
    public int getIsDef() {
        return this.isDef;
    }
    public void setIsDef(int isDef) {
        this.isDef = isDef;
    }

    public long getPingTime() {
        return pingTime;
    }

    public void setPingTime(long pingTime) {
        this.pingTime = pingTime;
    }

    public String getMainPhoneIndex() {
        return mainPhoneIndex;
    }

    public void setMainPhoneIndex(String mainPhoneIndex) {
        this.mainPhoneIndex = mainPhoneIndex;
    }

    public String getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(String tokenNum) {
        this.tokenNum = tokenNum;
    }

    public long getOnLineTime() {
        return onLineTime;
    }

    public void setOnLineTime(long onLineTime) {
        this.onLineTime = onLineTime;
    }

    public String getGateWayAddr() {
        return gateWayAddr;
    }

    public void setGateWayAddr(String gateWayAddr) {
        this.gateWayAddr = gateWayAddr;
    }
}
