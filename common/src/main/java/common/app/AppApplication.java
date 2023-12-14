

package common.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.StringRes;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import common.app.utils.PhoneUtil;



public class AppApplication {

    private static Application mApp;
    private static Context mcontext;
    public static List<WeakReference<Activity>> mActivitys = Collections
            .synchronizedList(new LinkedList<WeakReference<Activity>>());
    public static void init(Application app) {
        mApp = app;
        mcontext=app;
    }

    private static AppApplication sInstance;


    public synchronized static AppApplication getInstance() {
        if (null == sInstance) {
            sInstance = new AppApplication();
        }
        return sInstance;
    }

    public Context getApplicationContext() {
        return mApp.getApplicationContext();
    }

    public String getPhoneID() {
        return PhoneUtil.getIMEI(getApplicationContext());
    }

    public final String getString(@StringRes int resId) {
        return mApp.getResources().getString(resId);
    }





    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        mApp.registerActivityLifecycleCallbacks(callback);
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = mApp.getExternalCacheDir();
        } else {
            cacheDir = mApp.getCacheDir();
        }
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir.getAbsolutePath();

    }



    public Resources getResources() {
        return mApp.getResources();
    }

    public String getPackageName() {
        return mApp.getPackageName();
    }

    public static Context getContext() {
        return mcontext;
    }


    
    public static void pushActivity(Activity activity) {
        mActivitys.add(new WeakReference<>(activity));
    }

    
    public static void popActivity(Activity activity) {
        if (null == activity || mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Iterator<WeakReference<Activity>> iterator = mActivitys.iterator();
        while (iterator.hasNext()){
            WeakReference<Activity> wk = iterator.next();
            if (wk != null && wk.get() != null && activity == wk.get()) {
                iterator.remove();
            }
        }
    }

    
    public static Activity currentActivity() {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return null;
        }
        Activity activity = mActivitys.get(mActivitys.size()-1).get();
        return activity;
    }

    
    public static void finishCurrentActivity() {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return;
        }
        Activity activity = mActivitys.get(mActivitys.size()-1).get();
        if (null != activity) {
            activity.finish();
        }
    }

    
    public static void finishActivity(Class<?> cls) {
        if (mActivitys == null||mActivitys.isEmpty()) {
            return;
        }
        Iterator<WeakReference<Activity>> iterator = mActivitys.iterator();
        while (iterator.hasNext()){
            WeakReference<Activity> next = iterator.next();
            if (next != null && next.get() != null && next.get().getClass().equals(cls)) {
                iterator.remove();
                next.get().finish();
                next = null;
            }
        }
    }

    
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (WeakReference<Activity> wk : mActivitys) {
                if (wk != null && wk.get() !=null && wk.get().getClass().equals(cls)) {
                    targetActivity = wk.get();
                    break;
                }
            }
        }
        return targetActivity;
    }

    
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size).get();
        }
        return mBaseActivity;

    }

    
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size).get();
        }
        return mBaseActivity.getClass().getName();
    }

    
    public static void finishAllActivity() {
        finishAllActivityExMain();
    }

    
    public static void finishAllActivityExMain() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Iterator<WeakReference<Activity>> iterator = mActivitys.iterator();
        while (iterator.hasNext()){
            WeakReference<Activity> next = iterator.next();
            if (next != null && next.get() != null) {
                Activity activity = next.get();
                if (null != activity && !ActivityRouter.isInstanceof(activity, ActivityRouter.getMainActivityName())) {
                    iterator.remove();
                    activity.finish();
                }
                next = null;
            }
        }
    }

    
    public static void appExit() {
        try {
            finishAllPage();
        } catch (Exception e) {
        }
    }

    public static void finishAllPage() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Iterator<WeakReference<Activity>> iterator = mActivitys.iterator();
        while (iterator.hasNext()){
            WeakReference<Activity> next = iterator.next();
            if (next != null && next.get() != null) {
                iterator.remove();
                next.get().finish();
                next = null;
            }
        }
    }

}
