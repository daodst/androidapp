package common.app.base.fragment.mall.model;

import android.content.Context;
import android.text.TextUtils;

import common.app.R;


public class NewVersionBean {


    
    public int package_size;
    public String apk_md_5_hash;
    public String share_url;

    
    private String cur_version;
    private String package_name;

    public String getNewVersion() {
        if (TextUtils.isEmpty(cur_version)) {
            return "";
        }
        return cur_version.trim();
    }

    
    private String download_url;

    public String getDownloadUrl() {
        if (TextUtils.isEmpty(download_url)) {
            return "";
        }
        return download_url;
    }

    
    private String apk_last_update_error;

    public String tips(Context context) {

        if (TextUtils.equals(context.getPackageName(), package_name)) {
            if (TextUtils.isEmpty(apk_last_update_error)) {
                return "";
            }
            return apk_last_update_error;
        } else {
            return context.getString(R.string.apk_last_update_error);
        }

    }

    
    private int client_force_update;

    public boolean getClientForceUpdate(String packageName) {
        if (TextUtils.equals(packageName, package_name)) {
            return client_force_update == 1;
        } else {
            return false;
        }

    }
}
