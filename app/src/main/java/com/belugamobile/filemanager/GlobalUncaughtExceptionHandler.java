package com.belugamobile.filemanager;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by feng on 14-7-18.
 */
public class GlobalUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
    Context mContext;
    Thread.UncaughtExceptionHandler mHandler;

    public GlobalUncaughtExceptionHandler(Context context) {
        mContext = context;
        mHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        File dir = mContext.getExternalFilesDir(null);
        dir.mkdirs();
        File file = new File(dir, "crash_log_"+System.currentTimeMillis()+".txt");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (pw != null) {
            ex.printStackTrace(pw);
            pw.close();
        }
        if (mHandler != null) {
            mHandler.uncaughtException(thread, ex);
        }
    }
}
