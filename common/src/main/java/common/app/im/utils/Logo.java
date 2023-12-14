

package common.app.im.utils;

import android.text.TextUtils;

import common.app.base.model.http.HttpMethods;


public class Logo {

    public static final String BASE_URL = HttpMethods.BASE_SITE;

    public static String getLogo(String ico) {

        if (!TextUtils.isEmpty(ico)) {
            
            if (ico.startsWith("http") || ico.startsWith("https")) {
                return ico;
            } else {
                return BASE_URL + ico;
            }
        }
        return "";
    }
}
