package com.wallet.ctc.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class DappHistoryEntity {
    @Id(autoincrement = true)
    public Long id;  
    public String iconPath;
    public String url;
    public String title;
    public long time;
    public int isLike;
    public String params;
    public String params2;
    public String params3;

    @Transient
    public int iconRes;

    @Generated(hash = 800429246)
    public DappHistoryEntity(Long id, String iconPath, String url, String title,
            long time, int isLike, String params, String params2, String params3) {
        this.id = id;
        this.iconPath = iconPath;
        this.url = url;
        this.title = title;
        this.time = time;
        this.isLike = isLike;
        this.params = params;
        this.params2 = params2;
        this.params3 = params3;
    }
    @Generated(hash = 1894158298)
    public DappHistoryEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIconPath() {
        return this.iconPath;
    }
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getParams2() {
        return params2;
    }

    public void setParams2(String params2) {
        this.params2 = params2;
    }

    public String getParams3() {
        return params3;
    }

    public void setParams3(String params3) {
        this.params3 = params3;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }
}
