package com.belugamobile.filemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.provider.DataStructures;
import com.belugamobile.filemanager.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng Hu on 15-02-19.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FileSearchLoader extends AsyncTaskLoader<List<BelugaFileEntry>> {

    private static final String LOG_TAG = FileSearchLoader.class.getSimpleName();

    private List<BelugaFileEntry> mFiles;

    private String mSearchString;

    private static Uri[] sUris = {
            DataStructures.ImageColumns.CONTENT_URI,
            DataStructures.VideoColumns.CONTENT_URI,
            DataStructures.AudioColumns.CONTENT_URI,
            DataStructures.ApkColumns.CONTENT_URI,
            DataStructures.DocumentColumns.CONTENT_URI,
            DataStructures.ZipColumns.CONTENT_URI
    };

    private static String[] sTables = {
            DataStructures.ImageColumns.TABLE,
            DataStructures.VideoColumns.TABLE,
            DataStructures.AudioColumns.TABLE,
            DataStructures.ApkColumns.TABLE,
            DataStructures.DocumentColumns.TABLE,
            DataStructures.ZipColumns.TABLE,
    };

    public FileSearchLoader(Context context, String searchString) {
        super(context);
        mSearchString = searchString;
    }

    @Override
    public List<BelugaFileEntry> loadInBackground() {
        LogUtil.i(LOG_TAG, this.hashCode()+" load in background: "+mSearchString);
        List<BelugaFileEntry> entries = new ArrayList<BelugaFileEntry>();

        if (TextUtils.isEmpty(mSearchString)) {
            return entries;
        }

        String searchString = mSearchString;

        if(!TextUtils.isEmpty(searchString)) {
            searchString = searchString.replace("[","[[]");
            searchString = searchString.replace("%","[%]");
            searchString = searchString.replace("_","[_]");
            searchString = searchString.replace("^","[^]");
            searchString = searchString.replace("'", "''");
        }

        ContentResolver contentResolver = getContext().getContentResolver();
        int idx = 0;
        for (Uri uri : sUris) {
            Cursor cursor = null;
            try {
                cursor = contentResolver.query(uri,
                        DataStructures.FileColumns.PROJECTION,
                        sTables[idx] + "." + DataStructures.FileColumns.NAME + " LIKE '%" + searchString + "%'",
                        null,
                        null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        entries.add(new BelugaFileEntry(cursor));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            idx++;
        }

        return entries;
    }

    @Override
    public void deliverResult(List<BelugaFileEntry> data) {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader deliverResult with "+(data==null?0:data.size()));
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        List<BelugaFileEntry> oldFiles = mFiles;
        mFiles = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldFiles != null && oldFiles != data) {
            releaseResources(oldFiles);
        }

        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        LogUtil.i(LOG_TAG, this.hashCode() + " FileListLoader onStartLoading");
        if (mFiles != null ) {
            deliverResult(mFiles);
        }

        if(takeContentChanged() || mFiles == null) {
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onStopLoading");
        cancelLoad();
    }


    @Override
    protected void onReset() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onReset");
        // Ensure that loader is stopped
        onStopLoading();

        if (mFiles != null) {
            releaseResources(mFiles);
            mFiles = null;
        }
    }

    @Override
    public void onCanceled(List<BelugaFileEntry> data) {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onCanceled");
        if (data != null) {
            releaseResources(data);
        }
    }

    @Override
    public void forceLoad() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader forceLoad");
        super.forceLoad();
    }

    private void releaseResources(List<BelugaFileEntry> data) {
        // do nothing
    }
}
