package com.benny.openlauncher.notifications;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;


public class ActionNotificationListener {
    public static String NOTIFICATIONS_ACTION  = "sdt_update-notifications";
    private static HashMap<String, ArrayList<MDNotificationListener.NotificationCallback>> _currentNotifications = new HashMap<>();

    private ActionNotificationListener() {
    }

    private static final class SNotificationListenerHolder {
        static final ActionNotificationListener sNotificationListener = new ActionNotificationListener();
    }

    public static ActionNotificationListener getInstance() {
        return SNotificationListenerHolder.sNotificationListener;
    }

    public void setNotificationCallback(String actionName, MDNotificationListener.NotificationCallback callback) {
        ArrayList<MDNotificationListener.NotificationCallback> callbacks =_currentNotifications.get(actionName);

        if (callbacks != null) {
            callbacks.add(callback);
        } else {
            callbacks = new ArrayList<MDNotificationListener.NotificationCallback>(1);
            callbacks.add(callback);
            _currentNotifications.put(actionName, callbacks);

        }
    }

    public void processCallback(String actionName, int count) {
        
        
        ArrayList<MDNotificationListener.NotificationCallback> callbacks = _currentNotifications.get(actionName);
        

        if (callbacks != null) {
            for (MDNotificationListener.NotificationCallback callback : callbacks) {
                callback.notificationCallback(count);
            }
        }else {
            Log.e("jues_onReceive", "callbacks.======= null");
        }
    }
}
