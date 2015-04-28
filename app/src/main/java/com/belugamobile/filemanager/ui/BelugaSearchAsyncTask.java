package com.belugamobile.filemanager.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.belugamobile.filemanager.FileManager;
import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.provider.DataStructures;

/**
 * Created by Feng Hu on 15-02-11.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaSearchAsyncTask extends BelugaActionAsyncTask {

    public String mSearchString;
    ContentResolver mContentResolver;

    public BelugaActionType mType = BelugaActionType.SEARCH;

    public BelugaSearchAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate actionController, String searchString) {
        super(context, actionController);
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
    public boolean run() {

        String searchString = mSearchString;

        if(!TextUtils.isEmpty(searchString)) {
            searchString = searchString.replace("[","[[]");
            searchString = searchString.replace("%","[%]");
            searchString = searchString.replace("_","[_]");
            searchString = searchString.replace("^","[^]");
            searchString = searchString.replace("'", "''");
        }

        for (Uri uri : sUris) {
            Cursor cursor = null;
            try {
                cursor = mContentResolver.query(uri,
                        new String[]{DataStructures.FileColumns.PATH},
                        DataStructures.FileColumns.NAME + " LIKE '%" + searchString + "%'",
                        null,
                        null);
                if (cursor != null) {
                    BelugaFileEntry[] entries = new BelugaFileEntry[cursor.getCount()];
                    int idx = 0;
                    while (cursor.moveToNext()) {
                        entries[idx++] = new BelugaFileEntry(cursor.getString(0));
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
