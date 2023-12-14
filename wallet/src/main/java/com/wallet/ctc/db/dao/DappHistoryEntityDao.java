package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.DappHistoryEntity;


public class DappHistoryEntityDao extends AbstractDao<DappHistoryEntity, Long> {

    public static final String TABLENAME = "DAPP_HISTORY_ENTITY";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property IconPath = new Property(1, String.class, "iconPath", false, "ICON_PATH");
        public final static Property Url = new Property(2, String.class, "url", false, "URL");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Time = new Property(4, long.class, "time", false, "TIME");
        public final static Property IsLike = new Property(5, int.class, "isLike", false, "IS_LIKE");
        public final static Property Params = new Property(6, String.class, "params", false, "PARAMS");
        public final static Property Params2 = new Property(7, String.class, "params2", false, "PARAMS2");
        public final static Property Params3 = new Property(8, String.class, "params3", false, "PARAMS3");
    }


    public DappHistoryEntityDao(DaoConfig config) {
        super(config);
    }
    
    public DappHistoryEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DAPP_HISTORY_ENTITY\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"ICON_PATH\" TEXT," + 
                "\"URL\" TEXT," + 
                "\"TITLE\" TEXT," + 
                "\"TIME\" INTEGER NOT NULL ," + 
                "\"IS_LIKE\" INTEGER NOT NULL ," + 
                "\"PARAMS\" TEXT," + 
                "\"PARAMS2\" TEXT," + 
                "\"PARAMS3\" TEXT);"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DAPP_HISTORY_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DappHistoryEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String iconPath = entity.getIconPath();
        if (iconPath != null) {
            stmt.bindString(2, iconPath);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(3, url);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
        stmt.bindLong(5, entity.getTime());
        stmt.bindLong(6, entity.getIsLike());
 
        String params = entity.getParams();
        if (params != null) {
            stmt.bindString(7, params);
        }
 
        String params2 = entity.getParams2();
        if (params2 != null) {
            stmt.bindString(8, params2);
        }
 
        String params3 = entity.getParams3();
        if (params3 != null) {
            stmt.bindString(9, params3);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DappHistoryEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String iconPath = entity.getIconPath();
        if (iconPath != null) {
            stmt.bindString(2, iconPath);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(3, url);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
        stmt.bindLong(5, entity.getTime());
        stmt.bindLong(6, entity.getIsLike());
 
        String params = entity.getParams();
        if (params != null) {
            stmt.bindString(7, params);
        }
 
        String params2 = entity.getParams2();
        if (params2 != null) {
            stmt.bindString(8, params2);
        }
 
        String params3 = entity.getParams3();
        if (params3 != null) {
            stmt.bindString(9, params3);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DappHistoryEntity readEntity(Cursor cursor, int offset) {
        DappHistoryEntity entity = new DappHistoryEntity( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), 
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), 
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), 
            cursor.getLong(offset + 4), 
            cursor.getInt(offset + 5), 
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), 
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), 
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DappHistoryEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setIconPath(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTime(cursor.getLong(offset + 4));
        entity.setIsLike(cursor.getInt(offset + 5));
        entity.setParams(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setParams2(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setParams3(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DappHistoryEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DappHistoryEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DappHistoryEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
