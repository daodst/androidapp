

package com.wallet.ctc.util;

import android.os.Parcelable;


public class NetNotices {


    private String action;
    private Parcelable extra;

    public NetNotices(Parcelable extra, String action) {
        this.extra = extra;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public Parcelable getExtra() {
        return extra;
    }
}
