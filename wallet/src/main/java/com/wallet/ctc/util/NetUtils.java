

package com.wallet.ctc.util;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.NetworkInfo.State.CONNECTED;
import static android.net.wifi.WifiManager.EXTRA_WIFI_STATE;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_UNKNOWN;
import static android.telephony.TelephonyManager.NETWORK_TYPE_CDMA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA;
import static android.telephony.TelephonyManager.NETWORK_TYPE_LTE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_UMTS;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class NetUtils {

    
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        return info != null && info.isConnected();
    }


    
    public static boolean isWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        return info != null && info.getType() == TYPE_WIFI;
    }

    
    public static boolean wifiEnable(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        return info != null && info.getType() == TYPE_WIFI && ping();
    }


    
    public static boolean is2G(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        if (info != null) {
            int subType = info.getSubtype();
            return subType == NETWORK_TYPE_EDGE || subType == NETWORK_TYPE_GPRS || subType == NETWORK_TYPE_CDMA;
        } else {
            return false;
        }
    }

    
    public static boolean is3G(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        if (info != null) {
            int subType = info.getSubtype();
            return subType == NETWORK_TYPE_UMTS || subType == NETWORK_TYPE_HSDPA || subType == NETWORK_TYPE_EVDO_0;
        } else {
            return false;
        }
    }

    
    public static boolean is4G(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        if (info != null) {
            int subType = info.getSubtype();
            return subType == NETWORK_TYPE_LTE;
        } else {
            return false;
        }
    }

    
    public static boolean isMobie(Context context) {
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        return info != null && info.getType() == TYPE_MOBILE && info.isAvailable();
    }


    public static final int NONET = -1;
    public static final int G2 = 2;
    public static final int G3 = 3;
    public static final int G4 = 4;
    public static final int WIFI = 101;
    public static final int MOBIE = 102;

    
    public static int getNetType(Context context) {

        
        NetworkInfo info = getNetworkInfo(context.getApplicationContext());
        if (info == null) {
            
            return NONET;
        }
        int netType = info.getType();
        if (isWifi(context)) {
            netType = WIFI;
        } else if (isMobie(context)) {
            if (is4G(context)) {
                return G4;
            } else if (is3G(context)) {
                return G3;
            } else if (is2G(context)) {
                return G2;
            } else {
                return G2;
            }
        }
        return netType;
    }

    
    public static boolean isGPSEnabled(Context context) {
        return getLocationService(context.getApplicationContext()).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    
    public static String getIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                for (Enumeration<InetAddress> ipAddr = en.nextElement().getInetAddresses(); ipAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    
    public static String getIMEI(Context context) {
        return getTelephonyService(context.getApplicationContext()).getDeviceId();
    }

    

    public static final boolean ping() {
        try {
            String ip = "www.baidu.com";
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);
            
            
            
            
            
            
            
            
            
            
            int status = p.waitFor();
            if (status == 0) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }


    
    public static boolean isWifiEnabled(Context context) {
        TelephonyManager telManager = getTelephonyService(context.getApplicationContext());
        NetworkInfo info = getConnectivityService(context.getApplicationContext()).getActiveNetworkInfo();
        if (info != null) {
            return info.getState() == NetworkInfo.State.CONNECTED || telManager.getNetworkType() == NETWORK_TYPE_UMTS;
        } else {
            return false;
        }
    }


    
    private static NetworkInfo getNetworkInfo(Context context) {
        return getConnectivityService(context).getActiveNetworkInfo();
    }

    
    private static TelephonyManager getTelephonyService(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    
    private static ConnectivityManager getConnectivityService(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    
    private static LocationManager getLocationService(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    
    public static boolean wifiEnable(Parcelable extra) {
        if (null != extra) {
            NetworkInfo.State state = ((NetworkInfo) extra).getState();
            return state == CONNECTED;
        }
        return false;
    }

    
    public static int wifiOrNet(Context context, String action) {
        if (isNetworkConnected(context)) {
            return isWifi(context) ? WIFI : (isMobie(context) ? MOBIE : NONET);
        }
        return NONET;
    }

    
    public static boolean isWifiOpen(Intent intent, String action) {
        switch (intent.getIntExtra(EXTRA_WIFI_STATE, 0)) {
            case WIFI_STATE_ENABLED://wifi
                return true;
            case WIFI_STATE_ENABLING:
            case WIFI_STATE_DISABLING:
            case WIFI_STATE_UNKNOWN:
            case WIFI_STATE_DISABLED:
            default://wifi
                return false;

        }
    }

}
