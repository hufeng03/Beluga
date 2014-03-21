package com.hufeng.filemanager.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class OSUtil {
	
	private static final String LOG_TAG = OSUtil.class.getName();
	
    public static String getCurrProcessName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = "";
        List<RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        if (list != null) {
            int pid = android.os.Process.myPid();
            for (RunningAppProcessInfo info : list) {
                if (pid == info.pid) {
                    name = info.processName;
//                    LogUtil.d(LOG_TAG, "current process name:" + name);
                    break;
                }
            }
        }
        return name;
    }

}
