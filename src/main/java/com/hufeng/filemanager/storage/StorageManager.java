package com.hufeng.filemanager.storage;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/***
 * 
 * @author feng
 *
 */
public class StorageManager {

    private static final String LOG_TAG = StorageManager.class.getSimpleName();
	
	public ArrayList<StorageUnit> mStorageUnits = new ArrayList<StorageUnit>();
	
	private static StorageManager instance;
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static StorageManager getInstance(Context context) {
		if ( instance == null ) {
			instance = new StorageManager();
			instance.refreshStorageVolume(context);
		}
		return instance;
	}
	
	public boolean isStorage(String path){
		boolean result = false;
        if (path != null) {
            for (StorageUnit unit : mStorageUnits) {
                if ( path.equalsIgnoreCase(unit.path) ) {
                    result = true;
                    break;
                }
            }
        }
		return result;
	}
	
	public boolean isExternalStorage(String path){
		boolean result = false;
        if (path != null) {
            for (StorageUnit unit : mStorageUnits) {
                if(path.equalsIgnoreCase(unit.path)){
                    result = unit.removable;
                    break;
                }
            }
        }
		return result;
	}
	
	public boolean isInternalStorage(String path){
		boolean result = false;
        if (path != null) {
            for (StorageUnit unit : mStorageUnits) {
                if(path.equalsIgnoreCase(unit.path)){
                    result = !unit.removable;
                    break;
                }
            }
        }
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasMountedStorage(){
		boolean result = false;
		for (StorageUnit unit : mStorageUnits) {
			if ( Environment.MEDIA_MOUNTED.equals(unit.state) ) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public String[] getMountedStorages() {
		List<String> stors = new ArrayList<String>();
		for (StorageUnit unit : mStorageUnits) {
			if( Environment.MEDIA_MOUNTED.equals(unit.state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(unit.state)){
				stors.add(unit.path);
			}
		}
		return stors.toArray(new String[stors.size()]);
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getAllStorages() {
		List<String> stors = new ArrayList<String>();
		for (StorageUnit unit : mStorageUnits) {
			stors.add(unit.path);
		}
		return stors.toArray(new String[stors.size()]);
	}
	
	public String getStoragePath(String path) {
		if(path == null)
			return null;
		String storage = null;
		for (StorageUnit unit : mStorageUnits) {
			if(path.startsWith(unit.path)){
				storage = unit.path;
				break;
			}
		}
		return storage;
	}
	
	
	public String getExternalStorageDirectory(){
		List<String> stors = new ArrayList<String>();
		String path = null;
		for (int i=0;i<mStorageUnits.size();i++) {
			StorageUnit unit = mStorageUnits.get(i);
			if ( unit.removable ) {
				if(unit.path.contains("sdcard")){
					return unit.path;
				}
				if( path == null)
					path = unit.path;
			}
		}
		return path;	
	}
	
	public String getExternalStorageState(){
		List<String> stors = new ArrayList<String>();
		String state = Environment.MEDIA_REMOVED;
		boolean flag = false;
		for (int i=0;i<mStorageUnits.size();i++) {
			StorageUnit unit = mStorageUnits.get(i);
			if ( unit.removable ) {
				if(unit.path.contains("sdcard")){
					return unit.state;
				}
				if(!flag){
					state = unit.state;
					flag = true;
				}
			}
		}
		return state;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String[] getInnerStorages() {
		List<String> stors = new ArrayList<String>();
		for (StorageUnit unit : mStorageUnits) {
			if ( unit.removable ) {
				stors.add(unit.path);
			}
		}
		return stors.toArray(new String[stors.size()]);
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getExternalStorages(){
		List<String> stors = new ArrayList<String>();
		for (StorageUnit unit : mStorageUnits) {
			if ( !unit.removable ) {
				stors.add(unit.path);
			}
		}
		return stors.toArray(new String[stors.size()]);
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTotalAllSize(){
		long size = 0L;
		for( StorageUnit unit : mStorageUnits ) {
			size += unit.allSpace;
		}
		return size;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTotalAvailableSize() {
		long size = 0L;
		for( StorageUnit unit : mStorageUnits ) {
			size += unit.availableSpace;
		}
		return size;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public long getAllSize(String path){
		long size = 0L;
		if( path != null ) {
			for (StorageUnit unit : mStorageUnits) {
				if ( path.equals(unit.path) ) {
					size = unit.allSpace;
					break;
				}
			}
		}
		return size;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public long getAvailableSize(String path){
		long size = 0L;
		if( path != null ) {
			for (StorageUnit unit : mStorageUnits) {
				if ( path.equals(unit.path) ) {
					size = unit.availableSpace;
					break;
				}
			}
		}
		return size;
	}
	
	/**
	 * 
	 */
	public static void clear() {
		instance = null;
	}	
	
	private void refreshStorageVolume(Context context){
		android.os.storage.StorageManager sStorageManager = (android.os.storage.StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
		boolean flag_reflection_error = false;
		if(sStorageManager==null) {
			flag_reflection_error = true;
		}else{
			Class<?> StorageVolume;
			try {
				StorageVolume = Class.forName("android.os.storage.StorageVolume");
				Method method_getVolumeList = android.os.storage.StorageManager.class.getDeclaredMethod("getVolumeList");
				Method method_getPath = StorageVolume.getDeclaredMethod("getPath");
				//Method method_getDescription = StorageVolume.getDeclaredMethod("getDescription");
				Method method_isRemovable = StorageVolume.getDeclaredMethod("isRemovable");
				Method method_getVolumeState = sStorageManager.getClass().getDeclaredMethod("getVolumeState", String.class);
				try {
					Object volumeList = method_getVolumeList.invoke(sStorageManager);
					if(volumeList.getClass().isArray()){
						int len = Array.getLength(volumeList);
						mStorageUnits.clear();
						for(int i=0;i<len;i++){
							Object volume = Array.get(volumeList, i);
							Object real_volume = StorageVolume.cast(volume);
							
							String path = (String) method_getPath.invoke(real_volume);
							//String description = (String) method_getPath.invoke(real_volume);
							boolean isRemovable = (Boolean) method_isRemovable.invoke(real_volume);
							String state = (String) method_getVolumeState.invoke(sStorageManager, path);
							
							boolean flag = false;
							for(StorageUnit unit:mStorageUnits){
								if(unit.path.equals(path)){
									flag = true;
								}
							}
							if(!flag){
								long availableSize = StorageUtil.getAvailaleSize(path);
								long allSize = StorageUtil.getAllSize(path);
								mStorageUnits.add(new StorageUnit(path, null, isRemovable, state, availableSize, allSize ));
							}
						}
						String path = Environment.getDataDirectory().getAbsolutePath();
						if(path!=null && new File(path).canRead()){
							boolean flag = false;
							for(StorageUnit unit:mStorageUnits){
								if(unit.path.equals(path)){
									flag = true;
								}
							}
							if(!flag){
								long availableSize = StorageUtil.getAvailaleSize(path);
								long allSize = StorageUtil.getAllSize(path);
								mStorageUnits.add(new StorageUnit(path, null, false, Environment.MEDIA_MOUNTED, availableSize, allSize ));
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					flag_reflection_error = true;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					flag_reflection_error = true;
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					flag_reflection_error = true;
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				flag_reflection_error = true;
			} catch (SecurityException e) {
				e.printStackTrace();
				flag_reflection_error = true;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				flag_reflection_error = true;
			}
		}
		if(flag_reflection_error){
			if(mStorageUnits.size()==0){
				File file = Environment.getExternalStorageDirectory();
				String path = file.getAbsolutePath();
				if(!TextUtils.isEmpty(path)){
					boolean flag = false;
					for(StorageUnit unit:mStorageUnits){
						if(unit.path.equals(path)){
							flag = true;
						}
					}
					if(!flag){
						long availableSize = StorageUtil.getAvailaleSize(path);
						long allSize = StorageUtil.getAllSize(path);
						mStorageUnits.add(new StorageUnit(path, null, false, Environment.getExternalStorageState(), availableSize, allSize ));
					}
				}
			}
		}

        if(mStorageUnits.size()>0) {
            int idx = 0;
            for(StorageUnit storage:mStorageUnits) {
                Log.i(LOG_TAG, "storage unit "+idx+":"+storage.toString());
                idx ++;
            }
        } else {
            Log.i(LOG_TAG, "opps, no storage unit");
        }

		return;
	}
	
	public String getMyStoragePath(){
		String[] path = getMountedStorages();
		
		if(path.length>0){
			return path[0]+File.separator+"filemanager";
		}else{
			return getExternalStorageDirectory()+File.separator+"filemanager";
		}
	}
}
