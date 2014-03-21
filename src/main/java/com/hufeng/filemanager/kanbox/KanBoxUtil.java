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
        String[] storages = manager.getMountedStorages();
        String local_path = null;
        if(storages!=null) {
            int size = storages.length;
            int idx = 0;
            while(idx<size){
                String stor = storages[idx];
                File kanbox_dir = new File(stor, KanBoxConfig.LOCAL_STORAGE_DIRECTORY);
                if(local_path==null || kanbox_dir.exists()) {
                    local_path = kanbox_dir.getAbsolutePath();
                }
                idx++;
            }
        }
        if (!TextUtils.isEmpty(local_path)) {
            String name =  MD5Util.getMD5HexForString(Constants.KANBOX_APK_URL);
            return new File(local_path, name+".apk").getAbsolutePath();
//            if (!new File(local_path, name+".apk").exists()) {
//                return new File(local_path, name+".apk").getAbsolutePath();
//            } else {
//                int i = 1;
//                while (true) {
//                    File file = new File(local_path, name+"("+i+").apk");
//                    if (!file.exists()) {
//                        return file.getAbsolutePath();
//                    } else {
//                        i++;
//                    }
//                }
//            }
        }
        return null;
    }
}
