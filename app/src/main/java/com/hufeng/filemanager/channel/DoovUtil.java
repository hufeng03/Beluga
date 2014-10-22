package com.hufeng.filemanager.channel;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.utils.LogUtil;

/**
 * Created by feng on 13-11-28.
 */
public class DoovUtil {

    private static final String TAG = DoovUtil.class.getSimpleName();


    public static boolean mbVistor = false;
    public static final String ACTION_DOOV_VISTOR = "com.huaqin.doov.vistor";

    public static boolean initDoovVistor(Context context, String javafile, String function)
    {
        String isVistor = Settings.System.getString(context.getContentResolver(), "doov_vistor");

        LogUtil.i("doov_vistor", "called by " + javafile + ".java  function: " + function + "isVistor :" + isVistor);

        boolean result = false;
        if ((isVistor == null) || (isVistor.equals("no")))
            result = false;
        else if ((isVistor != null) && (isVistor.equals("yes")))
        {
            result = true;
        }

        mbVistor = result;
        LogUtil.i("doov_vistor", "result :" + result);

        return result;
    }

    public static boolean isDoovVistor()
    {
        LogUtil.i("doov_vistor", "mbVistor :" + mbVistor);
        LogUtil.i("DoovUtils", "mbVistor :" + mbVistor);
        return mbVistor;
    }

    public static void changeDoovVistor()
    {
        mbVistor = (!mbVistor);
        LogUtil.i("DoovUtils", "changeDoovVistor to "+mbVistor);
        ActivityManager manager = (ActivityManager) FileManager.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses("com.doov.filemanager");
    }

}
