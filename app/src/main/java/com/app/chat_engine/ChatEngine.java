package com.app.chat_engine;

import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_DAO;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_DVM;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_POS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.app.chat_engine.notice.AutoJoinGroupNotice;
import com.app.chat_engine.notice.BaseChatNotice;
import com.app.chat_engine.notice.ChainBridgeExCompleteNotice;
import com.app.chat_engine.notice.ChainBridgeExErrorNotice;
import com.app.chat_engine.notice.Continue2DayActiveNotice;
import com.app.chat_engine.notice.DailyReportNotice;
import com.app.chat_engine.notice.ExitDeviceGroupNotice;
import com.app.chat_engine.notice.FirstJoinGroupNotice;
import com.app.chat_engine.notice.GetAirDropWalletNotice;
import com.app.chat_engine.notice.GetIncomeNotice;
import com.app.chat_engine.notice.GroupLevelNotice;
import com.app.chat_engine.notice.GroupMoveNotice;
import com.app.chat_engine.notice.GroupVoteNotice;
import com.app.chat_engine.notice.GuidBuyDvmNotice;
import com.app.chat_engine.notice.GuidCreateGroupNotice;
import com.app.chat_engine.notice.NullChatNotice;
import com.app.chat_engine.notice.RandomActiveNotice;
import com.app.chat_engine.notice.YesterdayNoGetIncomeNotice;
import com.app.chat_engine.notice.YestordayNoActiveNotice;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import common.app.AppApplication;
import common.app.utils.NetWorkUtils;
import common.app.utils.SpUtil;
import im.vector.app.provide.ChatStatusProvide;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ChatEngine {

    public static final String PRE = "CE_";
    private static final String TAG = PRE + "ChatEngine";
    private List<IChatNotify> mChatList;
    private List<IChatNotify> mNoneChatList;
    private long mLastLoginTime = 0;

    private static class HolderClass {
        private static final ChatEngine INSTANCE = new ChatEngine();
    }

    public static ChatEngine getInstance() {
        ChatEngine intance = HolderClass.INSTANCE;
        return intance;
    }

    private ChatEngine() {
        
        mNoneChatList = new ArrayList<>();
        mNoneChatList.add(new GetAirDropWalletNotice());
        mNoneChatList.add(new ChainBridgeExErrorNotice());
        mNoneChatList.add(new ChainBridgeExCompleteNotice());


        mChatList = new ArrayList<>();
        
        mChatList.add(new GroupMoveNotice(GroupMoveNotice.FLAG_WAIT_MOVE));
        mChatList.add(new GroupMoveNotice(GroupMoveNotice.FLAG_MOVE_SUCCESS));
        mChatList.add(new AutoJoinGroupNotice(AutoJoinGroupNotice.FLAG_WAIT_JOIN));
        mChatList.add(new AutoJoinGroupNotice(AutoJoinGroupNotice.FLAG_JOIN_SUCCESS));
        mChatList.add(new ExitDeviceGroupNotice());

        mChatList.add(new FirstJoinGroupNotice());
        mChatList.add(new GroupVoteNotice());


        
        mChatList.add(new GetIncomeNotice(TIMELINE_AWARD_TYPE_DVM));
        mChatList.add(new GetIncomeNotice(TIMELINE_AWARD_TYPE_DAO));
        mChatList.add(new GetIncomeNotice(TIMELINE_AWARD_TYPE_POS));

        mChatList.add(new GroupLevelNotice());

        mChatList.add(new DailyReportNotice());
        mChatList.add(new YestordayNoActiveNotice());
        mChatList.add(new Continue2DayActiveNotice());
        mChatList.add(new GuidBuyDvmNotice(GuidBuyDvmNotice.TYPE_NO_DVM));
        mChatList.add(new GuidBuyDvmNotice(GuidBuyDvmNotice.TYPE_MORE_DVM));
        mChatList.add(new GuidCreateGroupNotice());
        mChatList.add(new YesterdayNoGetIncomeNotice());
        mChatList.add(new RandomActiveNotice());
    }

    
    private Context getAppContext() {
        return AppApplication.getContext();
    }

    
    private Activity getNowActivity() {
        return AppApplication.currentActivity();
    }

    
    private String getNodeUrl() {
        return SpUtil.getDefNode(WalletUtil.MCC_COIN);
    }

    
    private boolean isLogined() {
        Context context = getAppContext();
        if (null == context) {
            return false;
        }
        return ChatStatusProvide.loginStatus(context);
    }

    
    private String getLoginUid() {
        Context context = getAppContext();
        if (null == context) {
            return "";
        }
        
        String userId = ChatStatusProvide.getUserId(context);
        return userId;
    }

    private String getLoginUAddr() {
        String uid = getLoginUid();
        return AllUtils.getAddressByUid(uid);
    }

    
    private void updateLoginTime() {
        if (mLastLoginTime == 0) {
            
            mLastLoginTime = System.currentTimeMillis();
        } else {
            
            long useTime = System.currentTimeMillis() - mLastLoginTime;
            if (useTime > 0) {
                long addSenconds = useTime /1000;
                if (addSenconds > 0 && addSenconds >= 60) {
                    
                    ChatNoticeSp.updateAddLoginTime(getLoginUAddr(), addSenconds);
                }
            }
        }
    }

    
    public void resetLoginTime() {
        mLastLoginTime = System.currentTimeMillis();
        ChatNoticeSp.reSetHasLoginTime(getLoginUAddr());
    }

    
    @SuppressLint("CheckResult")
    public void insertGroupVoteNotice(String groupId, String senderNickName){
        if (TextUtils.isEmpty(groupId) || TextUtils.isEmpty(senderNickName)){
            return;
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                ChatNoticeDb.getInstance().insertOrUpdateGroupVoteEvent(getLoginUAddr(), groupId, senderNickName);
                emitter.onNext(1);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status->{
                    if (status == 1){
                        refresh();
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    
    Disposable mSyncDisposable;
    private long mLastSyncTime, mControlTime = 0L;
    AtomicBoolean mSyncState = new AtomicBoolean(false);
    AtomicBoolean mRefreshFlag = new AtomicBoolean(false);
    public void start() {
        start(false);
    }

    public void refresh(){
        start(true);
    }

    public void start(boolean forceRefresh) {
        
        if (!forceRefresh && System.currentTimeMillis() - mControlTime < 60000) {
            return;
        }
        mControlTime = System.currentTimeMillis();
        if (forceRefresh) {
            mRefreshFlag.set(forceRefresh);
        }

        if (!isNetConnected()) {
            logw("network is no connected");
            return;
        }

        if (!isLogined()) {
            logw("user is no logined");
            return;
        }

        
        updateLoginTime();

        
        if (mSyncState.get()) {
            long periodTime = (System.currentTimeMillis() - mLastSyncTime) / 1000;
            
            if (periodTime < 3600) {
                logw("syncing return。。。");
                return;
            } else {
                loge("out of one hour, maybe exception ");
            }
        }
        if (mSyncDisposable != null) {
            
            mSyncDisposable.dispose();
        }
        mRefreshFlag.set(false);

        Observable.create((ObservableOnSubscribe<IChatNotify>) e -> {
            mSyncState.set(true);
            mLastSyncTime = System.currentTimeMillis();

            IChatNotify chatResult = null;

            String url = getNodeUrl();
            long netState = NetWorkUtils.pingIpAddress2(url);
            if (netState == -1) {
                
                chatResult = new NullChatNotice();
            } else {
                
                chatResult = doStart();
            }
            if (chatResult == null) {
                chatResult = new NullChatNotice();
            }
            e.onNext(chatResult);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<IChatNotify>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mSyncDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull IChatNotify syncStateBean) {
                        mSyncState.set(false);
                        mSyncDisposable = null;
                        mLastSyncTime = System.currentTimeMillis();
                        logi("over----");
                        if (null != syncStateBean && syncStateBean instanceof BaseChatNotice) {
                            syncStateBean.process();
                        }
                        ChatNoticeDb.getInstance().ioTestPrint("after process noticess-------");
                        if (mRefreshFlag.get()) {
                            start(false);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        loge("startSync() ：" + e.getMessage());
                        mSyncState.set(false);
                        mSyncDisposable = null;
                        mLastSyncTime = System.currentTimeMillis();
                        if (mRefreshFlag.get()) {
                            start(false);
                        }
                    }

                    @Override
                    public void onComplete() {
                        logi("sync onComplete");
                    }
                });

    }


    
    private IChatNotify doStart() {

        
        ChatData.getInstance().init();

        IChatNotify result = null;
        
        if (null != mNoneChatList && mNoneChatList.size() > 0) {
            for (IChatNotify notice : mNoneChatList) {
                ChatCStateBean noticeState = notice.checkState();
                if (noticeState != null) {
                    result = notice;
                    break;
                }
            }
        }

        
        List<DeviceGroupBean> deviceGroups = ChatData.getInstance().httpGetMyDeviceGroups();
        if (result == null && deviceGroups != null && deviceGroups.size() > 0 && mChatList != null && mChatList.size() > 0) {
            for (IChatNotify notice : mChatList) {
                ChatCStateBean noticeState = notice.checkState();
                if (noticeState != null) {
                    result = notice;
                    break;
                }
            }
        }
        return result;
    }

    private void logi(String msg) {
        Log.i(TAG, msg);
    }

    private void logw(String msg) {
        Log.w(TAG, msg);
    }

    private void loge(String msg) {
        Log.e(TAG, msg);
    }


    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    
    protected boolean isNetConnected() {
        if (null == connectivityManager) {
            connectivityManager = (ConnectivityManager) getAppContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
