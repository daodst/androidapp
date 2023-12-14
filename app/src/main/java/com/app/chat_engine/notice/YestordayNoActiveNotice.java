package com.app.chat_engine.notice;

import static org.matrix.android.sdk.api.session.room.send.SendServiceKt.CUS_TEXT_TYPE_WARNING;

import android.text.SpannableString;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;


public class YestordayNoActiveNotice extends BaseChatNotice {

    public YestordayNoActiveNotice() {
        super("YesterdayNoActive");
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
        if (nowDayHasNoticed(systemNoticeGroup.groupId)) {
            
            return null;
        }
        if (httpYesterdayActive() == STATE_NO_ACTIVE) {
            
            SpannableString content = new SpannableString(getString(R.string.cn_yestorday_no_active));
            ChatCStateBean state = createNormalNoticeState(content, null, null, null);
            state.headName = getString(R.string.cn_system_notice_title);
            List<DeviceGroupBean> groups = new ArrayList<>();
            groups.add(systemNoticeGroup);
            state.deviceGroups = groups;
            state.chatMsgType = ChatCStateBean.CHAT_NORMAL;
            state.chatTips = getString(R.string.cn_yestorday_no_active_title);
            state.msgTitle = "";
            state.msgContent = content.toString();
            state.chatMsgSubType = CUS_TEXT_TYPE_WARNING;
            return state;
        }
        return null;
    }

}
