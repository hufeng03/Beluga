package com.hufeng.filemanager.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.hufeng.filemanager.data.BelugaApkEntry;
import com.hufeng.filemanager.data.BelugaAudioEntry;
import com.hufeng.filemanager.data.BelugaDocumentEntry;
import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.data.BelugaImageEntry;
import com.hufeng.filemanager.data.BelugaVideoEntry;
import com.hufeng.filemanager.data.BelugaZipEntry;
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

    public static BelugaFileEntry createFileEntryAccordingToCategory(String path, int category) {
        BelugaFileEntry entry = null;
        switch (category) {
            case FileCategoryHelper.CATEGORY_TYPE_APK:
                entry = new BelugaApkEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_AUDIO:
                entry = new BelugaAudioEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_DOCUMENT:
                entry = new BelugaDocumentEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_ZIP:
                entry = new BelugaZipEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_IMAGE:
                entry = new BelugaImageEntry(path);
                break;
            case FileCategoryHelper.CATEGORY_TYPE_VIDEO:
                entry = new BelugaVideoEntry(path);
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
        BelugaFileEntry newBelugaFileEntry = createFileEntryAccordingToCategory(newPath, category);

        if (uri == null || newBelugaFileEntry == null) {
            //Something unexpected happened
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        int count = 0;
        newBelugaFileEntry.fillContentValues(values);

        try {
            count = contentResolver.update(uri, values,
                    DataStructures.FileColumns.PATH + "=?",
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
            values.put(DataStructures.FavoriteColumns.NAME, newBelugaFileEntry.name);
            values.put(DataStructures.FavoriteColumns.PATH, newBelugaFileEntry.path);
            values.put(DataStructures.FavoriteColumns.DATE, newBelugaFileEntry.lastModified);
            values.put(DataStructures.FavoriteColumns.EXTENSION, newBelugaFileEntry.extension);
            try {
                count = contentResolver.update(DataStructures.FavoriteColumns.CONTENT_URI,
                        values,
                        DataStructures.FavoriteColumns.PATH + "=?",
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
        BelugaFileEntry newBelugaFileEntry = createFileEntryAccordingToCategory(path, category);
        if (uri == null || newBelugaFileEntry == null) {
            //Something unexpected happened
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();

        newBelugaFileEntry.fillContentValues(values);
        Uri newFileUri = null;

        try {
            newFileUri = contentResolver.insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (LogUtil.IDBG)
            LogUtil.i(TAG, "insert " + path + " return " + ((newFileUri==null)?"null":newFileUri.toString()));
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
                    DataStructures.FileColumns.PATH + "=?",
                    new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }

        int favorite_count = 0;
        try {
            favorite_count = contentResolver.delete(
                    DataStructures.FavoriteColumns.CONTENT_URI,
                    DataStructures.FileColumns.PATH + "=?",
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
    	BelugaFileEntry belugaFileEntry = new BelugaFileEntry(path);
        belugaFileEntry.fillContentValues(values);
    	if(new File(path).isDirectory()){
    		values.put(DataStructures.FavoriteColumns.IS_DIRECTORY, 1);
    		values.put(DataStructures.FileColumns.SIZE, 0);
    	} else {
    		values.put(DataStructures.FavoriteColumns.IS_DIRECTORY, 0);
    	}
    	Uri uri = DataStructures.FavoriteColumns.CONTENT_URI;
		int count = 0;
        try {
            count = contentResolver.update(uri, values, DataStructures.FileColumns.PATH + "=?", new String[]{path});
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
            contentResolver.delete(uri, DataStructures.FileColumns.PATH + "=?", new String[]{path});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
