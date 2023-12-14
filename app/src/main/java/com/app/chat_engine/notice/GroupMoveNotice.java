package com.app.chat_engine.notice;

import static org.matrix.android.sdk.api.session.room.send.SendServiceKt.CUS_TEXT_TYPE_NORMAL;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;

import com.app.R;
import com.app.chat_engine.ChatData;
import com.app.chat_engine.ChatNoticeDb;
import com.app.chat_engine.db.entity.DeviceGroupNoticeEntity;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.SpannableUtils;


public class GroupMoveNotice extends BaseChatNotice {

    public static final String EVENT_GROUP_MOVE = "moveGroupEvent";
    public static final int FLAG_WAIT_MOVE = 1;
    public static final int FLAG_MOVE_SUCCESS = 2;


    public static final String MOVE_GROUP_KEY = "moveGroup";

    private int mType;
    public GroupMoveNotice(int type) {
        super(MOVE_GROUP_KEY+type);
        mType = type;
    }

    @Override
    ChatCStateBean doCheckState() {

        List<DeviceGroupNoticeEntity> noticeList = dbQueryEventNotice(EVENT_GROUP_MOVE, EVENT_STATE_WAITE_NOTICE);
        if (noticeList == null || noticeList.size() == 0) {
            return null;
        }
        
        List<DeviceGroupBean> systemNoticeGroups = ChatData.getInstance().httpGetSystemGroups();
        DeviceGroupBean systemNoticeGroup = null;
        if (systemNoticeGroups != null && systemNoticeGroups.size() > 0) {
            systemNoticeGroup = systemNoticeGroups.get(0);
        }
        if (systemNoticeGroup == null) {
            return null;
        }
        if (mType == FLAG_WAIT_MOVE) {
            
            
            List<DeviceGroupNoticeEntity> needNoticeList = new ArrayList<>();
            String groupName = "";
            for (DeviceGroupNoticeEntity notice : noticeList) {
                if (notice.getFlag() == FLAG_WAIT_MOVE && notice.getState() == EVENT_STATE_WAITE_NOTICE &&
                        !nowDayHasNoticed(systemNoticeGroup.groupId)) {
                    
                    needNoticeList.add(notice);
                    if (TextUtils.isEmpty(groupName)) {
                        groupName = notice.getValue();
                    } else {
                        groupName = groupName + ","+notice.getValue();
                    }
                }
            }
            if (needNoticeList.size() == 0) {
                return null;
            }

            
            List<DeviceGroupBean> needMoveGroups = httpGetGroupsByNoticeList(needNoticeList);
            if (null == needMoveGroups || needMoveGroups.size() == 0) {
                return null;
            }

            
            List<DeviceGroupBean> noticeGroups = new ArrayList<>();
            noticeGroups.add(systemNoticeGroup);

            Spannable content = new SpannableString(String.format(getString(R.string.cn_move_group_wait_content), groupName));
            content = SpannableUtils.colorizeMatchingText(content, groupName, Color.parseColor("#FFFF00"));
            DeviceGroupBean finalSystemNoticeGroup = systemNoticeGroup;
            ChatCStateBean stateBean = createNormalNoticeState(content, "", getString(R.string.cn_move_group_wait_btn), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startRoomActivity(finalSystemNoticeGroup.groupId);
                }
            });
            stateBean.deviceGroups = noticeGroups;
            stateBean.deviceGroups2 = needMoveGroups;
            List<ChatCStateBean.CusChatMsg> cusMsgs = new ArrayList<>();
            for (DeviceGroupBean moveGroup: needMoveGroups) {
                String msgContent = String.format(getString(R.string.cn_move_group_wait_content), moveGroup.groupName);
                ChatCStateBean.CusChatMsg cusMsg = new ChatCStateBean.CusChatMsg(ChatCStateBean.CHAT_GUID_DPOS_DVM,
                        getString(R.string.cn_system_notice_title), getString(R.string.cn_move_group_wait_btn2)+moveGroup.groupName, "", msgContent, getString(R.string.cn_move_group_wait_btn2),
                        MOVE_GROUP_KEY,
                        moveGroup.groupId+","+moveGroup.groupName,
                        CUS_TEXT_TYPE_NORMAL);
                cusMsgs.add(cusMsg);
            }
            stateBean.setCusChatMsgs(cusMsgs);
            return stateBean;

        } else if(mType == FLAG_MOVE_SUCCESS) {
            
            List<DeviceGroupNoticeEntity> needNoticeList = new ArrayList<>();
            String groupName = "";
            for (DeviceGroupNoticeEntity notice : noticeList) {
                if (notice.getFlag() == FLAG_MOVE_SUCCESS && notice.getState() == EVENT_STATE_WAITE_NOTICE) {
                    
                    needNoticeList.add(notice);
                    if (TextUtils.isEmpty(groupName)) {
                        groupName = notice.getValue();
                    } else {
                        groupName = groupName + ","+notice.getValue();
                    }
                }
            }
            if (needNoticeList.size() == 0) {
                return null;
            }

            
            List<DeviceGroupBean> moveSuccessGroups = httpGetGroupsByNoticeList(needNoticeList);
            if (moveSuccessGroups==null || moveSuccessGroups.size() == 0) {
                return null;
            }

            
            List<DeviceGroupBean> noticeGroups = new ArrayList<>();
            noticeGroups.add(systemNoticeGroup);

            Spannable content = new SpannableString(String.format(getString(R.string.cn_move_group_success_content), groupName));
            content = SpannableUtils.colorizeMatchingText(content, groupName, Color.parseColor("#FFFF00"));

            ChatCStateBean stateBean = createNormalNoticeState(content, "", null, null);
            stateBean.deviceGroups = noticeGroups;
            stateBean.deviceGroups2 = moveSuccessGroups;

            List<ChatCStateBean.CusChatMsg> cusMsgs = new ArrayList<>();
            for (DeviceGroupBean moveGroup: moveSuccessGroups) {
                String msgContent = String.format(getString(R.string.cn_move_group_success_content), moveGroup.groupName);
                ChatCStateBean.CusChatMsg cusMsg = new ChatCStateBean.CusChatMsg(ChatCStateBean.CHAT_NORMAL,
                        getString(R.string.cn_system_notice_title), String.format(getString(R.string.cn_move_group_success_msg), moveGroup.groupName), "", msgContent, "", "",
                        "", CUS_TEXT_TYPE_NORMAL);
                cusMsgs.add(cusMsg);
            }
            stateBean.setCusChatMsgs(cusMsgs);
            return stateBean;
        }

        return null;
    }


    @Override
    protected void afterProcess(int processResult) {
        if (processResult == STATE_ACTIVE) {
            
            if (null != notice && null != notice.deviceGroups2 && notice.deviceGroups2.size() > 0) {
                String userAddr = getLoginUserAddress();
                ChatNoticeDb.getInstance().ioMultiUpdateEventState(userAddr, notice.deviceGroups2,
                        EVENT_GROUP_MOVE, EVENT_STATE_COMPLETE);
            }
        }
    }
}
