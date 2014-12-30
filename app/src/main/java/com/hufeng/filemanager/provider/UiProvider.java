package com.hufeng.filemanager.provider;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.root.RootHelper;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by feng on 2014-03-30.
 */
public class UiProvider {

    private static final String TAG = UiProvider.class.getSimpleName();

    public static String[] getDownloadDirs() {
        String[] files = null;
        String[] devices = StorageManager.getInstance(FileManager.getAppContext()).getMountedStorages();
        if (devices != null) {
            ArrayList<String> all_downloads = new ArrayList<String>();
            for(String device:devices) {
                String[] downloads = new File(device).list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        if(new File(dir, filename).isDirectory() && filename.toLowerCase().contains("download")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
                if (downloads != null) {
                    for (String download: downloads) {
                        all_downloads.add(new File(device, download).getAbsolutePath());
                    }
                }
            }
            files = all_downloads.toArray(new String[0]);
        }
        return files;
    }

    public static String[] getFavoriteFiles() {
        ArrayList<String> dirs = new ArrayList<String>(0);
        ArrayList<String> deleted_favorite_dirs = new ArrayList<String>(0);
        Cursor cursor = FileManager.getAppContext().getContentResolver().query(DataStructures.FavoriteColumns.CONTENT_URI, DataStructures.FavoriteColumns.FILE_PROJECTION, null, null, null);
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                String file = cursor.getString(DataStructures.FavoriteColumns.FILE_PATH_FIELD_INDEX);
                if(new File(file).exists())
                    dirs.add(file);
                else
                    deleted_favorite_dirs.add(file);
            }
            cursor.close();
        }

        if(!deleted_favorite_dirs.isEmpty())
        {
            StringBuilder selection = new StringBuilder();
            selection.append(DataStructures.FavoriteColumns.FILE_PATH_FIELD + " IN ");
            selection.append('(');
            boolean isFirst = true;
            for (String  path:deleted_favorite_dirs) {
                if (isFirst) {
                    selection.append('\'');
                    selection.append(path.replace("'", "\""));
                    selection.append('\'');
                    isFirst = false;
                } else {
                    selection.append(',');
                    selection.append('\'');
                    selection.append(path.replace("'", "\""));
                    selection.append('\'');
                }
            }
            selection.append(')');
            deleted_favorite_dirs.clear();
            int count = FileManager.getAppContext().getContentResolver().delete(DataStructures.FavoriteColumns.CONTENT_URI, selection.toString(), null);
            if(LogUtil.IDBG) LogUtil.i(TAG, "File Manager deletes "+count+" favorite files");
        }

        Set someSet   =   new HashSet(dirs);

        //将Set中的集合，放到一个临时的链表中(tempList)
        Iterator iterator   =   someSet.iterator();
        ArrayList<String>   tempList   =   new   ArrayList<String>();
        while   (iterator.hasNext())   {
            String path = iterator.next().toString();
            if(!new File(path).isDirectory())
            {
                tempList.add(path);
            }
        }
        iterator = someSet.iterator();
        while   (iterator.hasNext())   {
            String path = iterator.next().toString();
            if(new File(path).isDirectory())
            {
                tempList.add(path);
            }
        }
        dirs = tempList;

        if(dirs.size()==0)
            return null;
        else
        {
            String[] store = new String[dirs.size()];
            return dirs.toArray(store);
        }
    }

    public static String[] getStorageDirs() {
        String[] files = StorageManager.getInstance(FileManager.getAppContext()).getMountedStorages();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FileManager.getAppContext());
        boolean show_root = preferences.getBoolean("SHOW_ROOT_DIR", false);
        if (RootHelper.isRootedPhone() && show_root) {
            int len = (files == null ? 0: files.length) + 1;
            String[] new_files = new String[len];
            for (int i = 0; i < len-1; i++) {
                new_files[i] = files[i];
            }
            new_files[len-1] = "/";
            files = new_files;
        }
        return files;
    }
}
