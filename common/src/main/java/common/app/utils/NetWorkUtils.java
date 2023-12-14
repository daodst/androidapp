

package common.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.app.ActivityRouter;
import common.app.AppApplication;
import common.app.R;
import common.app.mall.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class NetWorkUtils {
    private static final String TAG = "NetWorkUtils";

    
    public static int NET_WORK_STATE_UN_CONNECT = 0;

    
    public static int NET_WORK_STATE_WIFI = 1;

    
    public static int NET_WORK_STATE_MOBILE = 2;

    public static int NET_WORK_STATE_UNKOWN = 3;

    public static int NET_WORK_STATE_CONNECTED = 4;


    public static int HOST_UN_REACHABLE = 5;
    public static int HOST_REACHABLE = 6;

    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_WIFI = 1;
    public static final int NETWORK_CLASS_2_G = 2;
    public static final int NETWORK_CLASS_3_G = 3;
    public static final int NETWORK_CLASS_4_G = 4;

    private Context mContext;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    public NetWorkUtils(Context mContext) {
        this.mContext = mContext;
        connectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    public NetWorkUtils() {
        this.mContext = AppApplication.getContext();
        connectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
    }

    public boolean isConnected() {
        return networkInfo != null && networkInfo.isConnected();
    }

    
    public int getNetType() {
        if (!isConnected()) {
            return NET_WORK_STATE_UN_CONNECT;
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return NET_WORK_STATE_WIFI;
        } else {
            return NET_WORK_STATE_MOBILE;
        }
    }

    
    public static int getNetWorkStatus(Context context) {
        int netWorkType = NETWORK_CLASS_UNKNOWN;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                netWorkType = getNetWorkClass(context);
            }
        }

        return netWorkType;
    }

    private static int getNetWorkClass(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;

            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;

            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;

            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    
    public static boolean isDomainOrIp(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String rexDomain = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?.*";
        String rexIp = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";

        Pattern pattern = Pattern.compile(rexDomain);
        Matcher matcher = pattern.matcher(url); 
        if (matcher.matches()) {
            return true;
        }
        pattern = Pattern.compile(rexIp);
        matcher = pattern.matcher(url); 
        return matcher.matches();
    }

    
    public static long pingIpAddress2(String url) {
        long time = -1;
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "url is null");
            return time;
        }
        URL uRL = null;
        try {
            uRL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "Url exception:", e);
            return time;
        }
        try {
            long startTime = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) uRL.openConnection();
            conn.setRequestMethod("TRACE");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            int code = conn.getResponseCode();
            long endTime = System.currentTimeMillis();
            time = endTime - startTime;
            conn.disconnect();
        } catch (Exception e) {
            time = -1;
        }
        return time;
    }


    
    public static boolean isNetWorkOnline(Context context) {
        if (!hasNetWork(context)) {
            return false;
        }
        int netState = checkNetStateByPing("");
        return netState == NET_WORK_STATE_CONNECTED;
    }


    
    public static boolean hasNetWork(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                return true;
            }
            NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());  
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public static void checkHostStatsAndAlertToast(Context context, String url) {
        if (null == context || TextUtils.isEmpty(url)) {
            return;
        }
        Context appContext = context.getApplicationContext();
        checkUrlReachableOb(appContext, url).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer state) {
                        if (null == appContext) {
                            return;
                        }
                        if (state == NETWORK_CLASS_UNKNOWN) {
                            ToastUtil.showToast(appContext.getString(R.string.network_class_unknown_info));
                        } else if (state == HOST_REACHABLE) {
                            ToastUtil.showToast(appContext.getString(R.string.host_reachable_info));
                        } else if (state == HOST_UN_REACHABLE) {
                            
                            Intent intent = ActivityRouter.getIntent(appContext, ActivityRouter.App.A_NodeUnableDialogActivity);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            appContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    
    @SuppressWarnings("all")
    public static void checkHostStatsAndAlert(Context context, String url) {
        if ((System.currentTimeMillis()- SpUtil.lastShowNodeAlertTime < SpUtil.NODE_ERROR_ALERT_TIME_OUT)) {
            Log.w(TAG, "checkHostStatsAndAlert() recent no show return");
            return;
        }
        if (null == context || TextUtils.isEmpty(url)) {
            return;
        }
        Context appContext = context.getApplicationContext();

        long currentTimeMillis = System.currentTimeMillis();
        Log.i(TAG, "-------------------" + currentTimeMillis);
        Disposable disposable = Observable.create((ObservableOnSubscribe<Integer>) e -> {
            int result = checkUrlReachable(context, url);
            e.onNext(result);
        }).flatMap((Function<Integer, ObservableSource<Integer>>) result -> {
            if (result == HOST_UN_REACHABLE) {
                
                return Observable.timer(1, TimeUnit.MINUTES).flatMap(new Function<Long, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) throws Exception {
                        return Observable.just(checkUrlReachable(context, url));
                    }
                });
            } else {
                return Observable.just(result);
            }
        }).compose(RxSchedulers.io_main()).subscribe(state -> {

            long currentTimeMillis2 = System.currentTimeMillis();
            Log.i(TAG, "-------------------" + (currentTimeMillis2 - currentTimeMillis)+", "+state);
            if (null != state && state == HOST_UN_REACHABLE) {
                
                if (null != appContext) {
                    Intent intent = ActivityRouter.getIntent(appContext, ActivityRouter.App.A_NodeUnableDialogActivity);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appContext.startActivity(intent);
                }
            }
        }, throwable -> {
            Log.e(TAG, throwable+":"+throwable.getMessage());
        });



    }

    
    public static Observable<Integer> checkUrlReachableOb(Context context, String url) {
        return Observable.create((ObservableOnSubscribe<Integer>) e -> {
            int result = checkUrlReachable(context, url);
            e.onNext(result);
            e.onComplete();
        }).subscribeOn(Schedulers.io());
    }

    
    public static int checkUrlReachable(Context context, String url) {
        if (!hasNetWork(context)) {
            Log.w(TAG, "no network connect");
            return NET_WORK_STATE_UN_CONNECT;
        }
        int netState = checkNetStateByPing("");
        if (netState == NET_WORK_STATE_CONNECTED) {
            
            netState = checkNetByUrlConnect(url);
            if (netState == NET_WORK_STATE_CONNECTED) {
                return HOST_REACHABLE;
            } else if (netState == NET_WORK_STATE_UN_CONNECT) {
                return HOST_UN_REACHABLE;
            } else {
                return NET_WORK_STATE_UNKOWN;
            }
        } else {
            Log.w(TAG, "no network connect--" + netState);
            return NET_WORK_STATE_UN_CONNECT;
        }
    }

    
    public static int checkNetBySocketPing() {
        int netState = NET_WORK_STATE_UNKOWN;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 3000);
            socket.close();
            netState = NET_WORK_STATE_CONNECTED;
        } catch (IOException e) {
            e.printStackTrace();
            netState = NET_WORK_STATE_UN_CONNECT;
        }
        return netState;
    }

    
    public static int checkNetByUrlConnect(String url) {
        if (TextUtils.isEmpty(url)) {
            url = "www.baidu.com";
        }
        URL uRL = null;
        try {
            uRL = new URL(url);
        } catch (MalformedURLException e) {
            LogUtil.w(TAG, "Url exception:"+e+":"+e.getMessage());
            return NET_WORK_STATE_UNKOWN;
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) uRL.openConnection();
            conn.setRequestMethod("TRACE");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            int code = conn.getResponseCode();
            conn.disconnect();
            return NET_WORK_STATE_CONNECTED;
        } catch (Exception e) {
            LogUtil.w(TAG, "urlConnect error "+e+":"+e.getMessage());
        }
        return NET_WORK_STATE_UN_CONNECT;
    }

    
    public static int checkNetStateByPing(String url) {
        
        int retryCount = 2;
        int timeOut = 1;
        if (TextUtils.isEmpty(url)) {
            url = "www.baidu.com";
        }
        int netState = NET_WORK_STATE_UNKOWN;
        final String cmdContent = " -c " + retryCount + " -w " + timeOut + " " + url;
        Process process = null;
        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec("ping" + cmdContent);
            if (process == null) {
                process = runtime.exec("/system/bin/ping" + cmdContent);
            }
            if (process == null) {
                return netState;
            }
            InputStream input = process.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuilder stringBuilder = new StringBuilder();
            String content;
            while ((content = in.readLine()) != null) {
                stringBuilder.append(content);
            }
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                
                netState = NET_WORK_STATE_CONNECTED;
            } else {
                if (stringBuilder.indexOf("packet loss") != -1) {
                    if (stringBuilder.indexOf("100% packet loss") == -1) {
                        
                        netState = NET_WORK_STATE_CONNECTED;
                    } else {
                        
                        netState = NET_WORK_STATE_UN_CONNECT;
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (null != process) {
                process.destroy();
            }
            runtime.gc();
        }
        return netState;
    }

    
    public static int pingIpAddress(String ipAddress) {
        try {
            Context context = AppApplication.getContext();
            if (getNetWorkStatus(context) == NetWorkUtils.NET_WORK_STATE_UN_CONNECT) {
                return 1000;
            }
            
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 1 " + ipAddress);
            int status = process.waitFor();
            
            BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = "";
            String delay = "";
            while ((str = buf.readLine()) != null) {
                LogUtil.i(TAG, ipAddress + "---str=" + str + ", status=" + status);
                if (str.contains("avg")) {
                    int i = str.indexOf("/", 20);
                    int j = str.indexOf(".", i);
                    System.out.println(":" + str.substring(i + 1, j));
                    delay = str.substring(i + 1, j);
                }
            }

            if (delay.equals("")) {
                return 1000;
            } else {
                return Integer.parseInt(delay);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ioException---" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("interuptedEx---" + e);
        }
        return -1;
    }
}
