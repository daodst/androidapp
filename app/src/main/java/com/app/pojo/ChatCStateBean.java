package com.app.pojo;

import android.text.Spannable;
import android.text.TextUtils;
import android.view.View;

import com.app.App;
import com.app.R;

import java.util.ArrayList;
import java.util.List;


public class ChatCStateBean {

    
    public static final int SHOW_NORMAL = 1;
    public static final int SHOW_IMPORTANT = 2;
    public static final int SHOW_NOTICE = 3;
    public static final int SHOW_NONE = 4;

    
    public static final int CHAT_NONE = 0;
    public static final int CHAT_NORMAL = 1;
    public static final int CHAT_GET_DEVICE_INCOME = 2;
    public static final int CHAT_GET_DVM_INCOME = 3;
    public static final int CHAT_GUID_CREATE_GROUP = 4;
    public static final int CHAT_GUID_DPOS_DVM = 5;
    public static final int CHAT_DAILY_REPORT = 6;
    public static final int CHAT_GET_OWNER_INCOME = 7;
    public static final int CHAT_GET_AIRDROP_INCOME = 8;

    public int showType;
    public String noticeKey;
    public int headRes;
    public String headName;
    public String subName;
    public String btnText;
    public Spannable content;
    public View.OnClickListener btnClickListener;
    public String value; 
    public String state; 

    
    public List<DeviceGroupBean> deviceGroups;
    public List<DeviceGroupBean> deviceGroups2;
    private List<CusChatMsg> cusChatMsgs;
    public int chatMsgType;
    public String chatTips;
    public String msgTitle;
    public String msgContent;
    public String msgBtnText;
    public String msgCusType;
    public String msgCusParams;
    public int chatMsgSubType;




    public ChatCStateBean() {
    }

    public ChatCStateBean(String noticeKey, int showType, int pHeadRes, String pHeadName) {
        this.noticeKey = noticeKey;
        this.showType = showType;
        this.headRes = pHeadRes;
        this.headName = pHeadName;
    }

    
    public boolean hasCusChatMsg() {
        List<CusChatMsg> msgs = getCusMsgList();
        if (msgs != null && msgs.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    
    public List<CusChatMsg> getCusMsgList() {
        if (cusChatMsgs != null && cusChatMsgs.size() > 0) {
            return cusChatMsgs;
        }
        if (chatMsgType != CHAT_NONE) {
            cusChatMsgs = new ArrayList<>();
            CusChatMsg cusMsg = new CusChatMsg(chatMsgType, headName, chatTips, msgTitle, msgContent, msgBtnText, msgCusType, msgCusParams, chatMsgSubType);
            cusChatMsgs.add(cusMsg);
            return cusChatMsgs;
        }
        return null;
    }

    public void setCusChatMsgs(List<CusChatMsg> msgs) {
        this.cusChatMsgs = msgs;
    }


    
    public String getMsgContent() {
        if (!TextUtils.isEmpty(msgContent)) {
            return msgContent;
        }
        if (null != content) {
            return content.toString();
        }
        return null;
    }

    
    public String getMsgTitle() {
        if (!TextUtils.isEmpty(msgTitle)) {
            return msgTitle;
        }
        if (!TextUtils.isEmpty(chatTips)) {
            return chatTips;
        }
        return null;
    }

    
    public String getChatTips() {
        if (!TextUtils.isEmpty(chatTips)) {
            return chatTips;
        }
        if (!TextUtils.isEmpty(msgTitle)) {
            return msgTitle;
        }
        return null;
    }

    public static ChatCStateBean createNoticeState(String noticeKey, int showType,
                                                   Spannable content, String subTitle, String btnTex,
                                                   View.OnClickListener btnclickListener) {
        ChatCStateBean notice;
        if (showType == SHOW_IMPORTANT) {
            notice = new ChatCStateBean(noticeKey, showType, R.drawable.ic_small_green_dst, App.getInstance().getString(R.string.chat_shoukuaizhushou));
        } else {
            notice = new ChatCStateBean(noticeKey, showType, R.drawable.ic_small_lingdang, App.getInstance().getString(R.string.chat_jiqunguanjia));
        }
        notice.content = content;
        notice.subName = subTitle;
        notice.btnText = btnTex;
        notice.btnClickListener = btnclickListener;
        return notice;

    }


    
    public static class CusChatMsg{
        public int chatMsgType;
        public String senderNickName;
        public String chatTips;
        public String msgTitle;
        public String msgContent;
        public String msgBtnText;
        public String msgCusType;
        public String msgCusParams;
        public int chatMsgSubType;

        public CusChatMsg(int chatMsgType, String senderNickName, String chatTips, String msgTitle, String msgContent,
                          String msgBtnText, String msgCusType, String msgCusParams, int chatMsgSubType) {
            this.chatMsgType = chatMsgType;
            this.senderNickName = senderNickName;
            this.chatTips = chatTips;
            this.msgTitle = msgTitle;
            this.msgContent = msgContent;
            this.msgBtnText = msgBtnText;
            this.msgCusType = msgCusType;
            this.msgCusParams = msgCusParams;
            this.chatMsgSubType = chatMsgSubType;
        }

        
        public String getMsgContent() {
            if (!TextUtils.isEmpty(msgContent)) {
                return msgContent;
            }
            return null;
        }

        
        public String getMsgTitle() {
            if (!TextUtils.isEmpty(msgTitle)) {
                return msgTitle;
            }
            if (!TextUtils.isEmpty(chatTips)) {
                return chatTips;
            }
            return null;
        }

        
        public String getChatTips() {
            if (!TextUtils.isEmpty(chatTips)) {
                return chatTips;
            }
            if (!TextUtils.isEmpty(msgTitle)) {
                return msgTitle;
            }
            return null;
        }
    }


    @Override
    public String toString() {
        return "ChatCStateBean{" +
                "showType='" + showType + '\'' +
                "noticeKey='" + noticeKey + '\'' +
                ", headRes=" + headRes +
                ", headName='" + headName + '\'' +
                ", subName='" + subName + '\'' +
                ", btnText='" + btnText + '\'' +
                ", content=" + content +
                ", btnClickListener=" + btnClickListener +
                '}';
    }
}
