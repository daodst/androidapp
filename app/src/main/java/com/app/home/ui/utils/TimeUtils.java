package com.app.home.ui.utils;

import android.content.Context;

import com.app.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import common.app.AppApplication;


public class TimeUtils {

    private static final String TAG = "TimeUtils";

    public static final String format(String timeStr) {
        
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
            String format = dateFormat2.format(format2Long(timeStr));
            return format;
        } catch (Exception e) {
        }
        return "";
    }

    public static final String format258(String timeStr) {
        
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd");
            String format = dateFormat2.format(format2Long(timeStr));
            return format;
        } catch (Exception e) {
        }
        return "";
    }

    public static final String format2(String timeStr) {
        
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            String format = dateFormat2.format(format2Long(timeStr));
            return format;
        } catch (Exception e) {
        }
        return "";
    }

    public static final String format3(String timeStr) {
        
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String format = dateFormat2.format(format2Long(timeStr));
            return format;
        } catch (Exception e) {
        }
        return "";
    }

    public static long format2Long(String timeStr) {
        
        
        try {
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateFormat2.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date time = null;
            try {
                time = dateFormat.parse(timeStr);
            } catch (Exception e) {
                time = dateFormat2.parse(timeStr);
            }
            if (null == time) {
                return 0;
            }
            return time.getTime();
        } catch (Exception e) {
        }
        return 0;
    }


    
    private final static long minute = 60;
    
    private final static long hour = 60 * minute;
    
    private final static long day = 24 * hour;
    
    private final static long month = 31 * day;
    
    private final static long year = 12 * month;

    public static String getTime(Context context, long time) {

        long diff = time;
        long r = 0;
        StringBuilder builder = new StringBuilder();
        if (diff > year) {
            r = (diff / year);
            builder.append(r).append(context.getString(R.string.time_year));
            diff -= r * year;
        }
        if (diff > month) {
            r = (diff / month);
            builder.append(r).append(context.getString(R.string.time_mounth));
            diff -= r * month;
        }
        if (diff > day) {
            r = (diff / day);
            builder.append(r).append(context.getString(R.string.time_day));
            diff -= r * day;
        }
        if (diff > hour) {
            r = (diff / hour);
            builder.append(r).append(context.getString(R.string.time_hour));
            diff -= r * hour;
        }
        if (diff > minute) {
            r = (diff / minute);
            builder.append(r).append(context.getString(R.string.time_minute));
            diff -= r * minute;
            return builder.toString();
        } else {
            return context.getString(R.string.time_just_now);
        }
    }



    public static String getTime(Context context, Date currentTime, Date firstTime) {
        if (null == firstTime) {
            return "--";
        }
        context = context.getApplicationContext();
        
        long diff = currentTime.getTime() - firstTime.getTime();
        
        Calendar currentTimes = dataToCalendar(currentTime);
        
        Calendar firstTimes = dataToCalendar(firstTime);
        
        int year = currentTimes.get(Calendar.YEAR) - firstTimes.get(Calendar.YEAR);
        int month = currentTimes.get(Calendar.MONTH) - firstTimes.get(Calendar.MONTH);
        int day = currentTimes.get(Calendar.DAY_OF_MONTH) - firstTimes.get(Calendar.DAY_OF_MONTH);
        if (day < 0) {
            month -= 1;
            currentTimes.add(Calendar.MONTH, -1);
            
            day = day + currentTimes.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        if (month < 0) {
            
            month = (month + 12) % 12;
            year--;
        }
        long days = diff / (1000 * 60 * 60 * 24);
        
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        
        long s = (diff / 1000 - days * 24 * 60 * 60 - hours * 60 * 60 - minutes * 60);
        StringBuilder builder = new StringBuilder();

        if (year > 0) {
            builder.append(year).append(context.getString(R.string.time_year));
        }
        if (month > 0) {
            builder.append(month).append(context.getString(R.string.time_mounth));
        }
        if (day > 0) {
            builder.append(day).append(context.getString(R.string.time_day));
        }
        if (hours > 0) {
            builder.append(hours).append(context.getString(R.string.time_hour));
        }
        if (minutes > 0) {
            builder.append(minutes).append(context.getString(R.string.time_minute));
        }

        return builder.toString();
    }

    
    public static Calendar dataToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }


    
    @Deprecated
    public static String passTime(AppApplication context, long time) {
        long diff = System.currentTimeMillis() - time;
        long r = 0;
        StringBuilder builder = new StringBuilder();
        if (diff > year) {
            r = (diff / year);
            builder.append(r).append(context.getString(R.string.time_year));
            diff -= r * year;
        }
        if (diff > month) {
            r = (diff / month);
            builder.append(r).append(context.getString(R.string.time_mounth));
            diff -= r * month;

        }
        if (diff > day) {
            r = (diff / day);
            builder.append(r).append(context.getString(R.string.time_day));
            diff -= r * day;
        }
        if (diff > hour) {
            r = (diff / hour);
            builder.append(r).append(context.getString(R.string.time_hour));
            diff -= r * hour;
        }
        if (diff > minute) {
            r = (diff / minute);
            builder.append(r).append(context.getString(R.string.time_minute));
            diff -= r * minute;
            return builder.toString();
        } else {
            return context.getString(R.string.time_just_now);
        }
    }
}
