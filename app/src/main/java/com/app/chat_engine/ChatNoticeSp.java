package com.app.chat_engine;

import android.text.TextUtils;

import com.tencent.mmkv.MMKV;


public class ChatNoticeSp {

    private static MMKV kv;

    private static MMKV getMMKV() {
        if (kv == null) {
            kv = MMKV.mmkvWithID("chatNotice");
        }
        return kv;
    }

    
    private static final String LOGINED_TIME = "loginTime";
    public static void updateAddLoginTime(String userAddr, long addSenconds) {
        if (TextUtils.isEmpty(userAddr)) {
            return;
        }
        long nowTime = getHasLoginTime(userAddr);
        long value = nowTime + addSenconds;
        String newKey = userAddr + LOGINED_TIME;
        getMMKV().encode(newKey, value);
    }

    public static long getHasLoginTime(String userAddr) {
        if (TextUtils.isEmpty(userAddr)) {
            return 0;
        }
        String newKey = userAddr + LOGINED_TIME;
        return getMMKV().getLong(newKey, 0L);
    }

    public static void reSetHasLoginTime(String userAddr) {
        String newKey = userAddr + LOGINED_TIME;
        getMMKV().encode(newKey, 0L);
    }




    
    private static final String NOTICED_TIME = "NoticedTime";
    public static void saveLastNoticeTime(String key, long timeStamp) {
        String newKey = key + NOTICED_TIME;
        getMMKV().encode(newKey, timeStamp);
    }

    
    public static long getLastNoticeTime(String key) {
        String newKey = key + NOTICED_TIME;
        return getMMKV().getLong(newKey, 0L);
    }


    
    private static final String NEXT_NOTICE_TIME = "NextNoticeTime";
    public static void saveNextNoticeTime(String key, long timeStamp) {
        String newKey = key + NEXT_NOTICE_TIME;
        getMMKV().encode(newKey, timeStamp);
    }

    
    public static long getNextNoticeTime(String key) {
        String newKey = key + NEXT_NOTICE_TIME;
        return getMMKV().getLong(newKey, 0L);
    }




}
