package com.hufeng.filemanager.utils;

import android.content.Intent;
import android.content.pm.ResolveInfo;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.kanbox.KanBoxConfig;

import java.util.ArrayList;
import java.util.List;

public class IntentUtils {
	
	public static ResolveInfo[] queryAvailableApps(Intent intent)
	{
		
		List<ResolveInfo> resolveInfos = null;
		try{
			resolveInfos = FileManager.getAppContext().getPackageManager().queryIntentActivities (intent, 0); 
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		boolean has_app = false;
		if(resolveInfos!=null)
			has_app = resolveInfos.size()>0; 
		if(!has_app)
		{
//			Toast.makeText(FileManager.getAppContext(), R.string.no_available_video_app, Toast.LENGTH_SHORT).show();
			return null;
		}
		ResolveInfo[] apps = (ResolveInfo[])resolveInfos.toArray(new ResolveInfo[0]);
		
		return apps;
	}
	
	public static ResolveInfo[] filterOutMms(ResolveInfo[] apps)
	{
		List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
		if(apps!=null)
		{
			for(ResolveInfo app:apps)
			{
				String pkg = app.activityInfo.applicationInfo.packageName;
				if(!"com.android.mms".equals(pkg))
				{
					resolveInfos.add(app);
				}
			}
		}
		ResolveInfo[] filtered_apps = (ResolveInfo[])resolveInfos.toArray(new ResolveInfo[0]);
		return filtered_apps;
	}
	
	public static ResolveInfo[] filterOutBluetooth(ResolveInfo[] apps)
	{
		List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
		if(apps!=null)
		{
			for(ResolveInfo app:apps)
			{
				String pkg = app.activityInfo.applicationInfo.packageName;
				if(pkg!=null && !pkg.contains("bluetooth"))
				{
					resolveInfos.add(app);
				}
			}
		}
		ResolveInfo[] filtered_apps = (ResolveInfo[])resolveInfos.toArray(new ResolveInfo[0]);
		return filtered_apps;
	}
	
	public static ResolveInfo[] filterOutDuplicate(ResolveInfo[] apps)
	{
		List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
		if(apps!=null)
		{
			for(ResolveInfo app:apps)
			{
				String pkg = app.activityInfo.applicationInfo.packageName;
				boolean flag = false;
				for(ResolveInfo app2:resolveInfos){
					String pkg2 = app2.activityInfo.applicationInfo.packageName;
					if(pkg.equals(pkg2)){
						flag = true;
						break;
					}
				}
				if(!flag)
					resolveInfos.add(app);
			}
		}
		ResolveInfo[] filtered_apps = (ResolveInfo[])resolveInfos.toArray(new ResolveInfo[0]);
		return filtered_apps;
	}

    public static ResolveInfo[] sort(ResolveInfo[] apps)
    {
        List<ResolveInfo> resolveInfos = new ArrayList<ResolveInfo>();
        if(apps!=null)
        {
            for(ResolveInfo app:apps)
            {
                String pkg = app.activityInfo.applicationInfo.packageName;
                if (pkg.equals(KanBoxConfig.KANBOX_PAKCAGE_NAME)) {
                    resolveInfos.add(0, app);
                } else {
                    resolveInfos.add(app);
                }
            }
        }
        ResolveInfo[] filtered_apps = (ResolveInfo[])resolveInfos.toArray(new ResolveInfo[0]);
        return filtered_apps;
    }
}
