package com.app.chat_engine.notice;

import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import common.app.utils.SpannableUtils;
import im.vector.app.provide.ChatStatusProvide;


public class AutoJoinGroupNotice extends BaseChatNotice {

    public static final String EVENT_AUTO_JOIN_GROUP = "autoJoinGroupEvent";
    public static final int FLAG_WAIT_JOIN = 1;
    public static final int FLAG_JOIN_SUCCESS = 2;
    private int mType;

    public AutoJoinGroupNotice(int type) {
        super("autoJoinGroup"+type);
        this.mType = type;
    }

    @Override
    ChatCStateBean doCheckState() {
        List<DeviceGroupNoticeEntity> noticeList = dbQueryEventNotice(EVENT_AUTO_JOIN_GROUP, EVENT_STATE_WAITE_NOTICE);
        if (noticeList == null || noticeList.size() == 0) {
            return null;
        }
        if (mType == FLAG_WAIT_JOIN) {
            
            for (DeviceGroupNoticeEntity notice : noticeList) {
                if (notice.getFlag() == FLAG_WAIT_JOIN && notice.getState() == EVENT_STATE_WAITE_NOTICE) {
                    
                    ChatStatusProvide.httpAutoJoinRoom(getAppContext(), notice.getGroupId());
                }
            }
            
            return null;
        } else if(mType == FLAG_JOIN_SUCCESS) {
            List<DeviceGroupBean> groups = ChatData.getInstance().httpGetMyDeviceGroups();
            if (null == groups || groups.size() == 0) {
                return null;
            }
            Map<String, DeviceGroupBean> groupMaps = DbConvertUtils.groupListToMap(groups);
            if (null == groupMaps || groupMaps.isEmpty()) {
                return null;
            }

            
            String groupName = "";
            List<DeviceGroupBean> sucessGroups = new ArrayList<>();
            for (DeviceGroupNoticeEntity notice : noticeList) {
                if (notice.getFlag() == FLAG_JOIN_SUCCESS && notice.getState() == EVENT_STATE_WAITE_NOTICE) {
                    DeviceGroupBean group = groupMaps.get(notice.getGroupId());
                    if (null != group) {
                        sucessGroups.add(group);
                        if (TextUtils.isEmpty(groupName)) {
                            groupName = group.groupName;
                        } else {
                            groupName = groupName+","+group.groupName;
                        }
                    }
                }
            }

            if (sucessGroups.size() == 0) {
                return null;
            }
            Spannable content = new SpannableString(String.format(getString(R.string.cn_autojoin_group_content), groupName));
            content = SpannableUtils.colorizeMatchingText(content, groupName, Color.parseColor("#FFFF00"));
            ChatCStateBean stateBean = createNormalNoticeState(content, "", null, null);
            stateBean.deviceGroups = sucessGroups;
            
            stateBean.chatMsgType = ChatCStateBean.CHAT_NORMAL;
            stateBean.chatTips = getString(R.string.cn_autojoin_group_title);
            stateBean.msgTitle = "";
            stateBean.msgContent = getString(R.string.cn_autojoin_group_msg);
            return stateBean;
        }
        return null;
    }

    @Override
    protected void afterProcess(int processResult) {
        super.afterProcess(processResult);
        if (processResult == STATE_ACTIVE && mType == FLAG_JOIN_SUCCESS && null != notice && notice.deviceGroups != null) {
            if (notice.deviceGroups.size() > 0) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups,
                        EVENT_AUTO_JOIN_GROUP, EVENT_STATE_COMPLETE);
            }
        }
    }
}
