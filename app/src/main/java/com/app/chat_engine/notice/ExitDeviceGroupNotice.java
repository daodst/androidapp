package com.app.chat_engine.notice;

import static org.matrix.android.sdk.api.session.room.send.SendServiceKt.CUS_TEXT_TYPE_WARNING;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import com.app.R;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.List;

import common.app.utils.SpannableUtils;


public class ExitDeviceGroupNotice extends BaseChatNotice {
    public static final String EVENT_EXIT_DEVICE_GROUP = "exitDeviceGroupEvent";

    public ExitDeviceGroupNotice() {
        super("exitDeviceGroup");
    }

    @Override
    ChatCStateBean doCheckState() {

        List<DeviceGroupNoticeEntity> noticeList = dbQueryEventNotice(EVENT_EXIT_DEVICE_GROUP, EVENT_STATE_WAITE_NOTICE);
        List<DeviceGroupBean> noticeGroups = httpGetGroupsByNoticeList(noticeList);
        if (null == noticeGroups || noticeGroups.size() == 0) {
            return null;
        }
        String groupName = "";
        for (DeviceGroupBean group: noticeGroups) {
            if (TextUtils.isEmpty(groupName)) {
                groupName = group.groupName;
            } else {
                groupName = groupName + ","+group.groupName;
            }
        }

        Spannable content = new SpannableString(String.format(getString(R.string.cn_exit_device_group_content), groupName));
        content = SpannableUtils.colorizeMatchingText(content, groupName, Color.parseColor("#FFFF00"));
        ChatCStateBean stateBean = createNormalNoticeState(content, "", null, null);
        stateBean.deviceGroups = noticeGroups;
        
        stateBean.chatMsgType = ChatCStateBean.CHAT_NORMAL;
        stateBean.chatTips = getString(R.string.cn_exit_device_group_title);
        stateBean.msgTitle = "";
        stateBean.msgContent = getString(R.string.cn_exit_device_group_msg);
        stateBean.chatMsgSubType = CUS_TEXT_TYPE_WARNING;
        return stateBean;
    }

    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE  && null != notice && notice.deviceGroups != null && notice.deviceGroups.size() > 0) {
            String userAddr = getLoginUserAddress();
            ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups,
                    EVENT_EXIT_DEVICE_GROUP, EVENT_STATE_COMPLETE);
        }
    }
}
