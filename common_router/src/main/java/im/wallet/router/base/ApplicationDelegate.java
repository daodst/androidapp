package im.wallet.router.base;

import im.wallet.router.app.IAppNotice;
import im.wallet.router.wallet.IWalletPay;


public interface ApplicationDelegate {


    
    int MOODLE_TYPE_APP = 1;

    int MOODLE_TYPE_WALLET = 2;

    
    IWalletPay getWalletPay();

    int getMoodleType();

    IAppNotice getAppNotice();

}

