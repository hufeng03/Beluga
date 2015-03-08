package com.hufeng.filemanager.loader;

/**
 * Created by feng on 14-2-15.
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.CategorySelectEvent;
import com.hufeng.filemanager.Constants;
import com.hufeng.filemanager.SortPreferenceReceiver;
import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.helper.BelugaSortHelper;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.provider.DataStructures;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class FileBrowserLoader extends AsyncTaskLoader<List<FileEntry>> {

    private static final String LOG_TAG = FileBrowserLoader.class.getSimpleName();

    private String mRoot;
    private String[] mDirs;
    private List<FileEntry> mFiles;
    private String mSearch;

    SortPreferenceReceiver mSortObserver;

    public FileBrowserLoader(Context context, String root, String[] dirs, String search) {
        super(context);
        mRoot = root;
        mDirs = dirs;
        mSearch = search;
    }

    @Override
    public List<FileEntry> loadInBackground() {
        LogUtil.i(LOG_TAG, this.hashCode() + " FileListLoader loadinbackground()");
        List<FileEntry> entries = new ArrayList<FileEntry>();
        if (!TextUtils.isEmpty(mRoot) && new File(mRoot).exists() && new File(mRoot).isDirectory()) {
            String[] files = new File(mRoot).list();
            if (files != null) {
                FileEntry entry;
                for (String file : files) {
                    entry = new FileEntry(mRoot, file);
                    if (entry.exist) {
                        if (TextUtils.isEmpty(mSearch) || entry.getName().toLowerCase().contains(mSearch.toLowerCase())) {
                            LogUtil.i(LOG_TAG, "add "+file+"!!!!!!!!!!"+entry);
                            entries.add(entry);
                        }
                    }
                }
            }
        } else if (mDirs != null && mDirs.length > 0) {
                FileEntry entry;
                for (String dir : mDirs) {
                    entry = new FileEntry(dir);
                    if (entry.exist) {
                        if (TextUtils.isEmpty(mSearch) || entry.getName().toLowerCase().contains(mSearch.toLowerCase())) {
                            entries.add(entry);
                        }
                    }
                }
        }

        if (entries.size() > 0) {
            // Retrieve favorite status from database
            StringBuilder whereClause = new StringBuilder();
            String[] whereArgs = new String[entries.size()];
            whereClause.append("?");
            whereArgs[0] = entries.get(0).path;
            for (int i = 1; i < entries.size(); i++) {
                whereClause.append(",?");
                whereArgs[i] = entries.get(i).path;
            }
            String where = DataStructures.FavoriteColumns.PATH + " IN(" + whereClause.toString() + ")";
            ContentResolver cr = getContext().getContentResolver();
            Cursor cursor = null;
            try {
                cursor = cr.query(DataStructures.FavoriteColumns.CONTENT_URI, new String[]{DataStructures.FavoriteColumns.PATH}, where, whereArgs, null);
                HashSet<String> favoritePaths = new HashSet<String>();
                while (cursor.moveToNext()) {
                    String path = cursor.getString(0);
                    favoritePaths.add(path);
                }
                for (int i = 0; i < entries.size(); i++) {
                    FileEntry entry = entries.get(i);
                    entry.isFavorite = favoritePaths.contains(entry.path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            // Sort the list.
            Collections.sort(entries, BelugaSortHelper.getComparator(getContext(), FileCategoryHelper.CATEGORY_TYPE_UNKNOW));
        }
        return entries;
    }

    @Override
    public void deliverResult(List<FileEntry> data) {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader deliverResult with "+(data==null?0:data.size()));
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        List<FileEntry> oldFiles = mFiles;
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
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onStartLoading");
        if (mFiles != null ) {
            deliverResult(mFiles);
        }

        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
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

    }


    @Override
    public void onCanceled(List<FileEntry> data) {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader onCanceled");
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public void forceLoad() {
        LogUtil.i(LOG_TAG, this.hashCode()+" FileListLoader forceLoad");
        super.forceLoad();
    }

    private void releaseResources(List<FileEntry> data) {
        // do nothing
    }
}
