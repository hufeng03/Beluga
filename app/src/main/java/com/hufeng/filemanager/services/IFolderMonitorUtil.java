package com.hufeng.filemanager.services;

import android.os.Environment;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.mount.MountPointManager;

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
            List<FileEntry> mountPoints = mangaer.getMountPointFileEntry();
            for (FileEntry mountPoint : mountPoints) {
                dirs.add(mountPoint.path);
                for (String name : IMPORTANT_FOLDER) {
                    File file = new File(mountPoint.path, name);
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
