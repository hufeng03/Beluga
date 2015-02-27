package com.hufeng.filemanager.helper;

import android.content.ContentValues;
import android.net.Uri;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.scan.ApkObject;
import com.hufeng.filemanager.scan.AudioObject;
import com.hufeng.filemanager.scan.DocumentObject;
import com.hufeng.filemanager.scan.FileObject;
import com.hufeng.filemanager.scan.ImageObject;
import com.hufeng.filemanager.scan.VideoObject;
import com.hufeng.filemanager.scan.ZipObject;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;

/**
 * Created by Feng Hu on 15-02-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaProviderHelper {

    private static final String TAG = "BelugaProviderHelper";

    public static void updateFileInDatabase(File oldfile, File newfile) {
        if (oldfile.isDirectory())
            return;
        int category = FileCategoryHelper.getFileCategoryForFile(oldfile.getAbsolutePath());
        Uri uri = null;
        String oldpath = oldfile.getPath();
        String newpath = newfile.getPath();
        ContentValues values = new ContentValues();
        FileObject object = new FileObject(newpath);
        values.put(DataStructures.FavoriteColumns.FILE_NAME_FIELD, object.getName());
        values.put(DataStructures.FavoriteColumns.FILE_PATH_FIELD, object.getPath());
        values.put(DataStructures.FavoriteColumns.FILE_DATE_FIELD, object.getDate());
        values.put(DataStructures.FavoriteColumns.FILE_EXTENSION_FIELD, object.getExtension());
        int count = 0;
        try {
            count = FileManager.getAppContext().getContentResolver().update(DataStructures.FavoriteColumns.CONTENT_URI, values, DataStructures.FavoriteColumns.FILE_PATH_FIELD + "=?", new String[]{oldpath});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LogUtil.IDBG)
            LogUtil.i(TAG, "update favorite from " + oldpath + " to " + newpath + " return " + count);

        values.clear();
        long update_count = 0;
        switch (category) {
            case FileCategoryHelper.CATEGORY_TYPE_APK:
                uri = DataStructures.ApkColumns.CONTENT_URI;
                new ApkObject(newpath).toContentValues(values);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                uri = DataStructures.AudioColumns.CONTENT_URI;
                new AudioObject(newpath).toContentValues(values);
//                update_count = updateInDb(oldpath, newpath, FileCategoryHelper.CATEGORY_TYPE_AUDIO);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                uri = DataStructures.DocumentColumns.CONTENT_URI;
                new DocumentObject(newpath).toContentValues(values);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                uri = DataStructures.ImageColumns.CONTENT_URI;
                new ImageObject(newpath).toContentValues(values);
//                update_count = updateInDb(oldpath, newpath, FileCategoryHelper.CATEGORY_TYPE_IMAGE);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                uri = DataStructures.VideoColumns.CONTENT_URI;
                new VideoObject(newpath).toContentValues(values);
//                update_count = updateInDb(oldpath, newpath, FileCategoryHelper.CATEGORY_TYPE_VIDEO);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                uri = DataStructures.ZipColumns.CONTENT_URI;
                new ZipObject(newpath).toContentValues(values);
                break;
            default:
                uri = DataStructures.FileColumns.CONTENT_URI;
                new FileObject(newpath).toContentValues(values);
                break;
        }
        if (LogUtil.IDBG) LogUtil.i(TAG, "update file in media database: " + update_count);
        if (uri != null) {
            try {
                count = FileManager.getAppContext().getContentResolver().update(uri, values, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{oldpath});
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (LogUtil.IDBG)
                LogUtil.i(TAG, "update " + oldpath + " to " + newpath + " return " + count);
            if (count <= 0) {
                try {
                    FileManager.getAppContext().getContentResolver().insert(uri, values);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (LogUtil.IDBG) LogUtil.i(TAG, "insert " + newpath + " return " + count);
            }
        }
    }
}
