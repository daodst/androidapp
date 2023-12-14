package com.app.chat_engine.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.chat_engine.db.entity.DeviceGroupEntity;

import java.util.List;

import io.reactivex.Flowable;



@Dao
public interface DeviceGroupDao {

    
    @Query("SELECT * FROM devicegroupentity WHERE userAddr=:address ORDER BY time DESC")
    List<DeviceGroupEntity> getAllGroups(String address);

    @Query("SELECT * FROM devicegroupentity WHERE id >= 0 ORDER BY time DESC")
    List<DeviceGroupEntity> testGetAllGroups();

    @Query("SELECT * FROM devicegroupentity WHERE userAddr=:address ORDER BY time DESC")
    Flowable<List<DeviceGroupEntity>> getAllGroupsRx(String address);

    @Query("SELECT * FROM devicegroupentity WHERE userAddr=:address ORDER BY time DESC")
    LiveData<List<DeviceGroupEntity>> getAllGroupsLiveData(String address);

    @Query("SELECT * FROM devicegroupentity WHERE userAddr=:address AND groupId=:groupId")
    DeviceGroupEntity getGroupInfo(String address, String groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(DeviceGroupEntity... groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroups(List<DeviceGroupEntity> groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGroupLists(List<DeviceGroupEntity> groups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceGroupEntity group);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateGroup(DeviceGroupEntity... groups);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateGroup(List<DeviceGroupEntity> groups);

    @Delete
    int delete(DeviceGroupEntity... groups);

    @Delete
    int delete(List<DeviceGroupEntity> groups);

    @Query("DELETE FROM devicegroupentity WHERE userAddr=:address AND groupId=:groupId")
    int deleteWhere(String address, String groupId);

    @Query("DELETE FROM devicegroupentity WHERE userAddr=:address")
    int deleteAllGroup(String address);
}
