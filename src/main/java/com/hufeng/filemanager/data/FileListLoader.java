package com.hufeng.filemanager.data;

/**
 * Created by feng on 14-2-15.
 */

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.hufeng.filemanager.FileBrowserFragment;
import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;
import com.hufeng.filemanager.root.RootHelper;
import com.hufeng.filemanager.storage.StorageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class FileListLoader extends AsyncTaskLoader<List<FileEntry>> {

    private static final String LOG_TAG = FileListLoader.class.getSimpleName();

    private FileBrowserFragment.BROWSER_TYPE mType;
    private String mRoot;
    private List<String> mDirs;
    private List<FileEntry> mFiles;
    private String mSearch;

//    private ImportDirectoryObserver mFileObserver;

    public FileListLoader(Context context, FileBrowserFragment.BROWSER_TYPE type, String root, List<String> dirs, String search) {
        super(context);
        mType = type;
        mRoot = root;
        mDirs = dirs;
        mSearch = search;
    }

    @Override
    public List<FileEntry> loadInBackground() {
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader loadinbackground()");
        List<FileEntry> entries = new ArrayList<FileEntry>();
        String[] files = null;
        boolean has_root_path;
        if (mRoot!=null && new File(mRoot).isDirectory()) {
            files = new File(mRoot).list();
            has_root_path = false;
        } else if (mDirs != null) {
            has_root_path = true;
            if (mDirs.size() > 0) {
                files = mDirs.toArray(new String[mDirs.size()]);
            }
        } else {
            has_root_path = true;
            if (mType == FileBrowserFragment.BROWSER_TYPE.DEVICE) {
                files = StorageManager.getInstance(getContext()).getMountedStorages();
            } else if (mType == FileBrowserFragment.BROWSER_TYPE.FAVORITE) {
                files = FileUtils.getFavoriteFiles();
            } else if (mType == FileBrowserFragment.BROWSER_TYPE.DOWNLOAD) {
                files = FileUtils.getDownloadDirs();
            }
        }

        if( files != null ) {
            for (String file : files) {
                FileEntry entry = null;
                if (has_root_path) {
                    entry = new FileEntry(file);
                } else {
                    entry = new FileEntry(mRoot, file);
                }
                if (entry != null && (TextUtils.isEmpty(mSearch) || entry.getName().contains(mSearch))) {
                    entries.add(entry);
                }
            }
        }

        if (has_root_path == true && mType == FileBrowserFragment.BROWSER_TYPE.DEVICE) {
            boolean show_root_dir = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("SHOW_ROOT_DIR",true);
            if (RootHelper.isRootedPhone() && show_root_dir) {
                FileEntry entry = new FileEntry("/");
                entries.add(entry);
            }
        }

        // Sort the list.
        FileSorter.SORTER sorter = FileSorter.getFileSorter(getContext(), FileUtils.FILE_TYPE_FILE);
        Collections.sort(entries, FileSorter.getComparator(sorter.field, sorter.order));

        return entries;
    }

    @Override
    public void deliverResult(List<FileEntry> data) {
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader deliverResult with "+(data==null?0:data.size()));
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
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader onStartLoading");
        if (mFiles != null ) {
            deliverResult(mFiles);
        }
        String[] files = null;
        //if (mFileObserver == null) {
            if (mRoot!=null && new File(mRoot).isDirectory()) {
                files = new String[]{mRoot};
            } else if (mDirs != null) {
                files = mDirs.toArray(new String[mDirs.size()]);
            } else {
                if (mType == FileBrowserFragment.BROWSER_TYPE.DEVICE) {
//                    files = StorageManager.getInstance(getContext()).getMountedStorages();
                } else if (mType == FileBrowserFragment.BROWSER_TYPE.FAVORITE) {
                    files = FileUtils.getFavoriteFiles();
                } else if (mType == FileBrowserFragment.BROWSER_TYPE.DOWNLOAD) {
                    files = FileUtils.getDownloadDirs();
                }
            }
            if (files != null && files.length>0) {
//                mFileObserver = new ImportDirectoryObserver(this, files);
//                mFileObserver.startWatching();
                for (String file:files) {
//                    UiServiceHelper.getInstance().addMonitor(file);
                }
            }
        //}

        if(takeContentChanged() || mFiles == null) {
            forceLoad();
        }
    }


    @Override
    protected void onStopLoading() {
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader onStopLoading");
        cancelLoad();
    }


    @Override
    protected void onReset() {
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader onReset");
        onStopLoading();

        if (mFiles != null) {
            releaseResources(mFiles);
            mFiles = null;
        }

        // Stop monitoring for changes.
//        if (mFileObserver != null) {
//            mFileObserver.stopWatching();
//            mFileObserver = null;
//        }

    }


    @Override
    public void onCanceled(List<FileEntry> data) {
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader onCanceled");
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public void forceLoad() {
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader forceLoad");
        super.forceLoad();
    }

    private void releaseResources(List<FileEntry> data) {
        // do nothing
    }
}
