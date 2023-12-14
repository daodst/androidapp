package com.inter.app;

import com.inter.InterWalletPayImpl;

import im.wallet.router.base.ApplicationDelegate;
import im.wallet.router.base.DefaultApplicationDelegate;
import im.wallet.router.wallet.IWalletPay;

public class WalletApplicationDelegate extends DefaultApplicationDelegate {

    
    @Override
    public IWalletPay getWalletPay() {
        return new InterWalletPayImpl();
    }

    @Override
    public int getMoodleType() {
        return ApplicationDelegate.MOODLE_TYPE_WALLET;
    }

}
