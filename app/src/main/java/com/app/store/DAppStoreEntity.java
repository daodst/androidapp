package com.app.store;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DAppStoreEntity {

    @SerializedName("app_logo")
    public String appLogo;
    @SerializedName("app_packagename")
    public String appPackagename;
    @SerializedName("app_name")
    public String appName;
    @SerializedName("app_desc")
    public String appDesc;
    @SerializedName("app_developer")
    public String appDeveloper;
    @SerializedName("app_url")
    public String appUrl;
    @SerializedName("app_size")
    public double appSize;
    @SerializedName("app_md5")
    public String appMd5;
    @SerializedName("app_grading")
    public String appGrading;
    @SerializedName("app_images")
    public List<String> appImages;
}
