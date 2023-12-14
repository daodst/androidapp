package com.app.chat_engine;

import android.text.TextUtils;
import android.util.Log;

import com.app.chat_engine.db.MyDb;
import com.app.chat_engine.db.entity.DeviceGroupEntity;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.chat_engine.notice.AutoJoinGroupNotice;
import com.app.chat_engine.notice.BaseChatNotice;
import com.app.chat_engine.notice.FirstJoinGroupNotice;
import com.app.chat_engine.notice.GroupMoveNotice;
import com.app.chat_engine.notice.GroupVoteNotice;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ChatNoticeDb {

    private final Executor mOneExecutor;
    private static final String TAG = "ChatNoticeDb";


    private static class HolderClass {
        private static final ChatNoticeDb INSTANCE = new ChatNoticeDb();
    }
    public static ChatNoticeDb getInstance() {
        ChatNoticeDb intance = HolderClass.INSTANCE;
        return intance;
    }
    private ChatNoticeDb() {
        this.mOneExecutor = Executors.newFixedThreadPool(1);
    }


    
    public void updateNoGruoupFlag(String userAddr) {
        if (TextUtils.isEmpty(userAddr)) {
            return;
        }
        DeviceGroupNoticeEntity notice = MyDb.getInstance().noticeDao().getNoticeInfo(userAddr, "", "NoGroupFlag");
        if (null == notice) {
            
            DeviceGroupNoticeEntity noticeEntity = new DeviceGroupNoticeEntity();
            noticeEntity.setUserAddr(userAddr);
            noticeEntity.setKeyType("NoGroupFlag");
            noticeEntity.setGroupId("");
            noticeEntity.setNoticeTime(System.currentTimeMillis());
            MyDb.getInstance().noticeDao().insert(noticeEntity);
        }
    }

    
    public DeviceGroupNoticeEntity getNoGroupFlag(String userAddr) {
        return MyDb.getInstance().noticeDao().getNoticeInfo(userAddr, "", "NoGroupFlag");
    }


    
    public void insertOrUpdateGroups(String userAddr, List<DeviceGroupBean> onlineMyGroups) {
        if (onlineMyGroups == null) {
            return;
        }
        if (onlineMyGroups.size() == 0) {
            
            List<DeviceGroupEntity> localEntityList = MyDb.getInstance().groupDao().getAllGroups(userAddr);
            if (localEntityList != null && localEntityList.size() > 0) {
                
                MyDb.getInstance().groupDao().deleteAllGroup(userAddr);
            }

            
            updateNoGruoupFlag(userAddr);
        } else {

            List<DeviceGroupBean> deviceGroupList = DbConvertUtils.filterDeviceGroup(onlineMyGroups);
            if (deviceGroupList == null || deviceGroupList.size() == 0) {
                
                updateNoGruoupFlag(userAddr);
            }

            
            List<DeviceGroupEntity> localEntityList = MyDb.getInstance().groupDao().getAllGroups(userAddr);
            
            List<DeviceGroupEntity> onlineEntityList = DbConvertUtils.convert(userAddr, onlineMyGroups);
            if (localEntityList == null || localEntityList.size() == 0) {
                
                if (onlineEntityList != null && onlineEntityList.size() > 0) {
                    List<DeviceGroupNoticeEntity> preNoGroupFlgs = MyDb.getInstance().noticeDao().getNoticeInfo(userAddr, "NoGroupFlag");
                    DeviceGroupNoticeEntity preNoGroupFlg = null;
                    if (null != preNoGroupFlgs && preNoGroupFlgs.size() > 0) {
                        preNoGroupFlg = preNoGroupFlgs.get(0);
                    }
                    List<DeviceGroupNoticeEntity> noticeList = new ArrayList<>();
                    if (preNoGroupFlg != null && preNoGroupFlg.getNoticeTime() > 0) {
                        
                        for (int i=0; i<onlineEntityList.size(); i++) {
                            DeviceGroupEntity group = onlineEntityList.get(i);
                            if (group.getIsDeviceGroup() == 1) {
                               DeviceGroupNoticeEntity notice = DbConvertUtils.createNoticeEvent(group, FirstJoinGroupNotice.EVENT_FIRST_JOIN_GROUP,
                                        FirstJoinGroupNotice.FLAG_FIRST, "", BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                               if (null != notice) {
                                   noticeList.add(notice);
                               }
                            }
                        }
                    }
                    MyDb.getInstance().groupDao().insertGroupLists(onlineEntityList);
                    if (noticeList.size() > 0) {
                        insertOrUpdateNotices(noticeList);
                    }
                }
            } else {
                List<DeviceGroupEntity> delList = new ArrayList<>();
                List<DeviceGroupEntity> addList = new ArrayList<>();
                List<DeviceGroupEntity> updateList = new ArrayList<>();

                
                List<DeviceGroupNoticeEntity> noticeList = new ArrayList<>();

                Map<String, DeviceGroupEntity> localMap = DbConvertUtils.entityListToMap(localEntityList);
                Map<String, DeviceGroupEntity> onlineMap = DbConvertUtils.entityListToMap(onlineEntityList);

                
                for (Map.Entry<String, DeviceGroupEntity> entry : onlineMap.entrySet()) {
                    String key = entry.getKey();
                    if (!localMap.containsKey(key)) {
                        DeviceGroupEntity newEntity = entry.getValue();
                        
                        if (newEntity.getIsDeviceGroup() == 1) {
                            DeviceGroupNoticeEntity notice = DbConvertUtils.createNoticeEvent(newEntity, FirstJoinGroupNotice.EVENT_FIRST_JOIN_GROUP,
                                    FirstJoinGroupNotice.FLAG_FIRST, "", BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                            if (null != notice) {
                                noticeList.add(notice);
                            }
                        }
                        addList.add(newEntity);
                    }
                }

                
                for (Map.Entry<String, DeviceGroupEntity> entry : localMap.entrySet()) {
                    String key = entry.getKey();
                    if (onlineMap.containsKey(key)) {
                        DeviceGroupEntity updateEntity = entry.getValue();
                        
                        DeviceGroupEntity onlineEntity = onlineMap.get(key);
                        if (!updateEntity.equals(onlineEntity)) {
                            List<DeviceGroupNoticeEntity> changeList = DbConvertUtils.getGroupChangeNotice(updateEntity, onlineEntity);
                            if (changeList != null && changeList.size() > 0) {
                                noticeList.addAll(changeList);
                            }
                            
                            updateEntity.updateData(onlineEntity);
                            updateList.add(updateEntity);
                        }
                    } else {
                        delList.add(entry.getValue());
                    }
                }

                
                if (delList.size() > 0) {
                    MyDb.getInstance().groupDao().delete(delList);
                    
                    for (DeviceGroupEntity entity : delList) {
                        List<DeviceGroupNoticeEntity> delNoticeList = findEventNoticeList(userAddr, entity.getGroupId());
                        if (null != delNoticeList && delNoticeList.size() > 0) {
                            for (int i=0; i<delNoticeList.size(); i++) {
                                DeviceGroupNoticeEntity notice = delNoticeList.get(i);
                                if (GroupMoveNotice.EVENT_GROUP_MOVE.equals(notice.getKeyType())) {
                                    
                                    String clusterId = notice.getExtra();
                                    DeviceGroupBean group = DbConvertUtils.findDeviceGroupByClusterId(clusterId, onlineMyGroups);
                                    if (null == group) {
                                        MyDb.getInstance().noticeDao().delete(notice);
                                    } else {
                                        
                                        notice.setFlag(GroupMoveNotice.FLAG_MOVE_SUCCESS);
                                        notice.setGroupId(group.groupId);
                                        notice.setValue(group.groupName);
                                        notice.setState(BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                                        MyDb.getInstance().noticeDao().updateNotice(notice);
                                    }
                                } else {
                                    MyDb.getInstance().noticeDao().delete(notice);
                                }
                            }
                        }

                    }
                }

                
                if (updateList.size() > 0) {
                    MyDb.getInstance().groupDao().updateGroup(updateList);
                }

                
                if (addList.size() > 0) {
                    MyDb.getInstance().groupDao().insertGroups(addList);
                }

                if (noticeList.size() > 0) {
                    insertOrUpdateNotices(noticeList);
                }
            }
        }

    }


    
    public void updateGroupMoveJoinState(String userAddr, Map<String, DeviceGroupBean> gateWayGroupMap,
                                         Map<String, DeviceGroupBean> chainGroupMap) {
        
        List<DeviceGroupNoticeEntity> autoJoinGroupEvents = findEventNoticeList(userAddr, AutoJoinGroupNotice.EVENT_AUTO_JOIN_GROUP);
        if (null != autoJoinGroupEvents && autoJoinGroupEvents.size() > 0) {
            for (DeviceGroupNoticeEntity notice : autoJoinGroupEvents) {
                if (notice.getFlag() == AutoJoinGroupNotice.FLAG_WAIT_JOIN) {
                    String groupId = notice.getGroupId();
                    if (gateWayGroupMap != null && gateWayGroupMap.size() > 0 && gateWayGroupMap.containsKey(groupId)) {
                        
                        notice.setFlag(AutoJoinGroupNotice.FLAG_JOIN_SUCCESS);
                        notice.setState(BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                        MyDb.getInstance().noticeDao().updateNotice(notice);
                    }
                }
            }
        }


        
        List<DeviceGroupBean> needAutoJoinRooms = new ArrayList<>();
        if (chainGroupMap != null && !chainGroupMap.isEmpty()) {
            for (String gid : chainGroupMap.keySet()) {
                if (gateWayGroupMap == null || gateWayGroupMap.isEmpty() || !gateWayGroupMap.containsKey(gid)) {
                    DeviceGroupBean group = chainGroupMap.get(gid);
                    needAutoJoinRooms.add(group);
                }
            }
        }
        List<DeviceGroupNoticeEntity> noticeList = new ArrayList<>();
        if (needAutoJoinRooms.size() > 0) {
            
            for (DeviceGroupBean group: needAutoJoinRooms) {
                if (group.isOwner) {
                    
                    DeviceGroupNoticeEntity moveNotice = DbConvertUtils.createNoticeEvent(userAddr, group.groupId,
                            GroupMoveNotice.EVENT_GROUP_MOVE, GroupMoveNotice.FLAG_WAIT_MOVE, group.groupName, BaseChatNotice.EVENT_STATE_WAITE_NOTICE);

                    if (null != moveNotice) {
                        moveNotice.setExtra(group.clusterId);
                        noticeList.add(moveNotice);
                    }
                } else {
                    
                    DeviceGroupNoticeEntity autoJoinNotice = DbConvertUtils.createNoticeEvent(userAddr, group.groupId,
                            AutoJoinGroupNotice.EVENT_AUTO_JOIN_GROUP, AutoJoinGroupNotice.FLAG_WAIT_JOIN, group.groupName, BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
                    if (null != autoJoinNotice) {
                        autoJoinNotice.setExtra(group.clusterId);
                        noticeList.add(autoJoinNotice);
                    }
                }
            }
        }

        if (noticeList.size() > 0) {
            insertOrUpdateNotices(noticeList);
        }
    }

    
    public void ioInsertOrUpdateNotices(List<DeviceGroupNoticeEntity> noticeEntityList) {
        mOneExecutor.execute(()->{
            insertOrUpdateNotices(noticeEntityList);
        });
    }

    public void ioInsertOrUpdateNotices(DeviceGroupNoticeEntity noticeEntity) {
        mOneExecutor.execute(()->{
            insertOrUpdateNotices(noticeEntity);
        });
    }

    public void insertOrUpdateNotices(DeviceGroupNoticeEntity noticeEntity) {
        if (null == noticeEntity) {
            return;
        }
        List<DeviceGroupNoticeEntity> list = new ArrayList<>();
        list.add(noticeEntity);
        insertOrUpdateNotices(list);
    }

    
    public void insertOrUpdateNotices(List<DeviceGroupNoticeEntity> noticeEntityList) {
        if (null == noticeEntityList || noticeEntityList.size() == 0) {
            return;
        }

        List<DeviceGroupNoticeEntity> addList = new ArrayList<>();
        List<DeviceGroupNoticeEntity> updateList = new ArrayList<>();

        for (DeviceGroupNoticeEntity notice: noticeEntityList) {
            String userAddr = notice.getUserAddr();
            String groupId = notice.getGroupId();
            String keyType = notice.getKeyType();
            DeviceGroupNoticeEntity localNotice = MyDb.getInstance().noticeDao().getNoticeInfo(userAddr, groupId, keyType);
            if (null != localNotice) {
                
                localNotice.updateData(notice);
                updateList.add(localNotice);
            } else {
                
                addList.add(notice);
            }
        }
        if (updateList.size() > 0) {
            MyDb.getInstance().noticeDao().updateNotice(updateList);
        }
        if (addList.size() > 0) {
            MyDb.getInstance().noticeDao().insertNoticeLists(addList);
        }
    }


    
    public DeviceGroupNoticeEntity getNoticeInfo(String userAddr, String groupId, String keyType) {
        return MyDb.getInstance().noticeDao().getNoticeInfo(userAddr, groupId, keyType);
    }

    
    public void insertOrUpdateNoticeTime(String userAddr, String groupId, String keyType, long noticeTime, String value) {
        insertOrUpdateNoticeTime(userAddr, groupId, keyType, noticeTime, -1, value);
    }

    
    public void ioInsertOrUpdateNoticeTime(String userAddr, String groupId, String keyType, long noticeTime, String value) {
        mOneExecutor.execute(()->{
            insertOrUpdateNoticeTime(userAddr, groupId, keyType, noticeTime, value);
        });
    }

    
    public void insertOrUpdateNextNoticeTime(String userAddr, String groupId, String keyType, long nextNoticeTime, String value) {
        insertOrUpdateNoticeTime(userAddr, groupId, keyType, -1, nextNoticeTime, value);
    }

    
    public void ioInsertOrUpdateNextNoticeTime(String userAddr, String groupId, String keyType, long nextNoticeTime, String value) {
        mOneExecutor.execute(()->{
            insertOrUpdateNextNoticeTime(userAddr, groupId, keyType, nextNoticeTime, value);
        });
    }

    
    public void insertOrUpdateNoticeTime(String userAddr, String groupId, String keyType, long noticeTime, long nextNoticeTime, String value) {
        DeviceGroupNoticeEntity entity = getNoticeInfo(userAddr, groupId, keyType);
        if (null == entity) {
            entity = new DeviceGroupNoticeEntity(userAddr, groupId, keyType);
            if (noticeTime >= 0) {
                entity.setNoticeTime(noticeTime);
            }
            if (nextNoticeTime >= 0) {
                entity.setNextNoticeTime(nextNoticeTime);
            }
            if (!TextUtils.isEmpty(value)) {
                entity.setValue(value);
            }

            MyDb.getInstance().noticeDao().insert(entity);
        } else {
            if (noticeTime >= 0) {
                entity.setNoticeTime(noticeTime);
            }
            if (nextNoticeTime >= 0) {
                entity.setNextNoticeTime(nextNoticeTime);
            }
            if (!TextUtils.isEmpty(value)) {
                entity.setValue(value);
            }
            MyDb.getInstance().noticeDao().updateNotice(entity);
        }
    }

    
    public void ioInsertOrUpdateNoticeTime(String userAddr, String groupId, String keyType, long noticeTime, long nextNoticeTime, String value) {
        mOneExecutor.execute(()->{
            insertOrUpdateNoticeTime(userAddr, groupId, keyType, noticeTime, nextNoticeTime, value);
        });
    }

    
    public List<DeviceGroupNoticeEntity> findEventNoticeList(String userAddr, String keyType) {
        return MyDb.getInstance().noticeDao().findEventNotice(userAddr, keyType);
    }

    
    public List<DeviceGroupNoticeEntity> findEventNoticeList(String userAddr, String keyType, int state) {
       return MyDb.getInstance().noticeDao().findEventNotice(userAddr, keyType, state);
    }

    
    public List<DeviceGroupNoticeEntity> findNormalNoticeList(String userAddr, String keyType, int state) {
       return MyDb.getInstance().noticeDao().findNormalNotice(userAddr, keyType, state);
    }




    
    public void updateNoticeState(String userAddr, String keyType, int nowState, int newState) {
        List<DeviceGroupNoticeEntity> list = findNormalNoticeList(userAddr, keyType, nowState);
        if (null != list && list.size() > 0) {
            for (int i=0; i<list.size(); i++) {
                list.get(i).setState(newState);
            }
            insertOrUpdateNotices(list);
        }
    }

    
    public void ioUpdateNoticeState(String userAddr, String keyType, int nowState, int newState) {
        mOneExecutor.execute(()->{
            updateNoticeState(userAddr, keyType, nowState, newState);
        });
    }

    
    public void updateEventNoticeState(String userAddr, String groupId, String keyType, int state) {
        MyDb.getInstance().noticeDao().updateEventNoticeState(userAddr, groupId, keyType, state, System.currentTimeMillis());
    }

    
    public void ioMultiUpdateEventState(String userAddr, List<DeviceGroupBean> groups, String keyType, int state) {
        mOneExecutor.execute(()->{
            for (DeviceGroupBean group : groups) {
                updateEventNoticeState(userAddr, group.groupId, keyType, state);
            }
        });
    }


    
    public void insertOrUpdateGroupVoteEvent(String userAddr, String groupId, String senderName) {
        DeviceGroupNoticeEntity groupVoteNotice = DbConvertUtils.createNoticeEvent(userAddr, groupId, GroupVoteNotice.EVENT_GROUP_VOTE,
                0, senderName, BaseChatNotice.EVENT_STATE_WAITE_NOTICE);
        insertOrUpdateNotices(groupVoteNotice);
    }




    public void testPrint(String desc) {
        Log.i(TAG, "---"+desc);
        testPrintAllGroups();
        testPrintAllNotices();
    }

    public void ioTestPrint(String desc) {
        mOneExecutor.execute(()->{
            testPrint(desc);
        });
    }


    public void testPrintAllGroups() {
        List<DeviceGroupEntity> groups = MyDb.getInstance().groupDao().testGetAllGroups();
        if (groups == null || groups.size() == 0) {
            Log.w(TAG, "--------groups size = 0");
            return;
        } else {
            Log.w(TAG, "--------groups size = "+groups.size());
        }
        for (DeviceGroupEntity group : groups) {
            Log.i(TAG, ""+group.toString());
        }

    }

    public void testPrintAllNotices() {
        List<DeviceGroupNoticeEntity> notices = MyDb.getInstance().noticeDao().testGetAllNotices();
        if (null == notices || notices.size() == 0) {
            Log.w(TAG, "-------notices size = 0");
            return;
        } else {
            Log.w(TAG, "-------notices size = "+notices.size());
        }
        for (DeviceGroupNoticeEntity notice : notices) {
            Log.i(TAG, ""+notice.toString());
        }
    }

}
