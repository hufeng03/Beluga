package com.belugamobile.filemanager.helper;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteFullException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.belugamobile.filemanager.ui.BelugaActionAsyncTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public final class MediaStoreHelper {

    private static final String TAG = "MediaStoreHelper";
    private final Context mContext;
    private BelugaActionAsyncTask mBaseAsyncTask;
    private String mDstFolder;
    private static final int SCAN_FOLDER_NUM = 20;

    /**
     * Constructor of MediaStoreHelper
     *
     * @param context the Application context
     */
    public MediaStoreHelper(Context context) {
        mContext = context;
    }

    public MediaStoreHelper(Context context, BelugaActionAsyncTask baseAsyncTask) {
        mContext = context;
        mBaseAsyncTask = baseAsyncTask;
    }

    public void updateInMediaStore(String oldPath, String newPath) {
        Log.d(TAG, "updateInMediaStore,newPath = " + newPath + ",oldPath = " + oldPath);
        if (mContext != null && !TextUtils.isEmpty(newPath) && !TextUtils.isEmpty(newPath)) {
            Uri uri = null;

            try {
                Method getMtpObjectsUriMethod = MediaStore.Files.class.getDeclaredMethod("getMtpObjectsUri", String.class);
                try {
                    uri = (Uri) getMtpObjectsUriMethod.invoke(null, "external");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            uri = uri.buildUpon().appendQueryParameter("need_update_media_values", "true").build();

            String where = MediaStore.Files.FileColumns.DATA + "=?";
            String[] whereArgs = new String[]{oldPath};

            ContentResolver cr = mContext.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, newPath);
            try {
                Log.d(TAG, "updateInMediaStore,update.");
                cr.update(uri, values, where, whereArgs);

                // mediaProvider.update() only update data columns of
                // database, it is need to other fields of the database, so scan the
                // new path after update(). see ALPS00416588
                scanPathforMediaStore(newPath);
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, "Error, database is closed!!!");
            } catch (NullPointerException e) {
                Log.e(TAG, "Error, NullPointerException:" + e + ",update db may failed!!!");
            } catch (SQLiteFullException e) {
                Log.e(TAG, "Error, database or disk is full!!!" + e);
                if (mBaseAsyncTask != null) {
                    mBaseAsyncTask.cancel(true);
                }
            }
        }
    }

    /**
     * scan Path for new file or folder in MediaStore
     *
     * @param path the scan path
     */
    public void scanPathforMediaStore(String path) {
        Log.d(TAG, "scanPathforMediaStore.path =" + path);
        if (mContext != null && !TextUtils.isEmpty(path)) {
            String[] paths = {path};
            Log.d(TAG, "scanPathforMediaStore,scan file .");
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        }
    }

    public void scanPathforMediaStore(List<String> scanPaths) {
        Log.d(TAG, "scanPathforMediaStore,scanPaths.");
        int length = scanPaths.size();
        if (mContext != null && length > 0) {
            String[] paths;
            if (mDstFolder != null && length > SCAN_FOLDER_NUM) {
                paths = new String[]{mDstFolder};
            } else {
                paths = new String[length];
                scanPaths.toArray(paths);
            }

            Log.d(TAG, "scanPathforMediaStore, scanFiles.");
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        }
    }

    /**
     * delete the record in MediaStore
     *
     * @param paths the delete file or folder in MediaStore
     */
    public void deleteInMediaStore(List<String> paths) {
        Log.d(TAG, "deleteInMediaStore.");
        Uri uri = MediaStore.Files.getContentUri("external");
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("?");
        for (int i = 0; i < paths.size() - 1; i++) {
            whereClause.append(",?");
        }
        String where = MediaStore.Files.FileColumns.DATA + " IN(" + whereClause.toString() + ")";
        // notice that there is a blank before "IN(".
        if (mContext != null && !paths.isEmpty()) {
            ContentResolver cr = mContext.getContentResolver();
            String[] whereArgs = new String[paths.size()];
            paths.toArray(whereArgs);
            Log.d(TAG, "deleteInMediaStore,delete.");
            try {
                cr.delete(uri, where, whereArgs);
            } catch (SQLiteFullException e) {
                Log.e(TAG, "Error, database or disk is full!!!" + e);
                if (mBaseAsyncTask != null) {
                    mBaseAsyncTask.cancel(true);
                }
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, "Error, database is closed!!!");
                if (mBaseAsyncTask != null) {
                    mBaseAsyncTask.cancel(true);
                }
            }
        }
    }

    /**
     * delete the record in MediaStore
     *
     * @param path the delete file or folder in MediaStore
     */
    public void deleteInMediaStore(String path) {
        Log.d(TAG, "deleteInMediaStore,path =" + path);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Uri uri = MediaStore.Files.getContentUri("external");
        String where = MediaStore.Files.FileColumns.DATA + "=?";
        String[] whereArgs = new String[]{path};
        if (mContext != null) {
            ContentResolver cr = mContext.getContentResolver();
            Log.d(TAG, "deleteInMediaStore,delete.");
            try {
                cr.delete(uri, where, whereArgs);
            } catch (SQLiteFullException e) {
                Log.e(TAG, "Error, database or disk is full!!!" + e);
                if (mBaseAsyncTask != null) {
                    mBaseAsyncTask.cancel(true);
                }
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, "Error, database is closed!!!");
                if (mBaseAsyncTask != null) {
                    mBaseAsyncTask.cancel(true);
                }
            }
        }
    }

    /**
     * Set dstfolder so when scan files size more than SCAN_FOLDER_NUM use folder
     * path to make scanner scan this folder directly.
     *
     * @param dstFolder
     */
    public void setDstFolder(String dstFolder) {
        mDstFolder = dstFolder;
    }
}
