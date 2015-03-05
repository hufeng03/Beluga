package com.hufeng.filemanager.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hufeng.filemanager.data.ApkEntry;
import com.hufeng.filemanager.data.AudioEntry;
import com.hufeng.filemanager.data.DocumentEntry;
import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.data.ImageEntry;
import com.hufeng.filemanager.data.VideoEntry;
import com.hufeng.filemanager.data.ZipEntry;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;


/**
 * Created by Feng Hu on 15-02-25.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaProviderHelper {

    private static final String TAG = "BelugaProviderHelper";

    private static Uri getUriAccordingToCategory(int category) {
        Uri uri = null;
        switch (category) {
            case FileCategoryHelper.CATEGORY_TYPE_APK:
                uri = DataStructures.ApkColumns.CONTENT_URI;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                uri = DataStructures.AudioColumns.CONTENT_URI;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                uri = DataStructures.DocumentColumns.CONTENT_URI;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                uri = DataStructures.ZipColumns.CONTENT_URI;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                uri = DataStructures.ImageColumns.CONTENT_URI;
                break;
            case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                uri = DataStructures.VideoColumns.CONTENT_URI;
                break;
            default:
                uri = null;
                break;
        }
        return uri;
    }

    public static FileEntry createFileEntryAccordingToCategory(String path, int category) {
        FileEntry entry = null;
        switch (category) {
            case FileCategoryHelper.CATEGORY_TYPE_APK:
                entry = new ApkEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                entry = new AudioEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                entry = new DocumentEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                entry = new ZipEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                entry = new ImageEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                entry = new VideoEntry(path);
                break;
            default:
                break;
        }
        return entry;
    }

    public static void updateInBelugaDatabase(Context context, String oldPath, String newPath) {
        int category = FileCategoryHelper.getFileCategoryForFile(newPath);
        if (category == 0) {
            //Something unexpected happened
            return;
        }
        Uri uri = getUriAccordingToCategory(category);
        FileEntry newFileEntry = createFileEntryAccordingToCategory(newPath, category);

        if (uri == null || newFileEntry == null) {
            //Something unexpected happened
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        int count = 0;
        newFileEntry.fillContentValues(values);

        try {
            count = contentResolver.update(uri, values,
                    DataStructures.FileColumns.FILE_PATH_FIELD + "=?",
                    new String[]{oldPath});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LogUtil.IDBG)
            LogUtil.i(TAG, "update " + oldPath + " to " + newPath + " return " + count);
        if (count <= 0) {
            try {
                Uri newUri = contentResolver.insert(uri, values);
                if (newUri != null) {
                    count = 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (LogUtil.IDBG)
                LogUtil.i(TAG, "insert " + newPath + " return " + count);
        }

        if (count > 0) {
            values.clear();
            values.put(DataStructures.FavoriteColumns.FILE_NAME_FIELD, newFileEntry.name);
            values.put(DataStructures.FavoriteColumns.FILE_PATH_FIELD, newFileEntry.path);
            values.put(DataStructures.FavoriteColumns.FILE_DATE_FIELD, newFileEntry.lastModified);
            values.put(DataStructures.FavoriteColumns.FILE_EXTENSION_FIELD, newFileEntry.extension);
            try {
                count = contentResolver.update(DataStructures.FavoriteColumns.CONTENT_URI,
                        values,
                        DataStructures.FavoriteColumns.FILE_PATH_FIELD + "=?",
                        new String[]{oldPath});
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (LogUtil.IDBG)
                LogUtil.i(TAG, "update favorite from " + oldPath + " to " + newPath + " return " + count);
        }
    }

    public static void insertInBelugaDatabase(Context context, String path) {
        int category = FileCategoryHelper.getFileCategoryForFile(path);
        if (category == 0) {
            //Something unexpected happened
            return;
        }

        Uri uri = getUriAccordingToCategory(category);
        FileEntry newFileEntry = createFileEntryAccordingToCategory(path, category);
        if (uri == null || newFileEntry == null) {
            //Something unexpected happened
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        newFileEntry.fillContentValues(values);
        Uri newFileUri = null;

        try {
            newFileUri = contentResolver.insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (LogUtil.IDBG)
            LogUtil.i(TAG, "insert " + path + " return " + newFileUri==null?"null":newFileUri.toString());
    }

    public static void deleteInBelugaDatabase(Context context, String path) {
        int category = FileCategoryHelper.getFileCategoryForFile(path);
        if (category == 0) {
            //Something unexpected happened
            return;
        }

        Uri uri = getUriAccordingToCategory(category);
        if (uri == null) {
            //Something unexpected happened
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        int count = 0;

        try {
            count = contentResolver.delete(uri,
                    DataStructures.FileColumns.FILE_PATH_FIELD + "=?",
                    new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }

        int favorite_count = 0;
        try {
            favorite_count = contentResolver.delete(
                    DataStructures.FavoriteColumns.CONTENT_URI,
                    DataStructures.FileColumns.FILE_PATH_FIELD + "=?",
                    new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (LogUtil.IDBG)
            LogUtil.i(TAG, "delete " + path + " return " + count + "," + favorite_count);
    }

    public static void setFavoriteInBelugaDatabase(Context context, String path) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues values = new ContentValues();
    	FileEntry fileEntry = new FileEntry(path);
        fileEntry.fillContentValues(values);
    	if(new File(path).isDirectory()){
    		values.put(DataStructures.FavoriteColumns.IS_DIRECTORY_FIELD, 1);
    		values.put(DataStructures.FileColumns.FILE_SIZE_FIELD, 0);
    	} else {
    		values.put(DataStructures.FavoriteColumns.IS_DIRECTORY_FIELD, 0);
    	}
    	Uri uri = DataStructures.FavoriteColumns.CONTENT_URI;
		int count = 0;
        try {
            count = contentResolver.update(uri, values, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(LogUtil.IDBG) LogUtil.i(TAG, "update "+path+" return "+count);
		if (count <= 0) {
            try {
                Uri favUri = contentResolver.insert(uri, values);
                if (favUri != null)
                    count = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
			if(LogUtil.IDBG) LogUtil.i(TAG, "insert "+path+" return "+count);
		}
    }

    public static void setUndoFavoriteInBelugaDatabase(Context context, String path) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = DataStructures.FavoriteColumns.CONTENT_URI;
        try {
            contentResolver.delete(uri, DataStructures.FileColumns.FILE_PATH_FIELD + "=?", new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
