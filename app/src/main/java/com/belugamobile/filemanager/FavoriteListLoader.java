package com.belugamobile.filemanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.provider.DataStructures;
import com.belugamobile.filemanager.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Feng Hu on 15-02-19.
 * <p/>
 * TODO: Add a class header comment.
 */
public class FavoriteListLoader extends AsyncTaskLoader<List<BelugaFileEntry>> {

    private static final String LOG_TAG = FavoriteListLoader.class.getSimpleName();

    final ForceLoadContentObserver mObserver;

    private List<BelugaFileEntry> mFiles;

    SortPreferenceReceiver mSortObserver;
    DownloadFolderObserver mDownloadFolderObserver;

    public FavoriteListLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    @Override
    public List<BelugaFileEntry> loadInBackground() {
        LogUtil.i(LOG_TAG, this.hashCode()+" load in background");
        List<BelugaFileEntry> entries = new ArrayList<BelugaFileEntry>();

        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(DataStructures.FavoriteColumns.CONTENT_URI,
                    new String[] {DataStructures.FavoriteColumns.PATH},
                    null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    BelugaFileEntry entry = new BelugaFileEntry(path);
                    if (entry.exist) {
                        entry.isFavorite = true;
                        entries.add(entry);
                    }
                }
            }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (cursor != null) {
              cursor.close();
          }
        }

        Collections.sort(entries, BelugaSortHelper.getComparator(getContext(), FileCategoryHelper.CATEGORY_TYPE_FAVORITE));

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

        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, FileCategoryHelper.CATEGORY_TYPE_FAVORITE);
        }

        if (mDownloadFolderObserver == null) {
            mDownloadFolderObserver = new DownloadFolderObserver(this);
        }

        getContext().getContentResolver().registerContentObserver(DataStructures.FavoriteColumns.CONTENT_URI, true, mObserver);

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

        // Stop monitoring for changes.
        if (mSortObserver != null) {
            mSortObserver.dismiss(getContext());
            mSortObserver = null;
        }

        // Stop monitoring for changes.
        if (mDownloadFolderObserver != null) {
            mDownloadFolderObserver.dismiss();
            mDownloadFolderObserver = null;
        }

        getContext().getContentResolver().unregisterContentObserver(mObserver);
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
