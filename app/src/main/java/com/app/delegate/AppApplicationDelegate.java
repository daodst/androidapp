package com.app.delegate;

import im.wallet.router.app.IAppNotice;
import im.wallet.router.base.ApplicationDelegate;
import im.wallet.router.base.DefaultApplicationDelegate;

public class AppApplicationDelegate extends DefaultApplicationDelegate {


    @Override
    public IAppNotice getAppNotice() {
        return new AppNoticeImpl();
    }

    @Override
    public int getMoodleType() {
        return ApplicationDelegate.MOODLE_TYPE_APP;
    }

}
