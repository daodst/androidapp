package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.AssertEntity;


public class AssertEntityDao extends AbstractDao<AssertEntity, Long> {

    public static final String TABLENAME = "ASSERT_ENTITY";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Logo = new Property(1, int.class, "logo", false, "LOGO");
        public final static Property Img_path = new Property(2, String.class, "img_path", false, "IMG_PATH");
        public final static Property Short_name = new Property(3, String.class, "short_name", false, "SHORT_NAME");
        public final static Property Full_name = new Property(4, String.class, "full_name", false, "FULL_NAME");
        public final static Property Contract = new Property(5, String.class, "contract", false, "CONTRACT");
        public final static Property Gas = new Property(6, String.class, "gas", false, "GAS");
        public final static Property Decimal = new Property(7, String.class, "decimal", false, "DECIMAL");
        public final static Property AssertsNum = new Property(8, String.class, "assertsNum", false, "ASSERTS_NUM");
        public final static Property AssertsSumPrice = new Property(9, String.class, "assertsSumPrice", false, "ASSERTS_SUM_PRICE");
        public final static Property WalletAddress = new Property(10, String.class, "walletAddress", false, "WALLET_ADDRESS");
        public final static Property UserName = new Property(11, String.class, "userName", false, "USER_NAME");
        public final static Property Type = new Property(12, int.class, "type", false, "TYPE");
        public final static Property Level = new Property(13, int.class, "level", false, "LEVEL");
        public final static Property Creator = new Property(14, String.class, "creator", false, "CREATOR");
        public final static Property Desc = new Property(15, String.class, "desc", false, "DESC");
        public final static Property Total = new Property(16, String.class, "total", false, "TOTAL");
        public final static Property Url = new Property(17, String.class, "url", false, "URL");
        public final static Property Award = new Property(18, String.class, "award", false, "AWARD");
        public final static Property Mineral = new Property(19, String.class, "mineral", false, "MINERAL");
    }


    public AssertEntityDao(DaoConfig config) {
        super(config);
    }
    
    public AssertEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ASSERT_ENTITY\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"LOGO\" INTEGER NOT NULL ," + 
                "\"IMG_PATH\" TEXT," + 
                "\"SHORT_NAME\" TEXT," + 
                "\"FULL_NAME\" TEXT," + 
                "\"CONTRACT\" TEXT," + 
                "\"GAS\" TEXT," + 
                "\"DECIMAL\" TEXT," + 
                "\"ASSERTS_NUM\" TEXT," + 
                "\"ASSERTS_SUM_PRICE\" TEXT," + 
                "\"WALLET_ADDRESS\" TEXT," + 
                "\"USER_NAME\" TEXT," + 
                "\"TYPE\" INTEGER NOT NULL ," + 
                "\"LEVEL\" INTEGER NOT NULL ," + 
                "\"CREATOR\" TEXT," + 
                "\"DESC\" TEXT," + 
                "\"TOTAL\" TEXT," + 
                "\"URL\" TEXT," + 
                "\"AWARD\" TEXT," + 
                "\"MINERAL\" TEXT);"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ASSERT_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AssertEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getLogo());
 
        String img_path = entity.getImg_path();
        if (img_path != null) {
            stmt.bindString(3, img_path);
        }
 
        String short_name = entity.getShort_name();
        if (short_name != null) {
            stmt.bindString(4, short_name);
        }
 
        String full_name = entity.getFull_name();
        if (full_name != null) {
            stmt.bindString(5, full_name);
        }
 
        String contract = entity.getContract();
        if (contract != null) {
            stmt.bindString(6, contract);
        }
 
        String gas = entity.getGas();
        if (gas != null) {
            stmt.bindString(7, gas);
        }
 
        String decimal = entity.getDecimal();
        if (decimal != null) {
            stmt.bindString(8, decimal);
        }
 
        String assertsNum = entity.getAssertsNum();
        if (assertsNum != null) {
            stmt.bindString(9, assertsNum);
        }
 
        String assertsSumPrice = entity.getAssertsSumPrice();
        if (assertsSumPrice != null) {
            stmt.bindString(10, assertsSumPrice);
        }
 
        String walletAddress = entity.getWalletAddress();
        if (walletAddress != null) {
            stmt.bindString(11, walletAddress);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(12, userName);
        }
        stmt.bindLong(13, entity.getType());
        stmt.bindLong(14, entity.getLevel());
 
        String creator = entity.getCreator();
        if (creator != null) {
            stmt.bindString(15, creator);
        }
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(16, desc);
        }
 
        String total = entity.getTotal();
        if (total != null) {
            stmt.bindString(17, total);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(18, url);
        }
 
        String award = entity.getAward();
        if (award != null) {
            stmt.bindString(19, award);
        }
 
        String mineral = entity.getMineral();
        if (mineral != null) {
            stmt.bindString(20, mineral);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AssertEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getLogo());
 
        String img_path = entity.getImg_path();
        if (img_path != null) {
            stmt.bindString(3, img_path);
        }
 
        String short_name = entity.getShort_name();
        if (short_name != null) {
            stmt.bindString(4, short_name);
        }
 
        String full_name = entity.getFull_name();
        if (full_name != null) {
            stmt.bindString(5, full_name);
        }
 
        String contract = entity.getContract();
        if (contract != null) {
            stmt.bindString(6, contract);
        }
 
        String gas = entity.getGas();
        if (gas != null) {
            stmt.bindString(7, gas);
        }
 
        String decimal = entity.getDecimal();
        if (decimal != null) {
            stmt.bindString(8, decimal);
        }
 
        String assertsNum = entity.getAssertsNum();
        if (assertsNum != null) {
            stmt.bindString(9, assertsNum);
        }
 
        String assertsSumPrice = entity.getAssertsSumPrice();
        if (assertsSumPrice != null) {
            stmt.bindString(10, assertsSumPrice);
        }
 
        String walletAddress = entity.getWalletAddress();
        if (walletAddress != null) {
            stmt.bindString(11, walletAddress);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(12, userName);
        }
        stmt.bindLong(13, entity.getType());
        stmt.bindLong(14, entity.getLevel());
 
        String creator = entity.getCreator();
        if (creator != null) {
            stmt.bindString(15, creator);
        }
 
        String desc = entity.getDesc();
        if (desc != null) {
            stmt.bindString(16, desc);
        }
 
        String total = entity.getTotal();
        if (total != null) {
            stmt.bindString(17, total);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(18, url);
        }
 
        String award = entity.getAward();
        if (award != null) {
            stmt.bindString(19, award);
        }
 
        String mineral = entity.getMineral();
        if (mineral != null) {
            stmt.bindString(20, mineral);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AssertEntity readEntity(Cursor cursor, int offset) {
        AssertEntity entity = new AssertEntity( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.getInt(offset + 1), 
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), 
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), 
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), 
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), 
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), 
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), 
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), 
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), 
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), 
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), 
            cursor.getInt(offset + 12), 
            cursor.getInt(offset + 13), 
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), 
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), 
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), 
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), 
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), 
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AssertEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLogo(cursor.getInt(offset + 1));
        entity.setImg_path(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setShort_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setFull_name(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setContract(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setGas(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setDecimal(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAssertsNum(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setAssertsSumPrice(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setWalletAddress(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setUserName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setType(cursor.getInt(offset + 12));
        entity.setLevel(cursor.getInt(offset + 13));
        entity.setCreator(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setDesc(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setTotal(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setUrl(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setAward(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setMineral(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AssertEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AssertEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AssertEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
