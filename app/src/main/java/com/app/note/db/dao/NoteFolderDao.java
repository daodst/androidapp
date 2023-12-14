package com.app.note.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.note.db.entity.NoteFolderEntity;

import java.util.List;

import io.reactivex.Flowable;



@Dao
public interface NoteFolderDao {

    
    @Query("SELECT * FROM notefolderentity WHERE id >= 0 ORDER BY createTime DESC")
    List<NoteFolderEntity> getAllFolders();

    @Query("SELECT * FROM notefolderentity WHERE id >= 0 ORDER BY createTime DESC")
    Flowable<List<NoteFolderEntity>> getAllFolderRx();

    @Query("SELECT * FROM notefolderentity WHERE id >= 0 ORDER BY createTime DESC")
    LiveData<List<NoteFolderEntity>> getAllNotesLiveData();

    @Query("SELECT * FROM notefolderentity WHERE id=:id")
    NoteFolderEntity getNote(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(NoteFolderEntity... notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(List<NoteFolderEntity> notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NoteFolderEntity note);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNote(NoteFolderEntity... notes);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNotes(List<NoteFolderEntity> notes);

    @Delete
    int delete(NoteFolderEntity... notes);

    @Delete
    int delete(List<NoteFolderEntity> notes);

    @Query("DELETE FROM NoteFolderEntity WHERE id=:id")
    int deleteWhere(int id);
}
