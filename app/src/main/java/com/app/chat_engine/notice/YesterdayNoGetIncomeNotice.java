package com.app.chat_engine.notice;

import static org.matrix.android.sdk.api.session.room.send.SendServiceKt.CUS_TEXT_TYPE_WARNING;

import android.text.Spannable;
import android.text.SpannableString;

import com.app.R;
import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.ArrayList;
import java.util.List;


public class YesterdayNoGetIncomeNotice extends BaseChatNotice {

    public YesterdayNoGetIncomeNotice() {
        super("YesNoGetIn");
    }

    @Override
    ChatCStateBean doCheckState() {

        
        List<DeviceGroupBean> nowDayNoNoticedGroups = getNowDayNoNoticeGroups();
        if (null == nowDayNoNoticedGroups || nowDayNoNoticedGroups.size() == 0) {
            logw("need notice groups is empty");
            return null;
        }
        List<DeviceGroupBean> needNoticeGroups = new ArrayList<>();
        if (httpYesterdayActive() == STATE_ACTIVE) {
            for (DeviceGroupBean group : nowDayNoNoticedGroups) {
                if(httpYesterdayNoGetDeviceReward(group.groupId)) {
                    needNoticeGroups.add(group);
                }
            }
        }
        if (needNoticeGroups.size() == 0) {
            return null;
        }
        Spannable cotent = new SpannableString(getString(R.string.cn_yesterday_no_income_content));
        ChatCStateBean stateBean = createNormalNoticeState(cotent, "", null, null);
        stateBean.deviceGroups = needNoticeGroups;
        stateBean.chatMsgType = ChatCStateBean.CHAT_NORMAL;
        stateBean.chatTips = getString(R.string.cn_yesterday_no_income_title);
        stateBean.msgTitle = "";
        stateBean.msgContent = getString(R.string.cn_yesterday_no_income_content);
        stateBean.chatMsgSubType = CUS_TEXT_TYPE_WARNING;
        return stateBean;

    }


}
