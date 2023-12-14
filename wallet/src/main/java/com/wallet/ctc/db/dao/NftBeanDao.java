package com.wallet.ctc.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.wallet.ctc.db.NftBean;


public class NftBeanDao extends AbstractDao<NftBean, Long> {

    public static final String TABLENAME = "NFT_BEAN";

    
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property WalletAddress = new Property(1, String.class, "walletAddress", false, "WALLET_ADDRESS");
        public final static Property WalletType = new Property(2, int.class, "walletType", false, "WALLET_TYPE");
        public final static Property Token_address = new Property(3, String.class, "token_address", false, "TOKEN_ADDRESS");
        public final static Property Token_id = new Property(4, String.class, "token_id", false, "TOKEN_ID");
        public final static Property Block_number_minted = new Property(5, String.class, "block_number_minted", false, "BLOCK_NUMBER_MINTED");
        public final static Property Owner_of = new Property(6, String.class, "owner_of", false, "OWNER_OF");
        public final static Property Block_number = new Property(7, String.class, "block_number", false, "BLOCK_NUMBER");
        public final static Property Amount = new Property(8, String.class, "amount", false, "AMOUNT");
        public final static Property Contract_type = new Property(9, String.class, "contract_type", false, "CONTRACT_TYPE");
        public final static Property Name = new Property(10, String.class, "name", false, "NAME");
        public final static Property Symbol = new Property(11, String.class, "symbol", false, "SYMBOL");
        public final static Property Token_uri = new Property(12, String.class, "token_uri", false, "TOKEN_URI");
    }


    public NftBeanDao(DaoConfig config) {
        super(config);
    }
    
    public NftBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NFT_BEAN\" (" + 
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + 
                "\"WALLET_ADDRESS\" TEXT," + 
                "\"WALLET_TYPE\" INTEGER NOT NULL ," + 
                "\"TOKEN_ADDRESS\" TEXT," + 
                "\"TOKEN_ID\" TEXT," + 
                "\"BLOCK_NUMBER_MINTED\" TEXT," + 
                "\"OWNER_OF\" TEXT," + 
                "\"BLOCK_NUMBER\" TEXT," + 
                "\"AMOUNT\" TEXT," + 
                "\"CONTRACT_TYPE\" TEXT," + 
                "\"NAME\" TEXT," + 
                "\"SYMBOL\" TEXT," + 
                "\"TOKEN_URI\" TEXT);"); 
    }

    
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NFT_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NftBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String walletAddress = entity.getWalletAddress();
        if (walletAddress != null) {
            stmt.bindString(2, walletAddress);
        }
        stmt.bindLong(3, entity.getWalletType());
 
        String token_address = entity.getToken_address();
        if (token_address != null) {
            stmt.bindString(4, token_address);
        }
 
        String token_id = entity.getToken_id();
        if (token_id != null) {
            stmt.bindString(5, token_id);
        }
 
        String block_number_minted = entity.getBlock_number_minted();
        if (block_number_minted != null) {
            stmt.bindString(6, block_number_minted);
        }
 
        String owner_of = entity.getOwner_of();
        if (owner_of != null) {
            stmt.bindString(7, owner_of);
        }
 
        String block_number = entity.getBlock_number();
        if (block_number != null) {
            stmt.bindString(8, block_number);
        }
 
        String amount = entity.getAmount();
        if (amount != null) {
            stmt.bindString(9, amount);
        }
 
        String contract_type = entity.getContract_type();
        if (contract_type != null) {
            stmt.bindString(10, contract_type);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(11, name);
        }
 
        String symbol = entity.getSymbol();
        if (symbol != null) {
            stmt.bindString(12, symbol);
        }
 
        String token_uri = entity.getToken_uri();
        if (token_uri != null) {
            stmt.bindString(13, token_uri);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NftBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String walletAddress = entity.getWalletAddress();
        if (walletAddress != null) {
            stmt.bindString(2, walletAddress);
        }
        stmt.bindLong(3, entity.getWalletType());
 
        String token_address = entity.getToken_address();
        if (token_address != null) {
            stmt.bindString(4, token_address);
        }
 
        String token_id = entity.getToken_id();
        if (token_id != null) {
            stmt.bindString(5, token_id);
        }
 
        String block_number_minted = entity.getBlock_number_minted();
        if (block_number_minted != null) {
            stmt.bindString(6, block_number_minted);
        }
 
        String owner_of = entity.getOwner_of();
        if (owner_of != null) {
            stmt.bindString(7, owner_of);
        }
 
        String block_number = entity.getBlock_number();
        if (block_number != null) {
            stmt.bindString(8, block_number);
        }
 
        String amount = entity.getAmount();
        if (amount != null) {
            stmt.bindString(9, amount);
        }
 
        String contract_type = entity.getContract_type();
        if (contract_type != null) {
            stmt.bindString(10, contract_type);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(11, name);
        }
 
        String symbol = entity.getSymbol();
        if (symbol != null) {
            stmt.bindString(12, symbol);
        }
 
        String token_uri = entity.getToken_uri();
        if (token_uri != null) {
            stmt.bindString(13, token_uri);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NftBean readEntity(Cursor cursor, int offset) {
        NftBean entity = new NftBean( 
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), 
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), 
            cursor.getInt(offset + 2), 
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), 
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), 
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), 
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), 
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), 
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), 
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), 
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), 
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), 
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) 
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NftBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setWalletAddress(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setWalletType(cursor.getInt(offset + 2));
        entity.setToken_address(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setToken_id(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBlock_number_minted(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOwner_of(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBlock_number(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAmount(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setContract_type(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setSymbol(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setToken_uri(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NftBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NftBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NftBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
