package com.app.chat_engine.db.entity;

import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import common.app.utils.TimeUtil;


@Entity(indices = {@Index(value = {"userAddr","groupId", "keyType"}, unique=true)})
public class DeviceGroupNoticeEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userAddr")
    private String userAddr;

    @ColumnInfo(name = "groupId")
    private String groupId;

    @ColumnInfo(name = "keyType")
    private String keyType;

    @ColumnInfo(name = "noticeType")
    private int noticeType;

    @ColumnInfo(name = "flag")
    private int flag;

    @ColumnInfo(name = "value")
    private String value;

    @ColumnInfo(name = "noticeTime")
    private long noticeTime;

    @ColumnInfo(name = "nextNoticeTime")
    private long nextNoticeTime;

    @ColumnInfo(name = "extra")
    private String extra;

    @ColumnInfo(name = "state")
    private int state;


    public DeviceGroupNoticeEntity(){
    }

    @Ignore
    public DeviceGroupNoticeEntity(String userAddr, String groupId, String keyType) {
        this.userAddr = userAddr;
        this.groupId = groupId;
        this.keyType = keyType;
    }

    @Ignore
    public DeviceGroupNoticeEntity(String userAddr, String groupId, String keyType, int flag, String value, int state) {
        this.userAddr = userAddr;
        this.groupId = groupId;
        this.keyType = keyType;
        this.flag = flag;
        this.value = value;
        this.state = state;
    }

    
    public static DeviceGroupNoticeEntity createEvent(String userAddr, String groupId, String keyType, int flag, String value, int state) {
        DeviceGroupNoticeEntity entity = new DeviceGroupNoticeEntity(userAddr, groupId, keyType, flag, value, state);
        entity.setNoticeType(1);
        entity.setNoticeTime(System.currentTimeMillis());
        return entity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
    }

    public String getGroupId() {
        if (null == groupId) {
            return "";
        }
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public long getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(long noticeTime) {
        this.noticeTime = noticeTime;
    }

    public long getNextNoticeTime() {
        return nextNoticeTime;
    }

    public void setNextNoticeTime(long nextNoticeTime) {
        this.nextNoticeTime = nextNoticeTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    
    public void updateData(DeviceGroupNoticeEntity newData) {
        if (null == newData || TextUtils.isEmpty(newData.getKeyType())) {
            return;
        }
        setFlag(newData.getFlag());
        setValue(newData.getValue());
        setNoticeTime(newData.getNoticeTime());
        setState(newData.getState());
        setExtra(newData.getExtra());
        setNextNoticeTime(newData.getNextNoticeTime());
    }


    
    public boolean nowDayHasNoticed() {
        long lastNoticeTime = noticeTime;
        if (lastNoticeTime == 0) {
            return false;
        }
        String dayStr = TimeUtil.getYYYYMMdd(lastNoticeTime);
        String nowDayStr = TimeUtil.getYYYYMMdd(System.currentTimeMillis());
        if (nowDayStr.equals(dayStr)) {
            
            return true;
        } else {
            return false;
        }
    }

    
    public boolean recentXDayHasNoticed(int xday) {
        long lastNoticeTime = getNoticeTime();
        if (lastNoticeTime == 0) {
            return false;
        }
        long nowDay = System.currentTimeMillis();
        long xDayTime = xday * 86400000;
        if(Math.abs(nowDay - lastNoticeTime) > xDayTime) {
            
            return false;
        } else {
            
            return true;
        }
    }


    @Override
    public String toString() {
        return "DeviceGroupNoticeEntity{" +
                "id=" + id +
                ", userAddr='" + userAddr + '\'' +
                ", groupId='" + groupId + '\'' +
                ", keyType='" + keyType + '\'' +
                ", flag=" + flag +
                ", value='" + value + '\'' +
                ", noticeTime=" + noticeTime +
                ", nextNoticeTime=" + nextNoticeTime +
                ", extra='" + extra + '\'' +
                ", state='" + state + '\'' +
                ", noticeType='" + noticeType + '\'' +
                '}';
    }
}
