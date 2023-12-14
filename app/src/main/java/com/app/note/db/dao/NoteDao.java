package com.app.note.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.app.note.db.entity.NoteEntity;

import java.util.List;

import io.reactivex.Flowable;



@Dao
public interface NoteDao {

    
    @Query("SELECT * FROM noteentity WHERE id >= 0 ORDER BY updateTime DESC")
    List<NoteEntity> getAllNotes();

    @Query("SELECT * FROM noteentity WHERE id >= 0 ORDER BY updateTime DESC")
    Flowable<List<NoteEntity>> getAllNotesRx();

    @Query("SELECT * FROM noteentity WHERE id >= 0 ORDER BY updateTime DESC")
    LiveData<List<NoteEntity>> getAllNotesLiveData();

    @Query("SELECT * FROM noteentity WHERE id=:id")
    NoteEntity getNote(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(NoteEntity... notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(List<NoteEntity> notes);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NoteEntity note);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNote(NoteEntity... notes);

    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    int updateNotes(List<NoteEntity> notes);

    @Delete
    int delete(NoteEntity... notes);

    @Delete
    int delete(List<NoteEntity> notes);

    @Query("DELETE FROM noteentity WHERE id=:id")
    int deleteWhere(int id);
}
