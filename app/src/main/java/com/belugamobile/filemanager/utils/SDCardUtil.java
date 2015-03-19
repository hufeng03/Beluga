package com.belugamobile.filemanager.utils;

import java.io.File;


import android.os.Environment;
import android.os.StatFs;

public class SDCardUtil {

    
    public static boolean hasDoubleSDCard()
    {
		File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
		//String path = FileUtils.getRootDir();
		long size = 0;
		if (path.getPath().startsWith("/mnt"))
		{
        	File dir = new File("/mnt");
        	if(dir.isDirectory())
        	{
        		File[] files = dir.listFiles();
        		if(files!=null)
        		{
            		for(File file : files)
            		{
            			if((file.getPath().contains("sdcard") || file.getPath().contains("SdCard")) && !file.getPath().equals(path.getPath()))
            			{
            				if(file.canRead())
            					return true;
            			}
            		}
        		}
        	}
		}
		return false;
    }
	
	public static long getAvailaleSize(){
		
		File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
		//String path = FileUtils.getRootDir();
		long size = 0;
		if (path.getPath().startsWith("/mnt"))
		{
        	File dir = new File("/mnt");
        	if(dir.isDirectory())
        	{
        		File[] files = dir.listFiles();
        		if(files!=null)
        		{
            		for(File file : files)
            		{
            			if((file.getPath().contains("sdcard") || file.getPath().contains("SdCard"))  || file.getPath().equals(path.getPath()))
            			{
            				if(file.canRead())
            				{
		            			StatFs stat = new StatFs(file.getPath());
		            			long blockSize = stat.getBlockSize(); 
		            			long availableBlocks = stat.getAvailableBlocks();
		            			size+= availableBlocks * blockSize; 
            				}
            			}
            		}
        		}
        	}
		}
		else
		{
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize(); 
			long availableBlocks = stat.getAvailableBlocks();
			size = availableBlocks * blockSize; 
		}
		return size;
		//(availableBlocks * blockSize)/1024      KIB
		//(availableBlocks * blockSize)/1024 /1024  MIB
	}
	
	public static long getAllSize(){
		//String path = FileUtils.getRootDir();
		File path = Environment.getExternalStorageDirectory(); 
		
		long size = 0;
		if (path.getPath().startsWith("/mnt"))
		{
        	File dir = new File("/mnt");
        	if(dir.isDirectory())
        	{
        		File[] files = dir.listFiles();
        		if(files!=null)
        		{
            		for(File file : files)
            		{
            			if((file.getPath().contains("sdcard") || file.getPath().contains("SdCard")) || file.getPath().equals(path.getPath()))
            			{
            				if(file.canRead())
            				{
		            			StatFs stat = new StatFs(file.getPath()); 
		            			long blockSize = stat.getBlockSize(); 
		            			long availableBlocks = stat.getBlockCount();
		            			size += availableBlocks * blockSize;  
            				}
            			}
            		}
        		}
        	}
		}
		else
		{
			StatFs stat = new StatFs(path.getPath()); 
			long blockSize = stat.getBlockSize(); 
			long availableBlocks = stat.getBlockCount();
			size = availableBlocks * blockSize; 
		}
		return size;
	}
	
	public static long getAvailaleSize2(){
		
		File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
		//String path = FileUtils.getRootDir();
		long size = 0;
		if (path.getPath().startsWith("/mnt"))
		{
        	File dir = new File("/mnt");
        	if(dir.isDirectory())
        	{
        		File[] files = dir.listFiles();
        		if(files!=null)
        		{
            		for(File file : files)
            		{
            			if((file.getPath().contains("sdcard") || file.getPath().contains("SdCard"))&& !file.getPath().equals(path.getPath()))
            			{
            				StatFs stat = new StatFs(file.getPath());
            				long blockSize = stat.getBlockSize(); 
            				long availableBlocks = stat.getAvailableBlocks();
            				size = availableBlocks * blockSize; 
            				return size;
            			}
            		}
        		}
        	}
		}
		return size;
		//(availableBlocks * blockSize)/1024      KIB
		//(availableBlocks * blockSize)/1024 /1024  MIB
	}
	
	public static long getAllSize2(){
		//String path = FileUtils.getRootDir();
		File path = Environment.getExternalStorageDirectory(); 
		
		long size = 0;
		if (path.getPath().startsWith("/mnt"))
		{
        	File dir = new File("/mnt");
        	if(dir.isDirectory())
        	{
        		File[] files = dir.listFiles();
        		if(files!=null)
        		{
            		for(File file : files)
            		{
            			if((file.getPath().contains("sdcard") || file.getPath().contains("SdCard"))  && !file.getPath().equals(path.getPath()))
            			{
	            			StatFs stat = new StatFs(file.getPath()); 
	            			long blockSize = stat.getBlockSize(); 
	            			long availableBlocks = stat.getBlockCount();
	            			size += availableBlocks * blockSize;  
            			}
            		}
        		}
        	}
		}
		return size;
	}

	
	public static long getAvailaleSize1(){
		
		File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
		//String path = FileUtils.getRootDir();
		long size = 0;
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getAvailableBlocks();
		size = availableBlocks * blockSize; 
		return size;
		//(availableBlocks * blockSize)/1024      KIB
		//(availableBlocks * blockSize)/1024 /1024  MIB
	}
	
	public static long getAllSize1(){
		//String path = FileUtils.getRootDir();
		File path = Environment.getExternalStorageDirectory(); 
		
		long size = 0;
		StatFs stat = new StatFs(path.getPath()); 
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getBlockCount();
		size = availableBlocks * blockSize; 
		return size;
	}
}
