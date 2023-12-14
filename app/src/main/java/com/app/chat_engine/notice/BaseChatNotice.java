package com.app.chat_engine.notice;

import static com.app.chat_engine.ChatEngine.PRE;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_AIRDROP;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_DAO;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_DVM;
import static im.vector.app.features.home.room.detail.TimelineFragmentKt.TIMELINE_AWARD_TYPE_POS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.DbConvertUtils;
import com.app.chat_engine.IChatNotify;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;
import com.wallet.ctc.model.blockchain.EvmosGetIncomeHistoryBean;
import com.wallet.ctc.model.blockchain.RpcApi;
import com.wallet.ctc.util.AllUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.utils.TimeUtil;
import im.vector.app.provide.ChatStatusProvide;


public abstract class BaseChatNotice implements IChatNotify {

    protected final int STATE_UN_KOWN = 0;
    protected final int STATE_ACTIVE = 1;
    protected final int STATE_NO_ACTIVE = 2;

    public static final int FLAG_NONE = 0;

    
    public static final int EVENT_STATE_NONE = 0;
    public static final int EVENT_STATE_WAITE_NOTICE = 1;
    public static final int EVENT_STATE_NOTICE_ONE = 2;
    public static final int EVENT_STATE_NOTICE_TWO = 3;
    public static final int EVENT_STATE_COMPLETE = 4;


    protected RpcApi mRpcApi;

    protected String key;
    protected ChatCStateBean notice;
    public BaseChatNotice(String key) {
        this.key = PRE+key;
    }


    @Override
    public ChatCStateBean checkState() {
        logi("checkState()------");
        ChatCStateBean result = doCheckState();
        notice = result;
        logi("result="+result);
        return result;
    }

    @Override
    public int process() {
        int result = doProcess();
        if (null != notice && result == STATE_UN_KOWN) {
            
           result = defaultProcess(notice);
        }
        if (result == STATE_ACTIVE) {
            updateNoticedTime();
        }
        afterProcess(result);
        return result;
    }

    abstract ChatCStateBean doCheckState();

    
    protected int doProcess() {
        return STATE_UN_KOWN;
    }

    
    protected void afterProcess(int processResult) {

    }

    
    private int defaultProcess(ChatCStateBean notice) {
        Activity activity = getNowActivity();
        if (null == activity || activity.isDestroyed() || activity.isFinishing()) {
            logw("defaultProcess error : activity is null or destroyed " + activity);
            return STATE_NO_ACTIVE;
        }
        if (null == notice || null == notice.content) {
            loge("defaultProcess notice is null or content is null");
            return STATE_NO_ACTIVE;
        }

        
        int insertChatMsgResult = insertLocalChatMsg(notice);
        if (insertChatMsgResult == STATE_ACTIVE) {
            
            insertChatMsgResult = showAlertDialog(notice);
        }
        return insertChatMsgResult;
    }

    
    protected int insertLocalChatMsg(ChatCStateBean notice) {
        int result = STATE_ACTIVE;
        if (notice.deviceGroups != null && notice.deviceGroups.size() > 0 && notice.hasCusChatMsg()) {
            boolean hasSuccess = false;
            Context appContext = getAppContext();
            List<ChatCStateBean.CusChatMsg> cusMsgs = notice.getCusMsgList();
            for (int i=0; i<cusMsgs.size(); i++) {
                ChatCStateBean.CusChatMsg cusMsg = cusMsgs.get(i);
                String chatTips = cusMsg.chatTips;
                String msgContent = cusMsg.getMsgContent();
                String msgTitle = cusMsg.getMsgTitle();
                int chatMsgType = cusMsg.chatMsgType;
                String userName = cusMsg.senderNickName;
                int chatMsgSubType = cusMsg.chatMsgSubType;
                String msgBtnText = cusMsg.msgBtnText;
                String msgCusType = cusMsg.msgCusType;
                String msgCusParams = cusMsg.msgCusParams == null ? "" : cusMsg.msgCusParams;
                for (DeviceGroupBean deviceGroup : notice.deviceGroups) {
                    boolean success = false;
                    if (chatMsgType == ChatCStateBean.CHAT_NORMAL) {
                        
                        success = ChatStatusProvide.insertLocalCusTxtMessage(appContext, deviceGroup.groupId, chatTips,
                                userName, msgTitle, msgContent, chatMsgSubType);

                    } else if(chatMsgType == ChatCStateBean.CHAT_GET_DEVICE_INCOME) {
                        
                        success = ChatStatusProvide.insertLocalRewardMessage(appContext, deviceGroup.groupId, TIMELINE_AWARD_TYPE_POS);
                    } else if(chatMsgType == ChatCStateBean.CHAT_GET_DVM_INCOME) {
                        
                        success = ChatStatusProvide.insertLocalRewardMessage(appContext, deviceGroup.groupId, TIMELINE_AWARD_TYPE_DVM);
                    } else if (chatMsgType == ChatCStateBean.CHAT_GET_OWNER_INCOME) {
                        
                        success = ChatStatusProvide.insertLocalRewardMessage(appContext, deviceGroup.groupId, TIMELINE_AWARD_TYPE_DAO);
                    } else if(chatMsgType == ChatCStateBean.CHAT_GET_AIRDROP_INCOME) {
                        
                        success = ChatStatusProvide.insertLocalRewardMessage(appContext, deviceGroup.groupId, TIMELINE_AWARD_TYPE_AIRDROP);
                    } else if(chatMsgType == ChatCStateBean.CHAT_GUID_DPOS_DVM) {
                        
                        success = ChatStatusProvide.insertLocalBuyDvmMessage(appContext, deviceGroup.groupId,
                                chatTips, userName, msgTitle, msgContent, msgBtnText, msgCusType, msgCusParams);
                    } else if(chatMsgType == ChatCStateBean.CHAT_GUID_CREATE_GROUP) {
                        
                        success = ChatStatusProvide.insertLocalCreateGroupMessage(appContext, deviceGroup.groupId);
                    } else if(chatMsgType == ChatCStateBean.CHAT_DAILY_REPORT) {
                        
                        
                    }
                    if (success) {
                        hasSuccess = true;
                    }
                }
            }
            if (hasSuccess) {
                result = STATE_ACTIVE;
            } else {
                result = STATE_NO_ACTIVE;
            }
        }
        return result;
    }

    
    protected int showAlertDialog(ChatCStateBean notice) {
        int result = STATE_UN_KOWN;
        if (notice.showType == ChatCStateBean.SHOW_IMPORTANT) {
            
            result = showImportentAlertDialog(notice);
        } else if (notice.showType == ChatCStateBean.SHOW_NOTICE) {
            
            result = showSmallNoticeAlertDialog(notice);
        } else if(notice.showType == ChatCStateBean.SHOW_NONE) {
            
            result = STATE_ACTIVE;
        } else {
            
            result = showNormalAlertDialog(notice);
        }
        return result;
    }

    
    protected String getLoginUserAddress() {
        String userId = ChatStatusProvide.getUserId(getAppContext());
        String address = AllUtils.getAddressByUid(userId);
        return address;
    }

    
    protected void startRoomActivity(String groupId) {
        Intent intent = ActivityRouter.getIntent(getNowActivity(), ActivityRouter.Im.A_DetailChatActivity);
        intent.setAction("ROOM_DETAILS_FROM_SHORTCUT");
        intent.putExtra("EXTRA_ROOM_ID", groupId);
        getNowActivity().startActivity(intent);
    }

    
    protected List<DeviceGroupBean> httpGetGroupsByNoticeList(List<DeviceGroupNoticeEntity> noticeList) {
        Map<String, DeviceGroupBean> groupMaps = DbConvertUtils.groupListToMap(ChatData.getInstance().httpGetMyDeviceGroups());
        if (null == groupMaps || groupMaps.isEmpty()) {
            return null;
        }
        
        List<DeviceGroupBean> gropuList = new ArrayList<>();
        for (DeviceGroupNoticeEntity notice : noticeList) {
            DeviceGroupBean group = groupMaps.get(notice.getGroupId());
            if (null != group) {
                gropuList.add(group);
            }
        }
        return gropuList;
    }

    
    protected boolean isFirstNotice(String groupId) {
        String userAddr = getLoginUserAddress();
        DeviceGroupNoticeEntity beforeNotice = ChatNoticeDb.getInstance().getNoticeInfo(userAddr, groupId, key);
        DeviceGroupNoticeEntity noGroupNotice = ChatNoticeDb.getInstance().getNoGroupFlag(userAddr);
        if (beforeNotice == null && noGroupNotice != null) {
            return true;
        } else {
            return false;
        }
    }

    
    protected DeviceGroupNoticeEntity findNotice(String groupId, String keyType) {
        String userAddr = getLoginUserAddress();
        return ChatNoticeDb.getInstance().getNoticeInfo(userAddr, groupId, keyType);
    }

    
    protected List<DeviceGroupNoticeEntity> dbQueryEventNotice(String keyType, int state) {
        String userAddr = getLoginUserAddress();
        return ChatNoticeDb.getInstance().findEventNoticeList(userAddr, keyType, state);
    }


    
    private ChatCStateBean createNoticeState(int showType, Spannable content, String subTitle,
                    String btnTex, View.OnClickListener btnclickListener) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        return ChatCStateBean.createNoticeState(key, showType, content, subTitle, btnTex, btnclickListener);
    }

    
    protected ChatCStateBean createNormalNoticeState(Spannable content, String subTitle,
                                                     String btnTex, View.OnClickListener btnclickListener) {
        return createNoticeState(ChatCStateBean.SHOW_NORMAL, content, subTitle, btnTex, btnclickListener);
    }

    
    protected ChatCStateBean createImportentNoticeState(Spannable content, String subTitle,
                                                     String btnTex, View.OnClickListener btnclickListener) {
        return createNoticeState(ChatCStateBean.SHOW_IMPORTANT, content, subTitle, btnTex, btnclickListener);
    }

    
    protected ChatCStateBean createSmallNoticeState(Spannable content, String subTitle,
                                                        String btnTex, View.OnClickListener btnclickListener) {
        return createNoticeState(ChatCStateBean.SHOW_NOTICE, content, subTitle, btnTex, btnclickListener);
    }

    
    protected List<DeviceGroupBean> getNowDayNoNoticeGroups(boolean requerIsOwner) {
        
        List<DeviceGroupBean> groups = ChatData.getInstance().httpGetMyDeviceGroups();
        if (null == groups || groups.size() == 0) {
            logw("device groups is empty");
            return null;
        }

        String loginAddress = getLoginUserAddress();
        if (TextUtils.isEmpty(loginAddress)) {
            loge("login user address is null");
            return null;
        }

        
        List<DeviceGroupBean> nowDayNoNoticedGroups = new ArrayList<>();
        String userAddr = getLoginUserAddress();
        for (DeviceGroupBean group: groups) {
            if (requerIsOwner && !group.isOwner) {
                
                continue;
            }
            DeviceGroupNoticeEntity entity = ChatNoticeDb.getInstance().getNoticeInfo(userAddr, group.groupId, key);
            if (null == entity || !entity.nowDayHasNoticed()) {
                nowDayNoNoticedGroups.add(group);
            }
        }
        return nowDayNoNoticedGroups;
    }

    
    protected List<DeviceGroupBean> getNowDayNoNoticeGroups() {
        return getNowDayNoNoticeGroups(false);
    }


    
    protected List<DeviceGroupBean> getRecentDayNoNoticeGroups(int xday) {
        
        List<DeviceGroupBean> groups = ChatData.getInstance().httpGetMyDeviceGroups();
        if (null == groups || groups.size() == 0) {
            logw("device groups is empty");
            return null;
        }

        String loginAddress = getLoginUserAddress();
        if (TextUtils.isEmpty(loginAddress)) {
            loge("login user address is null");
            return null;
        }

        
        List<DeviceGroupBean> noNoticedGroups = new ArrayList<>();
        String userAddr = getLoginUserAddress();
        for (DeviceGroupBean group: groups) {
            DeviceGroupNoticeEntity entity = ChatNoticeDb.getInstance().getNoticeInfo(userAddr, group.groupId, key);
            if (null == entity || !entity.recentXDayHasNoticed(xday)) {
                noNoticedGroups.add(group);
            }
        }
        return noNoticedGroups;
    }


    
    protected boolean nowDayHasNoticed(String groupId) {
        String userAddr = getLoginUserAddress();
        DeviceGroupNoticeEntity noticeEntity = ChatNoticeDb.getInstance().getNoticeInfo(userAddr, groupId, key);

        if (null == noticeEntity || noticeEntity.getNoticeTime() == 0) {
            return false;
        }
        long lastNoticeTime = noticeEntity.getNoticeTime();
        String dayStr = TimeUtil.getYYYYMMdd(lastNoticeTime);
        String nowDayStr = TimeUtil.getYYYYMMdd(System.currentTimeMillis());
        if (nowDayStr.equals(dayStr)) {
            
            return true;
        } else {
            return false;
        }
    }

    
    protected void updateNoticedTime() {
        if (notice != null) {
            String userAddr = getLoginUserAddress();
            List<DeviceGroupBean> groups = notice.deviceGroups;
            String value = notice.value;
            if (null != groups && groups.size() > 0) {
                for (DeviceGroupBean group:groups) {
                    String groupId = group.groupId;
                    ChatNoticeDb.getInstance().ioInsertOrUpdateNoticeTime(userAddr, groupId, key, System.currentTimeMillis(), value);
                }
            } else {
                ChatNoticeDb.getInstance().ioInsertOrUpdateNoticeTime(userAddr, "", key, System.currentTimeMillis(), value);
            }
        }
    }

    
    protected boolean recent3DayHasNoticed(String groupId) {
        return recentXDayHasNoticed(3, groupId);
    }

    
    protected boolean recent2DayHashNoticed(String groupId) {
        return recentXDayHasNoticed(2, groupId);
    }

    
    protected boolean recent7DayHasNoticed(String groupId) {
        return recentXDayHasNoticed(7, groupId);
    }

    
    protected boolean recentXDayHasNoticed(int xday, String groupId) {
        String userAddr = getLoginUserAddress();
        DeviceGroupNoticeEntity noticeEntity = ChatNoticeDb.getInstance().getNoticeInfo(userAddr, groupId, key);
        if (null == noticeEntity || noticeEntity.getNoticeTime() == 0) {
            return false;
        }
        long lastNoticeTime = noticeEntity.getNoticeTime();
        long nowDay = System.currentTimeMillis();
        long xDayTime = xday * 86400000;
        if(Math.abs(nowDay - lastNoticeTime) > xDayTime) {
            
            return false;
        } else {
            
            return true;
        }
    }

    
    protected int httpIsTodayActive() {
        
        String recent7DayActive = httpRecent7DayActive();
        if (TextUtils.isEmpty(recent7DayActive) || recent7DayActive.length() < 1) {
            return STATE_UN_KOWN;
        }
        String yesterday = recent7DayActive.substring(recent7DayActive.length()-1);
        if ("1".equals(yesterday)) {
            return STATE_ACTIVE;
        } else {
            return STATE_NO_ACTIVE;
        }
    }

    
    protected int httpYesterdayActive() {
        String recent7DayActive = httpRecent7DayActive();
        if (TextUtils.isEmpty(recent7DayActive) || recent7DayActive.length() < 2) {
            return STATE_UN_KOWN;
        }
        String yesterday = recent7DayActive.substring(recent7DayActive.length()-2, recent7DayActive.length()-1);
        if ("1".equals(yesterday)) {
            return STATE_ACTIVE;
        } else {
            return STATE_NO_ACTIVE;
        }
    }

    
    protected int httpContinue2DayActive() {
        String recent7DayActive = httpRecent7DayActive();
        if (TextUtils.isEmpty(recent7DayActive) || recent7DayActive.length() < 3) {
            return STATE_UN_KOWN;
        }
        String day2 = recent7DayActive.substring(recent7DayActive.length()-3, recent7DayActive.length()-1);
        if ("11".equals(day2)) {
            
            return STATE_ACTIVE;
        } else {
            return STATE_NO_ACTIVE;
        }
    }

    
    protected String httpRecent7DayActive() {
        Context context = AppApplication.getContext();
        if (context == null) {
            return "";
        }
        String recent7DayActive = ChatStatusProvide.httpRecent7DayActive(context);
        return recent7DayActive;
    }

    
    protected boolean httpRecent3DayGetDeviceReward(String groupId) {
        List<Integer> array = httpRecent3DayGetIncomeHistory(groupId, 0);
        if (array != null && array.size() == 3 && array.get(0) == 2 && array.get(1) ==2 && array.get(2)==2) {
            return true;
        } else {
            return false;
        }
    }


    
    protected boolean httpYesterdayNoGetDeviceReward(String groupId) {
        List<Integer> array = httpRecent3DayGetIncomeHistory(groupId, 0);
        if (array != null && array.size() >1 && array.get(0) == 1) {
            return true;
        } else {
            return false;
        }
    }

    
    protected boolean httpRecent3DayGetDvmReward(String groupId) {
        List<Integer> array = httpRecent3DayGetIncomeHistory(groupId, 1);
        if (array != null && array.size() == 3 && array.get(0) == 2 && array.get(1) ==2 && array.get(2) ==2) {
            return true;
        } else {
            return false;
        }
    }

    
    @SuppressLint("CheckResult")
    protected List<Integer> httpRecent3DayGetIncomeHistory(String groupId, int type) {
        if (null == mRpcApi) {
            mRpcApi = new RpcApi();
        }
        String userId = ChatStatusProvide.getUserId(getAppContext());
        String address = AllUtils.getAddressByUid(userId);
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        AtomicReference<EvmosGetIncomeHistoryBean> historyAtomic = new AtomicReference<>();
        mRpcApi.getEvmosGetIncomeHistory(address, groupId).subscribe(evmosGetIncomeHistoryBean -> {
            historyAtomic.set(evmosGetIncomeHistoryBean);
        }, throwable -> {
            throwable.printStackTrace();
            loge("getEvmosGetIncomeHistory exception:"+throwable.getMessage());
        });
        EvmosGetIncomeHistoryBean history = historyAtomic.get();
        if (null != history && history.isSuccess()) {
            if (type == 0) {
                return history.getDeviceHistory();
            } else {
                return history.getDvmHistory();
            }
        } else {
            String error = history != null ? history.getInfo() : "history is return null";
            loge("getEvmosGetIncomeHistory fail message:"+error);
            return null;
        }
    }


    
    protected int isReachNoticeTime(String groupId) {
        String userAddr = getLoginUserAddress();
        DeviceGroupNoticeEntity noticeEntity = ChatNoticeDb.getInstance().getNoticeInfo(userAddr, groupId, key);


        if (null == noticeEntity || noticeEntity.getNextNoticeTime() == 0) {
            return STATE_UN_KOWN;
        }
        long nextTime = noticeEntity.getNextNoticeTime();
        if (System.currentTimeMillis() >= nextTime) {
            return STATE_ACTIVE;
        } else {
            return STATE_NO_ACTIVE;
        }
    }

    protected void ioUpdateNextNoticeTime(long time, String groupId) {
        String userAddr = getLoginUserAddress();
        ChatNoticeDb.getInstance().ioInsertOrUpdateNextNoticeTime(userAddr, groupId, key, time, "");
    }

    
    protected void updateNextNoticeTime(long time, String groupId) {
        String userAddr = getLoginUserAddress();
        ChatNoticeDb.getInstance().insertOrUpdateNextNoticeTime(userAddr, groupId, key, time, "");
    }



    
    
    protected Context getAppContext() {
        return AppApplication.getContext();
    }
    
    protected Activity getNowActivity() {
        return AppApplication.currentActivity();
    }


    
    protected Alerter createAlerterDialog(int dialgLayout) {
        Activity activity = getNowActivity();
        if (null == activity || activity.isDestroyed() || activity.isFinishing()) {
            logw("showChatAlert error : activity is null or destroyed " + activity);
            return null;
        }
        Alerter alerter = Alerter.create(activity, dialgLayout)
                .setDuration(10000)
                .enableSwipeToDismiss()
                .setDismissable(true)
                .enableVibration(true)
                .setContentGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP)
                .setElevation(2f)
                .setBackgroundColorInt(Color.TRANSPARENT)
                .setSound();
        return alerter;
    }

    
    protected int showNormalAlertDialog(ChatCStateBean notice) {
        if (null == notice) {
            logw("showNormal:notice == null");
            return STATE_NO_ACTIVE;
        }
        Alerter alerter = createAlerterDialog(R.layout.dialog_top_chat);
        if (null == alerter) {
            logw("showNormal:alerter is null");
            return STATE_NO_ACTIVE;
        }
        Alert alert = alerter.show();
        if (null == alert) {
            logw("showNormal: alert is null");
            return STATE_NO_ACTIVE;
        }
        ImageView headIv = alert.findViewById(R.id.headIv);
        TextView nameTv = alert.findViewById(R.id.nameTv);
        TextView btn = alert.findViewById(R.id.clickBtn);
        TextView contentTv = alert.findViewById(R.id.contentTv);
        ViewGroup backgroundV = alert.findViewById(R.id.llAlertBackground);
        if (null != backgroundV) {
            backgroundV.setPadding(0, 0, 0, 0);
        }
        if (notice.headRes > 0) {
            headIv.setImageResource(notice.headRes);
        }
        if (!TextUtils.isEmpty(notice.headName)) {
            nameTv.setText(notice.headName);
        }
        if (!TextUtils.isEmpty(notice.btnText)) {
            btn.setText(notice.btnText);
            btn.setOnClickListener(notice.btnClickListener);
        } else {
            btn.setText("");
            btn.setOnClickListener(null);
        }
        if (null != notice.content) {
            contentTv.setText(notice.content);
        }
        return STATE_ACTIVE;
    }


    
    protected int showImportentAlertDialog(ChatCStateBean notice) {
        if (null == notice) {
            logw("showImportent:notice == null");
            return STATE_NO_ACTIVE;
        }
        Alerter alerter = createAlerterDialog(R.layout.dialog_top_chat_2);
        if (null == alerter) {
            logw("showImportent:notice == null");
            return STATE_NO_ACTIVE;
        }
        Alert alert = alerter.show();
        if (alert == null) {
            logw("showImportent:notice == null");
            return STATE_NO_ACTIVE;
        }
        ImageView headIv = alert.findViewById(R.id.ivLogo);
        TextView nameTv = alert.findViewById(R.id.tvName);
        TextView subNameTv = alert.findViewById(R.id.tvSubTitle);
        ImageView btn = alert.findViewById(R.id.ivGo);
        TextView contentTv = alert.findViewById(R.id.tvContent);
        ViewGroup backgroundV = alert.findViewById(R.id.llAlertBackground);
        if (null != backgroundV) {
            backgroundV.setPadding(0, 0, 0, 0);
        }

        if (notice.headRes > 0) {
            headIv.setImageResource(notice.headRes);
        }
        if (!TextUtils.isEmpty(notice.headName)) {
            nameTv.setText(notice.headName);
        }
        if (!TextUtils.isEmpty(notice.subName)) {
            subNameTv.setText(notice.subName);
        }
        if (!TextUtils.isEmpty(notice.btnText)) {
            
            btn.setOnClickListener(notice.btnClickListener);
            alert.setOnClickListener(notice.btnClickListener);
        } else {
            
            btn.setOnClickListener(null);
            alert.setOnClickListener(null);
        }
        if (null != notice.content) {
            contentTv.setText(notice.content);
        }
        return STATE_ACTIVE;
    }


    
    protected int showSmallNoticeAlertDialog(ChatCStateBean notice) {
        if (null == notice) {
            logw("showSmall:notice == null");
            return STATE_NO_ACTIVE;
        }
        Alerter alerter = createAlerterDialog(R.layout.dialog_top_chat_1);
        if (null == alerter) {
            logw("showSmall:notice == null");
            return STATE_NO_ACTIVE;
        }
        Alert alert = alerter.show();
        if (alert == null) {
            logw("showSmall:notice == null");
            return STATE_NO_ACTIVE;
        }

        TextView contentTv = alert.findViewById(R.id.contentTv);
        ViewGroup backgroundV = alert.findViewById(R.id.llAlertBackground);
        if (null != backgroundV) {
            backgroundV.setPadding(0, 0, 0, 0);
        }
        if (null != notice.content) {
            contentTv.setText(notice.content);
        }
        return STATE_ACTIVE;
    }



    
    protected String getString(int strRes) {
        Context appContext = getAppContext();
        if (appContext == null) {
            return "";
        }
        return appContext.getString(strRes);
    }


    protected void logi(String msg) {
        Log.i(key, msg);
    }

    protected void logw(String msg) {
        Log.w(key, msg);
    }

    protected void loge(String msg) {
        Log.e(key, msg);
    }



}
