

package common.app.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.app.ActivityRouter;
import common.app.BuildConfig;
import common.app.wallet.WalletBridge;



public class SpUtil {
    private static final String HOST = "host";
    private static final String JS_HOST = "jshost";
    private static final String WS = "ws";

    private static final String APP_ID = "appid";
    private static final String DEBUG = "debug";
    private static final String TYPE = "type";
    private static final String REG_URL = "register_url";
    
    private static final String SHARE_JYS = "exchanges";
    private static final String DEF_HUOBI = "currency_unit";
    private static final String SHARE_JDATA = "onclicklogin";
    private static final String LATELY_EMOJI = "lately_emoji";
    private static final String SMS_CODE = "is_enable_countrymobile";
    private static final String FEE_STATUS = "fee_status";
    private static final String IM_GROUP_CLEAN = "im_group_clean";
    private static final String IS_NO_VERIFY = "is_no_verify";
    private static final String DEF_NODE = "def_node";
    
    public static final String KEYBOARDHEIGHT = "keyBoardHeight";
    public static final String TYPE_PRIVATE = "private";
    public static final String IS_FIRST = "isfirst";
    public static final String CANDY_BOX = "candy_box";
    public static final String IS_ENABLE_APP = "is_enable_app";
    public static final String IS_ENABLE_APP_MSG = "is_enable_app_msg";

    
    private static final String SET_COOKIE = "Set_Cookie";
    private static MMKV kv;

    private static MMKV getMMKV() {
        if (kv == null) {
            kv = MMKV.defaultMMKV();
        }
        return kv;
    }


    private static final String IS_TODAY = "is_Today";

    public static void saveTodayINfo(String url) {
        getMMKV().encode(IS_TODAY, url);
    }

    public static String getToday() {
        return getMMKV().getString(IS_TODAY, "");
    }


    public static void saveHostApi(String url) {
        getMMKV().encode(HOST, url);
    }

    public static String getHostApi() {
        return getMMKV().getString(HOST, BuildConfig.HOST);
    }

    public static void saveSetCookie(String setCookie) {
        getMMKV().encode(SET_COOKIE, setCookie);
    }

    
    public static String getSetCookie() {
        return getMMKV().decodeString(SET_COOKIE, "");
    }

    private static final String KEY_WS_URL = "KEY_ws_url";

    private static void saveWsUrl(String ws_url) {
        getMMKV().encode(KEY_WS_URL, ws_url);
    }


    public static boolean isAgreePrivacyPolicy() {
        return getMMKV().decodeBool(TYPE_PRIVATE, false);
    }

    public static void setAgreePrivacyPolicy() {
        getMMKV().encode(TYPE_PRIVATE, true);
    }

    public static void saveAppid(String url) {
        getMMKV().encode(APP_ID, url);
    }

    public static String getAppid() {
        return getMMKV().decodeString(APP_ID, "");
    }




    public static void saveIsNoVerify(int data) {
        getMMKV().encode(IS_NO_VERIFY, data);
    }

    public static int getIsNoVerify() {
        return getMMKV().decodeInt(IS_NO_VERIFY, 0);
    }

    public static void saveJYS(String url) {

        getMMKV().encode(SHARE_JYS, url);

    }

    public static String getJYS() {
        return getMMKV().decodeString(SHARE_JYS, null);
    }

    public static void saveDcu(String url) {

        getMMKV().encode(DEF_HUOBI, url);

    }

    public static String getDcu() {
        return getMMKV().decodeString(DEF_HUOBI, BuildConfig.CURRENCY_UNIT);
    }


    public static void saveSMScode(int url) {

        getMMKV().encode(SMS_CODE, url);

    }

    public static int getSMScode() {
        return getMMKV().decodeInt(SMS_CODE, 0);
    }

    public static void saveFeeStatus(int url) {
        getMMKV().encode(FEE_STATUS, url);
    }

    public static int getFeeStatus() {
        return getMMKV().decodeInt(FEE_STATUS, 0);
    }

    public static void saveJData(String url) {

        getMMKV().encode(SHARE_JDATA, url);

    }


    public static void saveLatelyEmoji(String url) {

        getMMKV().encode(LATELY_EMOJI, url);

    }

    public static String getLatelyEmoji() {
        return getMMKV().decodeString(LATELY_EMOJI, "");
    }

    public static String getJData() {
        return getMMKV().decodeString(SHARE_JDATA, "");
    }

    public static void saveRegurl(String url) {

        getMMKV().encode(REG_URL, url);

    }

    public static void saveImGroupClean(String groupid, String time) {

        getMMKV().encode(IM_GROUP_CLEAN + groupid, time);

    }

    public static String getImGroupClean() {
        return getMMKV().decodeString(IM_GROUP_CLEAN, "");
    }

    public static String getRegurl() {
        return getMMKV().decodeString(REG_URL, null);
    }

    
    public static void setType(int b) {

        getMMKV().encode(TYPE, b);

    }

    public static int getType() {
        return getMMKV().decodeInt(TYPE, 0);
    }

    
    public static void setDebug(boolean b) {

        getMMKV().encode(DEBUG, b);

    }

    public static boolean isDebug() {
        return getMMKV().decodeBool(DEBUG, false);
    }

    
    public static int keyBoardHeight(Context context) {
        return getMMKV().decodeInt(KEYBOARDHEIGHT, 0);
    }

    public static void savekeyBoardHeight(int data) {
        getMMKV().encode(KEYBOARDHEIGHT, data);
    }

    
    public static boolean isFirst(Context context) {
        return getMMKV().decodeBool(IS_FIRST, true);
    }

    public static boolean setFirst(boolean data) {
        return getMMKV().encode(IS_FIRST, data);
    }

    public static int enableCandyBox(Context context) {
        return getMMKV().decodeInt(CANDY_BOX, 0);
    }

    public static void saveEnableCandyBox(int data) {
        getMMKV().encode(CANDY_BOX, data);
    }

    public static int enableApp(Context context) {
        return getMMKV().decodeInt(IS_ENABLE_APP, 0);
    }

    public static void saveEnableApp(int data) {
        getMMKV().encode(IS_ENABLE_APP, data);
    }

    public static String enableAppMsg(Context context) {
        return getMMKV().decodeString(IS_ENABLE_APP_MSG, "");
    }

    public static void saveEnableAppMsg(String data) {
        getMMKV().encode(IS_ENABLE_APP_MSG, data);
    }


    public static String getDefNode(int type) {
        String url = getMMKV().decodeString(DEF_NODE + type, "");
        if (TextUtils.isEmpty(url)) {
            WalletBridge walletBridge = ActivityRouter.getWalletBridge(ActivityRouter.Wallet.A_WALLETBRIDGEIMPL);
            url = walletBridge.getJsonRpc(type);
        }
        return url;
    }

    public static void saveDefNode(int type, String data) {
        getMMKV().encode(DEF_NODE + type, data);
    }

    
    private static final String KEY_NODE_NO_SEGM = "noSegment";

    public static String getNodeNoSegm() {
        if (false) {
            return "12345678945641231231234564";
        }
        return getMMKV().decodeString(KEY_NODE_NO_SEGM, "");
    }

    
    public static void saveNodeNoSegm(String noSegment) {
        getMMKV().encode(KEY_NODE_NO_SEGM, noSegment);
    }

    private static final String KEY_NODE_NAME = "nodeName";

    public static String getNodeName() {
        return getMMKV().decodeString(KEY_NODE_NAME, "");
    }

    
    public static void saveNodeName(String name) {
        getMMKV().encode(KEY_NODE_NAME, name);
    }

    private static final String KEY_IM_URL = "synImUrl";

    
    public static String getImUrl() {
        return getMMKV().decodeString(KEY_IM_URL, "");
    }

    
    public static void saveImUrl(String imUrl) {
        getMMKV().encode(KEY_IM_URL, imUrl);
    }


    private static final String KEY_NODE_SMART_URL = "nodeSmartUrl";

    
    public static String getNodeSmartUrl() {
        return getMMKV().decodeString(KEY_NODE_SMART_URL, "");
    }

    
    public static void saveNodeSmartUrl(String nodeSmartUrl) {
        getMMKV().encode(KEY_NODE_SMART_URL, nodeSmartUrl);
    }

    private static final String KEY_NODE_INFO_URL = "nodeInfoUrl";

    
    public static String getNodeInfoUrl() {
        return getMMKV().decodeString(KEY_NODE_INFO_URL, "");
    }

    
    public static void saveNodeInfoUrl(String nodeInfoUrl) {
        getMMKV().encode(KEY_NODE_INFO_URL, nodeInfoUrl);
    }

    
    public static String getNodeHost() {
        String url = getImUrl();
        return AllUtils.urlToHost(url);
    }


    
    public static void saveNodeInfo(int walletType, String nodeUrl, String noSegm, String nodeName, String imUrl,
                                    String nodeSmartUrl, String nodeInfoUrl, String chatCall, String chain_id, String ws_url, String tts_url) {
        saveDefNode(walletType, nodeUrl);
        saveNodeNoSegm(noSegm);
        saveNodeName(nodeName);
        saveImUrl(imUrl);
        saveNodeSmartUrl(nodeSmartUrl);
        saveNodeInfoUrl(nodeInfoUrl);
        saveChatCall(chatCall);
        saveTtsUrl(tts_url);
        if (!TextUtils.isEmpty(chain_id)) {
            saveChainId(chain_id);
        }
        saveWsUrl(ws_url);
    }

    private static final String TTS_URL = "tts_url";

    private static void saveTtsUrl(String tts_url) {
        getMMKV().encode(TTS_URL, tts_url);
    }

    public static String getTtsUrl() {
        return getMMKV().decodeString(TTS_URL, "");
    }

    private static final String KEY_CHAIN_ID = "KEY_chain_id";

    private static void saveChainId(String chain_id) {
        getMMKV().encode(KEY_CHAIN_ID, chain_id);
    }

    public static String getChainId() {
        return getMMKV().decodeString(KEY_CHAIN_ID, "");
    }

    private static final String KEY_CALL_URL = "KEY_call_URL";

    private static void saveChatCall(String chat23478) {
        getMMKV().encode(KEY_CALL_URL, chat23478);
    }

    public static String getChatCall() {
        return getMMKV().decodeString(KEY_CALL_URL, "");
    }


    
    public static final long NODE_ERROR_ALERT_TIME_OUT = 600000;
    public static long lastShowNodeAlertTime = 0L;

    private static final String KEY_PHONE_LIST = "mlist";

    public static void saveMyPhoneList(String userAddr, List<String> list) {
        if (null == list) {
            return;
        }
        Set<String> sets = new HashSet<>(list);
        getMMKV().encode(userAddr + KEY_PHONE_LIST, sets);
    }

    public static List<String> getMyPhoneList(String userAddr) {
        Set<String> sets = getMMKV().decodeStringSet(userAddr + KEY_PHONE_LIST);
        return new ArrayList<>(sets);
    }

    private static final String MXCALL_KEY_PHONE = "phone";

    public static void saveNowPhone(String userAddr, String nowPhone) {
        getMMKV().encode(userAddr + MXCALL_KEY_PHONE, nowPhone);
    }

    public static String getNowPhone(String userAddr) {
        return getMMKV().decodeString(userAddr + MXCALL_KEY_PHONE);
    }


    
    public static final String APP_STORE_INFO_KEY = "app_store_info_key";

    public static void setAppStoreInfo(String appStoreInfo) {
        getMMKV().encode(APP_STORE_INFO_KEY, appStoreInfo);
    }

    public static String getAppStoreInfo() {
        return getMMKV().decodeString(APP_STORE_INFO_KEY);
    }

    
    public static final String APP_LANGUAGE_HAS_SET = "app_language_has_set";

    public static void setAppLanguageHasSet(boolean hasSet) {
        getMMKV().encode(APP_LANGUAGE_HAS_SET, hasSet);
    }

    public static boolean getAppLanguageHasSet() {
        return getMMKV().decodeBool(APP_LANGUAGE_HAS_SET, false);
    }

    
    
    public static final String APP_BIOMETRIC_OPEN = "app_biometric_open";
    
    public static final String APP_WALLET_PASSWORD = "app_wallet_password";

    
    public static void setAppBiometricOpen(String key, boolean isOpen) {
        getMMKV().encode(key, isOpen);
        if (!isOpen) {
            
            setAppWalletPassword(key, "");
            removeBiometricOpenList(key);
        } else {
            saveBiometricOpenList(key);
        }
    }

    
    public static boolean getAppBiometricOpened(String key) {
        return getMMKV().decodeBool(key, false);
    }

    
    public static void setAppWalletPassword(String key, String appWalletPassword) {
        String newKey = key+"pwd";
        getMMKV().encode(newKey, appWalletPassword);
    }

    
    public static String getAppWalletPassword(String key) {
        String newKey = key+"pwd";
        return getMMKV().decodeString(newKey, "");
    }


    private static final String KEY_BIOMETRIC_OPEN_LIST = "biometricOpenList";

    
    public static void saveBiometricOpenList(String key) {
        if(TextUtils.isEmpty(key)) {
            return;
        }
        Set<String> nowList = getMMKV().decodeStringSet(KEY_BIOMETRIC_OPEN_LIST);
        if (nowList == null) {
            nowList = new HashSet<>();
        }
        if (!nowList.contains(key)) {
            nowList.add(key);
            getMMKV().encode(KEY_BIOMETRIC_OPEN_LIST, nowList);
        }
    }

    
    public static void removeBiometricOpenList(String key) {
        if (TextUtils.isEmpty(key)){
            return;
        }
        Set<String> nowList = getMMKV().decodeStringSet(KEY_BIOMETRIC_OPEN_LIST);
        if (nowList != null && nowList.size() > 0 && nowList.contains(key)){
            nowList.remove(key);
            getMMKV().encode(KEY_BIOMETRIC_OPEN_LIST, nowList);
        }
    }

    
    public static void cleanAllBiometricPaySetting() {
        Set<String> nowList = getMMKV().decodeStringSet(KEY_BIOMETRIC_OPEN_LIST);
        if (null != nowList) {
            for(String key: nowList){
                setAppBiometricOpen(key, false);
            }
        }
    }


}
