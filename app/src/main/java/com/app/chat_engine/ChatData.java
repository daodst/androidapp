package com.app.chat_engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.app.pojo.DeviceGroupBean;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;
import com.wallet.ctc.util.AllUtils;

import org.matrix.android.sdk.api.session.utils.bean.MyRoomList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.AppApplication;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.wallet.pojo.EvmosMyGroupDataBean;
import im.wallet.router.wallet.pojo.EvmosMyGroupList;


public class ChatData {

    private static final String TAG = "ChatData";

    private static class HolderClass {
        private static final ChatData INSTANCE = new ChatData();
    }
    public static ChatData getInstance() {
        ChatData intance = ChatData.HolderClass.INSTANCE;
        return intance;
    }
    private ChatData() {
    }

    private int aDecimal = 0;

    
    private List<DeviceGroupBean> myGroups = new ArrayList<>();

    
    private Context getAppContext() {
        return AppApplication.getContext();
    }

    public String getLoginUserId() {
        return ChatStatusProvide.getUserId(getAppContext());
    }

    
    public String getUserAddress() {
        String userId = getLoginUserId();
        return AllUtils.getAddressByUid(userId);
    }

    public int getDecimal() {
        if (aDecimal > 0) {
            return aDecimal;
        }
        AssertBean assertBean = WalletDBUtil.getInstent(getAppContext()).getWalletAssets(WalletUtil.MCC_COIN, BuildConfig.EVMOS_FAKE_UNINT);
        if (assertBean != null) {
            aDecimal = assertBean.getDecimal();
        }
        if (aDecimal == 0) {
            aDecimal = 18;
        }
        return aDecimal;
    }

    
    public String getTenValue(String bigAmount) {
        return AllUtils.getTenDecimalValue(bigAmount, getDecimal(), 6);
    }

    
    public void init() {
        httpGetAllGroups();
    }


    
    private synchronized List<DeviceGroupBean> updateMyGroupDatas(boolean success, Map<String, DeviceGroupBean> gateWayGroupMap,
                  Map<String, DeviceGroupBean> chainGroupMap, Map<String, DeviceGroupBean> allMap) {
        if (!success) {
            
            return myGroups;
        }

        ChatNoticeDb.getInstance().testPrint("before insert db");

        
        if (allMap != null && !allMap.isEmpty()) {
            ChatNoticeDb.getInstance().insertOrUpdateGroups(getUserAddress(), new ArrayList<>(allMap.values()));
        } else {
            ChatNoticeDb.getInstance().insertOrUpdateGroups(getUserAddress(), new ArrayList<>());
        }
        
        ChatNoticeDb.getInstance().updateGroupMoveJoinState(getUserAddress(), gateWayGroupMap, chainGroupMap);
        ChatNoticeDb.getInstance().testPrint("after insert db");





        myGroups.clear();
        if (allMap != null && allMap.size() > 0) {
            myGroups.addAll(allMap.values());
        }
        return myGroups;
    }



    
    public synchronized List<DeviceGroupBean> httpGetAllGroups() {

        
        boolean getSuccess = true;
        Map<String, DeviceGroupBean> allMap = new HashMap<>();
        Map<String, DeviceGroupBean> gateWayMap = new HashMap<>();
        MyRoomList chatRoomList = ChatStatusProvide.httpGetMyJoinedRooms(getAppContext());
        if (null != chatRoomList) {
            List<String> groupIds = chatRoomList.getJoined_rooms();
            if (groupIds != null && groupIds.size() > 0) {
                for (String groupId : groupIds) {
                    DeviceGroupBean group = new DeviceGroupBean(groupId);
                    allMap.put(groupId, group);
                    gateWayMap.put(groupId, group);
                }
            }
        } else {
            getSuccess = false;
            Log.e(TAG, "chat server get rooms fail");
        }

        
        String userAddr = getUserAddress();
        EvmosMyGroupList chainGroups = ChatSdk.httpGetMyDeviceGroups(userAddr);
        Map<String, DeviceGroupBean> chainMap = new HashMap<>();
        if (chainGroups != null && chainGroups.isSuccess()) {
            List<EvmosMyGroupList.DeviceGruop> deviceList = chainGroups.data != null ? chainGroups.data.device : null;
            List<String> ownerDeviceList = chainGroups.data != null ? chainGroups.data.owner : null;
            if (deviceList != null && deviceList.size() > 0) {
                for (EvmosMyGroupList.DeviceGruop deviceGruop : deviceList) {
                    String groupId = deviceGruop.groupId;
                    DeviceGroupBean group = new DeviceGroupBean(groupId, deviceGruop.groupName, deviceGruop.groupLevel, true);
                    if (ownerDeviceList != null && ownerDeviceList.contains(groupId)) {
                        group.isOwner = true;
                    } else {
                        group.isOwner = false;
                    }

                    
                    EvmosMyGroupDataBean myGroupData = ChatSdk.httpGetSomeGroupData(userAddr, groupId);
                    if (null != myGroupData && myGroupData.isSuccess() && myGroupData.data != null) {
                        if (!TextUtils.isEmpty(myGroupData.data.power_amount)) {
                            String dvmPowerAmount = getTenValue(myGroupData.data.power_amount);
                            group.setDvmPowerAmount(dvmPowerAmount);

                            group.burnAmount = getTenValue(myGroupData.data.burn_amount);
                            group.burnRatio = myGroupData.data.burn_ratio;
                            group.powerReward = getTenValue(myGroupData.data.power_reward);
                            group.deviceReward = getTenValue(myGroupData.data.device_reward);
                            group.ownerReward = getTenValue(myGroupData.data.owner_reward);
                            group.ownerAddr = myGroupData.data.cluster_owner;
                            group.dvmDayFreeGas = getTenValue(myGroupData.data.gas_day);
                            group.dvmAuthContract = myGroupData.data.auth_contract;
                            group.dvmAuthHeight = myGroupData.data.auth_height;

                            group.clusterId = myGroupData.data.cluster_id;
                        }
                    }
                    allMap.put(groupId, group);
                    chainMap.put(groupId, group);
                }
            }
        } else {
            getSuccess = false;
            String errorInfo = chainGroups != null ? chainGroups.getInfo() : "data is null";
            Log.e(TAG, "get chain group fail："+errorInfo);
        }
        return updateMyGroupDatas(getSuccess, gateWayMap, chainMap, allMap);
    }

    
    public synchronized List<DeviceGroupBean> httpGetMyDeviceGroups() {
        if (myGroups.size() == 0) {
            httpGetAllGroups();
        }
        return localGetMyDeviceGroups();
    }

    
    public synchronized List<DeviceGroupBean> httpGetSystemGroups() {
        if (myGroups.size() == 0) {
            httpGetAllGroups();
        }
        return localGetSystemNoticeGroups();
    }

    
    public synchronized List<DeviceGroupBean> localGetMyDeviceGroups() {
        if (null == myGroups || myGroups.size() == 0) {
            return null;
        }
        List<DeviceGroupBean> deviceGroups = new ArrayList<>();
        for (int i=0; i<myGroups.size(); i++) {
            DeviceGroupBean group = myGroups.get(i);
            if (group.isDeviceGroup) {
                deviceGroups.add(group);
            }
        }
        return deviceGroups;
    }

    
    public synchronized List<DeviceGroupBean> localGetSystemNoticeGroups() {
        if (null == myGroups || myGroups.size() == 0) {
            return null;
        }
        List<DeviceGroupBean> deviceGroups = new ArrayList<>();
        for (int i=0; i<myGroups.size(); i++) {
            DeviceGroupBean group = myGroups.get(i);
            if (group.isSystemNoticeGroup()) {
                deviceGroups.add(group);
            }
        }
        return deviceGroups;
    }










}
