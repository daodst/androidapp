package com.app.chat_engine.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;

import java.util.List;



@Dao
public interface DeviceGroupNoticeDao {

    
    @Query("SELECT * FROM devicegroupnoticeentity WHERE userAddr=:address AND groupId=:groupId AND keyType=:keyType")
    DeviceGroupNoticeEntity getNoticeInfo(String address, String groupId, String keyType);

    @Query("SELECT * FROM devicegroupnoticeentity WHERE userAddr=:address AND keyType=:keyType")
    List<DeviceGroupNoticeEntity> getNoticeInfo(String address, String keyType);

    @Query("SELECT * FROM devicegroupnoticeentity WHERE id >= 0")
    List<DeviceGroupNoticeEntity> testGetAllNotices();

    @Query("SELECT * FROM devicegroupnoticeentity WHERE userAddr=:address AND keyType=:keyType AND state=:state AND noticeType=1")
    List<DeviceGroupNoticeEntity> findEventNotice(String address, String keyType, int state);

    @Query("SELECT * FROM devicegroupnoticeentity WHERE userAddr=:address AND keyType=:keyType AND noticeType=1")
    List<DeviceGroupNoticeEntity> findEventNotice(String address, String keyType);

    @Query("SELECT * FROM devicegroupnoticeentity WHERE userAddr=:address AND keyType=:keyType AND state=:state")
    List<DeviceGroupNoticeEntity> findNormalNotice(String address, String keyType, int state);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotices(DeviceGroupNoticeEntity... notices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNoticeLists(List<DeviceGroupNoticeEntity> notices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceGroupNoticeEntity notice);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNotice(DeviceGroupNoticeEntity... notices);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNotice(List<DeviceGroupNoticeEntity> notices);

    @Query("UPDATE devicegroupnoticeentity SET noticeTime=:noticeTime WHERE userAddr=:address AND groupId=:groupId AND keyType=:keyType")
    int updateNoticeTime(String address, String groupId, String keyType, long noticeTime);

    @Query("UPDATE devicegroupnoticeentity SET noticeTime=:noticeTime, state=:state WHERE userAddr=:address AND groupId=:groupId AND keyType=:keyType")
    int updateEventNoticeState(String address, String groupId, String keyType, int state, long noticeTime);

    @Query("UPDATE devicegroupnoticeentity SET noticeTime=:noticeTime, nextNoticeTime=:nextNoticeTime WHERE userAddr=:address AND groupId=:groupId AND keyType=:keyType")
    int updateNoticeTime(String address, String groupId, String keyType, long noticeTime, long nextNoticeTime);

    @Delete
    int delete(DeviceGroupNoticeEntity... notices);

    @Query("DELETE from devicegroupnoticeentity WHERE userAddr=:address AND groupId=:groupId AND groupId is NOT NULL")
    int deleteGroupNotice(String address, String groupId);
}
