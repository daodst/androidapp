package com.appwidget.chat;

import static com.appwidget.chat.ChatWidgetService.ACTION_CHAT_WIDGET;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


@Deprecated
public class ChatWidgetReceiver extends BroadcastReceiver {
    private static final String TAG = "jues_ChatWidgetService";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_CHAT_WIDGET.equals(intent.getAction())) {
            
            
            try {
                Intent serviceIntent = new Intent(context, ChatWidgetService.class);
                context.startService(serviceIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
