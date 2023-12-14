package com.benny.openlauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.benny.openlauncher.notifications.ActionNotificationListener;

import java.util.List;
import java.util.Objects;

import common.app.pojo.ChatWidgetItemEntity;
import common.app.utils.AppWidgetUtils;


public class ActionNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), AppWidgetUtils.NOTIFICATIONS_ACTION)) {
            
            String action = intent.getStringExtra("action");
            String value = intent.getStringExtra("value");
            if (action == null || value == null) return;
            int number = 0;
            try {
                number = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            List<ChatWidgetItemEntity> list = AppWidgetUtils.chatUnreadLiveData.getValue();
            if (null != list) {
                int chatUnreadNum = 0;
                for (ChatWidgetItemEntity entity : list) chatUnreadNum += entity.number;
                if (chatUnreadNum == number) {
                    
                    ActionNotificationListener.getInstance().processCallback(action, number);
                }
            }
        }
    }
}
