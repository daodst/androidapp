package com.app.chat_engine.notice;

import android.text.Spannable;
import android.text.SpannableString;

import com.app.R;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;


public class GuidBuyDvmNotice extends BaseChatNotice {

    public static final int TYPE_NO_DVM = 0;
    public static final int TYPE_MORE_DVM = 1;
    private final int mGuidType;

    
    public static final String GUID_BUY_DVM_KEY = "guidBuyDvm";

    public GuidBuyDvmNotice(int type) {
        super(GUID_BUY_DVM_KEY+type);
        mGuidType = type;
    }

    @Override
    ChatCStateBean doCheckState() {
        
        List<DeviceGroupBean> groups = getRecentDayNoNoticeGroups(3);
        if (null == groups || groups.size() == 0) {
            return null;
        }

        if (mGuidType == TYPE_NO_DVM) {
            
            List<DeviceGroupBean> noDvmGroups = new ArrayList<>();
            for (int i=0; i<groups.size(); i++) {
                DeviceGroupBean group = groups.get(i);
                if (null != group && !group.isOwner && !group.isHasDvm()) {
                    if (httpRecent3DayGetDeviceReward(group.groupId)) {
                        noDvmGroups.add(group);
                    }
                }
            }
            if (noDvmGroups.size() == 0) {
                return null;
            }

            Spannable cotent = new SpannableString(getString(R.string.cn_guid_buydvm_content));
            ChatCStateBean stateBean = createImportentNoticeState(cotent, getString(R.string.cn_guid_buydvm_sutitle), "Go", view -> {
                startRoomActivity(noDvmGroups.get(0).groupId);
            });
            stateBean.headRes = R.drawable.icon_notice_guid_buy_dvm;
            stateBean.deviceGroups = noDvmGroups;
            stateBean.chatMsgType = ChatCStateBean.CHAT_GUID_DPOS_DVM;
            stateBean.chatTips = getString(R.string.cn_guid_buydvm_title);
            stateBean.msgTitle = "";
            stateBean.msgBtnText = getString(R.string.cn_guid_buydvm_btntxt);
            stateBean.msgCusType = GUID_BUY_DVM_KEY;
            stateBean.msgContent = getString(R.string.cn_guid_buydvm_content);
            return stateBean;

        } else if(mGuidType == TYPE_MORE_DVM) {
            
            List<DeviceGroupBean> hasDvmGroups = new ArrayList<>();
            for (int i=0; i<groups.size(); i++) {
                DeviceGroupBean group = groups.get(i);
                if (null != group && !group.isOwner && group.isHasDvm() && !group.isBurnRatioUp30()) {
                    if (httpRecent3DayGetDvmReward(group.groupId)) {
                        hasDvmGroups.add(group);
                    }
                }
            }
            if (hasDvmGroups.size() == 0) {
                return null;
            }

            Spannable cotent = new SpannableString(getString(R.string.cn_guid_buydvm_more_content));
            ChatCStateBean stateBean = createNormalNoticeState(cotent, "", null, null);
            stateBean.deviceGroups = hasDvmGroups;
            stateBean.chatMsgType = ChatCStateBean.CHAT_GUID_DPOS_DVM;
            stateBean.chatTips = getString(R.string.cn_guid_buydvm_more_title);
            stateBean.msgTitle = "";
            stateBean.msgBtnText = getString(R.string.cn_guid_buydvm_more_btn);
            stateBean.msgCusType = GUID_BUY_DVM_KEY;
            stateBean.msgContent = getString(R.string.cn_guid_buydvm_more_content);
            return stateBean;
        }
        return null;
    }


}
