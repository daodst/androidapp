

package com.wallet.ctc.util;

import static com.wallet.ctc.crypto.WalletUtil.BNB_COIN;
import static com.wallet.ctc.crypto.WalletUtil.ETH_COIN;
import static com.wallet.ctc.crypto.WalletUtil.MCC_COIN;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.wallet.ctc.R;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.model.blockchain.RootWalletInfo;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.app.AppApplication;
import common.app.mall.util.ToastUtil;
import common.app.utils.AppWidgetUtils;
import common.app.utils.digest.EAICoderUtil;




public class AllUtils {

    
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

    
    public final static String  getMD5(String plaintext) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = plaintext.getBytes();
            
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            
            mdInst.update(btInput);
            
            byte[] md = mdInst.digest();
            
            int j = md.length;
            char str[] = new char[j * 2];
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
    public static String getTimeByStr(String timeStr) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdr.parse(timeStr);
            long time = date.getTime() / 1000;
            return getTimeNY(time+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getTimeByStrText(String timeStr) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdr.parse(timeStr);
            long time = date.getTime()/1000;
            return getTimeFormatText(time+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public final static String getTime(){
        
        String  str=String.valueOf(System.currentTimeMillis()/1000);
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
    public static String getTimeJavaNYR(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        String times = sdr.format(new Date(time));
        return times;
    }

    public static String getTimeNYRSF(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        @SuppressWarnings("unused")
        
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String getTimeYRSF(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM/dd HH:mm");
        @SuppressWarnings("unused")
        
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String getTimeNY(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yy-MM");
        @SuppressWarnings("unused")
        
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String getTimeYR(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM-dd");
        @SuppressWarnings("unused")
        
                int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String getTimeSF(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm");
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
    private final static long minute = 60 * 1000;
    private final static long hour = 60 * minute;
    private final static long day = 24 * hour;
    private final static long month = 31 * day;
    private final static long year = 12 * month;


    
    public static String getTimeFormatText(String time) {
        Date tDate = new Date(Integer.parseInt(time) * 1000L);
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        if (tDate == null) {
            return null;
        }
        long diff = new Date().getTime() - tDate.getTime();
        long r = 0;
        if (diff > month) {
            String times = sdr.format(tDate);
            return times;
        }
        if (diff > day) {
            r = (diff / day);
            return r + AppApplication.getContext().getString(R.string.days_ago);
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + AppApplication.getContext().getString(R.string.hour_ago);
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + AppApplication.getContext().getString(R.string.minutes_ago);
        }
        return AppApplication.getContext().getString(R.string.just_a_moment_ago);
    }
    
    public static String getTimeFormatJavaText(long time) {
        Date tDate = new Date(time);
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        if (tDate == null) {
            return null;
        }
        long diff = new Date().getTime() - tDate.getTime();
        long r = 0;
        if (diff > month) {
            String times = sdr.format(tDate);
            return times;
        }
        if (diff > day) {
            r = (diff / day);
            return r + AppApplication.getContext().getString(R.string.days_ago);
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + AppApplication.getContext().getString(R.string.hour_ago);
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + AppApplication.getContext().getString(R.string.minutes_ago);
        }
        return AppApplication.getContext().getString(R.string.just_a_moment_ago);
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

    
    public static String str2HexStr(String str)
    {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    
    public static String hexStr2Str(String hexStr)
    {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    
    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    
    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
    }

    
    public static String strToUnicode(String strText)
            throws Exception
    {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++)
        {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else 
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    
    public static String unicodeToString(String hex)
    {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++)
        {
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

    
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    
    private static final String REGEX_HTML = "<[^>]+>";
    
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";
    public static String delHTMLTag(String htmlStr) {
        if (TextUtils.isEmpty(htmlStr)) {
            return "";
        }
        
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        return htmlStr.trim(); 
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
    public static String  danwei(BigDecimal amount){
        String danwei="";
        if(amount.doubleValue()>10000){
            amount=amount.divide(new BigDecimal(10000));
            danwei="W";
        }
        amount=amount.setScale(2,BigDecimal.ROUND_HALF_UP);
        return amount+danwei;
    }


    
    public static String getAddressByUid(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return userId;
        }
        String address = userId;
        if (address.startsWith("@")) {
            address = address.substring(1);
        }
        if (address.contains(":")) {
            String[] strs = address.split(":");
            if (null != strs && strs.length > 0) {
                address = strs[0];
            }
        }
        return address;
    }

    
    public static String getServerNameByUid(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return userId;
        }
        String address = userId;
        if (address.startsWith("@")) {
            address = address.substring(1);
        }
        if (address.contains(":")) {
            String[] strs = address.split(":");
            if (strs != null && strs.length == 2) {
                address = strs[1];
            }
        }
        return address;
    }

    
    public static void copyText(String text) {
        Context context = AppApplication.getContext();
        if (TextUtils.isEmpty(text) || null == context) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        
        cm.setText(text);
        ToastUtil.showToast(context.getResources().getString(R.string.copy_success));
    }

    
    public static boolean isWalletEquals(WalletEntity wallet1, WalletEntity wallet2) {
        if (null != wallet1 && wallet2 != null) {
            String addr1 = wallet1.getAllAddress();
            String addr2 = wallet2.getAllAddress();
            int type1 = wallet1.getType();
            int type2 = wallet2.getType();
            return !TextUtils.isEmpty(addr1) && addr1.equals(addr2) && type1 == type2;
        }
        return false;
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


    
    public static boolean isNumberUpZero(String numStr) {
        if (TextUtils.isEmpty(numStr)) {
            return false;
        }
        try {
            if (new BigDecimal(numStr).compareTo(new BigDecimal(0)) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return false;
    }


    
    public static boolean PAY_USE_ONE_PWD = true;
    public static String getFingerPayKey(Context context) {
        WalletEntity wallet = getFingerPayWallet(context);
        return AllUtils.getFingerPayKey(wallet);
    }

    
    public static WalletEntity getFingerPayWallet(Context context) {
        
        WalletEntity wallet = null;
        if (PAY_USE_ONE_PWD) {
            RootWalletInfo rootWalletInfo = WalletDBUtil.getInstent(context).getRootWalletInfo(WalletUtil.ETH_COIN);
            if (null != rootWalletInfo && rootWalletInfo.rootWallet != null) {
                wallet = rootWalletInfo.rootWallet;
            }
        } else {
            wallet = WalletDBUtil.getInstent(context).getWalletInfo();
        }
        return wallet;
    }


    
    public static String getFingerPayKey(WalletEntity wallet) {
        String key = "";
        if (wallet != null){
            key = wallet.getAllAddress()+","+wallet.getType();
        }
        if (!TextUtils.isEmpty(key)) {
            key = EAICoderUtil.getMd5Code16(key);
        }
        return key;
    }


    
    public static int getWalletTepyByRootName(String keyname) {
        if (AppWidgetUtils.KEY_DST.equalsIgnoreCase(keyname)){
            return MCC_COIN;
        } else if(AppWidgetUtils.KEY_ETH.equalsIgnoreCase(keyname)) {
            return ETH_COIN;
        } else if(AppWidgetUtils.KEY_BSC.equalsIgnoreCase(keyname)) {
            return BNB_COIN;
        }
        return -1;
    }


}
