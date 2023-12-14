

package common.app.base.fragment.mall.catcherror;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import common.app.Injection;
import common.app.R;
import common.app.base.model.OtherDeviceLoginCheck;
import common.app.im.exception.DbException;
import common.app.im.exception.RemoteServerException;
import common.app.mall.util.ToastUtil;
import common.app.utils.LogUtil;
import common.app.utils.NetUtils;



public class ApiErrorHelper {
    public static void handleCommonError(Context mActivity, Throwable e) {
        Throwable throwable = e.getCause();
        if (!NetUtils.isNetworkConnected(Injection.provideContext())) {
            toastTest(Injection.provideContext().getString(R.string.net_work_unconnected));
        } else if (e instanceof SocketTimeoutException) {
            toastTest(Injection.provideContext().getString(R.string.error_net_timeout));
        } else if (e instanceof ConnectException) {
            toastTest(Injection.provideContext().getString(R.string.error_net_connect_ex));
        } else if (e instanceof DbException || throwable instanceof DbException || throwable instanceof SocketTimeoutException || throwable instanceof SocketException) {
            
        } else if ((e instanceof JsonSyntaxException)) {
            LogUtil.d("zzz",e.getMessage());
            toastTest(Injection.provideContext().getString(R.string.ex_parse_error));
        } else if (throwable instanceof RemoteServerException) {
            if(TextUtils.isEmpty(throwable.getMessage())){
                return;
            }
            if (null != mActivity) {
                toastTest(throwable.getMessage());
            }
        } else {
            if (null != mActivity) {
                if (!TextUtils.isEmpty(e.getMessage()) && OtherDeviceLoginCheck.check(e.getMessage())) {
                    return;
                }
                if (!("null".equals(e.getMessage()) || TextUtils.isEmpty(e.getMessage()))) {
                    toastTest(e.getMessage());
                }
            }
        }
    }

    private static void toastTest(String txt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        
                        ToastUtil.showToast(txt);
                    }
                });
            }
        }).start();
    }
}
