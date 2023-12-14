package com.app.chat_engine;

import android.text.TextUtils;

import com.app.chat_engine.db.entity.DeviceGroupEntity;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.chat_engine.notice.BaseChatNotice;
import com.app.chat_engine.notice.BuyDvmNotice;
import com.app.chat_engine.notice.ExitDeviceGroupNotice;
import com.app.chat_engine.notice.FirstJoinGroupNotice;
import com.app.chat_engine.notice.GroupLevelNotice;
import com.app.pojo.DeviceGroupBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DbConvertUtils {

    
    public static List<DeviceGroupEntity> convert(String userAddr, List<DeviceGroupBean> groups) {
        if (TextUtils.isEmpty(userAddr) || groups == null || groups.size() == 0) {
            return null;
        }
        List<DeviceGroupEntity> list = new ArrayList<>();
        for (DeviceGroupBean group : groups) {
            DeviceGroupEntity entity = convert(userAddr, group);
            if (null != entity) {
                list.add(entity);
            }
        }
        return list;
    }

    
    public static List<DeviceGroupBean> convert(List<DeviceGroupEntity> entityList) {
        if (entityList == null || entityList.size() == 0) {
            return null;
        }
        List<DeviceGroupBean> list = new ArrayList<>();
        for (DeviceGroupEntity entity : entityList) {
            DeviceGroupBean group = convert(entity);
            if (null != group) {
                list.add(group);
            }
        }
        return list;
    }

    
    public static DeviceGroupEntity convert(String userAddr, DeviceGroupBean group) {
        if (TextUtils.isEmpty(userAddr) || null == group || TextUtils.isEmpty(group.groupId)) {
            return null;
        }
        DeviceGroupEntity entity = new DeviceGroupEntity();
        entity.setUserAddr(userAddr);
        entity.setGroupId(group.groupId);
        entity.setDvmPowerAmount(group.getDvmPowerAmount());
        entity.setGroupLevel(group.groupLevel);
        entity.setGroupName(group.groupName);
        entity.setIsDeviceGroup(group.isDeviceGroup ? 1 : 0);
        entity.setIsDeviceGroupOwner(group.isOwner ? 1 : 0);
        entity.setTime(System.currentTimeMillis());

        entity.setBurnAmount(group.burnAmount);
        entity.setBurnRatio(group.burnRatio);
        entity.setPowerReward(group.powerReward);
        entity.setDeviceReward(group.deviceReward);
        entity.setOwnerReward(group.ownerReward);
        entity.setOwnerAddr(group.ownerAddr);
        entity.setDvmDayFreeGas(group.dvmDayFreeGas);
        entity.setDvmAuthContract(group.dvmAuthContract);
        entity.setDvmAuthHeight(group.dvmAuthHeight);
        entity.setClusterId(group.clusterId);
        return entity;
    }

    
    public static DeviceGroupBean convert(DeviceGroupEntity entity) {
        if (null == entity || TextUtils.isEmpty(entity.getGroupId())) {
            return null;
        }
        DeviceGroupBean group = new DeviceGroupBean(entity.getGroupId());
        if (entity.getIsDeviceGroup() == 1) {
            
            group.groupLevel = entity.getGroupLevel();
            group.isDeviceGroup = entity.getIsDeviceGroup() == 1;
            group.isOwner = entity.getIsDeviceGroupOwner() == 1;
            group.dvmPowerAmount = entity.getDvmPowerAmount();
            group.burnAmount = entity.getBurnAmount();
            group.burnRatio = entity.getBurnRatio();
            group.powerReward = entity.getPowerReward();
            group.deviceReward = entity.getDeviceReward();
            group.ownerReward = entity.getOwnerReward();
            group.ownerAddr = entity.getOwnerAddr();
            group.dvmDayFreeGas = entity.getDvmDayFreeGas();
            group.dvmAuthContract = entity.getDvmAuthContract();
            group.dvmAuthHeight = entity.getDvmAuthHeight();
            group.clusterId = entity.getClusterId();
        }
        return group;
    }

    
    public static Map<String, DeviceGroupBean> groupListToMap(List<DeviceGroupBean> groups) {
        if (null == groups || groups.size() == 0) {
            return null;
        }
        Map<String,DeviceGroupBean> map = new HashMap<>();
        for (int i=0; i<groups.size(); i++) {
            DeviceGroupBean group = groups.get(i);
            if (null != group && !TextUtils.isEmpty(group.groupId)) {
                map.put(group.groupId, group);
            }
        }
        return map;
    }

    
    public static Map<String, DeviceGroupEntity> entityListToMap(List<DeviceGroupEntity> groups) {
        if (null == groups || groups.size() == 0) {
            return null;
        }
        Map<String,DeviceGroupEntity> map = new HashMap<>();
        for (int i=0; i<groups.size(); i++) {
            DeviceGroupEntity group = groups.get(i);
            if (null != group && !TextUtils.isEmpty(group.getGroupId())) {
                map.put(group.getGroupId(), group);
            }
        }
        return map;
    }

    private static DeviceGroupNoticeEntity createNotice(String userAddr, String groupId, String keyType,
                                                        int flag, String value, int state, boolean isEvent) {
        if (TextUtils.isEmpty(userAddr) || TextUtils.isEmpty(keyType)) {
            return null;
        }
        if (isEvent) {
            return DeviceGroupNoticeEntity.createEvent(userAddr, groupId, keyType, flag, value, state);
        } else {
            return new DeviceGroupNoticeEntity(userAddr, groupId, keyType, flag, value, state);
        }
    }


    public static DeviceGroupNoticeEntity createNoticeNormal(DeviceGroupEntity group,  String keyType,
                                                             int flag, String value, int state) {
        if (null == group) {
            return null;
        }
        String userAddr = group.getUserAddr();
        String groupId = group.getGroupId();
        return createNotice(userAddr, groupId, keyType, flag, value, state, false);
    }


    
    public static DeviceGroupNoticeEntity createNoticeEvent(String userAddr, String groupId, String keyType, int flag, String value, int state) {
        return createNotice(userAddr, groupId, keyType, flag, value, state, true);
    }
    public static DeviceGroupNoticeEntity createNoticeEvent(DeviceGroupEntity group, String keyType, int flag, String value, int state) {
        if (null == group) {
            return null;
        }
        String userAddr = group.getUserAddr();
        String groupId = group.getGroupId();
        return createNoticeEvent(userAddr, groupId, keyType, flag, value, state);
    }


    
    public static List<DeviceGroupNoticeEntity> getGroupChangeNotice(DeviceGroupEntity oldData, DeviceGroupEntity newData) {
        if (newData.getIsDeviceGroup() == 1) {
            
            List<DeviceGroupNoticeEntity> list = new ArrayList<>();
            
            int oldIsDeviceGroup = oldData.getIsDeviceGroup();
            if (oldIsDeviceGroup == 0) {
                
                DeviceGroupNoticeEntity firstJoinNotice = createNoticeEvent(oldData, FirstJoinGroupNotice.EVENT_FIRST_JOIN_GROUP,
                        FirstJoinGroupNotice.FLAG_FIRST, "", BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                list.add(firstJoinNotice);
            } else {
                
                try {
                    BigDecimal oldBDvmPowerAmount = new BigDecimal(oldData.getDvmPowerAmount());
                    BigDecimal newBDvmPowerAmount = new BigDecimal(newData.getDvmPowerAmount());
                    if (oldBDvmPowerAmount.compareTo(newBDvmPowerAmount) != 0) {
                        
                        String chgDvmAmount= newBDvmPowerAmount.subtract(oldBDvmPowerAmount).toPlainString();
                        DeviceGroupNoticeEntity buyDvmNotice = null;
                        if (oldBDvmPowerAmount.compareTo(new BigDecimal(0)) > 0) {
                            
                            buyDvmNotice = createNoticeEvent(oldData, BuyDvmNotice.EVENT_BUY_DVM,
                                    BuyDvmNotice.FLAG_CONTINUE, chgDvmAmount, BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                        } else {
                            buyDvmNotice = createNoticeEvent(oldData, BuyDvmNotice.EVENT_BUY_DVM,
                                    BuyDvmNotice.FLAG_FIRST, chgDvmAmount, BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                        }
                        if (buyDvmNotice != null) {
                            list.add(buyDvmNotice);
                        }
                    }
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }

                
                int oldGroupLevel = oldData.getGroupLevel();
                int newGroupLevel = newData.getGroupLevel();
                DeviceGroupNoticeEntity groupLevelNotice = null;
                if (newGroupLevel > oldGroupLevel) {
                    groupLevelNotice = createNoticeEvent(oldData, GroupLevelNotice.EVENT_GROUP_LEVEL_CHG,
                            GroupLevelNotice.FLAG_UP, "", BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                } else if(newGroupLevel < oldGroupLevel) {
                    groupLevelNotice = createNoticeEvent(oldData, GroupLevelNotice.EVENT_GROUP_LEVEL_CHG,
                            GroupLevelNotice.FLAG_DOWN, "", BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                }
                if (null != groupLevelNotice) {
                    list.add(groupLevelNotice);
                }
            }
            return list;
        } else if(oldData.getIsDeviceGroup() == 1){
            
            List<DeviceGroupNoticeEntity> list = new ArrayList<>();
            DeviceGroupNoticeEntity exitDeviceGroupNotice  = createNoticeEvent(oldData, ExitDeviceGroupNotice.EVENT_EXIT_DEVICE_GROUP,
                    0, oldData.getGroupName(), BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
            exitDeviceGroupNotice.setExtra(oldData.getClusterId());
            list.add(exitDeviceGroupNotice);
            return list;
        } else {
            return null;
        }
    }


    public static List<DeviceGroupBean> filterDeviceGroup(List<DeviceGroupBean> all) {
        if (null == all || all.size() == 0) {
            return null;
        }
        List<DeviceGroupBean> deviceGroups = new ArrayList<>();
        for (int i=0; i<all.size(); i++) {
            DeviceGroupBean group = all.get(i);
            if (null != group && group.isDeviceGroup) {
                deviceGroups.add(group);
            }
        }
        return deviceGroups;
    }

    
    public static DeviceGroupBean findDeviceGroupByClusterId(String clusterId, Map<String, DeviceGroupBean> groupMap) {
        if (null == groupMap || groupMap.isEmpty()) {
            return null;
        }
        return findDeviceGroupByClusterId(clusterId, groupMap.values());
    }

    
    public static DeviceGroupBean findDeviceGroupByClusterId(String clusterId, Collection<DeviceGroupBean> groups) {
        if (TextUtils.isEmpty(clusterId) || null == groups || groups.size() == 0) {
            return null;
        }
        Iterator<DeviceGroupBean> iterator = groups.iterator();
        while (iterator.hasNext()) {
            DeviceGroupBean group = iterator.next();
            if (group != null && group.isDeviceGroup && clusterId.equals(group.clusterId)) {
                return group;
            }
        }
        return null;
    }

}
