package com.hufeng.filemanager.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.text.TextUtils;

import com.hufeng.filemanager.BuildConfig;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * 
 * @author feng
 *
 */
public class StorageManager extends BroadcastReceiver{

    private static final String LOG_TAG = StorageManager.class.getSimpleName();
	
	public ArrayList<StorageUnit> mStorageUnits = new ArrayList<StorageUnit>();
	
	private static StorageManager instance;

    private Context mContext;

    private StorageManager(Context context) {
        mContext = context;
    }
	
	/**
	 * @param context
	 * @return
	 */
	public static synchronized StorageManager getInstance(Context context) {
		if ( instance == null ) {
            LogUtil.i(LOG_TAG, "getInstance "+System.currentTimeMillis());
			StorageManager new_instance = new StorageManager(context.getApplicationContext());
            new_instance.registerReceiver();
            new_instance.refreshStorageVolume(context);
            instance = new_instance;
		}
		return instance;
	}

    private void registerReceiver() {
        IntentFilter intent = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        mContext.registerReceiver(this, intent);
    }

    private void unregisterReceiver() {
        mContext.unregisterReceiver(this);
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

    public String getStorageDescription(String path){
        if (path != null) {
            for (StorageUnit unit : mStorageUnits) {
                if ( path.equalsIgnoreCase(unit.path) ) {
                    return unit.description;
                }
            }
        }
        return null;
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

	
	public String[] getMountedStorages() {

        String state = Environment.getExternalStorageState();
        String primary = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            primary =  Environment.getExternalStorageDirectory().getAbsolutePath();
            if (!FileUtils.isDirWritable(primary)) {
                primary = null;
            }
        }

        List<String> stors_primary = new ArrayList<String>();
		List<String> stors_writable_unremovable = new ArrayList<String>();
        List<String> stors_writable_removable = new ArrayList<String>();
        List<String> stors_readable_unremovable = new ArrayList<String>();
        List<String> stors_readable_removable = new ArrayList<String>();
		for (StorageUnit unit : mStorageUnits) {
            if (primary != null && primary.equals(unit.path)) {
                stors_primary.add(unit.path);
                LogUtil.i(LOG_TAG, "add primary: "+unit);
            } else {
                if (Environment.MEDIA_BAD_REMOVAL.equals(unit.state)
                        || Environment.MEDIA_NOFS.equals(unit.state)
                        || Environment.MEDIA_UNMOUNTABLE.equals(unit.state)
                        || Environment.MEDIA_UNMOUNTABLE.equals(unit.state)) {
                    continue;
                }
                if (/*Environment.MEDIA_MOUNTED.equals(unit.state)*/FileUtils.isDirWritable(unit.path)) {
                    if (!unit.removable) {
                        LogUtil.i(LOG_TAG, "unremovable writable: "+unit);
                        stors_writable_unremovable.add(unit.path);
                    } else {
                        LogUtil.i(LOG_TAG, "removable writable: "+unit);
                        stors_writable_removable.add(unit.path);
                    }
                }
                else if (/*Environment.MEDIA_MOUNTED_READ_ONLY.equals(unit.state)*/new File(unit.path).canRead()) {
                    if (!unit.removable) {
                        LogUtil.i(LOG_TAG, "unremovable readable: "+unit);
                        stors_readable_unremovable.add(unit.path);
                    } else {
                        LogUtil.i(LOG_TAG, "removable readable: "+unit);
                        stors_readable_removable.add(unit.path);
                    }
                } else {
                    LogUtil.i(LOG_TAG, "not mounted: "+unit);
                }
            }
		}
        stors_primary.addAll(stors_writable_unremovable);
        stors_primary.addAll(stors_writable_removable);
        stors_primary.addAll(stors_readable_unremovable);
        stors_primary.addAll(stors_readable_removable);
		return stors_primary.toArray(new String[stors_primary.size()]);
	}

    public String getPrimaryExternalStorage() {
        String best_hit_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String state = Environment.getExternalStorageState();
        if (isStorage(best_hit_storage) && Environment.MEDIA_MOUNTED.equals(state) && FileUtils.isDirWritable(best_hit_storage)) {
            return best_hit_storage;
        }
        int best_hit_priority = -1;
        for (StorageUnit unit : mStorageUnits) {
            if (/*Environment.MEDIA_MOUNTED.equals(unit.state)*/FileUtils.isDirWritable(unit.path)) {
                if (!unit.removable) {
                    if (best_hit_priority < 4) {
                        best_hit_storage = unit.path;
                        best_hit_priority = 4;
                    }
                } else {
                    if (best_hit_priority < 3) {
                        best_hit_storage = unit.path;
                        best_hit_priority = 3;
                    }
                }
            } else if (/*Environment.MEDIA_MOUNTED_READ_ONLY.equals(unit.state)*/new File(unit.path).canRead()) {
                if (!unit.removable) {
                    if (best_hit_priority < 2) {
                        best_hit_storage = unit.path;
                        best_hit_priority = 2;
                    }
                } else {
                    if (best_hit_priority < 1) {
                        best_hit_storage = unit.path;
                        best_hit_priority = 1;
                    }
                }
            } else {
                if (best_hit_priority < 0) {
                    best_hit_storage = unit.path;
                    best_hit_priority = 0;
                }
            }
        }
        return best_hit_storage;
    }
	
	/**
	 * 
	 * @return
	 */
	public String[] getAllStorages() {
		List<String> storages = new ArrayList<String>();
		for (StorageUnit unit : mStorageUnits) {
            storages.add(unit.path);
		}
		return storages.toArray(new String[storages.size()]);
	}
	
	public String getStorageForPath(String path) {
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
	
	
//	public String getExternalStorageDirectory(){
//		String path = null;
//		for (int i=0;i<mStorageUnits.size();i++) {
//			StorageUnit unit = mStorageUnits.get(i);
//			if ( unit.removable ) {
//				if(unit.path.contains("sdcard")){
//					return unit.path;
//				}
//				if( path == null)
//					path = unit.path;
//			}
//		}
//		return path;
//	}
	
//	public String getExternalStorageState(){
//		String state = Environment.MEDIA_REMOVED;
//		boolean flag = false;
//		for (int i=0;i<mStorageUnits.size();i++) {
//			StorageUnit unit = mStorageUnits.get(i);
//			if ( unit.removable ) {
//				if(unit.path.contains("sdcard")){
//					return unit.state;
//				}
//				if(!flag){
//					state = unit.state;
//					flag = true;
//				}
//			}
//		}
//		return state;
//	}
//
//
//	/**
//	 *
//	 * @return
//	 */
//	public String[] getInnerStorages() {
//		List<String> storages = new ArrayList<String>();
//		for (StorageUnit unit : mStorageUnits) {
//			if ( unit.removable ) {
//				storages.add(unit.path);
//			}
//		}
//		return storages.toArray(new String[storages.size()]);
//	}
//
//	/**
//	 *
//	 * @return
//	 */
//	public String[] getExternalStorages(){
//		List<String> storages = new ArrayList<String>();
//		for (StorageUnit unit : mStorageUnits) {
//			if ( !unit.removable ) {
//				storages.add(unit.path);
//			}
//		}
//		return storages.toArray(new String[storages.size()]);
//	}
	
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
					unit.availableSpace = StorageUtil.getAvailaleSize(unit.path);
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
	public static synchronized void clear() {
        if (instance != null) {
            LogUtil.i(LOG_TAG, "clear "+System.currentTimeMillis());
            instance.unregisterReceiver();
        }
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
                        int internal_count = 0;
                        int external_count = 0;
						for(int i=0;i<len;i++){
							Object volume = Array.get(volumeList, i);
							Object real_volume = StorageVolume.cast(volume);
							
							String path = (String) method_getPath.invoke(real_volume);
                            if (TextUtils.isEmpty(path)) return;
                            path = new File(path).getAbsolutePath();
                            if (TextUtils.isEmpty(path)) return;
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
                                String description = null;
                                if (isRemovable) {
                                    external_count ++;
                                    if (external_count > 1) {
                                        description = context.getString(R.string.external_storage)+ external_count;
                                    }
                                } else {
                                    internal_count ++;
                                    if (internal_count > 1) {
                                        description = context.getString(R.string.internal_storage)+internal_count;
                                    }
                                }
                                mStorageUnits.add(new StorageUnit(path, description, isRemovable, state, availableSize, allSize ));

							}
						}
                        for (int i=0; i<mStorageUnits.size(); i++) {
                            StorageUnit unit = mStorageUnits.get(i);
                            if (unit.description == null) {
                                if (unit.isRemovable()) {
                                    if (external_count <= 1) {
                                        unit.description = context.getString(R.string.external_storage);
                                    } else {
                                        unit.description = context.getString(R.string.external_storage);
                                    }
                                } else {
                                    if (external_count <= 1) {
                                        unit.description = context.getString(R.string.internal_storage);
                                    } else {
                                        unit.description = context.getString(R.string.internal_storage);
                                    }
                                }
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

        if (mStorageUnits.size() == 0) {
//            File file = Environment.getExternalStorageDirectory();
//            String path = file.getAbsolutePath();
//            if (!TextUtils.isEmpty(path)) {
//                boolean flag = false;
//                for (StorageUnit unit : mStorageUnits) {
//                    if (unit.path.equals(path)) {
//                        flag = true;
//                    }
//                }
//                if (!flag) {
//                    long availableSize = StorageUtil.getAvailaleSize(path);
//                    long allSize = StorageUtil.getAllSize(path);
//                    mStorageUnits.add(new StorageUnit(path, null, false, Environment.getExternalStorageState(), availableSize, allSize));
//                }
//            }
        }

        if(mStorageUnits.size()>0) {
            Collections.sort(mStorageUnits, new Comparator<StorageUnit>() {
                @Override
                public int compare(StorageUnit lhs, StorageUnit rhs) {
                    return lhs.path.compareTo(rhs.path);
                }
            });
            int idx = 0;
            for(StorageUnit storage:mStorageUnits) {
                LogUtil.i(LOG_TAG, "storage unit " + idx + ":" + storage.toString());
                idx ++;
            }
        } else {
            LogUtil.i(LOG_TAG, "opps, no storage unit");
        }

		return;
	}

    @Override
    public void onReceive(Context context, Intent intent) {
        clear();
        getInstance(context);
    }
}
