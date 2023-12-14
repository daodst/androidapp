package com.app.chat_engine.notice;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.DbConvertUtils;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;
import com.wallet.ctc.crypto.ChatSdk;
import com.wallet.ctc.model.blockchain.ChainBridgeServiceStatusBean;
import com.wallet.ctc.ui.me.chain_bridge2.orders.ChainBridgeOrdersActivity;

import java.util.ArrayList;
import java.util.List;

import common.app.RxBus;
import common.app.my.RxNotice;


public class ChainBridgeExErrorNotice extends BaseChatNotice {

    public static final String EVENT_CHAIN_B_EX_ERROR = "chainBErrorEvent";
    public static final String CHAIN_BRIDGE_ERROR_KEY = "chainBridgeError";
    public ChainBridgeExErrorNotice() {
        super(CHAIN_BRIDGE_ERROR_KEY);
    }

    @Override
    ChatCStateBean doCheckState() {

        
        ChainBridgeServiceStatusBean serviceStatus = ChatSdk.serviceStatus();
        if (serviceStatus == null || !serviceStatus.isRunning()){
            
            RxBus.getInstance().post(new RxNotice(RxNotice.MSG_START_CHAIN_BRIDGE_TASK));
        }
        if (serviceStatus != null) {
            String errorId = serviceStatus.getErrorIdKey();
            
            if(!TextUtils.isEmpty(errorId)){
                DeviceGroupNoticeEntity errorNotice = findNotice(errorId, EVENT_CHAIN_B_EX_ERROR);
                if (errorNotice == null){
                    
                    DeviceGroupNoticeEntity errorWaitNotice = DbConvertUtils.createNoticeEvent(getLoginUserAddress(), errorId,
                            EVENT_CHAIN_B_EX_ERROR, 0, "", EVENT_STATE_WAITE_NOTICE);
                    ChatNoticeDb.getInstance().insertOrUpdateNotices(errorWaitNotice);
                }
            }
        }

        
        List<DeviceGroupNoticeEntity> eventNotices = dbQueryEventNotice(EVENT_CHAIN_B_EX_ERROR, EVENT_STATE_WAITE_NOTICE);
        if (null == eventNotices || eventNotices.size() == 0) {
            return null;
        }
        
        List<DeviceGroupBean> systemNoticeGroups = ChatData.getInstance().httpGetSystemGroups();
        DeviceGroupBean systemNoticeGroup = null;
        if (systemNoticeGroups != null && systemNoticeGroups.size() > 0) {
            systemNoticeGroup = systemNoticeGroups.get(0);
        }
        if (systemNoticeGroup == null) {
            return null;
        }
        
        List<DeviceGroupBean> noticeGroups = new ArrayList<>();
        noticeGroups.add(systemNoticeGroup);

        Spannable content = new SpannableString(getString(R.string.cn_chain_b_notice_error_content));
        ChatCStateBean stateBean = createImportentNoticeState(content, getString(R.string.cn_chain_b_notice_error_title), "Go", view -> {
            Intent intent = new Intent(getNowActivity(), ChainBridgeOrdersActivity.class);
            getNowActivity().startActivity(intent);
        });
        stateBean.headName = getString(R.string.cn_chain_b_notice_error_zhushou);
        stateBean.headRes = R.drawable.icon_notice_chainbridge;
        stateBean.deviceGroups = noticeGroups;
        
        stateBean.chatMsgType = ChatCStateBean.CHAT_GUID_DPOS_DVM;
        stateBean.chatTips = getString(R.string.cn_chain_b_notice_error_title);
        stateBean.msgTitle = "";
        stateBean.msgBtnText = getString(R.string.cn_chain_b_notice_error_btn);
        stateBean.msgCusType = CHAIN_BRIDGE_ERROR_KEY;
        stateBean.msgContent = getString(R.string.cn_chain_b_notice_error_content);

        
        
        return stateBean;
    }


    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE) {
            
            if (null != notice) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioUpdateNoticeState(userAddr,
                        EVENT_CHAIN_B_EX_ERROR, EVENT_STATE_WAITE_NOTICE, EVENT_STATE_COMPLETE);
            }
        }
    }


}
