package com.belugamobile.filemanager.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.belugamobile.filemanager.FileManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;

public class MemoryUtil {
	
	static public long getFreeMemorySize() {
		MemoryInfo mi = new MemoryInfo();
		ActivityManager activityManager = (ActivityManager) FileManager.getAppContext().getSystemService(Activity.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		long availableMegs = mi.availMem /*/ 1048576L*/;
		return availableMegs;
    }

    static public long getTotalMemorySize() {
    	return getTotalMemory();
    }

    private static long getTotalMemory() {  
        String str1 = "/proc/meminfo";
        String str2;        
        String[] arrayOfString;
        long initial_memory = 0;
        try {
	        FileReader localFileReader = new FileReader(str1);
	        BufferedReader localBufferedReader = new BufferedReader(    localFileReader, 8192);
	        str2 = localBufferedReader.readLine();//meminfo
	        arrayOfString = str2.split("\\s+");
	        for (String num : arrayOfString) {
	        	if(LogUtil.IDBG) LogUtil.i(str2, num + "\t");
	        }
	        //total Memory
	        initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;   
	        localBufferedReader.close();
        } 
        catch (IOException e) 
        {       
        }
        return initial_memory;
    }  
}
