package com.app.chat_engine.notice;

import android.text.SpannableString;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;


public class Continue2DayActiveNotice extends BaseChatNotice {

    public Continue2DayActiveNotice() {
        super("Day2Active");
    }

    @Override
    ChatCStateBean doCheckState() {
        List<DeviceGroupBean> systemNoticeGroups = ChatData.getInstance().httpGetSystemGroups();
        DeviceGroupBean systemNoticeGroup = null;
        if (systemNoticeGroups != null && systemNoticeGroups.size() > 0) {
            systemNoticeGroup = systemNoticeGroups.get(0);
        }
        if (systemNoticeGroup == null) {
            return null;
        }
        if (recent2DayHashNoticed(systemNoticeGroup.groupId)) {
            
            return null;
        }
        if (httpContinue2DayActive() == STATE_ACTIVE) {
            
            SpannableString content = new SpannableString(getString(R.string.cn_continue_2day_active));
            ChatCStateBean state = createNormalNoticeState(content, null, null, null);
            List<DeviceGroupBean> groups = new ArrayList<>();
            groups.add(systemNoticeGroup);
            state.deviceGroups = groups;
            state.headName = getString(R.string.cn_system_notice_title);
            state.chatMsgType = ChatCStateBean.CHAT_NORMAL;
            state.chatTips = getString(R.string.cn_continue_2day_active_title);
            state.msgTitle = "";
            state.msgContent = content.toString();
            return state;
        }
        return null;
    }

}
