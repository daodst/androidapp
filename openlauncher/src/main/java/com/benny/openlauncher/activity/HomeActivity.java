package com.benny.openlauncher.activity;

import static android.view.View.GONE;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.appwidget.chat.ChatWidgetClickReceiver;
import com.benny.openlauncher.AppObject;
import com.benny.openlauncher.R;
import com.benny.openlauncher.activity.homeparts.HpAppDrawer;
import com.benny.openlauncher.activity.homeparts.HpDesktopOption;
import com.benny.openlauncher.activity.homeparts.HpDragOption;
import com.benny.openlauncher.activity.homeparts.HpInitSetup;
import com.benny.openlauncher.floatingwindow.CustomMoveButton;
import com.benny.openlauncher.interfaces.AppDeleteListener;
import com.benny.openlauncher.interfaces.AppUpdateListener;
import com.benny.openlauncher.manager.Setup;
import com.benny.openlauncher.model.App;
import com.benny.openlauncher.notifications.ActionNotificationListener;
import com.benny.openlauncher.notifications.MDNotificationListener;
import com.benny.openlauncher.receivers.ActionNotificationReceiver;
import com.benny.openlauncher.receivers.AppUpdateReceiver;
import com.benny.openlauncher.receivers.ShortcutReceiver;
import com.benny.openlauncher.receivers.WallpaperReceiver;
import com.benny.openlauncher.util.AppManager;
import com.benny.openlauncher.util.AppSettings;
import com.benny.openlauncher.util.DatabaseHelper;
import com.benny.openlauncher.util.DesktopAtomHelper;
import com.benny.openlauncher.util.LauncherAction;
import com.benny.openlauncher.util.LauncherAction.Action;
import com.benny.openlauncher.util.TabActionsHelper;
import com.benny.openlauncher.util.Tool;
import com.benny.openlauncher.viewutil.DialogHelper;
import com.benny.openlauncher.viewutil.MinibarAdapter;
import com.benny.openlauncher.viewutil.WidgetHost;
import com.benny.openlauncher.widget.AppDrawerController;
import com.benny.openlauncher.widget.AppItemView;
import com.benny.openlauncher.widget.Desktop;
import com.benny.openlauncher.widget.Desktop.OnDesktopEditListener;
import com.benny.openlauncher.widget.DesktopOptionView;
import com.benny.openlauncher.widget.Dock;
import com.benny.openlauncher.widget.GroupPopupView;
import com.benny.openlauncher.widget.ItemOptionView;
import com.benny.openlauncher.widget.MinibarView;
import com.benny.openlauncher.widget.PagerIndicator;
import com.benny.openlauncher.widget.SearchBar;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.gsantner.opoc.preference.OtherSpUtils;
import net.gsantner.opoc.util.ContextUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import common.app.ActivityRouter;
import common.app.base.base.BaseActivity;
import common.app.base.them.Eyes;
import common.app.my.abstracts.NoScrollLazyViewPager;
import common.app.pojo.ChatWidgetItemEntity;
import common.app.utils.ActivityContainer;
import common.app.utils.AppWidgetUtils;
import common.app.utils.GlideUtil;
import common.app.utils.LanguageUtil;

public abstract class HomeActivity extends BaseActivity implements OnDesktopEditListener, HomeActivityListener {
    public static final Companion Companion = new Companion();
    public static final int REQUEST_CREATE_APPWIDGET = 0x6475;
    public static final int REQUEST_PERMISSION_STORAGE = 0x3648;
    public static final int REQUEST_PICK_APPWIDGET = 0x2678;
    public static WidgetHost _appWidgetHost;
    public static AppWidgetManager _appWidgetManager;
    public static boolean ignoreResume;
    public static float _itemTouchX;
    public static float _itemTouchY;

    
    public static HomeActivity _launcher;
    public static DatabaseHelper _db;
    public static DesktopAtomHelper _desktopHelper;
    public static HpDesktopOption _desktopOption;

    
    private static final IntentFilter _appUpdateIntentFilter = new IntentFilter();
    private static final IntentFilter _shortcutIntentFilter = new IntentFilter();
    private static final IntentFilter _timeChangedIntentFilter = new IntentFilter();
    private static final IntentFilter _wallpaperChangedIntentFilter = new IntentFilter();
    private static final IntentFilter _actionNotificationFilter = new IntentFilter();
    @Deprecated
    private static final IntentFilter _chatWidgetFilter = new IntentFilter();
    private static final IntentFilter _alarmFilter = new IntentFilter();
    private static final IntentFilter _chatUpdateFilter = new IntentFilter();
    private AppUpdateReceiver _appUpdateReceiver;
    private ShortcutReceiver _shortcutReceiver;
    private BroadcastReceiver _timeChangedReceiver;
    private WallpaperReceiver _wallpaperReceiver;
    private ActionNotificationReceiver _actionNotificationReceiver;
    @Deprecated
    private ChatWidgetClickReceiver _chatWidgetReceiver;
    private BroadcastReceiver _alarmReceiver, _chatUpdateReceiver;

    private int cx;
    private int cy;

    
    private WindowManager wm;
    private CustomMoveButton mCustomMovebutton;
    private View mStatusBarView;
    @Deprecated
    private ActivityResultLauncher<Intent> mWidgetLauncher;
    @Deprecated
    private ActivityResultLauncher<Intent> mChatLauncher;
    private AlertDialog mGatewayAlertDialog;
    

    
    private AlarmManager mAlarmManager;
    private PendingIntent mAlarmPendingIntent;
    
    protected boolean mBlockHeightAlive = true;
    private AlertDialog mAlertDialog;

    
    private boolean desktopInit = false;
    private boolean isFirstLauncher = true;

    private long mLastAlarmTime;

    public static final class Companion {
        private Companion() {
        }

        public HomeActivity getLauncher() {
            return _launcher;
        }

        public void setLauncher(@Nullable HomeActivity v) {
            _launcher = v;
        }
    }

    static {
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        _wallpaperChangedIntentFilter.addAction(Intent.ACTION_WALLPAPER_CHANGED);
        _actionNotificationFilter.addAction(AppWidgetUtils.NOTIFICATIONS_ACTION);
        _appUpdateIntentFilter.addDataScheme("package");
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        _shortcutIntentFilter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
        _chatWidgetFilter.addAction(ChatWidgetClickReceiver.ACTION_CHAT_WIDGET_CLICK);
        _alarmFilter.addAction(AppWidgetUtils.ALARM_ACTION);
        _chatUpdateFilter.addAction(AppWidgetUtils.ChatWidgetDataFilter);
    }

    public final DrawerLayout getDrawerLayout() {
        return findViewById(R.id.drawer_layout);
    }

    public final ImageView getHomeBgLayout() {
        return findViewById(R.id.frame_home_layout);
    }

    public final Desktop getDesktop() {
        return findViewById(R.id.desktop);
    }

    public final Dock getDock() {
        return findViewById(R.id.dock);
    }

    public final AppDrawerController getAppDrawerController() {
        return findViewById(R.id.appDrawerController);
    }

    public final GroupPopupView getGroupPopup() {
        return findViewById(R.id.groupPopup);
    }

    public final SearchBar getSearchBar() {
        return findViewById(R.id.searchBar);
    }

    public final View getBackground() {
        return findViewById(R.id.background_frame);
    }

    public final PagerIndicator getDesktopIndicator() {
        return findViewById(R.id.desktopIndicator);
    }

    public final DesktopOptionView getDesktopOptionView() {
        return findViewById(R.id.desktop_option);
    }

    public final ItemOptionView getItemOptionView() {
        return findViewById(R.id.item_option);
    }

    public final FrameLayout getMinibarFrame() {
        return findViewById(R.id.minibar_frame);
    }

    public final View getStatusView() {
        return findViewById(R.id.status_frame);
    }

    public final View getNavigationView() {
        return findViewById(R.id.navigation_frame);
    }

    public final NoScrollLazyViewPager getFragmentContainer() {
        return findViewById(R.id.launcher_fragment_container);
    }

    public final TabLayout getTabLayout() {
        return findViewById(R.id.launcher_tl_main);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        scrollHomeMainPage();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        

        Companion.setLauncher(this);
        
        
        TabActionsHelper.setHomeActivity(this);

        String language = LanguageUtil.getNowSettingLaguage(this);
        ContextUtils contextUtils = new ContextUtils(getApplicationContext());
        contextUtils.setAppLanguage(language);
        if (!Setup.wasInitialised()) {
            Setup.init(new HpInitSetup(this));
            
            Setup.appSettings().setDesktopPageCurrent(1);
        }

        Companion.setLauncher(this);
        _db = Setup.dataManager();
        _desktopHelper = DesktopAtomHelper.instance(HomeActivity.this);
        

        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 
                        | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.argb(0, 0, 0, 0));
        getWindow().setNavigationBarColor(Color.argb(0, 0, 0, 0));
        
        setContentView(R.layout.activity_launcher_home);
        
        getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        init();

        
        
    }

    @Override
    public void recreate() {
        AppManager.getInstance(HomeActivity.this)._updateListeners.clear();
        AppManager.getInstance(HomeActivity.this)._deleteListeners.clear();
        desktopInit = false;
        super.recreate();
    }

    @Override
    protected boolean needState() {
        return false;
    }

    private void init() {
        
        
        _appWidgetManager = AppWidgetManager.getInstance(this);
        _appWidgetHost = new WidgetHost(getApplicationContext(), R.id.app_widget_host);
        _appWidgetHost.startListening();

        
        HpDragOption hpDragOption = new HpDragOption();
        View findViewById = findViewById(R.id.leftDragHandle);
        View findViewById2 = findViewById(R.id.rightDragHandle);
        hpDragOption.initDragNDrop(this, findViewById, findViewById2, getItemOptionView());

        registerBroadcastReceiver();
        initAppManager();
        initSettings();
        initViews();
        
        boolean changedWallpaper = OtherSpUtils.getInstance().wallPaperChanged();
        if (changedWallpaper) {
            getHomeBgLayout().setVisibility(GONE);
        } else {
            GlideUtil.showImg(this, R.mipmap.bg, getHomeBgLayout());
        }
        
        
        startAlarm();
    }

    
    @Deprecated
    private void registerForResult() {
        
        mWidgetLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (null != result && null != result.getData()) {
                _desktopOption.setOperateCallback(result1 -> {
                    OtherSpUtils.getInstance().putRequestWidgetGatewayPermission(result1);
                });
                _desktopOption.createWidget(result.getData());
            }
        });
        
        mChatLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (null != result && null != result.getData()) {
                _desktopOption.setOperateCallback(result1 -> {
                    OtherSpUtils.getInstance().putRequestWidgetChatPermission(result1);
                });
                _desktopOption.createWidget(result.getData());
            }
        });
    }

    protected void initAppManager() {
        isFirstLauncher = Setup.appSettings().getAppFirstLaunch();
        if (isFirstLauncher) {
            
            _desktopHelper.arrangeDockAtom(1);

            
            _desktopHelper.initDefaultApps();

            Setup.appSettings().setAppFirstLaunch(false);
            Setup.appSettings().setAppShowIntro(false);
        }
        
        if (OtherSpUtils.getInstance().getHomeDefaultAppNeedChange()) {
            
            _desktopHelper.updateDesktopAtom();
        }

        Setup.appLoader().addUpdateListener(new AppUpdateListener() {
            @Override
            public boolean onAppUpdated(List<App> it) {
                
                
                try {
                    
                    _desktopHelper.updateDesktopApps(it, () -> {
                        getDesktop().initDesktop();
                        
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                getDock().initDock();
                return false;
            }
        });
        Setup.appLoader().addDeleteListener(new AppDeleteListener() {
            @Override
            public boolean onAppDeleted(List<App> apps) {
                Log.d("jues_onLoadComplete", "addUpdateListener*addDeleteListener");
                getDesktop().initDesktop();
                getDock().initDock();
                return false;
            }
        });
        AppManager.getInstance(HomeActivity.this).init();
    }

    
    @Deprecated
    protected void createGatewayAppWidget() {
        int appWidgetId = _appWidgetHost.allocateAppWidgetId();
        AppWidgetProviderInfo appWidgetInfo = new AppWidgetProviderInfo();
        List<AppWidgetProviderInfo> widgetProviderInfos = _appWidgetManager.getInstalledProviders();
        for (AppWidgetProviderInfo providerInfo : widgetProviderInfos) {
            if (providerInfo.provider.getPackageName().equals(getPackageName()) && providerInfo.provider.getClassName().equals("com.appwidget.GatewayWidgetProvider")) {
                appWidgetInfo = providerInfo;
                break;
            }
        }
        
        
        
        ComponentName componentName = appWidgetInfo.provider;

        
        if (_appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, componentName)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtras(bundle);
            _desktopOption.createWidget(intent);
            OtherSpUtils.getInstance().putRequestWidgetGatewayPermission(true);

            return;
        }
        if (null != mGatewayAlertDialog) {
            if (!mGatewayAlertDialog.isShowing()) mGatewayAlertDialog.show();
            return;
        }

        mGatewayAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.desktop_remind)).setMessage(getString(R.string.desktop_open_gateway_widget)).setNegativeButton(getString(R.string.desktop_no), (dialog, which) -> {
            _appWidgetHost.deleteAppWidgetId(appWidgetId);
        }).setPositiveButton(getString(R.string.desktop_sure), (dialog, which) -> {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, componentName);
            mWidgetLauncher.launch(intent);
        }).create();
        mGatewayAlertDialog.show();
    }

    
    @Deprecated
    protected void createChatWidget() {
        int appWidgetId = _appWidgetHost.allocateAppWidgetId();
        AppWidgetProviderInfo appWidgetInfo = new AppWidgetProviderInfo();
        List<AppWidgetProviderInfo> widgetProviderInfos = _appWidgetManager.getInstalledProviders();
        for (AppWidgetProviderInfo providerInfo : widgetProviderInfos) {
            if (providerInfo.provider.getPackageName().equals(getPackageName()) && providerInfo.provider.getClassName().equals("com.appwidget.ChatWidgetProvider")) {
                appWidgetInfo = providerInfo;
                break;
            }
        }
        ComponentName componentName = appWidgetInfo.provider;

        
        if (_appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, componentName)) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtras(bundle);
            _desktopOption.createWidget(intent);
            OtherSpUtils.getInstance().putRequestWidgetChatPermission(true);
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.desktop_remind)).setMessage(getString(R.string.desktop_open_chat_widget)).setNegativeButton(getString(R.string.desktop_no), (dialog, which) -> _appWidgetHost.deleteAppWidgetId(appWidgetId)).setPositiveButton(getString(R.string.desktop_sure), (dialog, which) -> {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, componentName);
            mChatLauncher.launch(intent);
        }).create();
        alertDialog.show();
    }

    
    @Deprecated
    private void testAddStables() {
        Log.e("jues_test", "=======");
        Intent intent = new Intent(AppWidgetUtils.NOTIFICATIONS_ACTION);
        intent.putExtra("action", "action" + TabActionsHelper.ACTION_CALL_PHONE);
        intent.putExtra("value", "2");
        intent.putExtra("command", "action1");
        sendBroadcast(intent);


    }

    
    @Deprecated
    private void initFloatingButton() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        WindowManager.LayoutParams wmParams = AppObject.get().getMywmParams();
        if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.x = widthPixels;  
        wmParams.y = heightPixels - 360;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT; 
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mCustomMovebutton = new CustomMoveButton(getApplicationContext());
        mCustomMovebutton.setImageResource(R.mipmap.homepae);
        
        mCustomMovebutton.setForegroundGravity(Gravity.CENTER);
        
        wm.addView(mCustomMovebutton, wmParams);
        mCustomMovebutton.setOnSpeakListener(() -> {
            
            Tool.createScaleInScaleOutAnim(mCustomMovebutton, () -> floatButtonClick(false));
        });
        
        mCustomMovebutton.setVisibility(View.GONE);
    }

    private void initInnerFloatingButton() {
        
    }

    protected void initViews() {
        
        getAppDrawerController().init();
        getDock().setHome(this);

        getDesktop().setDesktopEditListener(this);
        getDesktop().setPageIndicator(getDesktopIndicator());
        getDesktopIndicator().setMode(Setup.appSettings().getDesktopIndicatorMode());

        AppSettings appSettings = Setup.appSettings();

        _desktopOption = new HpDesktopOption(this);

        getDesktopOptionView().setDesktopOptionViewListener(_desktopOption);
        getDesktopOptionView().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDesktopOptionView().updateLockIcon(appSettings.getDesktopLock());
            }
        }, 100);

        getDesktop().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                getDesktopOptionView().updateHomeIcon(appSettings.getDesktopPageCurrent() == position);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });

        new HpAppDrawer(this, findViewById(R.id.appDrawerIndicator)).initAppDrawer(getAppDrawerController());
        initMinibar();
    }

    
    protected void scrollHomeMainPage() {
        try {
            if (null != getDesktop()) {
                getDesktop().setCurrentItem(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public final void initMinibar() {
        final ArrayList<LauncherAction.ActionDisplayItem> items = AppSettings.get().getMinibarArrangement();
        MinibarView minibar = findViewById(R.id.minibar);
        minibar.setAdapter(new MinibarAdapter(this, items));
        minibar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if (items.get(i)._action == Action.SetWallpaper && !OtherSpUtils.getInstance().wallPaperChanged()) {
                    
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).setTitle(getString(R.string.desktop_remind)).setIcon(R.drawable.ic_bug).setMessage(getString(R.string.desktop_change_wallerpager)).setNegativeButton(getString(R.string.desktop_no), null).setPositiveButton(getString(R.string.desktop_sure), (dialog, which) -> {
                        
                        LauncherAction.RunAction(items.get(i), HomeActivity.this);
                    }).create();
                    alertDialog.show();
                    return;
                }
                LauncherAction.RunAction(items.get(i), HomeActivity.this);
            }
        });
    }

    public final void initSettings() {
        updateHomeLayout();

        AppSettings appSettings = Setup.appSettings();

        
        getDesktop().setBackgroundColor(appSettings.getDesktopBackgroundColor());
        getDock().setBackgroundColor(appSettings.getDockColor());

        
        getMinibarFrame().setBackgroundColor(appSettings.getMinibarBackgroundColor());
        getStatusView().setBackgroundColor(appSettings.getDesktopInsetColor());
        getNavigationView().setBackgroundColor(appSettings.getDesktopInsetColor());

        
    }

    private void registerBroadcastReceiver() {
        _appUpdateReceiver = new AppUpdateReceiver();
        _shortcutReceiver = new ShortcutReceiver();
        _timeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIME_TICK) || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED) || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
                    updateSearchClock();
                }
            }
        };
        
        _wallpaperReceiver = new WallpaperReceiver(() -> {
            Log.d("jues", "");
            
            getHomeBgLayout().setVisibility(GONE);
            OtherSpUtils.getInstance().putWallPaperChange(true);
        });
        
        _actionNotificationReceiver = new ActionNotificationReceiver();
        
        _chatWidgetReceiver = new ChatWidgetClickReceiver(this::showChatFragment);

        
        _alarmReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppWidgetUtils.ALARM_ACTION)) {
                    if (mBlockHeightAlive) startCycleTask();
                    mLastAlarmTime = System.currentTimeMillis();
                    onAlarmTick();
                    mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AppWidgetUtils.TIME_INTERVAL, mAlarmPendingIntent);
                }
            }
        };
        
        _chatUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra("bundle");
                String json = bundle.getString("data");
                Type type = new TypeToken<List<ChatWidgetItemEntity>>() {
                }.getType();
                List<ChatWidgetItemEntity> entityList = new GsonBuilder().create().fromJson(json, type);
                AppWidgetUtils.chatUnreadLiveData.postValue(entityList);

                
                int num = 0;
                for (ChatWidgetItemEntity entity : entityList) num += entity.number;
                ActionNotificationListener.getInstance().processCallback("action" + AppWidgetUtils.ACTION_CHAT, num);
            }
        };

        
        registerReceiver(_appUpdateReceiver, _appUpdateIntentFilter);
        registerReceiver(_shortcutReceiver, _shortcutIntentFilter);
        registerReceiver(_timeChangedReceiver, _timeChangedIntentFilter);
        registerReceiver(_wallpaperReceiver, _wallpaperChangedIntentFilter);
        registerReceiver(_actionNotificationReceiver, _actionNotificationFilter);
        registerReceiver(_chatWidgetReceiver, _chatWidgetFilter);
        registerReceiver(_alarmReceiver, _alarmFilter);
        registerReceiver(_chatUpdateReceiver, _chatUpdateFilter);
    }

    public final void onStartApp(@NonNull Context context, @NonNull App app, @Nullable View view) {
        String applicationId = context.getPackageName();
        if (applicationId.equals(app._packageName)) {
            LauncherAction.RunAction(Action.LauncherSettings, context);
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && app._userHandle != null) {
                LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
                List<LauncherActivityInfo> activities = launcherApps.getActivityList(app.getPackageName(), app._userHandle);
                for (int intent = 0; intent < activities.size(); intent++) {
                    if (app.getComponentName().equals(activities.get(intent).getComponentName().toString()))
                        launcherApps.startMainActivity(activities.get(intent).getComponentName(), app._userHandle, null, getActivityAnimationOpts(view));
                }
            } else {
                Intent intent = Tool.getIntentFromApp(app);
                context.startActivity(intent, getActivityAnimationOpts(view));
            }

            
            
            handleLauncherResume();
        } catch (Exception e) {
            e.printStackTrace();
            Tool.toast(context, R.string.toast_app_uninstalled);
        }
    }

    private Bundle getActivityAnimationOpts(View view) {
        Bundle bundle = null;
        if (view == null) {
            return null;
        }

        ActivityOptions options = null;
        if (VERSION.SDK_INT >= 23) {
            int left = 0;
            int top = 0;
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            if (view instanceof AppItemView) {
                width = (int) ((AppItemView) view).getIconSize();
                left = (int) ((AppItemView) view).getDrawIconLeft();
                top = (int) ((AppItemView) view).getDrawIconTop();
            }
            options = ActivityOptions.makeClipRevealAnimation(view, left, top, width, height);
        } else if (VERSION.SDK_INT < 21) {
            options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        if (options != null) {
            bundle = options.toBundle();
        }

        return bundle;
    }

    @Override
    public void onLoadComplete() {
        OnDesktopEditListener.super.onLoadComplete();
        getDesktop().postDelayed(this::checkWidgetPermission, 500);
    }

    public void onStartDesktopEdit() {
        Tool.visibleViews(100, getDesktopOptionView());
        updateDesktopIndicator(false);
        updateDock(false);
        updateSearchBar(false);
    }

    public void onFinishDesktopEdit() {
        Tool.invisibleViews(100, getDesktopOptionView());
        updateDesktopIndicator(true);
        updateDock(true);
        updateSearchBar(true);
    }

    public final void dimBackground() {
        Tool.visibleViews(200, getBackground());
        
        getWindow().setStatusBarColor(0xa1000000);
    }

    public final void unDimBackground() {
        Tool.invisibleViews(200, getBackground());
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public final void clearRoomForPopUp() {
        Tool.invisibleViews(200, getDesktop());
        updateDesktopIndicator(false);
        updateDock(false);
    }

    public final void unClearRoomForPopUp() {
        Tool.visibleViews(200, getDesktop());
        updateDesktopIndicator(true);
        updateDock(true);
    }

    public final void updateDock(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDockEnable() && show) {
            Tool.visibleViews(100, getDock());
        } else {
            if (appSettings.getDockEnable()) {
                Tool.invisibleViews(100, getDock());
            } else {
                Tool.goneViews(100, getDock());
            }
        }
    }

    public final void updateSearchBar(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getSearchBarEnable() && show) {
            Tool.visibleViews(100, getSearchBar());
        } else {
            if (appSettings.getSearchBarEnable()) {
                Tool.invisibleViews(100, getSearchBar());
            } else {
                Tool.goneViews(100, getSearchBar());
            }
        }
    }

    public final void updateDesktopIndicator(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDesktopShowIndicator() && show) {
            Tool.visibleViews(100, getDesktopIndicator());
        } else {
            Tool.goneViews(100, getDesktopIndicator());
        }
    }

    public final void updateSearchClock() {
        TextView textView = getSearchBar()._searchClock;

        if (textView.getText() != null) {
            try {
                getSearchBar().updateClock();
            } catch (Exception e) {
                getSearchBar()._searchClock.setText(R.string.bad_format);
            }
        }
    }

    public final void updateHomeLayout() {
        updateSearchBar(true);
        updateDock(true);
        updateDesktopIndicator(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                _desktopOption.configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                _desktopOption.createWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra("appWidgetId", -1);
            if (appWidgetId != -1) {
                _appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }

    }

    @Deprecated
    private void checkPermission() {
        if (!Settings.canDrawOverlays(this)) {
            
            if (OtherSpUtils.getInstance().getOverlayPermission()) {
                
                return;
            }

            if (null != mAlertDialog) {
                if (!mAlertDialog.isShowing()) mAlertDialog.show();
            }
            
            mAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.desktop_remind)).setMessage(getString(R.string.desktop_open_float_overlays)).setNegativeButton(getString(R.string.desktop_no), (dialog, which) -> {
                
            }).setPositiveButton(getString(R.string.desktop_sure), (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                
            }).create();
            mAlertDialog.show();
            OtherSpUtils.getInstance().putRequestOverlaysPermission(true);
        } else {
            initFloatingButton();
            
        }

        

    }

    private void checkWidgetPermission() {
        if (!OtherSpUtils.getInstance().getGatewayWidgetPermission()) {
            
            
            
            OtherSpUtils.getInstance().putRequestWidgetGatewayPermission(true);
        }
        if (!OtherSpUtils.getInstance().getChatWidgetPermission()) {
            
            
            OtherSpUtils.getInstance().putRequestWidgetChatPermission(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        showFloatingButton(VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (!getAppDrawerController()._isOpen) moveTaskToBack(true);
        handleLauncherResume();
    }

    @Override
    protected void onStart() {
        _appWidgetHost.startListening();
        _launcher = this;

        super.onStart();
    }

    private void checkNotificationPermissions() {
        Set<String> appList = NotificationManagerCompat.getEnabledListenerPackages(this);
        for (String app : appList) {
            if (app.equals(getPackageName())) {
                
                Intent i = new Intent(MDNotificationListener.UPDATE_NOTIFICATIONS_ACTION);
                i.setPackage(getPackageName());
                i.putExtra(MDNotificationListener.UPDATE_NOTIFICATIONS_COMMAND, MDNotificationListener.UPDATE_NOTIFICATIONS_UPDATE);
                sendBroadcast(i);
                return;
            }
        }

        
        DialogHelper.alertDialog(this, getString(R.string.notification_title), getString(R.string.notification_summary), getString(R.string.enable), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Tool.toast(HomeActivity.this, getString(R.string.toast_notification_permission_required));
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Exception e) {
            Log.d("jues_Recreate Error", "Recreate Error");
            e.printStackTrace();
        }
        _appWidgetHost.startListening();
        _launcher = this;
        if (mLastAlarmTime > 0) {
            long useTime = System.currentTimeMillis() - mLastAlarmTime;
            if (useTime > AppWidgetUtils.TIME_INTERVAL*2) {
                
                Log.e("HomAlarm", "alarm is stop to reStart");
                startAlarm();
            }
        }

        
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getAppRestartRequired()) {
            appSettings.setAppRestartRequired(false);
            recreate();
            return;
        }

        if (appSettings.getNotificationStatus()) {
            
            checkNotificationPermissions();
        }

        
        if (appSettings.getDesktopOrientationMode() == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (appSettings.getDesktopOrientationMode() == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        handleLauncherResume();
    }

    @Override
    protected void onDestroy() {
        try {
            _appWidgetHost.stopListening();
            _launcher = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        unregisterReceiver(_appUpdateReceiver);
        unregisterReceiver(_shortcutReceiver);
        unregisterReceiver(_timeChangedReceiver);
        unregisterReceiver(_wallpaperReceiver);
        unregisterReceiver(_actionNotificationReceiver);
        unregisterReceiver(_chatWidgetReceiver);
        unregisterReceiver(_alarmReceiver);
        unregisterReceiver(_chatUpdateReceiver);
        super.onDestroy();
        if (mCustomMovebutton != null) wm.removeView(mCustomMovebutton);
        if (null != mAlarmManager && null != mAlarmPendingIntent) {
            mAlarmManager.cancel(mAlarmPendingIntent);
        }
    }

    private void handleLauncherResume() {
        if (ignoreResume) {
            
            
            ignoreResume = false;
        } else {
            getSearchBar().collapse();
            getGroupPopup().collapse();
            
            getItemOptionView().collapse();
            
            getDrawerLayout().closeDrawers();
            if (getDesktop().getInEditMode()) {
                
                getDesktop().getCurrentPage().performClick();
            } else if (getAppDrawerController().getDrawer().getVisibility() == View.VISIBLE) {
                closeAppDrawer();
            }
        }
    }

    public final void openAppDrawer() {
        openAppDrawer(null, 0, 0);
    }

    public final void openAppDrawer(View view, int x, int y) {
        if (!(x > 0 && y > 0) && view != null) {
            int[] pos = new int[2];
            view.getLocationInWindow(pos);
            cx = pos[0];
            cy = pos[1];

            cx += view.getWidth() / 2f;
            cy += view.getHeight() / 2f;
            if (view instanceof AppItemView) {
                AppItemView appItemView = (AppItemView) view;
                if (appItemView.getShowLabel()) {
                    cy -= Tool.dp2px(14) / 2f;
                }
            }
            cy -= getAppDrawerController().getPaddingTop();
        } else {
            cx = x;
            cy = y;
        }
        
    }

    public final void closeAppDrawer() {
        getAppDrawerController().close(cx, cy);
        
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    public boolean isLogined() {
        return true;
    }

    @Override
    public void showFloatingButton(int visible) {
        mBlockHeightAlive = visible != VISIBLE;
        if (mCustomMovebutton != null) {
            mCustomMovebutton.setVisibility(visible);
        }
    }

    @Override
    public void floatButtonClick(boolean needScroll) {
        Intent intent = ActivityRouter.getMainActivityIntent(this);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

        for (Activity activity : ActivityContainer.getInstance().getList()) {
            if (activity instanceof HomeActivity) {
                scrollHomeMainPage();
            } else activity.finish();
        }
    }

    @Override
    public void showChatFragment() {
        

    }

    @Override
    public void showCallPhoneFragment() {
        addStatusBar();
    }

    @Override
    public void showWalletFragment() {
        addStatusBar();
    }

    @Override
    public void showMeFragment() {
        addStatusBar();
    }

    public void removeStatusBar() {
        if (null == mStatusBarView) return;
        Window window = getWindow();
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        if (mStatusBarView.getParent() != null) {
            decorViewGroup.removeView(mStatusBarView);
        }
    }

    private void addStatusBar() {
        Window window = getWindow();
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        if (null == mStatusBarView) {
            mStatusBarView = new View(window.getContext());
            int statusBarHeight = Eyes.getStatusBarHeight(window.getContext());
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
            params.gravity = Gravity.TOP;
            mStatusBarView.setLayoutParams(params);
            mStatusBarView.setBackgroundColor(Color.WHITE);
        }
        if (mStatusBarView.getParent() == null) {
            decorViewGroup.addView(mStatusBarView);
        }
    }

    public void showOptimizedAlertDialog() {
    }

    
    private void startAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mAlarmPendingIntent = PendingIntent.getBroadcast(_launcher, 0, new Intent(AppWidgetUtils.ALARM_ACTION), PendingIntent.FLAG_IMMUTABLE);
        mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), mAlarmPendingIntent);
        mLastAlarmTime = System.currentTimeMillis();
    }
}
