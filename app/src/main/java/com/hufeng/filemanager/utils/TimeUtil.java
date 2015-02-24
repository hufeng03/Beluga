package com.hufeng.filemanager.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;


public class TimeUtil {
	
	private static final String LOG_TAG = TimeUtil.class.getName();
	
	public static String formatVideoRecordingTime(long t){
		int m = (int)t/60;
		int s = (int)t - m*60;
		return String.format("%02d:%02d", m,s);
	}

    public static String getTimeString(long t) {
//        final Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(t);
//        int h = c.get(Calendar.HOUR_OF_DAY);
//        int m = c.get(Calendar.MINUTE);
//        int s = c.get(Calendar.SECOND);
//        return String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s);
        Time time = new Time();
        time.set(t);
        return time.format("%T");
    }

    public static String getDateString(long t) {
//        final Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(t);
//        int y = c.get(Calendar.YEAR);
//        int m = c.get(Calendar.MONTH) + 1;
//        int d = c.get(Calendar.DAY_OF_MONTH);
//        return String.valueOf(y) + "-" + String.valueOf(m) + "-" + String.valueOf(d);
        Time time = new Time();
        time.set(t);
        return time.format("%Y/%m/%d %H:%M");
    }

    public static String getDayString(long t) {
//        final Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(t);
//        int y = c.get(Calendar.YEAR);
//        int m = c.get(Calendar.MONTH) + 1;
//        int d = c.get(Calendar.DAY_OF_MONTH);
//        return String.valueOf(y) + "-" + String.valueOf(m) + "-" + String.valueOf(d);
        Time time = new Time();
        time.set(t);
        return time.format("%Y/%m/%d");
    }

    public static boolean isToday(long t) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t);
        int y = c.get(Calendar.YEAR);
        int dy = c.get(Calendar.DAY_OF_YEAR);
        long time = System.currentTimeMillis();
        c.setTimeInMillis(time);
        int cy = c.get(Calendar.YEAR);
        int cdy = c.get(Calendar.DAY_OF_YEAR);
        if (y == cy && dy == cdy) {
            return true;
        }
        return false;
    }
   
    public static String time2String(long time) {
    	SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");
        Calendar toyear = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        toyear.set(Calendar.MONTH, Calendar.JANUARY);
        toyear.set(Calendar.DATE, 1);
        toyear.set(Calendar.HOUR_OF_DAY, 0);
        toyear.set(Calendar.MINUTE, 0);
        toyear.set(Calendar.SECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        if (time > toyear.getTime().getTime() && time < today.getTime().getTime())
            df = new SimpleDateFormat("MM-dd");
        else if (time > today.getTime().getTime())
            df = new SimpleDateFormat("HH:mm");
        return df.format(new Date(time));
    }
    
    public static String getFormatDateStr(long t){
        long currTime = System.currentTimeMillis();
        int overTime = (int) ((currTime - t)/1000);
        if(LogUtil.IDBG) LogUtil.i("MessageItem","currTime , overTime = "+currTime+" "+overTime);

//        if(overTime/3600 != 0){
            Time time = new Time();
            time.set(currTime);
            int currYear = time.year;
            int currDay = time.yearDay;
            time.set(t);
            int day = time.yearDay;
            if(day == currDay){
            	return time.format(FileManager.getAppContext().getString(R.string.list_today_format));
            }else if(currDay - day == 1){
                return time.format(FileManager.getAppContext().getString(R.string.list_yesterday_format));
            }else if(currDay - day == 2){
                return time.format(FileManager.getAppContext().getString(R.string.list_before_yesterday_format));
            }else{
                if(time.year < currYear){
                    return time.format(FileManager.getAppContext().getString(R.string.list_before_year_format));
                }else{
                    return time.format(FileManager.getAppContext().getString(R.string.list_this_year_format));
                }
            }
//        }
//        else
//        {
//        	return null;
//        }
    }
    
    public static String getRegularDateTimeStr(long t) {
    	
    	Time time = new Time();
    	time.set(t);
    	return time.format("%Y/%m/%d %R");
    }
    
    public static String getFormatDateTimeStr(long t) {
        long currTime = System.currentTimeMillis();
        int overTime = (int) ((currTime - t) / 1000);
        if (overTime <= 0) {
            return FileManager.getAppContext().getString(R.string.just_now);
        }
        
        String language = FileManager.getAppContext().getString(R.string.time_language);
        if ("cn".equals(language)) {
            
            TypedArray chineseWeekDayArray = FileManager.getAppContext().getResources().obtainTypedArray(
                    R.array.weekday);
            
            if (overTime / 3600 == 0) {
                if (overTime / 60 == 0) {
                    return overTime + FileManager.getAppContext().getString(R.string.list_seconds_passed);
                } else {
                    return overTime / 60 + FileManager.getAppContext().getString(R.string.list_minutes_passed);
                }
            } else {
                Time tCurrTime = new Time();
                tCurrTime.set(currTime);
                Time time = new Time();
                time.set(t);

                TimeZone tz = TimeZone.getDefault();
                int currJulianDay = Time.getJulianDay(tCurrTime.toMillis(false), tz.getRawOffset());
                int julianDay = Time.getJulianDay(time.toMillis(false), tz.getRawOffset());
                int dayGap = currJulianDay - julianDay;

                if (dayGap == 0) {
                    return time.format("%R");
                } else if (dayGap == 1) {
                    return FileManager.getAppContext().getString(R.string.list_yesterday) + time.format(" %R");
                } else if (dayGap == 2) {
                    return FileManager.getAppContext().getString(R.string.list_before_yesterday) + time.format(" %R");
                } else if (2 < dayGap && dayGap < 7) {
                    String chineseWeekDay = chineseWeekDayArray.getString(time.weekDay);
                    if (tCurrTime.weekDay < time.weekDay || time.weekDay == Time.SUNDAY) {
                        return time.format(FileManager.getAppContext().getString(R.string.list_last_week_format) + chineseWeekDay);
                    } else {
                        return chineseWeekDay;
                    }
                }
                if (tCurrTime.year == time.year) {
                    return time.format("%m-%d");
                } else {
                    return time.format("%y-%m-%d");
                }
            }
        } else {
            Time time = new Time();
            time.set(currTime);
            int currYear = time.year;
            int currDay = time.yearDay;
            time.set(t);
            int day = time.yearDay;
            if (day == currDay) {
                return time.format("%I:%M%p");
            } else {
                if (time.year < currYear) {
                    return time.format("%b %d %y");
                } else {
                    return time.format("%b %d");
                }
            }
        }
    }
    
//    public static String getFormatDateTimeStr(long t, Context context){
//        return formatTimeStampString(context, t);
//    }
//    
//    public static String formatTimeStampString(Context context, long when) {
//        return formatTimeStampString(context, when, false);
//    }
//
//    public static String formatTimeStampString(Context context, long when, boolean fullFormat) {
//        Time then = new Time();
//        then.set(when);
//        Time now = new Time();
//        now.setToNow();
//
//        // Basic settings for formatDateTime() we want for all cases.
//        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |
//                           DateUtils.FORMAT_ABBREV_ALL |
//                           DateUtils.FORMAT_CAP_AMPM;
//
//        // If the message is from a different year, show the date and year.
//        if (then.year != now.year) {
//            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
//        } else if (then.yearDay != now.yearDay) {
//            // If it is from a different day than today, show only the date.
//            format_flags |= DateUtils.FORMAT_SHOW_DATE;
//        } else {
//            // Otherwise, if the message is from today, show the time.
//            format_flags |= DateUtils.FORMAT_SHOW_TIME;
//        }
//
//        // If the caller has asked for full details, make sure to show the date
//        // and time no matter what we've determined above (but still make showing
//        // the year only happen if it is a different year from today).
//        if (fullFormat) {
//            format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
//        }
//
//        return DateUtils.formatDateTime(context, when, format_flags);
//    }
}
