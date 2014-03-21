package com.hufeng.filemanager.storage;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/***
 * 
 * @author feng
 *
 */
public class StorageUtil {

	
	public static long getAvailaleSize(String path){

		long size = 0;
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getAvailableBlocks();
		size = availableBlocks * blockSize; 
		return size;
		//(availableBlocks * blockSize)/1024      KIB
		//(availableBlocks * blockSize)/1024 /1024  MIB
	}
	
	public static long getAllSize(String path){
		long size = 0;
		StatFs stat = new StatFs(path); 
		long blockSize = stat.getBlockSize(); 
		long availableBlocks = stat.getBlockCount();
		size = availableBlocks * blockSize; 
		return size;
	}
}
