package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.AddressBookEntity;


public class AddressBookEntityDao extends AbstractDao<AddressBookEntity, Long> {

    public static final String TABLENAME = "ADDRESS_BOOK_ENTITY";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Logo = new Property(1, int.class, "logo", false, "LOGO");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Address = new Property(3, String.class, "address", false, "ADDRESS");
        public final static Property Remark = new Property(4, String.class, "remark", false, "REMARK");
    }


    public AddressBookEntityDao(DaoConfig config) {
        super(config);
    }
    
    public AddressBookEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ADDRESS_BOOK_ENTITY\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"LOGO\" INTEGER NOT NULL ," + 
                "\"NAME\" TEXT," + 
                "\"ADDRESS\" TEXT," + 
                "\"REMARK\" TEXT);"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ADDRESS_BOOK_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AddressBookEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getLogo());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(5, remark);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AddressBookEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getLogo());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String address = entity.getAddress();
        if (address != null) {
            stmt.bindString(4, address);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(5, remark);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AddressBookEntity readEntity(Cursor cursor, int offset) {
        AddressBookEntity entity = new AddressBookEntity( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.getInt(offset + 1), 
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), 
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), 
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AddressBookEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLogo(cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAddress(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRemark(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AddressBookEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AddressBookEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(AddressBookEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
