package com.belugamobile.filemanager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.mount.MountPoint;
import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.provider.DataStructures;
import com.belugamobile.filemanager.utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Feng Hu on 15-02-19.
 * <p/>
 * TODO: Add a class header comment.
 */
public class DownloadListLoader extends AsyncTaskLoader<List<BelugaFileEntry>> {

    private static final String LOG_TAG = DownloadListLoader.class.getSimpleName();

    private List<BelugaFileEntry> mFiles;

    SortPreferenceReceiver mSortObserver;
    DownloadFolderObserver mDownloadFolderObserver;

    public DownloadListLoader(Context context) {
        super(context);
    }

    @Override
    public List<BelugaFileEntry> loadInBackground() {
        LogUtil.i(LOG_TAG, this.hashCode()+" load in background");
        List<BelugaFileEntry> entries = new ArrayList<BelugaFileEntry>();
        List<MountPoint> mountPoints = MountPointManager.getInstance().getMountPoints();

        if (mDownloadFolderObserver != null) {
            mDownloadFolderObserver.dismiss();
        }
        for(MountPoint mp:mountPoints) {
            String[] downloads = new File(mp.mPath).list(new FilenameFilter() {
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
                for (String download : downloads) {
                    entries.add(new BelugaFileEntry(new File(mp.mPath, download)));
                }
            }

            if (mDownloadFolderObserver != null) {
                mDownloadFolderObserver.register(mp.mPath);
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
                    BelugaFileEntry entry = entries.get(i);
                    entry.isFavorite = favoritePaths.contains(entry.path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            Collections.sort(entries, BelugaSortHelper.getComparator(FileCategoryHelper.CATEGORY_TYPE_DOWNLOAD));
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

        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, FileCategoryHelper.CATEGORY_TYPE_DOWNLOAD);
        }

        if (mDownloadFolderObserver == null) {
            mDownloadFolderObserver = new DownloadFolderObserver(this);
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
