package com.app.pojo;

import android.text.TextUtils;

import java.math.BigDecimal;


public class DeviceGroupBean {

    public String groupId;
    public String groupName;
    public int groupLevel;
    public boolean isDeviceGroup;
    public boolean isOwner;

    
    public String dvmPowerAmount;
    public String burnAmount;
    public String burnRatio;
    public String powerReward;
    public String deviceReward;
    public String ownerReward;
    public String ownerAddr;
    public String dvmDayFreeGas;
    public String dvmAuthContract;
    public String dvmAuthHeight;
    public String clusterId; 




    public DeviceGroupBean(String groupId) {
        this.groupId = groupId;
    }

    public DeviceGroupBean(String groupId, String groupName, int groupLevel, boolean isDeviceGroup) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupLevel = groupLevel;
        this.isDeviceGroup = isDeviceGroup;
    }

    
    public boolean isSystemNoticeGroup() {
        if (!TextUtils.isEmpty(groupId) && groupId.startsWith("!_server")) {
            return true;
        } else if(!TextUtils.isEmpty(groupId) && groupId.equals("!KLLZakWnSTbSX0X8:2222222.nxn")){
            return true;
        } else {
            return false;
        }
    }

    public String getDvmPowerAmount() {
        return dvmPowerAmount;
    }

    public void setDvmPowerAmount(String dvmPowerAmount) {
        this.dvmPowerAmount = dvmPowerAmount;
    }

    public boolean isHasDvm() {
        if (TextUtils.isEmpty(dvmPowerAmount)) {
            return false;
        }
        try {
            if (new BigDecimal(dvmPowerAmount).compareTo(new BigDecimal(0)) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean isBurnRatioUp30(){
        if (TextUtils.isEmpty(burnRatio) || TextUtils.isEmpty(burnAmount)) {
            return false;
        }
        try {
            if (new BigDecimal(burnAmount).compareTo(new BigDecimal(0)) > 0 &&
                    new BigDecimal(burnRatio).compareTo(new BigDecimal(0.3)) >= 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }
}
