

package common.app.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class AllUtils {

    
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    
    private static DecimalFormat dfs = null;

    public static DecimalFormat format(String pattern) {
        if (dfs == null) {
            dfs = new DecimalFormat();
        }
        dfs.setRoundingMode(RoundingMode.FLOOR);
        dfs.applyPattern(pattern);
        return dfs;
    }

    
  
    
    public static int getDisplayMetricsWidth(WindowManager window) {
        int i = window.getDefaultDisplay().getWidth();
        return i;
    }

    public static int getDisplayMetricsHeight(WindowManager window) {

        int j = window.getDefaultDisplay().getHeight();
        return j;
    }

    
    public static void backgroundAlpha(float bgAlpha, Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = bgAlpha; 
        window.setAttributes(lp);
    }

    
    public final static String getTimeJava() {
        
        String str = String.valueOf(System.currentTimeMillis());
        return str;
    }

    
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    
    public final static String getMD5(String plaintext) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = plaintext.getBytes();
            
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            
            mdInst.update(btInput);
            
            byte[] md = mdInst.digest();
            
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    
    public final static String getTime() {
        
        String str = String.valueOf(System.currentTimeMillis() / 1000);
        return str;
    }

    
    public static String times(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressWarnings("unused")
        
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    public static String getTimeNYR(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressWarnings("unused")
        
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }

    
    public static String formatDisplayTime(String time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                Date tDate = new Date(Integer.parseInt(time) * 1000L);

                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {

                    SimpleDateFormat halfDf = new SimpleDateFormat("MM-dd");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = "";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "" + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return display;
    }

    
    public static long getFreeSpace() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        
        
        
        
        return blockSize * availableBlocks;
    }

    
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
        
    }

    
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }

    
    public static String strToUnicode(String strText)
            throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128) {
                str.append("\\u" + strHex);
            } else 
            {
                str.append("\\u00" + strHex);
            }
        }
        return str.toString();
    }

    
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            
            String s1 = s.substring(2, 4) + "00";
            
            String s2 = s.substring(4);
            
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }


    
    public static int dp2px(Context context, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources()
                .getDisplayMetrics());
    }

    
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources()
                .getDisplayMetrics());
    }

    
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }


    
    public static boolean isAppOnForeground(Context context) {
        
        
        ActivityManager activityManager = (ActivityManager) context
                .getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isTargetRunningForeground(Context context,List<String> targetActivityNames) {
        String topActivityName =  ((Activity)context).getClass().getSimpleName();
        if (!TextUtils.isEmpty(topActivityName) && targetActivityNames.contains(topActivityName)) {
            return true;
        }
        return false;
    }
    
    private static boolean hasPermission(Context context){
        if( Build.VERSION.SDK_INT > 24 ) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    
    public static String createRandom(boolean numberFlag, int length){
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);

        return retStr;
    }


    
    public static String removeParam(String url, String ...name){
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        for (String s : name) {
            
            url = url.replaceAll("&?"+s+"=[^&]*","");
        }
        return url;
    }

    
    public static List<String> distinctList(List<String> list) {
        if (null == list || list.size() ==0) {
            return list;
        }
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(list);
        List<String> distinctList = new ArrayList<>(hashSet);
        return distinctList;
    }

    
    public static String getHomeUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String setUrl = url;
        if (!setUrl.contains("://")) {
            setUrl = "http://"+url;
        }
        Uri uri = Uri.parse(setUrl);
        return uri.getScheme()+"://"+uri.getHost();
    }

    
    public static String urlToHostPort(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        try {
            String setUrl = url;
            if (!setUrl.contains("://")) {
                setUrl = "http://"+url;
            }
            Uri uri = Uri.parse(setUrl);
            return uri.getHost()+":"+uri.getPort();
        } catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    
    public static boolean isHostEquals(String url1, String url2) {
        if (!TextUtils.isEmpty(url1) && !TextUtils.isEmpty(url2)) {
            if (url1.equals(url2)) {
                return true;
            }
            String host1= urlToHost(url1);
            String host2 = urlToHost(url2);
            if (!TextUtils.isEmpty(host1) && !TextUtils.isEmpty(host2)) {
                return host1.equals(host2);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    
    public static String urlToHost(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        try {
            String setUrl = url;
            if (!setUrl.contains("://")) {
                setUrl = "http://"+url;
            }
            Uri uri = Uri.parse(setUrl);
            return uri.getHost();
        } catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

    
    public static String urlDelDt(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        String urld = url;
        if (url.endsWith("/") && url.length() > 1 ) {
            urld = url.substring(0, url.length()-1);
        }
        return urld;
    }


    public static boolean isContextDestroyed(Context context) {
        if (null == context) {
            return true;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isFinishing() || activity.isDestroyed()) {
                return true;
            }
        }
        return false;
    }


    
    public static boolean isIp(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        try {
            String rexIp = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
            Pattern pattern = Pattern.compile(rexIp);
            Matcher matcher = pattern.matcher(host);
            return matcher.matches();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    
    public static String getTenDecimalValue(String bigNum, int decimal, int scale) {
        if (TextUtils.isEmpty(bigNum)) {
            return bigNum;
        }
        try {
            String amount = new BigDecimal(bigNum).divide(new BigDecimal(Math.pow(10, decimal)), scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
            return amount;
        } catch (Exception e){
            e.printStackTrace();
        }
        return bigNum;
    }


    
    public static String getBigDecimalValue(String tenNum, int decimal) {
        if (TextUtils.isEmpty(tenNum)) {
            return tenNum;
        }
        try {
            String amount = new BigDecimal(tenNum).multiply(new BigDecimal(Math.pow(10, decimal))).stripTrailingZeros().toPlainString();
            return amount;
        } catch (Exception e){
            e.printStackTrace();
        }
        return tenNum;
    }


    public static String listToStr(List<String> list) {
        if (list == null) {
            return null;
        }
        return Arrays.toString(list.toArray());
    }

    
    public static boolean isNowDayTime(long timeMs) {
        if (timeMs == 0) {
            return false;
        }
        String dayStr = TimeUtil.getYYYYMMdd(timeMs);
        String nowDayStr = TimeUtil.getYYYYMMdd(System.currentTimeMillis());
        if (nowDayStr.equals(dayStr)) {
            
            return true;
        } else {
            return false;
        }
    }
}
