package com.app.chat_engine.notice;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.SpannableUtils;


public class GroupLevelNotice extends BaseChatNotice {

    public static final String EVENT_GROUP_LEVEL_CHG = "groupLevelChgEvent";
    public static final int FLAG_UP = 1;
    public static final int FLAG_DOWN = 2;


    public GroupLevelNotice() {
        super("GroupLevelChg");
    }

    @Override
    ChatCStateBean doCheckState() {

        
        List<DeviceGroupNoticeEntity> eventNotices = dbQueryEventNotice(EVENT_GROUP_LEVEL_CHG, EVENT_STATE_WAITE_NOTICE);
        if (null == eventNotices || eventNotices.size() == 0) {
            return null;
        }
        
        DeviceGroupNoticeEntity noticeEntity = eventNotices.get(0);
        List<DeviceGroupBean> deviceGroups = ChatData.getInstance().httpGetMyDeviceGroups();
        if (null == deviceGroups || deviceGroups.size() == 0) {
            return null;
        }
        DeviceGroupBean chgGroup = null;
        for (DeviceGroupBean group : deviceGroups) {
            if (group.groupId.equals(noticeEntity.getGroupId())) {
                chgGroup = group;
                break;
            }
        }

        if (chgGroup == null) {
            logw("no found level change group");
            return null;
        }
        List<DeviceGroupBean> needNoticeGroups = new ArrayList<>();
        needNoticeGroups.add(chgGroup);

        String groupName = chgGroup.groupName;
        String groupLevel = chgGroup.groupLevel+getString(R.string.cn_group_level_tip1);
        Spannable content = new SpannableString(String.format(getString(R.string.cn_group_level_content), groupName, groupLevel));
        content = SpannableUtils.colorizeMatchingText(content, groupName, Color.parseColor("#FFFF00"));
        content = SpannableUtils.colorizeMatchingText(content, groupLevel, Color.parseColor("#FFFF00"));
        ChatCStateBean stateBean = createNormalNoticeState(content, "", null, null);

        stateBean.deviceGroups = needNoticeGroups;
        
        stateBean.chatMsgType = ChatCStateBean.CHAT_NORMAL;
        stateBean.chatTips = getString(R.string.cn_group_level_title)+groupLevel;
        stateBean.msgTitle = "";
        stateBean.msgContent = content.toString();
        return stateBean;
    }


    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE && null != notice && null != notice.deviceGroups) {
            if (notice.deviceGroups.size() > 0) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups,
                        EVENT_GROUP_LEVEL_CHG, EVENT_STATE_COMPLETE);
            }
        }
    }
}
