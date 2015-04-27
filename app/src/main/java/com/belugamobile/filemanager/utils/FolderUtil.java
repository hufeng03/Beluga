package com.belugamobile.filemanager.utils;

import android.text.TextUtils;

import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.root.BelugaRootManager;

import java.io.File;

/**
 * Created by Feng on 2015-04-26.
 */
public class FolderUtil {
    public static final String concatenateDirAndName(String dir, String name){
        if (dir.endsWith("/")) {
            return dir + name;
        } else {
            return dir + "/" +name;
        }
    }

    public static final String[] list(String dir) {
        String[] names = new File(dir).list();
        if (names == null || names.length == 0) {
            String mountPoints = MountPointManager.getInstance().getRealMountPointPath(dir);
            if (TextUtils.isEmpty(mountPoints)) {
                names = BelugaRootManager.getInstance().listSync(dir);
            }
        }
        return names;
    }

}
