package com.app.note.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(indices = {@Index(value = {"folderId","noteId"}, unique=true)})
public class NoteFolderRelationEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "folderId")
    private int folderId;

    @ColumnInfo(name = "noteId")
    private String noteId;

    @ColumnInfo(name = "updateTime")
    private long updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "NoteFolderRelationEntity{" +
                "id=" + id +
                ", folderId=" + folderId +
                ", noteId='" + noteId + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
