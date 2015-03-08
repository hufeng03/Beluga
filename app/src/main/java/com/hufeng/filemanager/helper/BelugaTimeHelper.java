package com.hufeng.filemanager.helper;

import android.text.format.Time;

/**
 * Created by Feng Hu on 15-03-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaTimeHelper {

    public static String getTimeString(long s) {
        Time time = new Time();
        time.set(s*1000);
        return time.format("%T");
    }

    public static String getDateString(long s) {
        Time time = new Time();
        time.set(s*1000);
        return time.format("%Y/%m/%d %H:%M");
    }

    public static String getDayString(long s) {
        Time time = new Time();
        time.set(s*1000);
        return time.format("%Y/%m/%d");
    }

}
