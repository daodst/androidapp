

package com.wallet.ctc.util;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mmkv.MMKV;
import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.model.blockchain.AssertBean;

import java.util.ArrayList;
import java.util.List;

import common.app.utils.SpUtil;


public class SettingPrefUtil {
    private static Gson gson = new Gson();
    private static final String ETH_HOST = "ethhost";
    private static final String DM_HOST = "dmhost";
    private static final String MCC_HOST = "mcchost";
    private static final String OTHER_HOST = "otherhost";
    private static final String TRX_HOST = "trxhost";
    private static final String HOST = "host";
    private static final String FIL_HOST = "filhost";
    private static final String DOGE_HOST = "dogehost";
    private static final String DOT_HOST = "dothost";
    private static final String LTC_HOST = "ltchost";
    private static final String BCH_HOST = "bchhost";
    private static final String ZEC_HOST = "zechost";
    private static final String ETC_HOST = "etchost";
    private static final String SGB_HOST = "sgbjost";
    private static final String SOL_HOST = "solhost";
    private static final String MATIC_HOST = "matichost";
    private static final String MARKET_PRICE_HOST = "market_price_host";
    private static final String TRUST_WALLET_HOST = "trustWallethost";
    private static final String APP_ID = "appid";

    
    public static Boolean getNeedCopy(Context context) {
        return MMKV.defaultMMKV().decodeBool("needcopy", true);
    }

    public static void setNeedCopy(Context context, Boolean bool) {
        MMKV.defaultMMKV().encode("needcopy", bool);
    }

    
    public static Boolean getAgreen(Context context) {
        return MMKV.defaultMMKV().decodeBool("Agreen", false);
    }

    public static void setgetAgreen(Context context, Boolean bool) {
        MMKV.defaultMMKV().encode("Agreen", bool);
    }

    
    public static int getNodeType(Context context) {
        return MMKV.defaultMMKV().decodeInt("nodetype", 1);
    }

    public static void setNodeType(Context context, int bool) {
        MMKV.defaultMMKV().encode("nodetype", bool);
    }

    
    public static void setWalletAddress(Context context, String walletAddress) {
        MMKV.defaultMMKV().encode("walletAddress", walletAddress);
    }

    public static String getWalletAddress(Context context) {

        String walletAddress = MMKV.defaultMMKV().decodeString("walletAddress", "");
        if (null == walletAddress || "".equals(walletAddress)) {
            return null;
        }
        return walletAddress;
    }

    
    public static void setWalletType(Context context, int walletType) {
        MMKV.defaultMMKV().encode("walletType", walletType);

    }

    public static int getWalletType(Context context) {
        return MMKV.defaultMMKV().decodeInt("walletType", 0);
    }

    
    public static void setWalletTypeAddress(Context context, int walletType, String walletAddress) {
        if (walletType >= 0 && !TextUtils.isEmpty(walletAddress)) {
            setWalletAddress(context, walletAddress);
            setWalletType(context, walletType);

            MMKV.defaultMMKV().encode("walletTypeAddr" + walletType, walletAddress);
        }
    }

    public static final String WALLET_AUTO_LOGIN = "wallet_auto_login";

    public static void setWalletLoginTypeAddress(Context context, String walletAddress) {
        if (!TextUtils.isEmpty(walletAddress)) {
            MMKV.defaultMMKV().encode(WALLET_AUTO_LOGIN, walletAddress);
        }
    }

    public static String getWalletLoginAddressWithType(Context context) {
        return MMKV.defaultMMKV().decodeString(WALLET_AUTO_LOGIN, "");
    }

    
    public static String getWalletTypeAddress(Context context, int walletType) {
        String walletAddress = MMKV.defaultMMKV().decodeString("walletTypeAddr" + walletType, "");
        if (TextUtils.isEmpty(walletAddress)) {
            return null;
        }
        return walletAddress;
    }

    
    public static void setWalletLoadNum(String address, int num) {
        MMKV.defaultMMKV().encode("walletloadnumaddress" + address, num);
    }

    public static int getWalletLoadNum(String address) {
        return MMKV.defaultMMKV().decodeInt("walletloadnumaddress" + address, 0);
    }


    
    public static void setMustAssets(Context context, String type, List<AssertBean> assertBeanList) {

        MMKV.defaultMMKV().encode("mustchooseassets" + type, gson.toJson(assertBeanList));
    }

    public static List<AssertBean> getMustAssets(Context context, String type) {
        String walletAddress = MMKV.defaultMMKV().decodeString("mustchooseassets" + type, "");
        if ("".equals(walletAddress) || walletAddress.length() < 5) {
            return new ArrayList<>();
        }
        return gson.fromJson(walletAddress, new TypeToken<List<AssertBean>>() {
        }.getType());
    }


    
    public static void setCanAssets(Context context, String type, List<AssertBean> assertBeanList) {

        MMKV.defaultMMKV().encode("canchooseassets" + type, gson.toJson(assertBeanList));
    }

    public static List<AssertBean> getCanAssets(Context context, String type) {

        String walletAddress = MMKV.defaultMMKV().decodeString("canchooseassets" + type, "");
        if ("".equals(walletAddress) || walletAddress.length() < 5) {
            return new ArrayList<>();
        }
        return gson.fromJson(walletAddress, new TypeToken<List<AssertBean>>() {
        }.getType());
    }

    
    public static String getHostUrl(Context context) {
        return BuildConfig.HOST_QUOTES;
    }

    public static void setHostUrl(Context context, String url) {

        MMKV.defaultMMKV().encode("hostUrl", url);
    }


    public static String getHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(HOST, BuildConfig.HOST);
    }

    public static String getEthHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(ETH_HOST, BuildConfig.ETH_HOST);
    }

    public static String getAppid(Context context) {

        return MMKV.defaultMMKV().decodeString(APP_ID, "");
    }

    public static String getDMHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(DM_HOST, BuildConfig.HOST_DM_QUOTES);
    }

    public static String getOtherHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(OTHER_HOST, BuildConfig.HOST_OTHER_QUOTES);
    }

    public static String getTrxHostApi(Context context) {
        return SpUtil.getDefNode(WalletUtil.TRX_COIN);
    }

    public static String getFilHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(FIL_HOST, BuildConfig.HOST_FIL);
    }

    public static boolean saveFilHostApi(String host) {

        return MMKV.defaultMMKV().encode(FIL_HOST, host);
    }

    public static String getDogeHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(DOGE_HOST, BuildConfig.HOST_DOGE);
    }

    public static boolean saveDogeHostApi(String host) {

        return MMKV.defaultMMKV().encode(DOGE_HOST, host);
    }

    public static String getDotHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(DOT_HOST, BuildConfig.HOST_DOT);
    }

    public static boolean saveDotHostApi(String host) {

        return MMKV.defaultMMKV().encode(DOT_HOST, host);
    }

    public static String getLtcHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(LTC_HOST, BuildConfig.HOST_LTC);
    }

    public static boolean saveLtcHostApi(String host) {

        return MMKV.defaultMMKV().encode(LTC_HOST, host);
    }

    public static String getBchHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(BCH_HOST, BuildConfig.HOST_BCH);
    }

    public static boolean saveBchHostApi(String host) {

        return MMKV.defaultMMKV().encode(BCH_HOST, host);
    }

    public static String getZecHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(ZEC_HOST, BuildConfig.HOST_ZEC);
    }

    public static boolean saveZecHostApi(String host) {

        return MMKV.defaultMMKV().encode(ZEC_HOST, host);
    }

    public static String getEtcHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(ETC_HOST, BuildConfig.HOST_ETC);
    }

    public static boolean saveEtcHostApi(String host) {

        return MMKV.defaultMMKV().encode(ETC_HOST, host);
    }

    public static String getSgbHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(SGB_HOST, BuildConfig.HOST_SGB);
    }

    public static boolean saveSgbHostApi(String host) {

        return MMKV.defaultMMKV().encode(SGB_HOST, host);
    }

    public static String getSolHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(SOL_HOST, BuildConfig.HOST_SOL);
    }

    public static boolean saveSolHostApi(String host) {

        return MMKV.defaultMMKV().encode(SOL_HOST, host);
    }

    public static String getMaticHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(MATIC_HOST, BuildConfig.HOST_MATIC);
    }

    public static boolean saveMaticHostApi(String host) {

        return MMKV.defaultMMKV().encode(MATIC_HOST, host);
    }

    public static String getMarketPriceHost(Context context) {

        return MMKV.defaultMMKV().decodeString(MARKET_PRICE_HOST, BuildConfig.HOST_MARKET_PRICE);
    }

    public static String getMCCHostApi(Context context) {
        return SpUtil.getDefNode(WalletUtil.MCC_COIN);
    }

    public static String getTrustWalletHostApi(Context context) {

        return MMKV.defaultMMKV().decodeString(TRUST_WALLET_HOST, BuildConfig.HOST_TRUSTWALLET);
    }
}
