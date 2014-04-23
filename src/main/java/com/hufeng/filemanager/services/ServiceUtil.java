package com.hufeng.filemanager.services;

import android.os.Environment;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by feng on 14-4-1.
 */
public class ServiceUtil {

    public static final String[] IMPORTANT_DIRECTORY = {
            Environment.DIRECTORY_DCIM,
            Environment.DIRECTORY_ALARMS,
            Environment.DIRECTORY_DOWNLOADS,
            Environment.DIRECTORY_MOVIES,
            Environment.DIRECTORY_MUSIC,
            Environment.DIRECTORY_NOTIFICATIONS,
            Environment.DIRECTORY_PODCASTS,
            Environment.DIRECTORY_RINGTONES,
            Environment.DIRECTORY_PICTURES,
            "Documents"
    };

    public static String[] getAllImportantDirectory() {
        ArrayList<String> dirs = new ArrayList<String>();
        String[] stors = StorageManager.getInstance(FileManager.getAppContext()).getMountedStorages();
        if (stors != null) {
            for (String stor : stors) {
                dirs.add(stor);
                for(String dir:IMPORTANT_DIRECTORY) {
                    File file = new File(stor, dir);
                    if (file.exists()) {
                        dirs.add(file.getAbsolutePath());
                        File[] childs = file.listFiles();
                        if (childs != null) {
                            for (File child : childs) {
                                if (child.isDirectory() && !child.isHidden()) {
                                    dirs.add(child.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }
        return (dirs == null || dirs.size() == 0)? null : dirs.toArray(new String[dirs.size()]);
    }

}
