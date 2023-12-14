

package com.wallet.ctc.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;



public class PhoneUtil {
    private static final String TAG = "PhoneUtil";

    
    public static String getMyUUID(Activity mActivity){

        final TelephonyManager tm = (TelephonyManager)mActivity.getBaseContext().getSystemService(mActivity.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, tmPhone, androidId;

        tmDevice = "" + tm.getDeviceId();

        tmSerial = "" + tm.getSimSerialNumber();

        androidId = "" + Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());

        String uniqueId = deviceUuid.toString();

        return uniqueId;
    }
    
    public static String getIMEI(Context mActivity){
        try {
            TelephonyManager tm = (TelephonyManager)mActivity.getSystemService(Activity.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if(imei==null)
            {
                
                imei = Settings.Secure.getString(mActivity.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static String getphoneModel(){
        return android.os.Build.MODEL;
    }
    
    public static String getphoneManufactor(){
        return android.os.Build.MANUFACTURER;
    }
     
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    
    public static String getIPAddress(Context context) {
        try {
            NetworkInfo info = ((ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    try {
                        
                        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                            NetworkInterface intf = en.nextElement();
                            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                                InetAddress inetAddress = enumIpAddr.nextElement();
                                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                    return inetAddress.getHostAddress();
                                }
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }

                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
                    return ipAddress;
                }
            } else {
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
