package com.appwidget.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

@Deprecated
public class ChatWidgetClickReceiver extends BroadcastReceiver {
    private static final String TAG = "jues_ChatWidgetService";
    public static final String ACTION_CHAT_WIDGET_CLICK = "com.android.launcher.action.CHAT_WIDGET";
    private OnChatWidgetClick mOnChatWidgetClick = null;

    public ChatWidgetClickReceiver() {
    }

    public ChatWidgetClickReceiver(OnChatWidgetClick onChatWidgetClick) {
        mOnChatWidgetClick = onChatWidgetClick;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (Objects.equals(intent.getAction(), ACTION_CHAT_WIDGET_CLICK)) {
            if (intent.hasExtra("roomId")){
                String roomId = intent.getStringExtra("roomId");
                
            }
            if (null != mOnChatWidgetClick) mOnChatWidgetClick.onChatWidgetClick();
        }
    }

    public interface OnChatWidgetClick {
        void onChatWidgetClick();
    }
}
