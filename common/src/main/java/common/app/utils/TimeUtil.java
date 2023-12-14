

package common.app.utils;


import android.annotation.SuppressLint;
import android.text.format.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import common.app.Injection;
import common.app.R;


public class TimeUtil {

    private static final String TAG = "TimeUtil";

    
    public static String getDate(long time) {

        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");

        String week = DateUtils.formatDateTime(Injection.provideContext(), time, DateUtils.FORMAT_SHOW_WEEKDAY).toString();
        return new StringBuilder().append(formate.format(date)).append(" ").append(week).toString();
    }

    
    public static String getStrTime(String timeStamp) {
        if (timeStamp != null && "".equals(timeStamp) && timeStamp.length() < 5) {
            return "";
        }
        String timeString = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        long l = Long.valueOf(timeStamp);
        timeString = sdf.format(new Date(l * 1000L));
        return timeString;
    }


    public static String getYYYYMMdd(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        return formate.format(date);
    }

    public static String getYYYYMMdd2(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy/MM/dd");
        return formate.format(date);
    }

    public static String getRecentTime(long timeMillis){
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(timeMillis);
        if(calendar.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)){
            
            if(calendar.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)){
                
                if(calendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)){
                    
                    return getTime(timeMillis);
                } else {
                    return getMMddHHMM2(timeMillis);
                }
            } else {
                return getMMddHHMM2(timeMillis);
            }
        } else {
            return getYYYYMMddHHMM2(timeMillis);
        }
    }

    public static String getMMddHHMM(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("MM-dd HH:mm");
        return formate.format(date);
    }

    public static String getMMddHHMM2(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("MM/dd HH:mm");
        return formate.format(date);
    }

    public static String getYYYYMMddHHMM(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formate.format(date);
    }

    public static String getYYYYMMddHHMM2(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return formate.format(date);
    }

    public static String getYYYYMMddHHMMSS(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formate.format(date);
    }

    public static String getdd(long time) {
        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("dd");
        return formate.format(date);
    }

    
    public static String getTimeS(long time) {

        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("HH:mm:ss");
        return formate.format(date);
    }

    public static String getTimeMS(long time) {

        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("mmss");
        return formate.format(date);
    }

    
    public static String getTime(long time) {

        Date date = new Date(time);
        SimpleDateFormat formate = new SimpleDateFormat("HH:mm");
        return formate.format(date);
    }

    
    public static String getDay(long time) {
        boolean isToday = DateUtils.isToday(time);
        if (isToday) {
            return getDifftime(time);
        } else {
            
            Date date = new Date(time);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            
            Calendar now = Calendar.getInstance();
            long nowTime = System.currentTimeMillis();
            now.setTimeInMillis(nowTime);
            
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
            SimpleDateFormat formatDay = new SimpleDateFormat("dd");
            if (formatYear.format(time).equals(formatYear.format(nowTime))) {
                
                if (formatMonth.format(time).equals(formatMonth.format(nowTime))) {
                    
                    int yestoday = Integer.valueOf(formatDay.format(nowTime)) - 1;
                    if (formatDay.format(time).equals(String.valueOf(yestoday))) {
                        return Injection.provideContext().getString(R.string.date_y);
                    }
                } else {
                    
                    int cMonth = Integer.valueOf(formatMonth.format(time));
                    int nowMonth = Integer.valueOf(formatMonth.format(nowTime));
                    int cLastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    int cDay = Integer.valueOf(formatDay.format(time));
                    int nowDay = Integer.valueOf(formatDay.format(nowTime));

                    if ((nowDay == 1) && (cDay == cLastDay) && (cMonth == nowMonth - 1 || cMonth == nowMonth + 12)) {
                        return Injection.provideContext().getString(R.string.date_y);
                    }
                }
                SimpleDateFormat formate = new SimpleDateFormat("MM-dd");
                return formate.format(date).toString();
            } else {

                SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
                return formate.format(date).toString();
            }
            
            
        }
    }

    
    public static String getDay(Date date) {
        long time = date.getTime();
        boolean isToday = DateUtils.isToday(time);
        if (isToday) {
            return getDifftime(time);
        } else {
            
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            
            Calendar now = Calendar.getInstance();
            long nowTime = System.currentTimeMillis();
            now.setTimeInMillis(nowTime);
            
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM");
            SimpleDateFormat formatDay = new SimpleDateFormat("dd");
            if (formatYear.format(time).equals(formatYear.format(nowTime))) {
                
                if (formatMonth.format(time).equals(formatMonth.format(nowTime))) {
                    
                    int yestoday = Integer.valueOf(formatDay.format(nowTime)) - 1;
                    if (formatDay.format(time).equals(String.valueOf(yestoday))) {
                        return Injection.provideContext().getString(R.string.date_y);
                    }
                } else {
                    
                    int cMonth = Integer.valueOf(formatMonth.format(time));
                    int nowMonth = Integer.valueOf(formatMonth.format(nowTime));
                    int cLastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    int cDay = Integer.valueOf(formatDay.format(time));
                    int nowDay = Integer.valueOf(formatDay.format(nowTime));

                    if ((nowDay == 1) && (cDay == cLastDay) && (cMonth == nowMonth - 1 || cMonth == nowMonth + 12)) {
                        return Injection.provideContext().getString(R.string.date_y);
                    }
                }
                SimpleDateFormat formate = new SimpleDateFormat("MM-dd");
                return formate.format(date).toString();
            } else {

                SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
                return formate.format(date).toString();
            }
            
            
        }
    }

    
    public static boolean isSameDay(long when1, long when2) {

        Date date1 = new Date(when1);
        Date date2 = new Date(when2);
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        if (formate.format(date1).equals(formate.format(date2))) {
            return true;
        }
        return false;
    }

    
    private static String getDifftime(long time) {
        Date date = new Date(time);
        
        long delta = (new Date().getTime() - date.getTime()) / 1000;
        
        if (delta / (60) > 5 && delta / (60) < 46) {
            
            return delta / (60) + Injection.provideContext().getString(R.string.date_minites);
        } else if (delta / (60) < 5) {
            return Injection.provideContext().getString(R.string.date_just);
        } else {
            return getTime(time);
        }
    }


    public static String getTimestampString(Date var0) {
        String var1 = null;
        String var2 = Locale.getDefault().getLanguage();
        boolean var3 = var2.startsWith("zh");
        long var4 = var0.getTime();
        if (isSameDay(var4)) {
            if (var3) {
                var1 = "HH:mm";
            } else {
                var1 = "HH:mm";
            }
        } else if (isYesterday(var4)) {
            if (!var3) {
                return "Yesterday " + (new SimpleDateFormat("HH:mm", Locale.ENGLISH)).format(var0);
            }

            var1 = " HH:mm";
        } else if (var3) {
            var1 = "Md HH:mm";
        } else {
            var1 = "MMM HH:mm ";
        }

        return var3 ? (new SimpleDateFormat(var1, Locale.CHINESE)).format(var0) : (new SimpleDateFormat(var1, Locale.ENGLISH)).format(var0);
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }

        return var4 < 30000L;
    }

    @SuppressLint({"DefaultLocale"})
    public static String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    private static boolean isYesterday(long var0) {
        TimeInfo var2 = getYesterdayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.add(Calendar.DATE, -1);
        var0.set(Calendar.HOUR_OF_DAY, 0);
        var0.set(Calendar.MINUTE, 0);
        var0.set(Calendar.SECOND, 0);
        var0.set(Calendar.MILLISECOND, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.add(Calendar.DATE, -1);
        var4.set(Calendar.HOUR_OF_DAY, 23);
        var4.set(Calendar.MINUTE, 59);
        var4.set(Calendar.SECOND, 59);
        var4.set(Calendar.MILLISECOND, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    private static boolean isSameDay(long var0) {
        TimeInfo var2 = getTodayStartAndEndTime();
        return var0 > var2.getStartTime() && var0 < var2.getEndTime();
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar var0 = Calendar.getInstance();
        var0.set(Calendar.HOUR_OF_DAY, 0);
        var0.set(Calendar.MINUTE, 0);
        var0.set(Calendar.SECOND, 0);
        var0.set(Calendar.MILLISECOND, 0);
        Date var1 = var0.getTime();
        long var2 = var1.getTime();
        Calendar var4 = Calendar.getInstance();
        var4.set(Calendar.HOUR_OF_DAY, 23);
        var4.set(Calendar.MINUTE, 59);
        var4.set(Calendar.SECOND, 59);
        var4.set(Calendar.MILLISECOND, 999);
        Date var5 = var4.getTime();
        long var6 = var5.getTime();
        TimeInfo var8 = new TimeInfo();
        var8.setStartTime(var2);
        var8.setEndTime(var6);
        return var8;
    }

    public static class TimeInfo {
        private long startTime;
        private long endTime;

        public TimeInfo() {
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long var1) {
            this.startTime = var1;
        }

        public long getEndTime() {
            return this.endTime;
        }

        public void setEndTime(long var1) {
            this.endTime = var1;
        }
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
            return r + "";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "";
        }
        return "";
    }

    
    public static String getTimeFormatText2(long diff) {
        
        diff = diff / 1000000000 * 1000;
        if (diff > day) {
            return new BigDecimal(diff).divide(new BigDecimal(day), 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString() + "";
        }
        if (diff > hour) {
            return new BigDecimal(diff).divide(new BigDecimal(hour), 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString() + "";
        }
        if (diff > minute) {
            return new BigDecimal(diff).divide(new BigDecimal(minute), 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString() + "";
        }
        return "1 ";

    }

    
    public static String getRemainTime(long seconds){
        long time = seconds;
        long hour = seconds / 3600;
        time = seconds % 3600;
        long minute = time / 60;
        long second = time % 60;
        StringBuilder stringBuilder = new StringBuilder();
        if(hour>0){
            stringBuilder.append(hour+"").append(Injection.provideContext().getString(R.string.hour));
        }
        if(minute > 0){
            stringBuilder.append(minute+"").append(Injection.provideContext().getString(R.string.minutes));
        }
        if(second > 0){
            stringBuilder.append(second+"").append(Injection.provideContext().getString(R.string.seconds));
        }
        return stringBuilder.toString();
    }
}
