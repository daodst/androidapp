package com.app.chat_engine.notice;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.DbConvertUtils;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.app.utils.SpannableUtils;
import im.vector.app.provide.ChatStatusProvide;


public class FirstJoinGroupNotice extends BaseChatNotice {

    
    public static final String EVENT_FIRST_JOIN_GROUP = "firstJoinGroupEvent";
    public static final int FLAG_FIRST = 1;

    public FirstJoinGroupNotice() {
        super("firstJoinGroup");
    }

    @Override
    ChatCStateBean doCheckState() {
        

        
        List<DeviceGroupNoticeEntity> eventNotices = dbQueryEventNotice(EVENT_FIRST_JOIN_GROUP, EVENT_STATE_WAITE_NOTICE);
        if (null == eventNotices || eventNotices.size() == 0) {
            return null;
        }
        List<DeviceGroupBean> allGroups = ChatData.getInstance().httpGetMyDeviceGroups();
        if (null == allGroups || allGroups.size() == 0) {
            return null;
        }
        Map<String,DeviceGroupBean> map = DbConvertUtils.groupListToMap(allGroups);
        if (null == map || map.isEmpty()) {
            return null;
        }
        List<DeviceGroupBean> needNoticeGroups = new ArrayList<>();
        boolean hasOwner = false;
        String ownerGroupName = "";
        String otherGroupName = "";
        for (DeviceGroupNoticeEntity notice : eventNotices) {
            if (map.containsKey(notice.getGroupId())) {
                DeviceGroupBean group = map.get(notice.getGroupId());
                needNoticeGroups.add(group);
                if (group.isOwner) {
                    hasOwner = true;
                    if (TextUtils.isEmpty(ownerGroupName)) {
                        ownerGroupName = group.groupName;
                    } else {
                        ownerGroupName = ownerGroupName+","+group.groupName;
                    }
                } else {
                    if (TextUtils.isEmpty(otherGroupName)) {
                        otherGroupName = group.groupName;
                    } else {
                        otherGroupName = otherGroupName+","+group.groupName;
                    }
                }
            }
        }

        if (null == needNoticeGroups || needNoticeGroups.size() == 0) {
            logw("need notice groups is empty");
            return null;
        }

        Spannable content = null;
        String chatTips = "";
        String msgContent = "";
        if (hasOwner) {
            content = new SpannableString(String.format(getString(R.string.cn_first_join_owner_content), ownerGroupName));
            content = SpannableUtils.colorizeMatchingText(content, ownerGroupName, Color.parseColor("#FFFF00"));
        } else {
            content = new SpannableString(String.format(getString(R.string.cn_first_join_other_content), otherGroupName));
            content = SpannableUtils.colorizeMatchingText(content, otherGroupName, Color.parseColor("#FFFF00"));
        }

        ChatCStateBean stateBean = createNormalNoticeState(content, "", getString(R.string.cn_dazhaohu), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                startRoomActivity(needNoticeGroups.get(0).groupId);
            }
        });
        stateBean.deviceGroups = needNoticeGroups;
        
        stateBean.chatMsgType = ChatCStateBean.CHAT_NONE;
        stateBean.chatTips = chatTips;
        stateBean.msgTitle = "";
        stateBean.msgContent = msgContent;

        return stateBean;
    }

    @Override
    protected void afterProcess(int processResult) {
        
        
        if (processResult == STATE_ACTIVE && null != notice && null != notice.deviceGroups) {
            if (notice.deviceGroups.size() > 0) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups,
                        EVENT_FIRST_JOIN_GROUP, EVENT_STATE_COMPLETE);
                for (DeviceGroupBean group : notice.deviceGroups) {
                    if (!group.isOwner) {
                        
                        ChatStatusProvide.noticeFirtJoinState(getAppContext(), group.groupId);
                    }
                }
            }
        }
    }
}
