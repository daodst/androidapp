

package com.app.base.activity;

import static android.app.Notification.EXTRA_CHANNEL_ID;
import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static com.wallet.ctc.BuildConfig.ENABLE_CREAT_ALL_WALLET;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.app.AppController;
import com.app.BuildConfig;
import com.app.R;
import com.app.chat_engine.ChatEngine;
import com.app.did_guid.BuyDidGuidActivity;
import com.app.lg4e.pojo.MainTabBean;
import com.app.lg4e.ui.LanguagePopup;
import com.app.me.SMNewMeFragment;
import com.app.me.SMNewSettingActivity;
import com.app.my.reduce.DstReduceActivity;
import com.app.store.DAppStoreActivity;
import com.app.view.privatedvm.PrivateDVMActivity;
import com.benny.openlauncher.activity.HomeActivity;
import com.benny.openlauncher.model.Item;
import com.benny.openlauncher.util.TabActionsHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.tapadoo.alerter.Alerter;
import com.wallet.ctc.AppHolder;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.EvmosGatewayBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.ui.blockchain.blockchainlogin.BlockchainLoginActivity;
import com.wallet.ctc.ui.blockchain.choosewallet.ChooseManagerWalletActivity;
import com.wallet.ctc.ui.blockchain.collectmoney.CollectMoneyActivity;
import com.wallet.ctc.ui.blockchain.creatwallet.CreatWalletActivity;
import com.wallet.ctc.ui.blockchain.home.NewHomeFragment;
import com.wallet.ctc.ui.blockchain.transfer.TransferActivity;
import com.wallet.ctc.ui.dapp.DappWebViewActivity;
import com.wallet.ctc.ui.me.chain_bridge2.ChainBridgeActivity2;
import com.wallet.ctc.ui.me.virtualphone.SMVirtualPhoneActivity;
import com.wallet.ctc.util.AllUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import common.app.ActivityRouter;
import common.app.base.fragment.mall.api.VerApi;
import common.app.base.fragment.mall.model.BaseEntity;
import common.app.base.fragment.mall.model.NewVersionBean;
import common.app.base.share.qr.QrCodeUtils;
import common.app.base.them.Eyes;
import common.app.crash.CrashManager;
import common.app.im.base.NextSubscriber;
import common.app.im.event.Notice;
import common.app.mall.util.ToastUtil;
import common.app.my.RxNotice;
import common.app.my.abstracts.NoScrollLazyViewPager;
import common.app.my.view.MyAlertDialog;
import common.app.utils.AppVerUtil;
import common.app.utils.FileUtils;
import common.app.utils.LanguageUtil;
import common.app.utils.NetUtils;
import common.app.utils.RxSchedulers;
import common.app.utils.SpUtil;
import common.app.utils.UriUtil;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.EntryPointAccessors;
import im.vector.app.AppStateHandler;
import im.vector.app.core.di.ActiveSessionHolder;
import im.vector.app.core.di.ActivityEntryPoint;
import im.vector.app.core.utils.SystemUtilsKt;
import im.vector.app.easyfloat.floatingview.FloatingView;
import im.vector.app.features.call.phone.SMDialFragment;
import im.vector.app.features.configuration.VectorConfiguration;
import im.vector.app.features.home.HomeActivityArgs;
import im.vector.app.features.home.HomeActivityFragment;
import im.vector.app.features.home.HomeActivityHolderFragment;
import im.vector.app.features.home.ImLoading;
import im.vector.app.features.invite.AutoAcceptInvites;
import im.vector.app.features.notifications.NotificationUtils;
import im.vector.app.features.popup.PopupAlertManager;
import im.vector.app.features.settings.VectorLocale;
import im.vector.app.features.usercode.UserCodeActivity;
import im.vector.app.features.workers.signout.SignOutUiWorker;
import im.vector.app.provide.ChatStatusProvide;
import im.vector.app.provide.GlobalErrorHelper;
import im.vector.app.provide.MainChatDetailViewModel;
import im.vector.app.provide.UserInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.ele.uetool.UETool;


@AndroidEntryPoint
public class MainActivity extends HomeActivity implements ImLoading {
    @Inject
    public PopupAlertManager popupAlertManager;

    private static final String TAG = "MainActivity";
    private List<MainTabBean> mTabList;
    public NoScrollLazyViewPager mVpMain;
    private TabLayout mTlMain;
    
    private static final int FIRST_SHOW_PAGE_INDEX = 3;
    
    private RpcApi mRpcApi;
    private MainViewModel mViewModel;

    static {
        System.loadLibrary("TrustWalletCore");
    }

    @Inject
    ActiveSessionHolder sessionHolder;

    @Inject
    AppStateHandler appStateHandler;

    @Inject
    AutoAcceptInvites autoAcceptInvites;

    private HomeActivityFragment chatFragment;

    private TextView mPbText;
    private ProgressBar mPbLoading;
    private View mPbRoot;
    AlertDialog mAlertDialog;

    private WelcomeClassActivity mWelcome;

    private long mLastHomeTime, mLastSystemCloseTime, mLastOnResumeTime;
    private BroadcastReceiver mSysCloseReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWelcome = new WelcomeClassActivity();
        mWelcome.initRequstData();
        if (BuildConfig.DEBUG && false) {
            UETool.showUETMenu();
        }

        
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.injectLifecycleProvider(this, false);
        mViewModel.setLifecyleOwner(this);





        
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith(BuildConfig.SCHEME)) {
                
                QrCodeUtils.createInstance(this).parseQrCode(uri.toString());
            }
        }
        

        if (null == mRpcApi) mRpcApi = new RpcApi();
        addSubscription(mRpcApi.getEvmosGateway(SpUtil.getNodeNoSegm()).compose(RxSchedulers.io_main()).subscribeWith(new NextSubscriber<EvmosGatewayBean>() {
            @Override
            public void dealData(EvmosGatewayBean value) {
                if (value.isSuccess() && null != value.data) {
                    EvmosGatewayBean.Data data = value.data;
                    if (!TextUtils.isEmpty(data.gateway_name)) {
                        SpUtil.saveNodeName(data.gateway_name);
                    }
                }
            }
        }));
        mAlertDialog = onCreateAlertDialog();
        super.onCreate(savedInstanceState);
        this.mTlMain = getTabLayout();
        this.mVpMain = getFragmentContainer();
        initDataNew();
        initEventNew();
        mAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        GlobalErrorHelper helper = new GlobalErrorHelper();
        helper.init(this);

        registerReceiver(mSysCloseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mLastSystemCloseTime = System.currentTimeMillis();
                checkHomeKeyPress();
            }
        }, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));



        if (ChatStatusProvide.loginStatus(this)) {
            ChatStatusProvide.addMonitor(this.getApplicationContext());
            try {
                MainChatDetailViewModel viewModel = ViewModelProviders.of(this).get(MainChatDetailViewModel.class);
                viewModel.observeRoomSummaries(appStateHandler, autoAcceptInvites, sessionHolder.getActiveSession());
                viewModel.getGroupChatNum().observe(this, chatDataNum -> {
                    Log.i("MainActivity", "-----getGroupChatNum-------" + chatDataNum);
                });
                viewModel.getPeopleChatNum().observe(this, chatDataNum -> {
                    Log.i("MainActivity", "------getPeopleChatNum------" + chatDataNum);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static final String KEY_ARG = "mavericks:arg";

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (null != intent) {
            Parcelable extra = intent.getParcelableExtra(KEY_ARG);
            if (extra instanceof HomeActivityArgs && null != chatFragment) {
                chatFragment.onNewIntent(intent);
            }
        }
        setIntent(intent);
    }


    protected void initDataNew() {
        Eyes.setStatusBarTextColor(this, false);

        ActivityEntryPoint activityEntryPoint = EntryPointAccessors.fromActivity(this, ActivityEntryPoint.class);
        FragmentFactory fragmentFactory = activityEntryPoint.fragmentFactory();

        
        getSupportFragmentManager().setFragmentFactory(fragmentFactory);


        if (ChatStatusProvide.loginStatus(this)) {
            
            chatFragment = new HomeActivityFragment();
        }


        mTabList = new ArrayList<>();
        mTabList.add(new MainTabBean(R.drawable.main_tab_selector_call, R.string.call, new SMDialFragment(), true, true));
        if (null != chatFragment) {
            mTabList.add(new MainTabBean(R.drawable.main_tab_selector_shequn, R.string.shequn, chatFragment, true, true));
        } else {
            mTabList.add(new MainTabBean(R.drawable.main_tab_selector_shequn, R.string.shequn, new HomeActivityHolderFragment(), true, true));
        }
        mTabList.add(new MainTabBean(R.drawable.main_tab_selector_mall, R.string.zichan, new NewHomeFragment(), false, true));
        mTabList.add(new MainTabBean(R.drawable.main_tab_selector_walletme, R.string.me, new SMNewMeFragment(), false, false));


        if (!checkNotifySetting()) {
            showNotification();
        }
    }

    private void showNotification() {
        MyAlertDialog dialog = new MyAlertDialog(this, getString(R.string.show_notification_des));
        dialog.setonclick(new MyAlertDialog.Onclick() {
            @Override
            public void Yes() {
                dialog.dismiss();
                launchNotification();
            }

            @Override
            public void No() {
                dialog.dismiss();
            }
        });
        dialog.setTitle(String.format(getString(R.string.show_notification_title), getString(R.string.app_name)));
        dialog.show();
    }

    private void launchNotification() {
        try {
            
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            
            intent.putExtra(EXTRA_APP_PACKAGE, getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, getApplicationInfo().uid);

            
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);

            
            
            
            
            
            
            
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            
            Intent intent = new Intent();

            
            
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    
    private boolean checkNotifySetting() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        
        return manager.areNotificationsEnabled();
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initEventNew() {
        mVpMain.setScroll(false);
        mVpMain.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            @Override
            public Fragment getItem(int position) {
                return mTabList.get(position).getFragment();
            }

            @Override
            public int getCount() {
                return mTabList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(mTabList.get(position).getTitle());
            }
        });


        mVpMain.setOnTouchListener(((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }));
        mVpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mVpMain.getParent().requestDisallowInterceptTouchEvent(true);
            }

            @Override
            public void onPageSelected(int position) {
                
                if (mTabList.get(position).isNeedLogin()) {
                    if (!isLogined()) {
                        startActivity(new Intent(MainActivity.this, im.vector.app.features.MainActivity.class));

                        return;
                    }
                }
                if (mTabList.get(position).getFragment() instanceof HomeActivityHolderFragment) {
                    
                    startActivity(new Intent(MainActivity.this, im.vector.app.features.MainActivity.class));

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        
        
        mTlMain.setupWithViewPager(mVpMain);
        mTlMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("SMMe", "onTabReselected()");
                mTabList.get(tab.getPosition()).getFragment().onResume();
            }
        });

        
        mVpMain.setOffscreenPageLimit(mTabList.size());
        boolean booleanExtra = false;
        if (null != getIntent()) {
            booleanExtra = getIntent().getBooleanExtra(NotificationUtils.FROM_NOTIFICATIONUTILS, false);
        }
        if (booleanExtra) {
            mVpMain.setCurrentItem(0, false);
        } else {
            mVpMain.setCurrentItem(FIRST_SHOW_PAGE_INDEX);
        }

        mVpMain.setVisibility(View.INVISIBLE);

        

        
        if (isLogined()) {
            
            String userId = ChatStatusProvide.getUserId(this);
            if (null != mViewModel) mViewModel.getChatInfo(userId);
        } else {
            
        }
    }


    
    public boolean isLogined() {
        return ChatStatusProvide.loginStatus(this);
    }


    
    long lastCheckVerTime = 0L;

    @Override
    protected void onResume() {
        super.onResume();
        
        if (!isLogined()) {
            
            boolean hasSetLanguage = SpUtil.getAppLanguageHasSet();
            if (!hasSetLanguage) {
                new XPopup.Builder(this)
                        .dismissOnBackPressed(false)
                        .dismissOnTouchOutside(false)
                        .asCustom(new LanguagePopup(this, unused -> {
                            startActivity(new Intent(MainActivity.this, im.vector.app.features.MainActivity.class));
                        })).show();
            } else {
                startActivity(new Intent(MainActivity.this, im.vector.app.features.MainActivity.class));
            }
            return;
        }

        if (System.currentTimeMillis() - lastCheckVerTime > 1800000) {
            
            new AppVerUtil(MainActivity.this).checkVer(new AppVerUtil.VerCheckListener() {

                @Override
                public void success() {
                    lastCheckVerTime = System.currentTimeMillis();
                }

                @Override
                public void onCancel() {

                }
            });
        }

        
        AppController.checkHostAndRefreshGateWayListCache();

        
        checkMyPhoneList();

        
        checkAndSetDefaultAvatar();

        
        getFragmentContainer().postDelayed(() -> {
            showFloatingButton(getFragmentContainer().getVisibility());
        }, 200);

        mLastOnResumeTime = System.currentTimeMillis();
        checkHomeKeyPress();

        freshRootWalletInfo();


        
        startChainBridgeTask();
    }


    
    private void startChainBridgeTask() {
        
        if (null != mViewModel) {
            mViewModel.startChainBridgeTask(false);
        }
    }

    private void freshRootWalletInfo() {
        if (null != mViewModel) {
            mViewModel.freshRootWalletInfo();
        }
    }

    private void checkHomeKeyPress() {
        if (System.currentTimeMillis() - mLastHomeTime < 1000) {
            
            return;
        }
        if (mLastOnResumeTime > 0 && mLastSystemCloseTime > 0 && Math.abs(mLastOnResumeTime - mLastSystemCloseTime) < 100) {
            mLastHomeTime = System.currentTimeMillis();
            try {
                if (null != getFragmentContainer() && getFragmentContainer().getVisibility() == View.VISIBLE) {
                    Log.i(TAG, "onHomeClick");
                    hideFragment();
                    removeStatusBar();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    
    private void checkAndSetDefaultAvatar() {
        
        if (isLogined()) {
            
            UserInfo userInfo = ChatStatusProvide.getLoginUserInfo(this);
            if (null != userInfo && !TextUtils.isEmpty(userInfo.getUserId())) {
                String userId = userInfo.getUserId();
                String avatorUrl = userInfo.getAvatarUrl();
                String nickName = userInfo.getDisplayName();
                String uidAddr = AllUtils.getAddressByUid(userId);
                if (!TextUtils.isEmpty(uidAddr) && (TextUtils.isEmpty(nickName) || nickName.equalsIgnoreCase(uidAddr))) {
                    
                    WalletEntity wallet = WalletDBUtil.getInstent(this).getWalletInfoByAddress(uidAddr, MCC_COIN);
                    if (null != wallet && !TextUtils.isEmpty(wallet.getName())) {
                        try {
                            String newNickName = wallet.getName();
                            ChatStatusProvide.setDisplayName(MainActivity.this, userId, newNickName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (TextUtils.isEmpty(avatorUrl)) {
                    String address = AllUtils.getAddressByUid(userId);
                    String logoAssetPath = AppHolder.getLogoAssetsPath(address);
                    if (!TextUtils.isEmpty(logoAssetPath)) {
                        Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                String filePath = FileUtils.copyAssetGetFilePath(logoAssetPath);
                                String fileName = FileUtils.getFileNameOrSuffix(filePath, false, false);
                                Uri uri = UriUtil.getFileUri(MainActivity.this, new File(filePath));
                                ChatStatusProvide.updateAvatar(MainActivity.this, userId, uri, fileName);
                                emitter.onNext(filePath);
                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NextSubscriber<String>() {
                            @Override
                            public void dealData(String filePath) {

                            }

                            @Override
                            protected void dealError(Throwable e) {
                                e.printStackTrace();
                            }
                        });
                    }


                }
            }
        }
    }


    
    private void checkMyPhoneList() {

        String walletPwd = "";
        if (null != getIntent()) {
            Parcelable extra = getIntent().getParcelableExtra(KEY_ARG);
            if (null != extra && extra instanceof HomeActivityArgs) {
                HomeActivityArgs args = (HomeActivityArgs) extra;
                walletPwd = args.getWalletPwd();
            }
        }
        mViewModel.checkMyPhoneList(walletPwd);
    }

    public void succeed(Object obj) {

        if (obj instanceof Notice) {
            Notice notice = (Notice) obj;
            if (notice.mType == Notice.PING_FAIL) {
                
            }
            if (notice.mType == Notice.TEST_NET_STATUS) {
                
                AppController.checkHostAndRefreshGateWayListCache(true);
            }
        }
        if (obj instanceof RxNotice) {
            
            RxNotice rxNotice = (RxNotice) obj;
            if (rxNotice.mType == RxNotice.MSG_LOGOUT) {
                String logOutAddress = rxNotice.mData;
                if (isLogined()) {
                    String loginAddress = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(MainActivity.this));
                    if (!TextUtils.isEmpty(logOutAddress)) {
                        if (logOutAddress.equalsIgnoreCase(loginAddress)) {
                            finish();
                            new SignOutUiWorker(this).forceSignOut();
                            return;
                        } else {
                            
                            return;
                        }
                    } else {
                        
                        finish();
                        new SignOutUiWorker(this).forceSignOut();
                    }
                } else {
                    if (!TextUtils.isEmpty(logOutAddress)) {
                        
                        return;
                    }
                    finish();
                }
            } else if (rxNotice.mType == RxNotice.MSG_DELETE_WALLET) {
                
            } else if (rxNotice.mType == RxNotice.MSG_START_CHAIN_BRIDGE_TASK) {
                if (null != mViewModel) {
                    boolean showError = !TextUtils.isEmpty(rxNotice.mData) && "1".equals(rxNotice.mData);
                    mViewModel.startChainBridgeTask(showError);
                }
            } else if (rxNotice.mType == RxNotice.MSG_REFRESH_CHAT_ENGINE) {
                ChatEngine.getInstance().refresh();
            } else if (rxNotice.mType == RxNotice.MSG_RPC_NODE_CHANGE) {
                
                if (null != mViewModel) {
                    mViewModel.updateChainBridgeRpcNode();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentContainer().getVisibility() == View.VISIBLE) {
            getFragmentContainer().setVisibility(View.GONE);
            hideFragment();
            removeStatusBar();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void showChatFragment() {
        if (!isLogined()) {
            Intent intent = ActivityRouter.getIntent(this, ActivityRouter.Launcher.A_LoginPage);
            startActivity(intent);
            return;
        }
        super.showChatFragment();
        hideFragment();
        Eyes.setStatusBarTextColor(this, true);
        showFloatingButton(View.VISIBLE);
        getFragmentContainer().setVisibility(View.VISIBLE);
        shouldOnResume(1);
        getFragmentContainer().setCurrentItem(1);

    }

    @Override
    public void showCallPhoneFragment() {
        if (!isLogined()) {
            Intent intent = ActivityRouter.getIntent(this, ActivityRouter.Launcher.A_LoginPage);
            startActivity(intent);
            return;
        }
        super.showCallPhoneFragment();
        hideFragment();
        Eyes.setStatusBarTextColor(this, true);
        showFloatingButton(View.VISIBLE);
        getFragmentContainer().setVisibility(View.VISIBLE);
        shouldOnResume(0);
        getFragmentContainer().setCurrentItem(0);
    }

    @Override
    public void showWalletFragment() {
        List<WalletEntity> mWallName = WalletDBUtil.getInstent(MainActivity.this).getWallName();
        if (mWallName == null || mWallName.size() < 1 || (mWallName.get(0).getLevel() != 1 && !TextUtils.isEmpty(ENABLE_CREAT_ALL_WALLET))) {
            Intent intent = new Intent(MainActivity.this, BlockchainLoginActivity.class);
            startActivity(intent);
            return;
        }
        super.showWalletFragment();
        hideFragment();
        Eyes.setStatusBarTextColor(this, true);
        showFloatingButton(View.VISIBLE);
        getFragmentContainer().setVisibility(View.VISIBLE);
        shouldOnResume(2);
        getFragmentContainer().setCurrentItem(2);

        
        mWelcome.initDefaultAssets();
    }

    @Override
    public void showMeFragment() {
        super.showMeFragment();
        hideFragment();
        Eyes.setStatusBarTextColor(this, true);
        showFloatingButton(View.VISIBLE);
        getFragmentContainer().setVisibility(View.VISIBLE);
        shouldOnResume(3);
        getFragmentContainer().setCurrentItem(3);
    }

    @Override
    public void showFloatingButton(int visible) {
        super.showFloatingButton(visible);
        if (visible == View.VISIBLE) FloatingView.get().attach(this);
        else FloatingView.get().detach(this);
    }

    VerApi mApi = new VerApi();
    Gson gson = new Gson();

    @Override
    public void onStableItemClick(Item item) {
        if (item._type != Item.Type.STABLE) {
            return;
        }
        Intent intent = new Intent();
        String address = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(MainActivity.this));
        switch (item._actionValue) {
            case TabActionsHelper.FLAG_DAO://"DAO":
                intent = ActivityRouter.getNewContentIntent(MainActivity.this, ActivityRouter.Launcher.A_DAO);
                break;
            case TabActionsHelper.FLAG_My_QRCode://""://com.app.my.MyQR
            case TabActionsHelper.FLAG_SCAN://""://im.vector.app.features.qrcode.QrCodeScannerActivity
                if (!isLogined()) {
                    intent = ActivityRouter.getIntent(this, ActivityRouter.Launcher.A_LoginPage);
                    break;
                }
                intent = UserCodeActivity.Companion.newIntent(this, sessionHolder.getActiveSession().getMyUserId());
                break;
            case TabActionsHelper.FLAG_FM:
            case TabActionsHelper.FLAG_VPN://"VPN":

            case TabActionsHelper.FLAG_TRADE://"":
                Toast.makeText(this, getString(R.string.stay_tuned), Toast.LENGTH_SHORT).show();
                return;
            case TabActionsHelper.FLAG_TRANSFER://"":
                WalletEntity wallet = WalletDBUtil.getInstent(this).getWalletInfo(MCC_COIN);
                if (null == wallet) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.no_found_wallet_error);
                    return;
                }
                intent = new Intent(this, TransferActivity.class);
                intent.putExtra("amountStr", "0");
                intent.putExtra("toAddress", "");
                intent.putExtra("type", wallet.getType());
                intent.putExtra("tokenName", "dst");
                intent.putExtra("from", TransferActivity.FROM_MAIN);
                startActivity(intent);
                return;
            case TabActionsHelper.FLAG_COLLECT_MONEY://"":
                wallet = WalletDBUtil.getInstent(this).getWalletInfo(MCC_COIN);
                if (null == wallet) {
                    ToastUtil.showToast(com.wallet.ctc.R.string.no_found_wallet_error);
                    return;
                }
                CollectMoneyActivity.startCollectMoneyActivity(this, wallet.getType(), "dst", 18, "DST");
                return;
            case TabActionsHelper.FLAG_DESTROY_MINING://"":
                intent = ActivityRouter.getIntent(MainActivity.this, ActivityRouter.Launcher.A_SM_Pledge);
                
                
                break;
            case TabActionsHelper.FLAG_ACQUISITION_OF_VOTING_RIGHTS://"":
                intent = ActivityRouter.getIntent(this, ActivityRouter.Launcher.A_ZHIYA);
                
                intent.putExtra("param_num", "0");
                break;
            case TabActionsHelper.FLAG_SHARE://""://com.wallet.ctc.ui.me.shareapp.ShareAppActivity
                addSubscription(mApi.getVersion2().compose(RxSchedulers.io_main()).subscribeWith(new NextSubscriber<BaseEntity>() {
                    @Override
                    public void dealData(BaseEntity value) {
                        if (value.getStatus() == 1) {
                            NewVersionBean version = gson.fromJson(gson.toJson(value.getData()), NewVersionBean.class);
                            String downloadUrl = version.share_url;
                            if (!TextUtils.isEmpty(downloadUrl)) {
                                String url = getString(R.string.downloadUrl_tips) + " " + downloadUrl;
                                SystemUtilsKt.startSharePlainTextIntent(MainActivity.this, null, url, "", "");
                            } else {
                                Toast.makeText(MainActivity.this, R.string.down_load_url_unkonw, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }));
                
                
                return;
            case TabActionsHelper.FLAG_RANKINGLIST://""://com.app.levelranking.SMLevelRankingActivity
                intent = needLoginIntent(MainActivity.this, ActivityRouter.Launcher.A_RankList, true);
                break;
            case TabActionsHelper.FLAG_DAPP://"DAPP"://com.wallet.ctc.ui.dapp.list.DappActivity
                intent = ActivityRouter.getIntent(MainActivity.this, ActivityRouter.Launcher.A_Dapp);
                break;
            case TabActionsHelper.FLAG_CHOOSEPOINT://""://com.app.node.NodeListsActivity
                intent = ActivityRouter.getIntent(MainActivity.this, ActivityRouter.Launcher.A_NodeList);
                break;
            case TabActionsHelper.FLAG_ADDRESS://""://com.wallet.ctc.ui.blockchain.addressbook.AddressBookAddActivity
                intent = ActivityRouter.getIntent(MainActivity.this, ActivityRouter.Launcher.A_AddressBook);
                break;
            case TabActionsHelper.FLAG_PLEDGE_MANAGEMENT://"":
                intent = needLoginIntent(MainActivity.this, ActivityRouter.Launcher.A_SM_Pledge, true);
                intent.putExtra("address", address);
                break;
            case TabActionsHelper.FLAG_CROSS_CHAIN_BRIDGE://""://com.wallet.ctc.ui.me.chain_bridge.ChainBridgeActivity
                intent = ActivityRouter.getIntent(MainActivity.this, ActivityRouter.Launcher.A_ChainBirge);
                break;
            case TabActionsHelper.FLAG_CHANGE_LANGUAGE://"":
                changeLanguage(MainActivity.this);
                
                return;
            case TabActionsHelper.FLAG_CHECKUPDATE://"":
                if (!NetUtils.isNetworkConnected(MainActivity.this)) {
                    ToastUtil.showToast(MainActivity.this.getString(com.benny.openlauncher.R.string.connect_failuer_toast));
                }
                new AppVerUtil(MainActivity.this).checkVer(null);
                
                return;
            case TabActionsHelper.FLAG_INFORMATION_ON_THE_CHAIN://"":
                intent = needLoginIntent(MainActivity.this, ActivityRouter.App.A_ChainSyncActivity, true);
                break;
            case TabActionsHelper.FLAG_VIRTUAL_PHONE_BOOK://"":
                
                intent = needLoginIntent(MainActivity.this, ActivityRouter.Launcher.A_VirtualPhone, true);
                intent.putExtra("address", address);
                break;
            case TabActionsHelper.FLAG_PRIVACY_SETTING://"":
                intent = needLoginIntent(MainActivity.this, ActivityRouter.Launcher.A_PrivatePolicy, true);
                break;
            case TabActionsHelper.FLAG_CHAT_SETTING://"":A_SettingActivity
                intent = needLoginIntent(MainActivity.this, ActivityRouter.Launcher.A_SettingActivity, true);
                break;
            case TabActionsHelper.FLAG_ONE_KEY_PUBLISH_COIN://:
                intent = ActivityRouter.getIntent(MainActivity.this, ActivityRouter.Launcher.A_IssuanceCoinActivity);
                break;
            case TabActionsHelper.FLAG_EXCHANGE:
                String url = "file:
                DappWebViewActivity.startDappWebViewActivity(getActivity(), url, getString(R.string.exchange_center), MCC_COIN);
                break;
            case TabActionsHelper.FLAG_COMPUTING_POWER_MANAGEMENT://------- (DVM)
                
                intent = new Intent(getActivity(), PrivateDVMActivity.class);
                break;
            case TabActionsHelper.FLAG_DST_NUMBER://DST
                if (!isLogined()) {
                    intent = ActivityRouter.getIntent(this, ActivityRouter.Launcher.A_LoginPage);
                    break;
                }
                String address2 = AllUtils.getAddressByUid(ChatStatusProvide.getUserId(this));
                startActivity(SMVirtualPhoneActivity.getIntent(getActivity(), address2));
                return;
            case TabActionsHelper.FLAG_DAPP_STORE: 
                intent = new Intent(this, DAppStoreActivity.class);
                break;
            case TabActionsHelper.FLAG_SETTING: 
                intent = new Intent(this, SMNewSettingActivity.class);
                break;
            case TabActionsHelper.FLAG_RECRUIT: 
                String recruitUrl = "file:
                DappWebViewActivity.startDappWebViewActivity(getActivity(), recruitUrl, getString(R.string.super_node_recruit), MCC_COIN);
                break;
            case TabActionsHelper.FLAG_DST_REDUCE_PLAN: 
                intent = new Intent(this, DstReduceActivity.class);
                break;
            default:
                return;
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @Override
    public void startCycleTask() {
        mViewModel.getBlockHeight();
        mViewModel.getPing();
    }

    
    @Override
    public void onAlarmTick() {
        ChatEngine.getInstance().start();
    }

    @Override
    public void onRootWalletTouch(String from, String to) {
        if (TextUtils.isEmpty(to) && !TextUtils.isEmpty(from) && !"Root".equalsIgnoreCase(from)) {
            
            int walletType = AllUtils.getWalletTepyByRootName(from);
            startActivity(ChooseManagerWalletActivity.getIntent(getActivity(), walletType));
        } else if (!TextUtils.isEmpty(to) && !TextUtils.isEmpty(from)) {
            
            if ("Root".equalsIgnoreCase(from) || "Root".equalsIgnoreCase(to)) {
                
                String type = "Root".equalsIgnoreCase(from) ? to : from;
                int walletType = AllUtils.getWalletTepyByRootName(type);
                Intent intent = new Intent(getActivity(), CreatWalletActivity.class);
                intent.putExtra("type", walletType);
                startActivity(intent);
            } else {
                int fromType = AllUtils.getWalletTepyByRootName(from);
                int toType = AllUtils.getWalletTepyByRootName(to);
                startActivity(ChainBridgeActivity2.getIntent(getActivity(), fromType, toType));
            }
        }
    }

    @Override
    public void floatButtonClick(boolean needScroll) {
        if (getFragmentContainer().getVisibility() == View.VISIBLE) {
            hideFragment();
            removeStatusBar();
            if (needScroll) {
                scrollHomeMainPage();
            }

            freshRootWalletInfo();
            
        }
        
    }

    private void hideFragment() {
        Eyes.setStatusBarTextColor(this, false);
        showFloatingButton(View.GONE);
        getFragmentContainer().setVisibility(View.GONE);
    }

    
    private void shouldOnResume(int position) {
        int currentPosition = mVpMain.getCurrentItem();
        if (currentPosition == position) {
            try {
                mTabList.get(currentPosition).getFragment().onResume();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CrashManager.uninstall();
        mAlertDialog = null;
        if (mSysCloseReceiver != null) {
            unregisterReceiver(mSysCloseReceiver);
        }
    }

    private void install() {
        CrashManager.install((thread, throwable) -> new Handler(Looper.getMainLooper()).post(() -> {
            try {
                
                throwable.printStackTrace();
                Log.i(TAG, "=====CrashManager=======" + throwable.getMessage());
                if (BuildConfig.DEBUG) {
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }));
    }

    private Intent needLoginIntent(Context context, String path, boolean isActivity) {
        if (!isLogined()) {
            return ActivityRouter.getIntent(context, ActivityRouter.Launcher.A_LoginPage);
        } else {
            if (isActivity) {
                return ActivityRouter.getIntent(context, path);
            } else {
                return ActivityRouter.getNewContentIntent(context, path);
            }
        }
    }

    
    private AlertDialog onCreateAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.merge_overlay_waiting_view, null);

        mPbText = dialogView.findViewById(R.id.waitingStatusText);
        mPbLoading = dialogView.findViewById(R.id.waitingHorizontalProgress);
        mPbRoot = dialogView.findViewById(R.id.waiting_view);
        mPbRoot.setVisibility(View.VISIBLE);

        builder.setView(dialogView);
        builder.setCancelable(false);
        return builder.create();
    }

    @Override
    public void renderState(boolean show, @NonNull String text, int progress) {
        
        if (null == mAlertDialog) {
            return;
        }
        mPbText.setText(text);
        mPbText.setVisibility(show ? View.VISIBLE : View.GONE);

        mPbLoading.setVisibility(show ? View.VISIBLE : View.GONE);

        mPbLoading.setMax(100);
        mPbLoading.setIndeterminate(false);
        mPbLoading.setProgress(progress);
        mPbRoot.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            mAlertDialog.show();
        } else {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void imShowOrHideLoading(boolean show, @NonNull String msg) {
        
        if (null == mAlertDialog) {
            return;
        }
        mPbText.setText(msg);
        mPbText.setVisibility(show ? View.VISIBLE : View.GONE);
        
        mPbLoading.setVisibility(show ? View.VISIBLE : View.GONE);

        mPbLoading.setIndeterminate(true);
        mPbRoot.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            mAlertDialog.show();
        } else {
            mAlertDialog.dismiss();
        }
    }


    
    private void changeLanguage(Context context) {
        LanguageUtil.showSettingDialog(context, () -> {
            Locale nowLocal = LanguageUtil.getNowLocal(context);
            VectorLocale.INSTANCE.saveApplicationLocale(nowLocal);
            VectorConfiguration configuration = new VectorConfiguration(context);
            configuration.applyToApplicationContext();
            recreate();
        });
    }


    
    private float posX, posY = 0;
    private float currX, currY = 0;

    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void showOptimizedAlertDialog() {
        long millis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = dateFormat.format(millis);
        String today = SpUtil.getToday();
        if (TextUtils.equals(today, format)) {
            return;
        }

        @SuppressLint("UseCompatLoadingForDrawables") Alerter alerter = Alerter.create(this, R.layout.dialog_did_apply).setBackgroundDrawable(getDrawable(R.drawable.bg_did_apply_alert)).setDismissable(false)
                
                .enableInfiniteDuration(true);
        TextView applyBtn = alerter.getLayoutContainer().findViewById(R.id.appCompatButton);
        ImageView collapse = alerter.getLayoutContainer().findViewById(R.id.imgTop);
        applyBtn.setOnClickListener(v -> {
            Eyes.setStatusBarTextColor(this, false);
            startActivity(new Intent(getActivity(), BuyDidGuidActivity.class));
            
        });
        collapse.setOnClickListener(v -> {
            Eyes.setStatusBarTextColor(this, false);
            Alerter.hide();
            SpUtil.saveTodayINfo(format);
        });
        alerter.getLayoutContainer().setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    posX = event.getX();
                    posY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    currX = event.getX();
                    currY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (posY - currY > 10) {
                        
                        Eyes.setStatusBarTextColor(this, false);
                        Alerter.hide();
                    }
                    break;
            }
            return true;
        });
        View parentView = (View) alerter.getLayoutContainer().getParent();
        int topPadding = (int) getResources().getDimension(R.dimen.alerter_alert_view_padding_top);
        parentView.setPadding(0, topPadding, 0, 0);
        alerter.show();
        Eyes.setStatusBarTextColor(this, true);
    }
}
