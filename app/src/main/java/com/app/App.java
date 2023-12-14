

package com.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.app.base.activity.MainActivity;
import com.app.base.activity.WelcomActivity;
import com.app.delegate.AppApplicationDelegate;
import com.benny.openlauncher.AppObject;
import com.inter.app.WalletApplicationDelegate;
import com.tencent.mmkv.MMKV;
import com.wallet.IChatInfo;
import com.wallet.ctc.WalletAppProvider;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;

import java.util.ArrayList;
import java.util.List;

import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.Injection;
import common.app.base.them.Eyes;
import common.app.mall.util.ToastUtil;
import common.app.my.view.MyAlertDialog;
import common.app.utils.ActivityContainer;
import common.app.utils.AllUtils;
import common.app.utils.DisplayUtils;
import common.app.utils.LanguageUtil;
import im.vector.app.VectorApplication;
import im.vector.app.easyfloat.EasyFloat;
import im.vector.app.easyfloat.floatingview.FloatingView;
import im.vector.app.easyfloatcall.CallEasyFloat;
import im.vector.app.features.call.VectorCallActivity;
import im.vector.app.provide.ChatStatusProvide;
import im.wallet.router.base.ApplicationDelegate;
import im.wallet.router.base.DefaultApplicationDelegate;
import im.wallet.router.base.IApplication;
import io.reactivex.plugins.RxJavaPlugins;
import kotlin.Unit;




public class App extends VectorApplication implements IApplication, WalletAppProvider, IChatInfo {

    private static final String TAG = "AppApplication";
    private static App sInstance;
    private MyAlertDialog myAlertDialog;
    private Context mContext;
    public DexUtils mDexUtils;

    private EasyFloat mEasyFloat;

    @Override
    public String getLoginAccount() {
        return ChatStatusProvide.getAddress(this);
    }

    @Override
    public void onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        new AppObject().init(this);
        MMKV.initialize(this);
        super.onCreate();
        if (isMainProcess()) {
            Injection.mApplication = this;
            AppApplication.init(this);
            LanguageUtil.initLanguage(this);
        }
        sInstance = this;


        registerApplicationDelegate();


        RxJavaPlugins.setErrorHandler(throwable -> {
            System.out.println(" App    throwable");
            throwable.printStackTrace();
        });

    }

    @NonNull
    @Override
    public String getUserId() {
        return super.getUserId();
    }


    

    @Override
    public String getAccessToken() {
        return authenticationService.getAccessToken();
    }

    private SparseArray<ApplicationDelegate> mDelegates = new SparseArray<>();

    private void registerApplicationDelegate() {
        mDelegates.clear();
        DefaultApplicationDelegate.appContext = this;

        
        WalletApplicationDelegate walletApplicationDelegate = new WalletApplicationDelegate();
        mDelegates.put(walletApplicationDelegate.getMoodleType(), walletApplicationDelegate);
        
        AppApplicationDelegate applicationDelegate = new AppApplicationDelegate();
        mDelegates.put(applicationDelegate.getMoodleType(), applicationDelegate);
    }

    @Override
    public ApplicationDelegate getDelegate(int type) {
        return mDelegates.get(type);
    }

    private boolean isInit = false;

    public void dealyInit() {
        if (!isInit) {
            isInit = true;
            init();
            onCreateInite();
            registerActivityListener();
        }

    }


    private void init() {


        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);


        ToastUtil.register(this);

        try {
            new WebView(this).destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        WalletUtil.initAarSdk();

        


    }


    public static App getInstance() {
        return sInstance;
    }



    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                AppApplication.pushActivity(activity);
                if (null == mEasyFloat) {
                    List<Class<?>> blackList = new ArrayList<>();
                    blackList.add(WelcomActivity.class);
                    blackList.add(im.vector.app.features.MainActivity.class);
                    blackList.add(BlockchainLoginActivity.class);
                    mEasyFloat = EasyFloat.INSTANCE.layout(R.layout.custom_dragger_button)
                            .blackList(blackList)
                            .layoutParams(initLayoutParams())
                            .dragEnable(true)
                            .setAutoMoveToEdge(true)
                            .listener(pView -> Unit.INSTANCE, pClick -> {
                                floatButtonClick(activity);
                                return Unit.INSTANCE;
                            });
                }
                mEasyFloat.show(activity);

                

                List<Class<?>> blackList2 = new ArrayList<>();
                blackList2.add(WelcomActivity.class);
                blackList2.add(VectorCallActivity.class);
                CallEasyFloat.INSTANCE.layout(R.layout.custom_dragger_button2)
                        .blackList(blackList2)
                        .layoutParams(initCallLayoutParams())
                        .dragEnable(true)
                        .setAutoMoveToEdge(true)
                        .listener(pView -> Unit.INSTANCE, pClick -> {
                            onTapToReturnToCall();
                            return Unit.INSTANCE;
                        })
                        .show(activity);

                CallEasyFloat.INSTANCE.setIsVisible(hasActiveCall());

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activity instanceof WelcomActivity || activity instanceof BlockchainLoginActivity) {
                    
                } else {
                    FloatingView.get().attach(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity instanceof WelcomActivity || activity instanceof BlockchainLoginActivity) {
                    
                } else {
                    FloatingView.get().detach(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppApplication.popActivity(activity);
            }
        });
    }

    @Override
    public void setCallIsVisible(boolean isVisible) {
        super.setCallIsVisible(isVisible);
        Log.i("setCallIsVisible", "===========" + isVisible);
        CallEasyFloat.INSTANCE.setIsVisible(isVisible);
    }

    private FrameLayout.LayoutParams initLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(0, 0, 0, DisplayUtils.dp2px(this, 300));
        return params;
    }

    private FrameLayout.LayoutParams initCallLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.setMargins(0, 0, 0, DisplayUtils.dp2px(this, 50));
        return params;
    }

    
    public void floatButtonClick(Activity pContext) {

        Activity activity = FloatingView.get().attachActivity;
        if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            Eyes.setStatusBarTextColor(mainActivity, true);
            mainActivity.floatButtonClick(true);
            return;
        } else {
            if (null != mEasyFloat) mEasyFloat.dismiss(pContext);
            
            for (Activity act : ActivityContainer.getInstance().getList()) {
                if (act instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) act;
                    Eyes.setStatusBarTextColor(mainActivity, true);
                    mainActivity.floatButtonClick(false);
                }
            }
        }
        Intent intent = ActivityRouter.getMainActivityIntent(pContext);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pContext.startActivity(intent);
    }




    private void initNoNet() {
        if (null == mContext || AllUtils.isContextDestroyed(mContext)) {
            return;
        }
        myAlertDialog = new MyAlertDialog(mContext, getString(R.string.net_error_tips));
        myAlertDialog.setCancelable(true);
        myAlertDialog.setNoBtnGone();
        myAlertDialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                
                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                mContext.startActivity(intent);
            }

            @Override
            public void No() {

            }
        });
    }

    
    private void testManifestPlaceHolder() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            String baiduKey = appInfo.metaData.getString("com.baidu.lbsapi.API_KEY");
            Log.i(TAG, "baiduKey=" + baiduKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }


    
}
