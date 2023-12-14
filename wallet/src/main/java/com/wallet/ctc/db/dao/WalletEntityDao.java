package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.WalletEntity;


public class WalletEntityDao extends AbstractDao<WalletEntity, Long> {

    public static final String TABLENAME = "WALLET_ENTITY";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property MMnemonic = new Property(1, byte[].class, "mMnemonic", false, "M_MNEMONIC");
        public final static Property MKeystore = new Property(2, byte[].class, "mKeystore", false, "M_KEYSTORE");
        public final static Property MPrivateKey = new Property(3, byte[].class, "mPrivateKey", false, "M_PRIVATE_KEY");
        public final static Property MPublicKey = new Property(4, byte[].class, "mPublicKey", false, "M_PUBLIC_KEY");
        public final static Property MAddress = new Property(5, String.class, "mAddress", false, "M_ADDRESS");
        public final static Property MAddress2 = new Property(6, String.class, "mAddress2", false, "M_ADDRESS2");
        public final static Property ChatAddress = new Property(7, String.class, "chatAddress", false, "CHAT_ADDRESS");
        public final static Property ChatEthAddress = new Property(8, String.class, "chatEthAddress", false, "CHAT_ETH_ADDRESS");
        public final static Property ChatPrivateKey = new Property(9, String.class, "chatPrivateKey", false, "CHAT_PRIVATE_KEY");
        public final static Property Name = new Property(10, String.class, "Name", false, "NAME");
        public final static Property Logo = new Property(11, int.class, "Logo", false, "LOGO");
        public final static Property MPassword = new Property(12, String.class, "mPassword", false, "M_PASSWORD");
        public final static Property MPasswordHint = new Property(13, String.class, "mPasswordHint", false, "M_PASSWORD_HINT");
        public final static Property MCurrency = new Property(14, String.class, "mCurrency", false, "M_CURRENCY");
        public final static Property MQuotes = new Property(15, String.class, "mQuotes", false, "M_QUOTES");
        public final static Property MBalance = new Property(16, String.class, "mBalance", false, "M_BALANCE");
        public final static Property MPrice = new Property(17, String.class, "mPrice", false, "M_PRICE");
        public final static Property SumPrice = new Property(18, String.class, "sumPrice", false, "SUM_PRICE");
        public final static Property UserName = new Property(19, String.class, "userName", false, "USER_NAME");
        public final static Property Level = new Property(20, int.class, "Level", false, "LEVEL");
        public final static Property WalletId = new Property(21, String.class, "walletId", false, "WALLET_ID");
        public final static Property Type = new Property(22, int.class, "type", false, "TYPE");
        public final static Property MMnemonicBackup = new Property(23, int.class, "mMnemonicBackup", false, "M_MNEMONIC_BACKUP");
        public final static Property MBackup = new Property(24, int.class, "mBackup", false, "M_BACKUP");
        public final static Property Defwallet = new Property(25, Integer.class, "defwallet", false, "DEFWALLET");
    }


    public WalletEntityDao(DaoConfig config) {
        super(config);
    }
    
    public WalletEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WALLET_ENTITY\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"M_MNEMONIC\" BLOB," + 
                "\"M_KEYSTORE\" BLOB," + 
                "\"M_PRIVATE_KEY\" BLOB," + 
                "\"M_PUBLIC_KEY\" BLOB," + 
                "\"M_ADDRESS\" TEXT," + 
                "\"M_ADDRESS2\" TEXT," + 
                "\"CHAT_ADDRESS\" TEXT," + 
                "\"CHAT_ETH_ADDRESS\" TEXT," + 
                "\"CHAT_PRIVATE_KEY\" TEXT," + 
                "\"NAME\" TEXT," + 
                "\"LOGO\" INTEGER NOT NULL ," + 
                "\"M_PASSWORD\" TEXT," + 
                "\"M_PASSWORD_HINT\" TEXT," + 
                "\"M_CURRENCY\" TEXT," + 
                "\"M_QUOTES\" TEXT," + 
                "\"M_BALANCE\" TEXT," + 
                "\"M_PRICE\" TEXT," + 
                "\"SUM_PRICE\" TEXT," + 
                "\"USER_NAME\" TEXT," + 
                "\"LEVEL\" INTEGER NOT NULL ," + 
                "\"WALLET_ID\" TEXT," + 
                "\"TYPE\" INTEGER NOT NULL ," + 
                "\"M_MNEMONIC_BACKUP\" INTEGER NOT NULL ," + 
                "\"M_BACKUP\" INTEGER NOT NULL ," + 
                "\"DEFWALLET\" INTEGER);"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WALLET_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, WalletEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        byte[] mMnemonic = entity.getMMnemonic();
        if (mMnemonic != null) {
            stmt.bindBlob(2, mMnemonic);
        }
 
        byte[] mKeystore = entity.getMKeystore();
        if (mKeystore != null) {
            stmt.bindBlob(3, mKeystore);
        }
 
        byte[] mPrivateKey = entity.getMPrivateKey();
        if (mPrivateKey != null) {
            stmt.bindBlob(4, mPrivateKey);
        }
 
        byte[] mPublicKey = entity.getMPublicKey();
        if (mPublicKey != null) {
            stmt.bindBlob(5, mPublicKey);
        }
 
        String mAddress = entity.getMAddress();
        if (mAddress != null) {
            stmt.bindString(6, mAddress);
        }
 
        String mAddress2 = entity.getMAddress2();
        if (mAddress2 != null) {
            stmt.bindString(7, mAddress2);
        }
 
        String chatAddress = entity.getChatAddress();
        if (chatAddress != null) {
            stmt.bindString(8, chatAddress);
        }
 
        String chatEthAddress = entity.getChatEthAddress();
        if (chatEthAddress != null) {
            stmt.bindString(9, chatEthAddress);
        }
 
        String chatPrivateKey = entity.getChatPrivateKey();
        if (chatPrivateKey != null) {
            stmt.bindString(10, chatPrivateKey);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(11, Name);
        }
        stmt.bindLong(12, entity.getLogo());
 
        String mPassword = entity.getMPassword();
        if (mPassword != null) {
            stmt.bindString(13, mPassword);
        }
 
        String mPasswordHint = entity.getMPasswordHint();
        if (mPasswordHint != null) {
            stmt.bindString(14, mPasswordHint);
        }
 
        String mCurrency = entity.getMCurrency();
        if (mCurrency != null) {
            stmt.bindString(15, mCurrency);
        }
 
        String mQuotes = entity.getMQuotes();
        if (mQuotes != null) {
            stmt.bindString(16, mQuotes);
        }
 
        String mBalance = entity.getMBalance();
        if (mBalance != null) {
            stmt.bindString(17, mBalance);
        }
 
        String mPrice = entity.getMPrice();
        if (mPrice != null) {
            stmt.bindString(18, mPrice);
        }
 
        String sumPrice = entity.getSumPrice();
        if (sumPrice != null) {
            stmt.bindString(19, sumPrice);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(20, userName);
        }
        stmt.bindLong(21, entity.getLevel());
 
        String walletId = entity.getWalletId();
        if (walletId != null) {
            stmt.bindString(22, walletId);
        }
        stmt.bindLong(23, entity.getType());
        stmt.bindLong(24, entity.getMMnemonicBackup());
        stmt.bindLong(25, entity.getMBackup());
 
        Integer defwallet = entity.getDefwallet();
        if (defwallet != null) {
            stmt.bindLong(26, defwallet);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, WalletEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        byte[] mMnemonic = entity.getMMnemonic();
        if (mMnemonic != null) {
            stmt.bindBlob(2, mMnemonic);
        }
 
        byte[] mKeystore = entity.getMKeystore();
        if (mKeystore != null) {
            stmt.bindBlob(3, mKeystore);
        }
 
        byte[] mPrivateKey = entity.getMPrivateKey();
        if (mPrivateKey != null) {
            stmt.bindBlob(4, mPrivateKey);
        }
 
        byte[] mPublicKey = entity.getMPublicKey();
        if (mPublicKey != null) {
            stmt.bindBlob(5, mPublicKey);
        }
 
        String mAddress = entity.getMAddress();
        if (mAddress != null) {
            stmt.bindString(6, mAddress);
        }
 
        String mAddress2 = entity.getMAddress2();
        if (mAddress2 != null) {
            stmt.bindString(7, mAddress2);
        }
 
        String chatAddress = entity.getChatAddress();
        if (chatAddress != null) {
            stmt.bindString(8, chatAddress);
        }
 
        String chatEthAddress = entity.getChatEthAddress();
        if (chatEthAddress != null) {
            stmt.bindString(9, chatEthAddress);
        }
 
        String chatPrivateKey = entity.getChatPrivateKey();
        if (chatPrivateKey != null) {
            stmt.bindString(10, chatPrivateKey);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(11, Name);
        }
        stmt.bindLong(12, entity.getLogo());
 
        String mPassword = entity.getMPassword();
        if (mPassword != null) {
            stmt.bindString(13, mPassword);
        }
 
        String mPasswordHint = entity.getMPasswordHint();
        if (mPasswordHint != null) {
            stmt.bindString(14, mPasswordHint);
        }
 
        String mCurrency = entity.getMCurrency();
        if (mCurrency != null) {
            stmt.bindString(15, mCurrency);
        }
 
        String mQuotes = entity.getMQuotes();
        if (mQuotes != null) {
            stmt.bindString(16, mQuotes);
        }
 
        String mBalance = entity.getMBalance();
        if (mBalance != null) {
            stmt.bindString(17, mBalance);
        }
 
        String mPrice = entity.getMPrice();
        if (mPrice != null) {
            stmt.bindString(18, mPrice);
        }
 
        String sumPrice = entity.getSumPrice();
        if (sumPrice != null) {
            stmt.bindString(19, sumPrice);
        }
 
        String userName = entity.getUserName();
        if (userName != null) {
            stmt.bindString(20, userName);
        }
        stmt.bindLong(21, entity.getLevel());
 
        String walletId = entity.getWalletId();
        if (walletId != null) {
            stmt.bindString(22, walletId);
        }
        stmt.bindLong(23, entity.getType());
        stmt.bindLong(24, entity.getMMnemonicBackup());
        stmt.bindLong(25, entity.getMBackup());
 
        Integer defwallet = entity.getDefwallet();
        if (defwallet != null) {
            stmt.bindLong(26, defwallet);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public WalletEntity readEntity(Cursor cursor, int offset) {
        WalletEntity entity = new WalletEntity( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1), 
            cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2), 
            cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3), 
            cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4), 
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), 
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), 
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), 
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), 
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), 
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), 
            cursor.getInt(offset + 11), 
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), 
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), 
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), 
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), 
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), 
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), 
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), 
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), 
            cursor.getInt(offset + 20), 
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), 
            cursor.getInt(offset + 22), 
            cursor.getInt(offset + 23), 
            cursor.getInt(offset + 24), 
            cursor.isNull(offset + 25) ? null : cursor.getInt(offset + 25) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, WalletEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMMnemonic(cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1));
        entity.setMKeystore(cursor.isNull(offset + 2) ? null : cursor.getBlob(offset + 2));
        entity.setMPrivateKey(cursor.isNull(offset + 3) ? null : cursor.getBlob(offset + 3));
        entity.setMPublicKey(cursor.isNull(offset + 4) ? null : cursor.getBlob(offset + 4));
        entity.setMAddress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setMAddress2(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setChatAddress(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setChatEthAddress(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setChatPrivateKey(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setLogo(cursor.getInt(offset + 11));
        entity.setMPassword(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setMPasswordHint(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setMCurrency(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setMQuotes(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setMBalance(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setMPrice(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setSumPrice(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setUserName(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setLevel(cursor.getInt(offset + 20));
        entity.setWalletId(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setType(cursor.getInt(offset + 22));
        entity.setMMnemonicBackup(cursor.getInt(offset + 23));
        entity.setMBackup(cursor.getInt(offset + 24));
        entity.setDefwallet(cursor.isNull(offset + 25) ? null : cursor.getInt(offset + 25));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(WalletEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(WalletEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(WalletEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
