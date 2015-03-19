package com.belugamobile.filemanager.utils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtil {
	
	private static final String LOG_TAG="PackageUtils";
	
	public static int getVersionCode(Context context)
	{
		int verCode = 0;
		try{
			String name = getPackageName(context);
			verCode = context.getPackageManager().getPackageInfo(name, 0).versionCode;
		}catch(NameNotFoundException e)
		{
			LogUtil.e(LOG_TAG, e.getMessage());
		}
		return verCode;
	}
	
	public static String getVersionName(Context context)
	{
		String verName ="";
		try{
			String name = getPackageName(context);
			verName = context.getPackageManager().getPackageInfo(name, 0).versionName;
		}catch(NameNotFoundException e)
		{
			LogUtil.e(LOG_TAG, e.getMessage());
		}
		return verName;
	}
	
	public static String getPackageName(Context context)
	{
		return context.getPackageName();
	}
	

	
}
