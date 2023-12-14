

package common.app.base.model;

import android.text.TextUtils;

import common.app.AppApplication;
import common.app.R;
import common.app.RxBus;
import common.app.im.event.AccountError;



public class OtherDeviceLoginCheck {

    public static boolean check(String info) {
        if (!TextUtils.isEmpty(info) && info.equals("")) {
            RxBus.getInstance().post(new AccountError(AppApplication.getInstance().getString(R.string.error_login),
                    AppApplication.getInstance().getString(R.string.err_user_login_another_device),
                    AccountError.ERROR_CODE_LOGIN_OTHER_DEVICE));
            return true;
        } else {
            return false;
        }
    }
}
