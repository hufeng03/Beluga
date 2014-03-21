package com.hufeng.filemanager.channel;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class ChannelUtil {

    private static String mChannel;

    private static String readChannel(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        mChannel = sp.getString("INITIAL_UMENG_CHANNEL", "");
        if(TextUtils.isEmpty(mChannel))
        {
            ApplicationInfo appInfo;
            try {
                appInfo = context.getPackageManager()
                        .getApplicationInfo(context.getPackageName(),  PackageManager.GET_META_DATA);
                mChannel=appInfo.metaData.getString("UMENG_CHANNEL");
                System.out.println("UMENG_CHANNEL:"+mChannel);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("INITIAL_UMENG_CHANNEL", mChannel);
                edit.commit();
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mChannel;
    }

    public static boolean isROMChannel(Context context)
    {
        if(TextUtils.isEmpty(mChannel))
            readChannel(context);
        if(mChannel.endsWith("ROM"))
            return true;
        else
            return false;
    }

    public static boolean isDEWAV_ROOMChannel(Context context)
    {
        if(TextUtils.isEmpty(mChannel))
            readChannel(context);
        if(mChannel.endsWith("DEWAV ROM"))
            return true;
        else
            return false;
    }

    public static boolean isDOOV_ROOMChannel(Context context)
    {
        if(TextUtils.isEmpty(mChannel))
            readChannel(context);
        if(mChannel.endsWith("DOOV ROM"))
            return true;
        else
            return false;
    }

//    public static boolean isKanBoxChannel(Context context) {
//        if(TextUtils.isEmpty(mChannel))
//            readChannel(context);
//        if(mChannel.toLowerCase().contains("kanbox"))
//            return true;
//        else
//            return false;
//    }

}

