package com.app.chat_engine.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.app.App;
import com.app.chat_engine.db.dao.DeviceGroupDao;
import com.app.chat_engine.db.dao.DeviceGroupNoticeDao;
import com.app.chat_engine.db.entity.DeviceGroupEntity;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;


@Database(entities = {DeviceGroupEntity.class, DeviceGroupNoticeEntity.class}, version = 2, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class MyDb extends RoomDatabase {

    private static final String DB_NAME = "chatEngin.db";
    public static final String TAG = "MyDb";

    private static MyDb sInstance;

    public abstract DeviceGroupDao groupDao();
    public abstract DeviceGroupNoticeDao noticeDao();

    public static MyDb getInstance() {
        if (sInstance == null) {
            synchronized (MyDb.class) {
                if (sInstance == null) {
                    sInstance = create(App.getInstance());
                }
            }
        }
        return sInstance;
    }


    private static MyDb create(final Context context) {
        return Room.databaseBuilder(
                context,
                MyDb.class,
                DB_NAME).addMigrations(MIGRATION_1_2).addCallback(new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }
        }).build();
    }


    
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE DeviceGroupEntity ADD COLUMN clusterId TEXT ");
        }
    };



}
