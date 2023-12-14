package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.CreatEthEntity;


public class CreatEthEntityDao extends AbstractDao<CreatEthEntity, Long> {

    public static final String TABLENAME = "CREAT_ETH_ENTITY";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Img_path = new Property(1, String.class, "img_path", false, "IMG_PATH");
        public final static Property Short_name = new Property(2, String.class, "short_name", false, "SHORT_NAME");
        public final static Property Full_name = new Property(3, String.class, "full_name", false, "FULL_NAME");
        public final static Property Decimal = new Property(4, String.class, "decimal", false, "DECIMAL");
        public final static Property WalletAddress = new Property(5, String.class, "walletAddress", false, "WALLET_ADDRESS");
        public final static Property Address = new Property(6, String.class, "address", false, "ADDRESS");
        public final static Property UserName = new Property(7, String.class, "userName", false, "USER_NAME");
        public final static Property AssestNum = new Property(8, String.class, "assestNum", false, "ASSEST_NUM");
        public final static Property HexValue = new Property(9, String.class, "hexValue", false, "HEX_VALUE");
        public final static Property Type = new Property(10, int.class, "type", false, "TYPE");
        public final static Property Statu = new Property(11, int.class, "statu", false, "STATU");
        public final static Property CreatTime = new Property(12, int.class, "creatTime", false, "CREAT_TIME");
    }


    public CreatEthEntityDao(DaoConfig config) {
        super(config);
    }
    
    public CreatEthEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CREAT_ETH_ENTITY\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"IMG_PATH\" TEXT," + 
                "\"SHORT_NAME\" TEXT," + 
                "\"FULL_NAME\" TEXT," + 
                "\"DECIMAL\" TEXT," + 
                "\"WALLET_ADDRESS\" TEXT," + 
                "\"ADDRESS\" TEXT," + 
                "\"USER_NAME\" TEXT," + 
                "\"ASSEST_NUM\" TEXT," + 
                "\"HEX_VALUE\" TEXT," + 
                "\"TYPE\" INTEGER NOT NULL ," + 
                "\"STATU\" INTEGER NOT NULL ," + 
                "\"CREAT_TIME\" INTEGER NOT NULL );"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CREAT_ETH_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CreatEthEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String img_path = entity.getImg_path();
        if (img_path != null) {
            stmt.bindString(2, img_path);
        }
 
        String short_name = entity.getShort_name();
        if (short_name != null) {
            stmt.bindString(3, short_name);
        }
 
        String full_name = entity.getFull_name();
        if (full_name != null) {
            stmt.bindString(4, full_name);
        }
 
        String decimal = entity.getDecimal();
        if (decimal != null) {
            stmt.bindString(5, decimal);
        }
 
        String walletAddress = entity.getWalletAddress();
        if (walletAddress != null) {
            stmt.bindString(6, walletAddress);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(7, address);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(8, userName);
        }
 
        String assestNum = entity.getAssestNum();
        if (assestNum != null) {
            stmt.bindString(9, assestNum);
        }
 
        String hexValue = entity.getHexValue();
        if (hexValue != null) {
            stmt.bindString(10, hexValue);
        }
        stmt.bindLong(11, entity.getType());
        stmt.bindLong(12, entity.getStatu());
        stmt.bindLong(13, entity.getCreatTime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CreatEthEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String img_path = entity.getImg_path();
        if (img_path != null) {
            stmt.bindString(2, img_path);
        }
 
        String short_name = entity.getShort_name();
        if (short_name != null) {
            stmt.bindString(3, short_name);
        }
 
        String full_name = entity.getFull_name();
        if (full_name != null) {
            stmt.bindString(4, full_name);
        }
 
        String decimal = entity.getDecimal();
        if (decimal != null) {
            stmt.bindString(5, decimal);
        }
 
        String walletAddress = entity.getWalletAddress();
        if (walletAddress != null) {
            stmt.bindString(6, walletAddress);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(7, address);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(8, userName);
        }
 
        String assestNum = entity.getAssestNum();
        if (assestNum != null) {
            stmt.bindString(9, assestNum);
        }
 
        String hexValue = entity.getHexValue();
        if (hexValue != null) {
            stmt.bindString(10, hexValue);
        }
        stmt.bindLong(11, entity.getType());
        stmt.bindLong(12, entity.getStatu());
        stmt.bindLong(13, entity.getCreatTime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CreatEthEntity readEntity(Cursor cursor, int offset) {
        CreatEthEntity entity = new CreatEthEntity( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), 
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), 
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), 
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), 
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), 
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), 
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), 
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), 
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), 
            cursor.getInt(offset + 10), 
            cursor.getInt(offset + 11), 
            cursor.getInt(offset + 12) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CreatEthEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setImg_path(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setShort_name(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setFull_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDecimal(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setWalletAddress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAddress(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUserName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAssestNum(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setHexValue(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setType(cursor.getInt(offset + 10));
        entity.setStatu(cursor.getInt(offset + 11));
        entity.setCreatTime(cursor.getInt(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CreatEthEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CreatEthEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CreatEthEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
