package im.wallet.router.base;

import android.content.Context;

import im.wallet.router.app.IAppNotice;
import im.wallet.router.wallet.IWalletPay;


public abstract class DefaultApplicationDelegate implements ApplicationDelegate {
    public static Context appContext;

    @Override
    public IWalletPay getWalletPay() {
        return null;
    }

    @Override
    public IAppNotice getAppNotice() {
        return null;
    }
}
