package com.hufeng.filemanager;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.hufeng.filemanager.data.FileEntry;
import com.hufeng.filemanager.mount.MountPoint;
import com.hufeng.filemanager.mount.MountPointManager;
import com.hufeng.filemanager.utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng Hu on 15-02-19.
 * <p/>
 * TODO: Add a class header comment.
 */
public class DownloadListLoader extends AsyncTaskLoader<List<FileEntry>> {

    private static final String LOG_TAG = DownloadListLoader.class.getSimpleName();

    private List<FileEntry> mFiles;

    SortPreferenceReceiver mSortObserver;
    DownloadFolderObserver mDownloadFolderObserver;

    public DownloadListLoader(Context context) {
        super(context);
    }

    @Override
    public List<FileEntry> loadInBackground() {
        LogUtil.i(LOG_TAG, this.hashCode()+" load in background");
        List<FileEntry> entries = new ArrayList<FileEntry>();
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
                    entries.add(new FileEntry(new File(mp.mPath, download)));
                }
            }

            if (mDownloadFolderObserver != null) {
                mDownloadFolderObserver.register(mp.mPath);
            }
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
        LogUtil.i(LOG_TAG, this.hashCode() + " FileListLoader onStartLoading");
        if (mFiles != null ) {
            deliverResult(mFiles);
        }

        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, CategorySelectEvent.CategoryType.NONE);
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
    public void onCanceled(List<FileEntry> data) {
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

    private void releaseResources(List<FileEntry> data) {
        // do nothing
    }




}
