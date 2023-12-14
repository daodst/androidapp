package com.wallet.ctc.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;



public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 24;

    
    public static void createAllTables(Database db, boolean ifNotExists) {
        AddressBookEntityDao.createTable(db, ifNotExists);
        AssertEntityDao.createTable(db, ifNotExists);
        CreatEthEntityDao.createTable(db, ifNotExists);
        DappHistoryEntityDao.createTable(db, ifNotExists);
        NftBeanDao.createTable(db, ifNotExists);
        SettingNodeEntityDao.createTable(db, ifNotExists);
        WalletEntityDao.createTable(db, ifNotExists);
    }

    
    public static void dropAllTables(Database db, boolean ifExists) {
        AddressBookEntityDao.dropTable(db, ifExists);
        AssertEntityDao.dropTable(db, ifExists);
        CreatEthEntityDao.dropTable(db, ifExists);
        DappHistoryEntityDao.dropTable(db, ifExists);
        NftBeanDao.dropTable(db, ifExists);
        SettingNodeEntityDao.dropTable(db, ifExists);
        WalletEntityDao.dropTable(db, ifExists);
    }

    
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(AddressBookEntityDao.class);
        registerDaoClass(AssertEntityDao.class);
        registerDaoClass(CreatEthEntityDao.class);
        registerDaoClass(DappHistoryEntityDao.class);
        registerDaoClass(NftBeanDao.class);
        registerDaoClass(SettingNodeEntityDao.class);
        registerDaoClass(WalletEntityDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
