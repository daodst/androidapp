package common.app.utils;

import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.app.pojo.ChatWidgetItemEntity;


public class AppWidgetUtils {
    
    @Deprecated
    public static final MutableLiveData<Boolean> isReady = new MutableLiveData<>();
    
    public static final MutableLiveData<String> gatewayBlock = new MutableLiveData<>();
    
    public static final MutableLiveData<Long> gatewayPing = new MutableLiveData<>();
    public static final MutableLiveData<String> gatewayName = new MutableLiveData<>();
    
    public static final MutableLiveData<List<ChatWidgetItemEntity>> chatUnreadLiveData = new MutableLiveData<>();

    
    public static final MutableLiveData<Map<String,Integer>> walletCountLD = new MutableLiveData<>();

    
    public static final String ALARM_ACTION = "com.app.utils.alarm.clock";
    public static final int TIME_INTERVAL = 5000;

    public static final String ChatWidgetDataFilter = "com.appwidget.chat.data";


    
    public static String NOTIFICATIONS_ACTION = "sdt_update-notifications";
    public static final int ACTION_CALL_PHONE = 0x1000;
    public static final int ACTION_CHAT = 0x1001;
    public static final int ACTION_CHAT_DETAIL = 0x10011;
    public static final int ACTION_DEFAULT = 0x1002;
    public static final int ACTION_WALLET = 0x1003;
    public static final int ACTION_MINE = 0x1004;
    public static final int ACTION_ROOT_WALLET = 0x1005;


    public static final String KEY_DST = "DST";
    public static final String KEY_ETH = "ETH";
    public static final String KEY_BSC = "BSC";
    public static void updateWalletCount(int dstCount, int ethCount, int bscCount) {
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put(KEY_DST, dstCount);
        countMap.put(KEY_ETH, ethCount);
        countMap.put(KEY_BSC, bscCount);
        if (isMainThread()) {
            walletCountLD.setValue(countMap);
        } else {
            walletCountLD.postValue(countMap);
        }

    }


    
    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}
