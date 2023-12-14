package com.app.chat_engine.notice;

import android.text.Spannable;
import android.text.SpannableString;

import com.app.R;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;


public class GuidCreateGroupNotice extends BaseChatNotice {

    public GuidCreateGroupNotice() {
        super("guidCreateGroup");
    }

    @Override
    ChatCStateBean doCheckState() {
        
        List<DeviceGroupBean> groups = getRecentDayNoNoticeGroups(7);
        if (null == groups || groups.size() == 0) {
            return null;
        }
        boolean hasCreatedGroup = false;
        
        List<DeviceGroupBean> canCreateGroups = new ArrayList<>();
        for (int i=0; i<groups.size(); i++) {
            DeviceGroupBean group = groups.get(i);
            if (null != group && !group.isOwner && group.isHasDvm() && group.isBurnRatioUp30()) {
                canCreateGroups.add(group);
            } else if(null != group && group.isOwner) {
                hasCreatedGroup = true;
            }
        }
        if (hasCreatedGroup) {
            return null;
        }
        if (canCreateGroups.size() == 0) {
            return null;
        }
        Spannable cotent = new SpannableString(getString(R.string.cn_guid_create_group_content));
        ChatCStateBean stateBean = createNormalNoticeState(cotent, "", null, null);
        stateBean.showType = ChatCStateBean.SHOW_NONE;
        stateBean.deviceGroups = canCreateGroups;
        stateBean.chatMsgType = ChatCStateBean.CHAT_GUID_CREATE_GROUP;
        stateBean.chatTips = getString(R.string.cn_guid_create_group_title);
        stateBean.msgTitle = "";
        stateBean.msgContent = getString(R.string.cn_guid_create_group_title);
        return stateBean;
    }

}
