package com.hufeng.filemanager.app;

import java.util.ArrayList;

import com.hufeng.filemanager.utils.LogUtil;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private static final String LOG_TAG = "AppInfo";
	public String appName="";
    public String packageName="";
    public String versionName="";
    public int versionCode=0;
    public long lastUpdateTime = -1;
    public long firstInstallTime = -1;
    public Drawable appIcon=null;
    public boolean isRunning =  false;
    public boolean isSystem = false;
    public boolean inExternal = false;
    
	private long cachesize ;   //缓存大小
	private long datasize ;    //数据大小
	private long codesieze ;   //应用程序大小
	
	
    public ArrayList<ProcessInfo> process = new ArrayList<ProcessInfo>();
    public void print()
    {
        LogUtil.v(LOG_TAG,"Name:"+appName+" Package:"+packageName);
        LogUtil.v(LOG_TAG,"Name:"+appName+" versionName:"+versionName);
        LogUtil.v(LOG_TAG,"Name:"+appName+" versionCode:"+versionCode);
        LogUtil.v(LOG_TAG,"Running:"+isRunning);
        LogUtil.v(LOG_TAG,"System:"+isSystem);
    }
    
}

class ProcessInfo{
	public String processName = "";
	public int processId = 0;
	public int processMem = 0;//kb
}
