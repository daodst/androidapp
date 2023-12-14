package com.wallet.ctc.db.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.wallet.ctc.db.AddressBookEntity;
import com.wallet.ctc.db.AssertEntity;
import com.wallet.ctc.db.CreatEthEntity;
import com.wallet.ctc.db.DappHistoryEntity;
import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.db.SettingNodeEntity;
import com.wallet.ctc.db.WalletEntity;

import com.wallet.ctc.db.dao.AddressBookEntityDao;
import com.wallet.ctc.db.dao.AssertEntityDao;
import com.wallet.ctc.db.dao.CreatEthEntityDao;
import com.wallet.ctc.db.dao.DappHistoryEntityDao;
import com.wallet.ctc.db.dao.NftBeanDao;
import com.wallet.ctc.db.dao.SettingNodeEntityDao;
import com.wallet.ctc.db.dao.WalletEntityDao;



public class DaoSession extends AbstractDaoSession {

    private final DaoConfig addressBookEntityDaoConfig;
    private final DaoConfig assertEntityDaoConfig;
    private final DaoConfig creatEthEntityDaoConfig;
    private final DaoConfig dappHistoryEntityDaoConfig;
    private final DaoConfig nftBeanDaoConfig;
    private final DaoConfig settingNodeEntityDaoConfig;
    private final DaoConfig walletEntityDaoConfig;

    private final AddressBookEntityDao addressBookEntityDao;
    private final AssertEntityDao assertEntityDao;
    private final CreatEthEntityDao creatEthEntityDao;
    private final DappHistoryEntityDao dappHistoryEntityDao;
    private final NftBeanDao nftBeanDao;
    private final SettingNodeEntityDao settingNodeEntityDao;
    private final WalletEntityDao walletEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        addressBookEntityDaoConfig = daoConfigMap.get(AddressBookEntityDao.class).clone();
        addressBookEntityDaoConfig.initIdentityScope(type);

        assertEntityDaoConfig = daoConfigMap.get(AssertEntityDao.class).clone();
        assertEntityDaoConfig.initIdentityScope(type);

        creatEthEntityDaoConfig = daoConfigMap.get(CreatEthEntityDao.class).clone();
        creatEthEntityDaoConfig.initIdentityScope(type);

        dappHistoryEntityDaoConfig = daoConfigMap.get(DappHistoryEntityDao.class).clone();
        dappHistoryEntityDaoConfig.initIdentityScope(type);

        nftBeanDaoConfig = daoConfigMap.get(NftBeanDao.class).clone();
        nftBeanDaoConfig.initIdentityScope(type);

        settingNodeEntityDaoConfig = daoConfigMap.get(SettingNodeEntityDao.class).clone();
        settingNodeEntityDaoConfig.initIdentityScope(type);

        walletEntityDaoConfig = daoConfigMap.get(WalletEntityDao.class).clone();
        walletEntityDaoConfig.initIdentityScope(type);

        addressBookEntityDao = new AddressBookEntityDao(addressBookEntityDaoConfig, this);
        assertEntityDao = new AssertEntityDao(assertEntityDaoConfig, this);
        creatEthEntityDao = new CreatEthEntityDao(creatEthEntityDaoConfig, this);
        dappHistoryEntityDao = new DappHistoryEntityDao(dappHistoryEntityDaoConfig, this);
        nftBeanDao = new NftBeanDao(nftBeanDaoConfig, this);
        settingNodeEntityDao = new SettingNodeEntityDao(settingNodeEntityDaoConfig, this);
        walletEntityDao = new WalletEntityDao(walletEntityDaoConfig, this);

        registerDao(AddressBookEntity.class, addressBookEntityDao);
        registerDao(AssertEntity.class, assertEntityDao);
        registerDao(CreatEthEntity.class, creatEthEntityDao);
        registerDao(DappHistoryEntity.class, dappHistoryEntityDao);
        registerDao(NftBean.class, nftBeanDao);
        registerDao(SettingNodeEntity.class, settingNodeEntityDao);
        registerDao(WalletEntity.class, walletEntityDao);
    }
    
    public void clear() {
        addressBookEntityDaoConfig.clearIdentityScope();
        assertEntityDaoConfig.clearIdentityScope();
        creatEthEntityDaoConfig.clearIdentityScope();
        dappHistoryEntityDaoConfig.clearIdentityScope();
        nftBeanDaoConfig.clearIdentityScope();
        settingNodeEntityDaoConfig.clearIdentityScope();
        walletEntityDaoConfig.clearIdentityScope();
    }

    public AddressBookEntityDao getAddressBookEntityDao() {
        return addressBookEntityDao;
    }

    public AssertEntityDao getAssertEntityDao() {
        return assertEntityDao;
    }

    public CreatEthEntityDao getCreatEthEntityDao() {
        return creatEthEntityDao;
    }

    public DappHistoryEntityDao getDappHistoryEntityDao() {
        return dappHistoryEntityDao;
    }

    public NftBeanDao getNftBeanDao() {
        return nftBeanDao;
    }

    public SettingNodeEntityDao getSettingNodeEntityDao() {
        return settingNodeEntityDao;
    }

    public WalletEntityDao getWalletEntityDao() {
        return walletEntityDao;
    }

}
