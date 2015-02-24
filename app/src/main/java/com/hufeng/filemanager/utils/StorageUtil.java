package com.hufeng.filemanager.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.hufeng.filemanager.storage.StorageUnit;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng Hu on 15-02-20.
 * <p/>
 * TODO: Add a class header comment.
 */
public class StorageUtil {

    public static List<StorageUnit> getMountedStorageUnits(Context context) {
        List<StorageUnit> storageUnits = new ArrayList<StorageUnit>();

        StorageManager sStorageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
        if(sStorageManager != null) {
            Class<?> StorageVolume;
            try {
                StorageVolume = Class.forName("android.os.storage.StorageVolume");
                Method method_getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                Method method_getPath = StorageVolume.getDeclaredMethod("getPath");
                //Method method_getDescription = StorageVolume.getDeclaredMethod("getDescription");
                Method method_isRemovable = StorageVolume.getDeclaredMethod("isRemovable");
                Method method_getVolumeState = sStorageManager.getClass().getDeclaredMethod("getVolumeState", String.class);
                try {
                    Object volumeList = method_getVolumeList.invoke(sStorageManager);
                    if(volumeList.getClass().isArray()){
                        int len = Array.getLength(volumeList);
                        for(int i=0;i<len;i++){
                            Object volume = Array.get(volumeList, i);
                            Object real_volume = StorageVolume.cast(volume);

                            String path = (String) method_getPath.invoke(real_volume);
                            if (TextUtils.isEmpty(path)) continue;
                            path = new File(path).getAbsolutePath();
                            if (TextUtils.isEmpty(path)) continue;
                            boolean isRemovable = (Boolean) method_isRemovable.invoke(real_volume);
                            String state = (String) method_getVolumeState.invoke(sStorageManager, path);

                            long availableSize = com.hufeng.filemanager.storage.StorageUtil.getAvailaleSize(path);
                            long allSize = com.hufeng.filemanager.storage.StorageUtil.getAllSize(path);
                            String description = null;
                            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                                storageUnits.add(new StorageUnit(path, description, isRemovable, state, availableSize, allSize));
                            }
                        }
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return storageUnits;
    }
}
