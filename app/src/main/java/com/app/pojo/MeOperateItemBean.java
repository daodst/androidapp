

package com.app.pojo;

import androidx.annotation.DrawableRes;



public class MeOperateItemBean {

    public MeOperateItemBean(String id, @DrawableRes int icon, String title) {
        this.id = id;
        this.iconRes = icon;
        this.name = title;
    }

    public String id;
    public int iconRes;
    public String name;
}
