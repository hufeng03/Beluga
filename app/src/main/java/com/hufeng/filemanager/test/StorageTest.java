package com.hufeng.filemanager.test;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.test.AndroidTestCase;

public class StorageTest extends AndroidTestCase{
	
	private static String mGoodStorageVolumeDirectory = ""; 
	private static String mGoodStorageVolumeState = "";
	
	public void testStorageVolume() throws Throwable{
		// TODO Auto-generated method stub
		StorageManager sStorageManager = (StorageManager)getContext().getSystemService(Context.STORAGE_SERVICE);
		Class<?> StorageVolume;
		try {
			StorageVolume = Class.forName("android.os.storage.StorageVolume");
			Method method_getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
			Method method_getPath = StorageVolume.getDeclaredMethod("getPath");
			Method method_getDescription = StorageVolume.getDeclaredMethod("getDescription");
			Method method_isRemovable = StorageVolume.getDeclaredMethod("isRemovable");
			Method method_getVolumeState = StorageManager.class.getDeclaredMethod("getVolumeState", String.class);
			try {
				Object volumeList = method_getVolumeList.invoke(sStorageManager);
				if(volumeList.getClass().isArray()){
					int len = Array.getLength(volumeList);
					boolean flag = false;
					for(int i=0;i<len;i++){
						Object volume = Array.get(volumeList, i);
						Object real_volume = StorageVolume.cast(volume);
						
						String path = (String) method_getPath.invoke(real_volume);
						String description = (String) method_getPath.invoke(real_volume);
						boolean isRemovable = (Boolean) method_isRemovable.invoke(real_volume);
						String state = (String) method_getVolumeState.invoke(sStorageManager, path);
						
						if(Environment.MEDIA_MOUNTED.equals(state)){
							if(!flag){
								mGoodStorageVolumeDirectory = path;
								mGoodStorageVolumeState = Environment.MEDIA_MOUNTED;
								flag = true;
							}
						}else{
							if(i==0){
								mGoodStorageVolumeDirectory = path;
								mGoodStorageVolumeState = state;
							}
						}
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   	
		return;
	}
}
