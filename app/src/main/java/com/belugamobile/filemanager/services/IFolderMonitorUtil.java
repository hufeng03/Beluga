package com.belugamobile.filemanager.services;

import android.os.Environment;

import com.belugamobile.filemanager.mount.MountPoint;
import com.belugamobile.filemanager.mount.MountPointManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng on 14-4-1.
 */
public class IFolderMonitorUtil {

    public static final String[] IMPORTANT_FOLDER = {
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

    public static List<String> getAllImportantFolders() {
        List<String> dirs = new ArrayList<String>();
        MountPointManager mangaer = MountPointManager.getInstance();
        if (mangaer != null) {
            List<MountPoint> mountPoints = mangaer.getMountPoints();
            for (MountPoint mountPoint : mountPoints) {
                dirs.add(mountPoint.mPath);
                for (String name : IMPORTANT_FOLDER) {
                    File file = new File(mountPoint.mPath, name);
                    if (file.exists()) {
                        dirs.add(file.getAbsolutePath());
                        File[] children = file.listFiles();
                        if (children != null) {
                            for (File child : children) {
                                if (child.isDirectory() && !child.isHidden()) {
                                    dirs.add(child.getAbsolutePath());
                                }
                            }
                        }
                    }
                }
            }
        }
        return dirs;
    }

}
