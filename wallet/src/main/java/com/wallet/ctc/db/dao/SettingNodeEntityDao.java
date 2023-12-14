package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.SettingNodeEntity;


public class SettingNodeEntityDao extends AbstractDao<SettingNodeEntity, Long> {

    public static final String TABLENAME = "SETTING_NODE_ENTITY";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NodeName = new Property(1, String.class, "nodeName", false, "NODE_NAME");
        public final static Property NodeUrl = new Property(2, String.class, "nodeUrl", false, "NODE_URL");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property Choose = new Property(4, boolean.class, "choose", false, "CHOOSE");
        public final static Property IsDef = new Property(5, int.class, "isDef", false, "IS_DEF");
        public final static Property MainPhoneIndex = new Property(6, String.class, "mainPhoneIndex", false, "MAIN_PHONE_INDEX");
        public final static Property TokenNum = new Property(7, String.class, "tokenNum", false, "TOKEN_NUM");
        public final static Property OnLineTime = new Property(8, long.class, "onLineTime", false, "ON_LINE_TIME");
        public final static Property GateWayAddr = new Property(9, String.class, "gateWayAddr", false, "GATE_WAY_ADDR");
    }


    public SettingNodeEntityDao(DaoConfig config) {
        super(config);
    }
    
    public SettingNodeEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SETTING_NODE_ENTITY\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"NODE_NAME\" TEXT," + 
                "\"NODE_URL\" TEXT," + 
                "\"TYPE\" INTEGER NOT NULL ," + 
                "\"CHOOSE\" INTEGER NOT NULL ," + 
                "\"IS_DEF\" INTEGER NOT NULL ," + 
                "\"MAIN_PHONE_INDEX\" TEXT," + 
                "\"TOKEN_NUM\" TEXT," + 
                "\"ON_LINE_TIME\" INTEGER NOT NULL ," + 
                "\"GATE_WAY_ADDR\" TEXT);"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SETTING_NODE_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SettingNodeEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String nodeName = entity.getNodeName();
        if (nodeName != null) {
            stmt.bindString(2, nodeName);
        }
 
        String nodeUrl = entity.getNodeUrl();
        if (nodeUrl != null) {
            stmt.bindString(3, nodeUrl);
        }
        stmt.bindLong(4, entity.getType());
        stmt.bindLong(5, entity.getChoose() ? 1L: 0L);
        stmt.bindLong(6, entity.getIsDef());
 
        String mainPhoneIndex = entity.getMainPhoneIndex();
        if (mainPhoneIndex != null) {
            stmt.bindString(7, mainPhoneIndex);
        }
 
        String tokenNum = entity.getTokenNum();
        if (tokenNum != null) {
            stmt.bindString(8, tokenNum);
        }
        stmt.bindLong(9, entity.getOnLineTime());
 
        String gateWayAddr = entity.getGateWayAddr();
        if (gateWayAddr != null) {
            stmt.bindString(10, gateWayAddr);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SettingNodeEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String nodeName = entity.getNodeName();
        if (nodeName != null) {
            stmt.bindString(2, nodeName);
        }
 
        String nodeUrl = entity.getNodeUrl();
        if (nodeUrl != null) {
            stmt.bindString(3, nodeUrl);
        }
        stmt.bindLong(4, entity.getType());
        stmt.bindLong(5, entity.getChoose() ? 1L: 0L);
        stmt.bindLong(6, entity.getIsDef());
 
        String mainPhoneIndex = entity.getMainPhoneIndex();
        if (mainPhoneIndex != null) {
            stmt.bindString(7, mainPhoneIndex);
        }
 
        String tokenNum = entity.getTokenNum();
        if (tokenNum != null) {
            stmt.bindString(8, tokenNum);
        }
        stmt.bindLong(9, entity.getOnLineTime());
 
        String gateWayAddr = entity.getGateWayAddr();
        if (gateWayAddr != null) {
            stmt.bindString(10, gateWayAddr);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SettingNodeEntity readEntity(Cursor cursor, int offset) {
        SettingNodeEntity entity = new SettingNodeEntity( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), 
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), 
            cursor.getInt(offset + 3), 
            cursor.getShort(offset + 4) != 0, 
            cursor.getInt(offset + 5), 
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), 
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), 
            cursor.getLong(offset + 8), 
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SettingNodeEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNodeName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNodeUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setChoose(cursor.getShort(offset + 4) != 0);
        entity.setIsDef(cursor.getInt(offset + 5));
        entity.setMainPhoneIndex(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTokenNum(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setOnLineTime(cursor.getLong(offset + 8));
        entity.setGateWayAddr(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SettingNodeEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SettingNodeEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SettingNodeEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
