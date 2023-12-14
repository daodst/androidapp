

package com.wallet.ctc.db;

import static com.wallet.ctc.crypto.WalletDBUtil.USER_ID;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.wallet.ctc.db.dao.AddressBookEntityDao;
import com.wallet.ctc.db.dao.AssertEntityDao;
import com.wallet.ctc.db.dao.CreatEthEntityDao;
import com.wallet.ctc.db.dao.DaoMaster;
import com.wallet.ctc.db.dao.DaoSession;
import com.wallet.ctc.db.dao.DappHistoryEntityDao;
import com.wallet.ctc.db.dao.NftBeanDao;
import com.wallet.ctc.db.dao.SettingNodeEntityDao;
import com.wallet.ctc.db.dao.WalletEntityDao;
import com.wallet.ctc.util.LogUtil;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.WebSiteUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



public class DBManager {
    private final static String dbName = "greendao-unittest-db.temp";
    private static DBManager mInstance;
    private static DaoMaster.DevOpenHelper openHelper;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private Context context;
    
    public static final boolean ENCRYPTED = true;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new MyOpenHelper(context, dbName, null);
    }

    
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    
    public static DaoMaster getDaoMaster(Context context) {
        if (null == mDaoMaster) {
            synchronized (DBManager.class) {
                if (null == mDaoMaster) {
                    MyOpenHelper helper = new MyOpenHelper(context, dbName, null);
                    mDaoMaster = new DaoMaster(helper.getWritableDatabase());
                }
            }
        }
        return mDaoMaster;
    }

    
    public static DaoSession getDaoSession(Context context) {
        if (null == mDaoSession) {
            synchronized (DBManager.class) {
                mDaoSession = getDaoMaster(context).newSession();
            }
        }

        return mDaoSession;
    }

    
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }

    
    public void insertWallet(WalletEntity user) {
        user.setUserName(USER_ID + "");
        if(user.Logo>100){
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.insert(user);
    }

    public void insertAssert(AssertEntity user) {
        user.setUserName(USER_ID);
        LogUtil.d(""+user.toString());
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        userDao.insert(user);
    }

    public void insertAddress(AddressBookEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookEntityDao userDao = daoSession.getAddressBookEntityDao();
        userDao.insert(user);
    }

    public void insertCreatHistory(CreatEthEntity user) {
        user.setUserName(USER_ID + "");
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CreatEthEntityDao userDao = daoSession.getCreatEthEntityDao();
        userDao.insert(user);
    }

    
    public void insertWalletList(List<WalletEntity> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.insertInTx(users);
    }


    
    public void updateWallet(WalletEntity user) {
        if(user.Logo>100){
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.update(user);
    }

    public void updateAssert(AssertEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        userDao.update(user);
    }

    public void updateCreatHistory(CreatEthEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CreatEthEntityDao userDao = daoSession.getCreatEthEntityDao();
        userDao.update(user);
    }

    public void updateAddress(AddressBookEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookEntityDao userDao = daoSession.getAddressBookEntityDao();
        userDao.update(user);
    }

    
    public void updateAssertList(List<AssertEntity> data) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        userDao.updateInTx(data);
    }

    public List<AddressBookEntity> queryAdressBook() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookEntityDao userDao = daoSession.getAddressBookEntityDao();
        QueryBuilder<AddressBookEntity> qb = userDao.queryBuilder();
        List<AddressBookEntity> list = qb.list();
        return list;
    }

    public List<AddressBookEntity> queryAdressBookByAddress(String address) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookEntityDao userDao = daoSession.getAddressBookEntityDao();
        QueryBuilder<AddressBookEntity> qb = userDao.queryBuilder();
        qb.where(AddressBookEntityDao.Properties.Address.like("%" + address));
        List<AddressBookEntity> list = qb.list();
        return list;
    }

    public void deleteAddressBook(AddressBookEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AddressBookEntityDao userDao = daoSession.getAddressBookEntityDao();
        userDao.delete(user);
    }

    
    public List<CreatEthEntity> queryCreatHistory(String username) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        CreatEthEntityDao userDao = daoSession.getCreatEthEntityDao();
        QueryBuilder<CreatEthEntity> qb = userDao.queryBuilder();
        qb.where(CreatEthEntityDao.Properties.UserName.eq(username));
        List<CreatEthEntity> list = qb.list();
        return list;
    }

    
    public WalletEntity queryWalletDetailTypeUser(String Address, String user,int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(qb.or(WalletEntityDao.Properties.MAddress.eq(Address), WalletEntityDao.Properties.MAddress2.eq(Address)),
                WalletEntityDao.Properties.UserName.eq(user), WalletEntityDao.Properties.Type.eq(type)));
        List<WalletEntity> list = qb.list();
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    
    public List<WalletEntity> queryWalletListByTypeUser(String username,int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(WalletEntityDao.Properties.UserName.eq(username),WalletEntityDao.Properties.Type.eq(type)));
        List<WalletEntity> list = qb.list();
        return list;
    }



    
    public List<WalletEntity> queryWalletListByUsername(String username) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(WalletEntityDao.Properties.UserName.eq(username)).orderDesc(WalletEntityDao.Properties.Level);
        List<WalletEntity> list = qb.list();
        return list;
    }
    
    public List<WalletEntity> queryWalletListByIdentity() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(WalletEntityDao.Properties.Level.eq(1)).orderDesc(WalletEntityDao.Properties.Level);
        List<WalletEntity> list = qb.list();
        return list;
    }
    
    public List<WalletEntity> queryWalletETHByIdentity() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(WalletEntityDao.Properties.Level.eq(1),WalletEntityDao.Properties.Type.eq(ETH_COIN)));
        List<WalletEntity> list = qb.list();
        return list;
    }
    
    private static final String SQL_DISTINCT_ENAME = "SELECT DISTINCT type FROM "+WalletEntityDao.TABLENAME;

    public List<Integer> queryWalletTypeList(String username) {
        ArrayList<Integer> result = new ArrayList<>();
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        Cursor c = daoSession.getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        try{
            if (c.moveToFirst()) {
                do {
                    result.add(c.getInt(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    
    private static final String SQL_DISTINCT_DEF = "SELECT DISTINCT type FROM "+WalletEntityDao.TABLENAME +" WHERE Level=1 ";

    public List<Integer> queryDefWalletTypeList(String username) {
        ArrayList<Integer> result = new ArrayList<>();
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        Cursor c = daoSession.getDatabase().rawQuery(SQL_DISTINCT_DEF, null);
        try{
            if (c.moveToFirst()) {
                do {
                    result.add(c.getInt(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }



    
    public List<WalletEntity> queryWalletList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        List<WalletEntity> list = qb.list();
        return list;
    }

    
    public List<AssertEntity> queryAssestList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(AssertEntityDao.Properties.WalletAddress.eq(""));
        List<AssertEntity> list = qb.list();
        return list;
    }


    
    public List<AssertEntity> queryAssestList(int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(""), AssertEntityDao.Properties.Type.eq(type)));
        List<AssertEntity> list = qb.list();
        return list;
    }

    
    public List<AssertEntity> queryAssestListByUser(int type, String userName) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(""), AssertEntityDao.Properties.Type.eq(type), AssertEntityDao.Properties.UserName.eq(userName)));
        List<AssertEntity> list = qb.list();
        return list;
    }
    
    public List<AssertEntity> queryAssestList(String address, int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(address), AssertEntityDao.Properties.Type.eq(type)));
        List<AssertEntity> list = qb.list();
        return list;
    }
    
    public List<AssertEntity> queryMustAssestList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(AssertEntityDao.Properties.Level.eq(0));
        List<AssertEntity> list = qb.list();
        return list;
    }

    
    public List<AssertEntity> queryAssestListByUser(String address, int type, String userName) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(address), AssertEntityDao.Properties.Type.eq(type), AssertEntityDao.Properties.UserName.eq(userName)));
        List<AssertEntity> list = qb.list();
        return list;
    }

    
    public WalletEntity queryWalletDetail(String Address, int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(qb.or(WalletEntityDao.Properties.MAddress.eq(Address), WalletEntityDao.Properties.MAddress2.eq(Address))
                , WalletEntityDao.Properties.Type.eq(type)));
        List<WalletEntity> list = qb.list();
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public WalletEntity queryWalletDetailUser(String Address, String user) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder<WalletEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(qb.or(WalletEntityDao.Properties.MAddress.eq(Address), WalletEntityDao.Properties.MAddress2.eq(Address)),
                WalletEntityDao.Properties.UserName.eq(user)));
        List<WalletEntity> list = qb.list();
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public AssertEntity getAssetsByAddress(String Address, String assetsAddress,int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder<AssertEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(Address), AssertEntityDao.Properties.Contract.eq(assetsAddress), AssertEntityDao.Properties.Type.eq(type)));
        List<AssertEntity> list = qb.list();
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    
    public void deleteWallet(WalletEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.delete(user);
    }

    
    public void deleteWalletByAddress(String address) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.or(WalletEntityDao.Properties.MAddress.eq(address), WalletEntityDao.Properties.MAddress2.eq(address)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteWalletByAddressUser(String address, String username) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(qb.or(WalletEntityDao.Properties.MAddress.eq(address), WalletEntityDao.Properties.MAddress2.eq(address)),
                WalletEntityDao.Properties.UserName.eq(username)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    public void deleteWalletByAddressUser(String address, String username,int type) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(qb.or(WalletEntityDao.Properties.MAddress.eq(address), WalletEntityDao.Properties.MAddress2.eq(address)),
                WalletEntityDao.Properties.UserName.eq(username),WalletEntityDao.Properties.Type.eq(type)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void cancleDefWallet(WalletEntity walletEntity) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.update(walletEntity);
    }

    
    public void setDefWallet(WalletEntity address) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.update(address);
    }

    
    public void deleteWalletByType(int type) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(WalletEntityDao.Properties.Type.eq(type));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteWalletByUserName(String useranme) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(WalletEntityDao.Properties.UserName.eq(useranme));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteWalletByAddressUserName(String useranme, String address) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(WalletEntityDao.Properties.UserName.eq(useranme));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssets(AssertEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        userDao.delete(user);
    }

    
    public void deleteAssertByType(int type) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(AssertEntityDao.Properties.Type.eq(type));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssertByWallet(String address) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(AssertEntityDao.Properties.WalletAddress.eq(address));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssertByWalletAndUser(String address, String userName) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(address), AssertEntityDao.Properties.UserName.eq(userName)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    public void deleteAssertByWalletAndUser(String address, String userName,int type) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(address), AssertEntityDao.Properties.UserName.eq(userName), AssertEntityDao.Properties.Type.eq(type)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssertByWalletAndName(String address, String name) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(address), AssertEntityDao.Properties.Short_name.eq(name)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssertByWalletAndName(String name) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.Short_name.eq(name), AssertEntityDao.Properties.Type.eq(1)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssertByName(String name,String contract,int type) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.Short_name.eq(name), AssertEntityDao.Properties.Contract.eq(contract),AssertEntityDao.Properties.Type.eq(type), AssertEntityDao.Properties.Level.eq(2)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAssertByWalletAndName(String address, String name, String assAddress) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(AssertEntityDao.Properties.WalletAddress.eq(address), AssertEntityDao.Properties.Short_name.eq(name), AssertEntityDao.Properties.Contract.eq(assAddress)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void delWalletAll() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        WalletEntityDao userDao = daoSession.getWalletEntityDao();
        userDao.deleteAll();
    }

    
    public void delAssetAll() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        AssertEntityDao userDao = daoSession.getAssertEntityDao();
        userDao.deleteAll();
    }
    
    public List<SettingNodeEntity> getAllDefNode() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        QueryBuilder<SettingNodeEntity> qb = userDao.queryBuilder();
        qb.where(SettingNodeEntityDao.Properties.Choose.eq(true));
        List<SettingNodeEntity> list = qb.list();
        return list;
    }
    
    public List<SettingNodeEntity> getDefChooseNode(int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        QueryBuilder<SettingNodeEntity> qb = userDao.queryBuilder();
        qb.where(qb.and(SettingNodeEntityDao.Properties.Type.eq(type),SettingNodeEntityDao.Properties.Choose.eq(true)));
        List<SettingNodeEntity> list = qb.list();
        return list;
    }
    
    public List<SettingNodeEntity> getAllTypeNode(int type) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        QueryBuilder<SettingNodeEntity> qb = userDao.queryBuilder();
        qb.where(SettingNodeEntityDao.Properties.Type.eq(type)).orderAsc(SettingNodeEntityDao.Properties.IsDef);
        List<SettingNodeEntity> list = qb.list();
        return list;
    }

    
    public void deleteNode(SettingNodeEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        userDao.delete(user);
    }
    
    public void deleteAllDefNode() {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(SettingNodeEntityDao.Properties.IsDef.eq(0));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }

    
    public void deleteAllDefNode(int walletType) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        QueryBuilder qb = userDao.queryBuilder();
        qb.where(qb.and(SettingNodeEntityDao.Properties.IsDef.eq(0), SettingNodeEntityDao.Properties.Type.eq(walletType)));
        DeleteQuery delete = qb.buildDelete();
        delete.executeDeleteWithoutDetachingEntities();
    }



    
    public void insertNode(SettingNodeEntity user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        userDao.insert(user);
    }
    
    public void insertListNode(List<SettingNodeEntity> user) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        SettingNodeEntityDao userDao = daoSession.getSettingNodeEntityDao();
        userDao.insertOrReplaceInTx(user);
    }
    
    public NftBean queryNftAssets(String walletAddress, String nftAddress, int walletType) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        NftBeanDao nftBeanDao = daoSession.getNftBeanDao();
        QueryBuilder<NftBean> qb = nftBeanDao.queryBuilder();
        qb.where(qb.and(NftBeanDao.Properties.WalletAddress.eq(walletAddress), NftBeanDao.Properties.Token_address.eq(nftAddress),  NftBeanDao.Properties.WalletType.eq(walletType)));
        List<NftBean> list = qb.list();
        if (list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    
    public List<NftBean> queryNftAssets(String walletAddress, int walletType) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        NftBeanDao nftBeanDao = daoSession.getNftBeanDao();
        QueryBuilder<NftBean> qb = nftBeanDao.queryBuilder();
        qb.where(qb.and(NftBeanDao.Properties.WalletAddress.eq(walletAddress), NftBeanDao.Properties.WalletType.eq(walletType)));
        List<NftBean> list = qb.list();
        return qb.list();
    }

    
    public void insertNftToken(NftBean nftBean) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        NftBeanDao nftBeanDao = daoSession.getNftBeanDao();
        nftBeanDao.insert(nftBean);
    }

    
    public void deleteNftToken(NftBean nftBean) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        NftBeanDao nftBeanDao = daoSession.getNftBeanDao();
        nftBeanDao.delete(nftBean);
    }


    
    public Observable<Boolean> cancelLikeBrowserHistory(String url) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                List<DappHistoryEntity> list = daoSession.getDappHistoryEntityDao().queryBuilder()
                        .where(DappHistoryEntityDao.Properties.IsLike.eq(1), DappHistoryEntityDao.Properties.Url.eq(url))
                        .orderAsc(DappHistoryEntityDao.Properties.Time).build().list();
                if (list != null && list.size() > 0) {
                    for (int i =0; i<list.size(); i++) {
                        list.get(i).isLike = 0;
                    }
                    daoSession.getDappHistoryEntityDao().updateInTx(list);
                }
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    
    public Observable<Boolean> setLikeBrowseHistory(String url, String name, String logoPath) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                List<DappHistoryEntity> list = daoSession.getDappHistoryEntityDao().queryBuilder()
                        .where(DappHistoryEntityDao.Properties.Url.eq(url))
                        .orderAsc(DappHistoryEntityDao.Properties.Time).build().list();
                if (list == null || list.size() == 0) {
                    
                    DappHistoryEntity en = new DappHistoryEntity();
                    en.setTime(System.currentTimeMillis());
                    en.isLike = 1;
                    en.setUrl(url);
                    if (!TextUtils.isEmpty(name)) {
                        en.title = name;
                    }
                    if (!TextUtils.isEmpty(logoPath)) {
                        en.iconPath = logoPath;
                    }
                    doInsertBrowseHistory(en);
                    if (TextUtils.isEmpty(name) && TextUtils.isEmpty(logoPath)) {
                        loadWebsiteInfo(en);
                    }
                } else {
                    
                    for (int i =0; i<list.size(); i++) {
                        list.get(i).isLike = 1;
                        if (!TextUtils.isEmpty(name)) {
                            list.get(i).title = name;
                        }
                        if (!TextUtils.isEmpty(logoPath)) {
                            list.get(i).iconPath = logoPath;
                        }
                    }
                    daoSession.getDappHistoryEntityDao().updateInTx(list);
                }

                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public Observable<List<DappHistoryEntity>> getLikeBrowseHistoryList(String url) {
        return Observable.create(new ObservableOnSubscribe<List<DappHistoryEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<DappHistoryEntity>> emitter) throws Exception {
                DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                List<DappHistoryEntity> list = daoSession.getDappHistoryEntityDao().queryBuilder()
                        .where(DappHistoryEntityDao.Properties.IsLike.eq(1), DappHistoryEntityDao.Properties.Url.eq(url))
                        .orderAsc(DappHistoryEntityDao.Properties.Time).build().list();
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public Observable<List<DappHistoryEntity>> getLikeBrowseHistoryList() {
        return Observable.create(new ObservableOnSubscribe<List<DappHistoryEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<DappHistoryEntity>> emitter) throws Exception {
                DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                List<DappHistoryEntity> list = daoSession.getDappHistoryEntityDao().queryBuilder()
                        .where(DappHistoryEntityDao.Properties.IsLike.eq(1))
                        .orderDesc(DappHistoryEntityDao.Properties.Time).build().list();
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public Observable<List<DappHistoryEntity>> getBrowseHistoryList() {
        return Observable.create(new ObservableOnSubscribe<List<DappHistoryEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<DappHistoryEntity>> emitter) throws Exception {
                DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                List<DappHistoryEntity> list = daoSession.getDappHistoryEntityDao().queryBuilder()
                        .orderDesc(DappHistoryEntityDao.Properties.Time).build().list();
                emitter.onNext(list);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public Observable<Boolean> insertBrowseHistory(DappHistoryEntity entity) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                doInsertBrowseHistory(entity);
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    private void doInsertBrowseHistory(DappHistoryEntity entity) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        DappHistoryEntity temp = daoSession.getDappHistoryEntityDao().queryBuilder().where(DappHistoryEntityDao.Properties.Url.eq(entity.url)).unique();
        if (null == temp) {
            daoSession.getDappHistoryEntityDao().insert(entity);
        } else {
            temp.setTime(System.currentTimeMillis());
            daoSession.getDappHistoryEntityDao().update(entity);
        }
    }

    
    public Observable<Boolean> updateBrowseHistory(DappHistoryEntity entity) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                doUpdateBrowseHistory(entity);
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    private void doUpdateBrowseHistory(DappHistoryEntity entity) {
        if (null == entity) {
            return;
        }
        entity.setTime(System.currentTimeMillis());
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        daoSession.getDappHistoryEntityDao().update(entity);

    }

    
    public Observable<Boolean> deleteBrowseHistory() {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
                DaoSession daoSession = daoMaster.newSession();
                daoSession.getDappHistoryEntityDao().queryBuilder().where(DappHistoryEntityDao.Properties.IsLike.notEq(1)).buildDelete();
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    
    public void loadWebsiteInfo(DappHistoryEntity en) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Exception {
                try {
                    Document doc = Jsoup.connect(en.url).get();
                    en.title = doc.head().getElementsByTag("title").text();
                    Elements elements = doc.head().getElementsByTag("link");
                    for (int i = 0; i < elements.size(); i++) {
                        Element et = elements.get(i);
                        Elements temp = et.getElementsByAttributeValue("rel", "icon");
                        if (temp.size() == 0) {
                            temp = et.getElementsByAttributeValue("rel", "shortcut icon");
                        }
                        if (temp.size() == 0) {
                            continue;
                        }
                        for (Element e : temp) {
                            String path = e.attr("href");
                            if (!TextUtils.isEmpty(path)) {
                                en.iconPath = WebSiteUtil.getDomain(en.url) + path;
                                doUpdateBrowseHistory(en);
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });
    }
}
