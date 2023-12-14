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
import com.wallet.ctc.model.blockchain.ChainBridgeCompleteIdsBean;
import com.wallet.ctc.ui.me.chain_bridge2.orders.ChainBridgeOrdersActivity;

import java.util.ArrayList;
import java.util.List;


public class ChainBridgeExCompleteNotice extends BaseChatNotice {

    public static final String EVENT_CHAIN_B_EX_COMPLETE = "chainBCompleteEvent";
    public static final String CHAIN_BRIDGE_COMPLETE_KEY = "chainBridgeComplete";
    public ChainBridgeExCompleteNotice() {
        super(CHAIN_BRIDGE_COMPLETE_KEY);
    }

    @Override
    ChatCStateBean doCheckState() {

        
        ChainBridgeCompleteIdsBean completeOrder = ChatSdk.completeOrder();
        if (completeOrder != null && completeOrder.isSuccess() && null != completeOrder.data && completeOrder.data.size() > 0){
            String orderIdKey = completeOrder.getOrderIdKey();
            if (!TextUtils.isEmpty(orderIdKey)) {
                DeviceGroupNoticeEntity notice = findNotice(orderIdKey, EVENT_CHAIN_B_EX_COMPLETE);
                if (notice == null){
                    
                    DeviceGroupNoticeEntity waitNotice = DbConvertUtils.createNoticeEvent(getLoginUserAddress(), orderIdKey,
                            EVENT_CHAIN_B_EX_COMPLETE, 0, "", EVENT_STATE_WAITE_NOTICE);
                    ChatNoticeDb.getInstance().insertOrUpdateNotices(waitNotice);
                }
            }
        }

        
        List<DeviceGroupNoticeEntity> eventNotices = dbQueryEventNotice(EVENT_CHAIN_B_EX_COMPLETE, EVENT_STATE_WAITE_NOTICE);
        if (null == eventNotices || eventNotices.size() == 0) {
            return null;
        }
        
        List<DeviceGroupBean> systemNoticeGroups = ChatData.getInstance().httpGetSystemGroups();
        DeviceGroupBean systemNoticeGroup = null;
        if (systemNoticeGroups != null && systemNoticeGroups.size() > 0) {
            systemNoticeGroup = systemNoticeGroups.get(0);
        }
        
        List<DeviceGroupBean> noticeGroups = new ArrayList<>();
        if (null != systemNoticeGroup){
            noticeGroups.add(systemNoticeGroup);
        }
        Spannable content = new SpannableString(getString(R.string.cn_chain_b_notice_complete_content));
        ChatCStateBean stateBean = createImportentNoticeState(content, getString(R.string.cn_chain_b_notice_complete_title), "Go", view -> {
            Intent intent = new Intent(getNowActivity(), ChainBridgeOrdersActivity.class);
            intent.putExtra("type",1);
            getNowActivity().startActivity(intent);
        });
        stateBean.headName = getString(R.string.cn_chain_b_notice_error_zhushou);
        stateBean.headRes = R.drawable.icon_notice_chainbridge2;
        stateBean.deviceGroups = noticeGroups;
        
        stateBean.chatMsgType = ChatCStateBean.CHAT_GUID_DPOS_DVM;
        stateBean.chatTips = getString(R.string.cn_chain_b_notice_complete_title);
        stateBean.msgTitle = "";
        stateBean.msgBtnText = getString(R.string.cn_chain_b_notice_error_btn);
        stateBean.msgCusType = CHAIN_BRIDGE_COMPLETE_KEY;
        stateBean.msgContent = getString(R.string.cn_chain_b_notice_complete_content);

        
        
        return stateBean;
    }


    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE) {
            
            if (null != notice) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioUpdateNoticeState(userAddr,
                        EVENT_CHAIN_B_EX_COMPLETE, EVENT_STATE_WAITE_NOTICE, EVENT_STATE_COMPLETE);
            }
        }
    }


}
