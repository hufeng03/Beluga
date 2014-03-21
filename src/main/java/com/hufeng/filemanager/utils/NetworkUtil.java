package com.hufeng.filemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.provider.Settings;

public class NetworkUtil {

	public static boolean getAirplaneMode(Context context){
		int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
		Settings.System.AIRPLANE_MODE_ON, 0) ;
		return (isAirplaneMode == 1)?true:false;
	}

    public static boolean isWifiConnected(Context context) {
//        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        int statusCode = wifiMgr.getWifiState();
//        if (statusCode == WifiManager.WIFI_STATE_ENABLED) {
//            return true;
//        }

        final ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi =connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile =connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if(wifi.isConnected()/*||mobile.isAvailable()*/)
            return true;
        else
            return false;
    }

}
