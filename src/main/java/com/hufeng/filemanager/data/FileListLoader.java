package com.hufeng.filemanager.data;

/**
 * Created by feng on 14-2-15.
 */

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.hufeng.filemanager.browser.FileEntry;
import com.hufeng.filemanager.browser.FileSorter;
import com.hufeng.filemanager.browser.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class FileListLoader extends AsyncTaskLoader<List<FileEntry>> {

    private static final String LOG_TAG = FileListLoader.class.getSimpleName();

    private String mRoot;
    private String[] mDirs;
    private List<FileEntry> mFiles;
    private String mSearch;
    private boolean mNoDirectory;

//    private ImportDirectoryObserver mFileObserver;

    public FileListLoader(Context context, String root, String[] dirs, String search, boolean no_directory) {
        super(context);
        mRoot = root;
        mDirs = dirs;
        mSearch = search;
        mNoDirectory = no_directory;
    }

    @Override
    public List<FileEntry> loadInBackground() {
//        android.os.Debug.waitForDebugger();
        Log.i(LOG_TAG, this.hashCode()+" FileListLoader loadinbackground()");
        List<FileEntry> entries = new ArrayList<FileEntry>();
        if (!TextUtils.isEmpty(mRoot) && new File(mRoot).exists() && new File(mRoot).isDirectory()) {
            String[] files = new File(mRoot).list();
            if (files != null) {
                FileEntry entry = null;
                for (String file : files) {
                    entry = new FileEntry(mRoot, file);
                    if (entry.path!=null && (!entry.isDirectory() || !mNoDirectory)) {
                        if (TextUtils.isEmpty(mSearch) || entry.getName().contains(mSearch)) {
                            Log.i(LOG_TAG, "add "+file+"!!!!!!!!!!"+entry);
                            entries.add(entry);
                        }
                    }
                }
            }
        } else if (mDirs != null && mDirs.length > 0) {
                FileEntry entry = null;
                for (String file : mDirs) {
                    entry = new FileEntry(file);
                    if (!TextUtils.isEmpty(entry.path) && new File(entry.path).exists() && (!entry.isDirectory() || !mNoDirectory)) {
                        if (TextUtils.isEmpty(mSearch) || entry.getName().contains(mSearch)) {
                            entries.add(entry);
                        }
                    }
                }
        }
//        else {
//            has_root_path = true;
//            if (mType == FileBrowserFragment.BROWSER_TYPE.DEVICE) {
//                files = StorageManager.getInstance(getContext()).getMountedStorages();
//            } else if (mType == FileBrowserFragment.BROWSER_TYPE.FAVORITE) {
//                files = FileUtils.getFavoriteFiles();
//            } else if (mType == FileBrowserFragment.BROWSER_TYPE.DOWNLOAD) {
//                files = FileUtils.getDownloadDirs();
//            }
//        }

//        if (has_root_path == true && mType == FileBrowserFragment.BROWSER_TYPE.DEVICE) {
//            boolean show_root_dir = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("SHOW_ROOT_DIR",true);
//            if (RootHelper.isRootedPhone() && show_root_dir) {
//                FileEntry entry = new FileEntry("/");
//                entries.add(entry);
//            }
//        }

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
//        String[] files = null;
        //if (mFileObserver == null) {
//            if (mRoot!=null && new File(mRoot).isDirectory()) {
//                files = new String[]{mRoot};
//            } else if (mDirs != null) {
//                files = mDirs.toArray(new String[mDirs.size()]);
//            } else {
//                if (mType == FileBrowserFragment.BROWSER_TYPE.DEVICE) {
////                    files = StorageManager.getInstance(getContext()).getMountedStorages();
//                } else if (mType == FileBrowserFragment.BROWSER_TYPE.FAVORITE) {
//                    files = FileUtils.getFavoriteFiles();
//                } else if (mType == FileBrowserFragment.BROWSER_TYPE.DOWNLOAD) {
//                    files = FileUtils.getDownloadDirs();
//                }
//            }
//            if (files != null && files.length>0) {
////                mFileObserver = new ImportDirectoryObserver(this, files);
////                mFileObserver.startWatching();
//                for (String file:files) {
////                    UiServiceHelper.getInstance().addMonitor(file);
//                }
//            }
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
