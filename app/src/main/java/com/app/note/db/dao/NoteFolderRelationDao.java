package com.app.note.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.note.db.entity.NoteFolderRelationEntity;

import java.util.List;



@Dao
public interface NoteFolderRelationDao {

    
    @Query("SELECT * FROM NoteFolderRelationEntity WHERE folderId =:folderId ORDER BY updateTime DESC")
    List<NoteFolderRelationEntity> getAllNotesIds(int folderId);

    @Query("SELECT * FROM NoteFolderRelationEntity WHERE noteId =:noteId ORDER BY updateTime DESC")
    List<NoteFolderRelationEntity> getAllFolderIds(int noteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(NoteFolderRelationEntity... notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(List<NoteFolderRelationEntity> notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NoteFolderRelationEntity note);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNote(NoteFolderRelationEntity... notes);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNotes(List<NoteFolderRelationEntity> notes);

    @Delete
    int delete(NoteFolderRelationEntity... notes);

    @Delete
    int delete(List<NoteFolderRelationEntity> notes);

    @Query("DELETE FROM NoteFolderRelationEntity WHERE id=:id")
    int deleteWhere(int id);

    @Query("DELETE FROM NoteFolderRelationEntity WHERE folderId=:folderId AND noteId=:noteId")
    int deleteWhere(int folderId, int noteId);
}
