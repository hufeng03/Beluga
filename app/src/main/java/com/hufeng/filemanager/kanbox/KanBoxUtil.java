package com.hufeng.filemanager.kanbox;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.MD5Util;

import java.io.File;

/**
 * Created by feng on 2/24/2014.
 */
public class KanBoxUtil {

    public static String getKanboxApkPath(Context context) {
        StorageManager manager = StorageManager.getInstance(context);
        if (manager == null) {
            return null;
        }
        String[] storages = manager.getMountedStorages();
        String local_path = null;
        String name =  MD5Util.getMD5HexForString(Constants.KANBOX_APK_URL);
        boolean flag_local_exist = false;
        if(storages!=null) {
            int size = storages.length;
            int idx = 0;
            while(idx<size){
                String stor = storages[idx];
                if (new File(stor, KanBoxConfig.LOCAL_STORAGE_DIRECTORY+File.separator+name+".apk").exists()) {
                    flag_local_exist = true;
                    local_path = stor;
                }
                idx++;
            }
        }
        if (flag_local_exist) {
            return new File(local_path, KanBoxConfig.LOCAL_STORAGE_DIRECTORY+File.separator+name+".apk").getAbsolutePath();
        } else {
            String stor = manager.getPrimaryExternalStorage();
            if (TextUtils.isEmpty(stor))
                return null;
            else
                return new File(stor, KanBoxConfig.LOCAL_STORAGE_DIRECTORY+File.separator+name+".apk").getAbsolutePath();
        }
    }
}
