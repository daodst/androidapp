package net.gsantner.opoc.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;


public class OtherSpUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    public SharedPreferences mPreferences;

    
    public static final String APP_DESKTOP_MODIFY_VERSION = "23.11.04";

    public static final String CHANGED_WALLPAPER = "change_wallpaper";
    public static final String PERMISSION_OVERLAYS = "permission_overlays";
    public static final String PERMISSION_GATEWAY_WIDGET = "permission_gateway_widget";
    public static final String PERMISSION_CHAT_WIDGET = "permission_chat_widget";

    public static final String HOME_PAGE_APP_LOADED = "home_page_app_loaded";
    public static final String HOME_PAGE_APP_LOAD_MODE = "home_page_app_load_mode";

    public static final String APP_NOTIFY_VERSION_NAME = "app_notify_version_name";
    public static final String HOME_DEFAULT_APP_NEED_CHANGE = "home_default_app_need_change";

    public static final int HOME_PAGE_APP_LOAD_MODE_TILED = 1;
    public static final int HOME_PAGE_APP_LOAD_MODE_DRAWER = 2;

    private OtherSpUtils() {
        mPreferences = mContext.getSharedPreferences("other_shared", Context.MODE_PRIVATE);
    }

    private static final class OtherSpUtilsHolder {
        @SuppressLint("StaticFieldLeak")
        static final OtherSpUtils OTHER_SP_UTILS = new OtherSpUtils();
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static OtherSpUtils getInstance() {
        return OtherSpUtilsHolder.OTHER_SP_UTILS;
    }

    public SharedPreferences.Editor editor() {
        return mPreferences.edit();
    }

    public void putWallPaperChange(boolean changed) {
        mPreferences.getBoolean(CHANGED_WALLPAPER, changed);
    }

    public Boolean wallPaperChanged() {
        return mPreferences.getBoolean(CHANGED_WALLPAPER, false);
    }

    public void putRequestOverlaysPermission(boolean licensed) {
        editor().putBoolean(PERMISSION_OVERLAYS, licensed).apply();
    }

    public boolean getOverlayPermission() {
        return mPreferences.getBoolean(PERMISSION_OVERLAYS, false);
    }

    public void putRequestWidgetGatewayPermission(boolean licensed) {
        editor().putBoolean(PERMISSION_GATEWAY_WIDGET, licensed).apply();
    }

    public boolean getGatewayWidgetPermission() {
        return mPreferences.getBoolean(PERMISSION_GATEWAY_WIDGET, false);
    }

    public void putRequestWidgetChatPermission(boolean licensed) {
        editor().putBoolean(PERMISSION_CHAT_WIDGET, licensed).apply();
    }

    public boolean getChatWidgetPermission() {
        return mPreferences.getBoolean(PERMISSION_CHAT_WIDGET, false);
    }

    public void putHomePageAppLoaded(boolean isLoaded) {
        editor().putBoolean(HOME_PAGE_APP_LOADED, isLoaded).apply();
    }

    public boolean getHomePageAppLoaded() {
        return mPreferences.getBoolean(HOME_PAGE_APP_LOADED, false);
    }

    
    public void putHomePageAppLoadMode(int loadMode) {
        editor().putInt(HOME_PAGE_APP_LOAD_MODE, loadMode).apply();
    }

    
    public int getHomePageAppLoadMode() {
        return mPreferences.getInt(HOME_PAGE_APP_LOAD_MODE, 1);
    }

    public void putAppNotifyVersionName(String versionName) {
        editor().putString(APP_NOTIFY_VERSION_NAME, versionName).apply();
    }

    public String getAppNotifyVersionName() {
        return mPreferences.getString(APP_NOTIFY_VERSION_NAME, "");
    }

    public void putHomeDefaultAppNeedChange(boolean needChange) {
        editor().putBoolean(HOME_DEFAULT_APP_NEED_CHANGE, needChange).apply();
    }

    public boolean getHomeDefaultAppNeedChange() {
        return mPreferences.getBoolean(HOME_DEFAULT_APP_NEED_CHANGE, false);
    }
}
