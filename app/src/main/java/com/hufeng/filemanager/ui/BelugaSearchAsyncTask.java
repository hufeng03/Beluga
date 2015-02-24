package com.hufeng.filemanager.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.provider.DataStructures;

/**
 * Created by Feng Hu on 15-02-11.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaSearchAsyncTask extends BelugaActionAsyncTask {

    public String mSearchString;
    ContentResolver mContentResolver;

    public BelugaSearchAsyncTask(BelugaActionAsyncTaskCallbackDelegate actionController, String searchString) {
        super(actionController);
        mSearchString = searchString;
        mContentResolver = FileManager.getAppContext().getContentResolver();
    }

    private static Uri[] sUris = {
        DataStructures.ImageColumns.CONTENT_URI,
        DataStructures.VideoColumns.CONTENT_URI,
        DataStructures.AudioColumns.CONTENT_URI,
        DataStructures.ApkColumns.CONTENT_URI,
        DataStructures.DocumentColumns.CONTENT_URI,
        DataStructures.ZipColumns.CONTENT_URI
    };

    @Override
    public boolean run(FileEntry[] params) {

        String searchString = mSearchString;

        if(!TextUtils.isEmpty(searchString)) {
            searchString.replace("[","[[]");
            searchString.replace("%","[%]");
            searchString.replace("_","[_]");
            searchString.replace("^","[^]");
            searchString = searchString.replace("'", "''");
        }

        for (Uri uri : sUris) {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(uri,
                        new String[]{DataStructures.FileColumns.FILE_PATH_FIELD},
                        DataStructures.FileColumns.FILE_NAME_FIELD + " LIKE '%" + searchString + "%'",
                        null,
                        null);
                if (cursor != null) {
                    FileEntry[] entries = new FileEntry[cursor.getCount()];
                    int idx = 0;
                    while (cursor.moveToNext()) {
                        entries[idx++] = new FileEntry(cursor.getString(0));
                    }
                    publishProgress(entries);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        try {
            Thread.sleep(5000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.search_progress_dialog_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.search_progress_dialog_title);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        return context.getString(R.string.search_complete);
    }
}
