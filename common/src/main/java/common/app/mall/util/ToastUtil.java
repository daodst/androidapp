

package common.app.mall.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class ToastUtil {

    public static Context mContext;

    private ToastUtil() {
    }

    public static void register(Context context) {
        mContext = context.getApplicationContext();
    }

    public static void showToast(int resId) {
        Toast.makeText(mContext, mContext.getString(resId), Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String msg) {
        if(TextUtils.isEmpty(msg) || null == mContext){
            return;
        }
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(int resId) {
        Toast.makeText(mContext, mContext.getString(resId), Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(String msg) {
        if(TextUtils.isEmpty(msg)){
            return;
        }
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }
}
