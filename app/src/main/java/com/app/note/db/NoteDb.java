package com.app.note.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.App;
import com.app.note.db.dao.NoteDao;
import com.app.note.db.dao.NoteFolderDao;
import com.app.note.db.dao.NoteFolderRelationDao;
import com.app.note.db.entity.NoteEntity;
import com.app.note.db.entity.NoteFolderEntity;
import com.app.note.db.entity.NoteFolderRelationEntity;


@Database(entities = {NoteEntity.class, NoteFolderEntity.class, NoteFolderRelationEntity.class}, version = 2, exportSchema = false)
public abstract class NoteDb extends RoomDatabase {

    private static final String DB_NAME = "note.db";
    public static final String TAG = "NoteDb";

    private static NoteDb sInstance;

    public abstract NoteDao noteDao();
    public abstract NoteFolderDao noteFolderDao();
    public abstract NoteFolderRelationDao noteFolderRelationDao();

    public static NoteDb getInstance() {
        if (sInstance == null) {
            synchronized (NoteDb.class) {
                if (sInstance == null) {
                    sInstance = create(App.getInstance());
                }
            }
        }
        return sInstance;
    }


    private static NoteDb create(final Context context) {
        return Room.databaseBuilder(
                context,
                NoteDb.class,
                DB_NAME).addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        }).build();
    }



}
