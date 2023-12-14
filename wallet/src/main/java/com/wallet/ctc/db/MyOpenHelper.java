

package com.wallet.ctc.db;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wallet.ctc.db.dao.AddressBookEntityDao;
import com.wallet.ctc.db.dao.AssertEntityDao;
import com.wallet.ctc.db.dao.CreatEthEntityDao;
import com.wallet.ctc.db.dao.DaoMaster;
import com.wallet.ctc.db.dao.DappHistoryEntityDao;
import com.wallet.ctc.db.dao.NftBeanDao;
import com.wallet.ctc.db.dao.SettingNodeEntityDao;
import com.wallet.ctc.db.dao.WalletEntityDao;

import org.greenrobot.greendao.database.Database;

public class MyOpenHelper extends DaoMaster.DevOpenHelper {

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        
        MigrationHelper.migrate(db, WalletEntityDao.class, AssertEntityDao.class, AddressBookEntityDao.class, CreatEthEntityDao.class, SettingNodeEntityDao.class, NftBeanDao.class, DappHistoryEntityDao.class);
    }
}
