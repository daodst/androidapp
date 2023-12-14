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


public class GroupVoteNotice extends BaseChatNotice {

    public static final String EVENT_GROUP_VOTE = "groupVoteEvent";
    public GroupVoteNotice() {
        super("groupVote");
    }

    @Override
    ChatCStateBean doCheckState() {

        
        List<DeviceGroupNoticeEntity> eventNotices = dbQueryEventNotice(EVENT_GROUP_VOTE, EVENT_STATE_WAITE_NOTICE);
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

        String senderNickName = noticeEntity.getValue();
        Spannable content = new SpannableString(String.format(getString(R.string.cn_group_vote_content1), senderNickName));
        if (chgGroup.isOwner){
            
            content = new SpannableString(String.format(getString(R.string.cn_group_vote_content2), senderNickName));
        }
        content = SpannableUtils.colorizeMatchingText(content, senderNickName, Color.parseColor("#FFFF00"));
        String noticeGroupId = chgGroup.groupId;
        ChatCStateBean stateBean = createNormalNoticeState(content, "", getString(R.string.cn_group_vote_btn_text), view -> {
            startRoomActivity(noticeGroupId);
        });

        stateBean.deviceGroups = needNoticeGroups;
        
        stateBean.chatMsgType = ChatCStateBean.CHAT_NONE;
        stateBean.chatTips = "";
        stateBean.msgTitle = "";
        stateBean.msgContent = "";
        return stateBean;
    }

    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE && null != notice && null != notice.deviceGroups) {
            if (notice.deviceGroups.size() > 0) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups,
                        EVENT_GROUP_VOTE, EVENT_STATE_COMPLETE);
            }
        }
    }


}
