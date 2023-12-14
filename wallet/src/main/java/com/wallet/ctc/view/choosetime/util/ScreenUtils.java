

package com.wallet.ctc.view.choosetime.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.wallet.ctc.util.LogUtil;


public final class ScreenUtils {
    private static boolean isFullScreen = false;

    
    public static DisplayMetrics displayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        LogUtil.d("screen width=" + dm.widthPixels + "px, screen height=" + dm.heightPixels
                + "px, densityDpi=" + dm.densityDpi + ", density=" + dm.density);
        return dm;
    }

    
    public static int widthPixels(Context context) {
        return displayMetrics(context).widthPixels;
    }

    
    public static int heightPixels(Context context) {
        return displayMetrics(context).heightPixels;
    }

    
    public static float density(Context context) {
        return displayMetrics(context).density;
    }

    
    public static int densityDpi(Context context) {
        return displayMetrics(context).densityDpi;
    }

    
    public static boolean isFullScreen() {
        return isFullScreen;
    }

    
    public static void toggleFullScreen(Activity activity) {
        Window window = activity.getWindow();
        int flagFullscreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (isFullScreen) {
            window.clearFlags(flagFullscreen);
            isFullScreen = false;
        } else {
            window.setFlags(flagFullscreen, flagFullscreen);
            isFullScreen = true;
        }
    }

    
    public static void keepBright(Activity activity) {
        
        int keepScreenOn = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        activity.getWindow().setFlags(keepScreenOn, keepScreenOn);
    }

}
