package common.app.utils;

import android.text.TextUtils;

public class WebSiteUtil {

    public static String getDomain(String url) {
        String result = "";
        if (!TextUtils.isEmpty(url)) {
            int inidex1 = url.indexOf(':') + 3;
            if (url.length() > inidex1) {
                int index2 = url.indexOf('/', inidex1);
                if (index2 == -1) {
                    index2 = url.length();
                }
                result = url.substring(0, index2);
            }
        }
        return result;
    }
}
