package im.wallet.router.wallet.pojo;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import im.wallet.router.base.DefaultApplicationDelegate;


public class BaseResultBean {
    @SerializedName(value = "status", alternate = {"Status"})
    protected int status;
    @SerializedName(value = "info", alternate = {"Info"})
    protected String info;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    
    public boolean isSuccess() {
        return status == 1;
    }

    public String getInfo() {
        return dealmsg(info);
    }

    private String dealmsg(String msg) {
        Context mActivity = DefaultApplicationDelegate.appContext;
        if (null == mActivity || TextUtils.isEmpty(msg)) {
            return msg;
        }

        String realMsgName = "error_" + msg.toLowerCase()
                .replace("'", "")
                .replace("%", "")
                .replace(";", "")
                .replace("(", "")
                .replace(",", "")
                .replace(")", "")
                .replace("-", "")
                .replace(" ", "_");

        int id = mActivity.getResources().getIdentifier(realMsgName, "string", mActivity.getPackageName());
        String realMsg = "";
        if (0 != id) {
            realMsg = mActivity.getString(id);
        } else {
            if (msg.contains("insufficient funds")) {
                id = mActivity.getResources().getIdentifier("insufficient_balance", "string", mActivity.getPackageName());
                if (0 != id) {
                    realMsg = mActivity.getString(id);
                }
            } else if (msg.contains("insufficient level to transfer")) {
                int strid = mActivity.getResources().getIdentifier("error_insufficient_level_to_transfer", "string", mActivity.getPackageName());
                realMsg = mActivity.getString(strid);
            }
        }
        if (TextUtils.isEmpty(realMsg)) {
            realMsg = msg;
        }
        return realMsg;
    }
}
