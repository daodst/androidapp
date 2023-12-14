package com.app.chat_engine.notice;

import com.app.pojo.ChatCStateBean;
import com.app.pojo.DeviceGroupBean;

import java.util.List;


public class DailyReportNotice extends BaseChatNotice {

    public DailyReportNotice() {
        super("DailyReport");
    }


    @Override
    ChatCStateBean doCheckState() {


        
        List<DeviceGroupBean> nowDayNoNoticedGroups = getNowDayNoNoticeGroups(true);
        if (null == nowDayNoNoticedGroups || nowDayNoNoticedGroups.size() == 0) {
            logw("need notice groups is empty");
            return null;
        }

        
        for (DeviceGroupBean group: nowDayNoNoticedGroups) {

        }

        return null;
    }


}
