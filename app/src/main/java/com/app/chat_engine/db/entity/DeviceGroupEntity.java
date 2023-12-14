package com.app.chat_engine.db.entity;

import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;


@Entity(indices = {@Index(value = {"userAddr","groupId"}, unique=true)})
public class DeviceGroupEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userAddr")
    private String userAddr;

    @ColumnInfo(name = "groupId")
    private String groupId;

    @ColumnInfo(name = "clusterId")
    private String clusterId; 

    @ColumnInfo(name = "isDeviceGroup")
    private int isDeviceGroup;

    @ColumnInfo(name = "isDeviceGroupOwner")
    private int isDeviceGroupOwner;

    @ColumnInfo(name = "groupName")
    private String groupName;

    @ColumnInfo(name = "groupLogo")
    private String groupLogo;

    @ColumnInfo(name = "groupLevel")
    private int groupLevel;

    @ColumnInfo(name = "dvmPowerAmount")
    private String dvmPowerAmount;

    @ColumnInfo(name = "time")
    private long time;

    @ColumnInfo(name = "burnAmount")
    private String burnAmount;
    @ColumnInfo(name = "burnRatio")
    private String burnRatio;
    @ColumnInfo(name = "powerReward")
    private String powerReward;
    @ColumnInfo(name = "deviceReward")
    private String deviceReward;
    @ColumnInfo(name = "ownerReward")
    private String ownerReward;
    @ColumnInfo(name = "ownerAddr")
    private String ownerAddr;
    @ColumnInfo(name = "dvmDayFreeGas")
    private String dvmDayFreeGas;
    @ColumnInfo(name = "dvmAuthContract")
    private String dvmAuthContract;
    @ColumnInfo(name = "dvmAuthHeight")
    private String dvmAuthHeight;
    @ColumnInfo(name = "groupVotes")
    private String groupVotes;
    @ColumnInfo(name = "state")
    private int state;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "extra")
    private String extra;
    @ColumnInfo(name = "extra2")
    private String extra2;




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
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getIsDeviceGroup() {
        return isDeviceGroup;
    }

    public void setIsDeviceGroup(int isDeviceGroup) {
        this.isDeviceGroup = isDeviceGroup;
    }

    public int getIsDeviceGroupOwner() {
        return isDeviceGroupOwner;
    }

    public void setIsDeviceGroupOwner(int isDeviceGroupOwner) {
        this.isDeviceGroupOwner = isDeviceGroupOwner;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupLogo() {
        return groupLogo;
    }

    public void setGroupLogo(String groupLogo) {
        this.groupLogo = groupLogo;
    }

    public int getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(int groupLevel) {
        this.groupLevel = groupLevel;
    }

    public String getDvmPowerAmount() {
        if (TextUtils.isEmpty(dvmPowerAmount)) {
            return "0";
        }
        return dvmPowerAmount;
    }

    public void setDvmPowerAmount(String dvmPowerAmount) {
        this.dvmPowerAmount = dvmPowerAmount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBurnAmount() {
        return burnAmount;
    }

    public void setBurnAmount(String burnAmount) {
        this.burnAmount = burnAmount;
    }

    public String getBurnRatio() {
        return burnRatio;
    }

    public void setBurnRatio(String burnRatio) {
        this.burnRatio = burnRatio;
    }

    public String getPowerReward() {
        return powerReward;
    }

    public void setPowerReward(String powerReward) {
        this.powerReward = powerReward;
    }

    public String getDeviceReward() {
        return deviceReward;
    }

    public void setDeviceReward(String deviceReward) {
        this.deviceReward = deviceReward;
    }

    public String getOwnerReward() {
        return ownerReward;
    }

    public void setOwnerReward(String ownerReward) {
        this.ownerReward = ownerReward;
    }

    public String getOwnerAddr() {
        return ownerAddr;
    }

    public void setOwnerAddr(String ownerAddr) {
        this.ownerAddr = ownerAddr;
    }

    public String getDvmDayFreeGas() {
        return dvmDayFreeGas;
    }

    public void setDvmDayFreeGas(String dvmDayFreeGas) {
        this.dvmDayFreeGas = dvmDayFreeGas;
    }

    public String getDvmAuthContract() {
        return dvmAuthContract;
    }

    public void setDvmAuthContract(String dvmAuthContract) {
        this.dvmAuthContract = dvmAuthContract;
    }

    public String getDvmAuthHeight() {
        return dvmAuthHeight;
    }

    public void setDvmAuthHeight(String dvmAuthHeight) {
        this.dvmAuthHeight = dvmAuthHeight;
    }

    public String getGroupVotes() {
        return groupVotes;
    }

    public void setGroupVotes(String groupVotes) {
        this.groupVotes = groupVotes;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceGroupEntity entity = (DeviceGroupEntity) o;
        return isDeviceGroup == entity.isDeviceGroup &&
                isDeviceGroupOwner == entity.isDeviceGroupOwner &&
                groupLevel == entity.groupLevel &&
                groupId.equals(entity.groupId) &&
                Objects.equals(groupName, entity.groupName) &&
                Objects.equals(groupLogo, entity.groupLogo) &&
                Objects.equals(dvmPowerAmount, entity.dvmPowerAmount) &&
                Objects.equals(clusterId, entity.clusterId)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, isDeviceGroup, isDeviceGroupOwner, groupName, groupLogo,
                groupLevel, dvmPowerAmount, clusterId);
    }


    
    public void updateData(DeviceGroupEntity newData) {
        if (null == newData) {
            return;
        }
        if (this.equals(newData)) {
            return;
        }
        setGroupLogo(newData.getGroupLogo());
        setGroupName(newData.getGroupName());
        setGroupLevel(newData.getGroupLevel());
        setDvmPowerAmount(newData.getDvmPowerAmount());
        setIsDeviceGroup(newData.getIsDeviceGroup());
        setIsDeviceGroupOwner(newData.getIsDeviceGroupOwner());
        setTime(System.currentTimeMillis());
        setClusterId(newData.getClusterId());

        setBurnAmount(newData.getBurnAmount());
        setBurnRatio(newData.getBurnRatio());
        setPowerReward(newData.getPowerReward());
        setDeviceReward(newData.getDeviceReward());
        setOwnerReward(newData.getOwnerReward());
        setOwnerAddr(newData.getOwnerAddr());
        setDvmDayFreeGas(newData.getDvmDayFreeGas());
        setDvmAuthContract(newData.getDvmAuthContract());
        setDvmAuthHeight(newData.getDvmAuthHeight());
    }

    @Override
    public String toString() {
        return "DeviceGroupEntity{" +
                "id=" + id +
                ", userAddr='" + userAddr + '\'' +
                ", groupId='" + groupId + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", isDeviceGroup=" + isDeviceGroup +
                ", isDeviceGroupOwner=" + isDeviceGroupOwner +
                ", groupName='" + groupName + '\'' +
                ", groupLogo='" + groupLogo + '\'' +
                ", groupLevel=" + groupLevel +
                ", dvmPowerAmount='" + dvmPowerAmount + '\'' +
                ", time=" + time +
                ", burnAmount='" + burnAmount + '\'' +
                ", burnRatio='" + burnRatio + '\'' +
                ", powerReward='" + powerReward + '\'' +
                ", deviceReward='" + deviceReward + '\'' +
                ", ownerReward='" + ownerReward + '\'' +
                ", ownerAddr='" + ownerAddr + '\'' +
                ", dvmDayFreeGas='" + dvmDayFreeGas + '\'' +
                ", dvmAuthContract='" + dvmAuthContract + '\'' +
                ", dvmAuthHeight='" + dvmAuthHeight + '\'' +
                ", groupVotes='" + groupVotes + '\'' +
                ", state=" + state +
                ", type='" + type + '\'' +
                ", extra='" + extra + '\'' +
                ", extra2='" + extra2 + '\'' +
                '}';
    }
}
