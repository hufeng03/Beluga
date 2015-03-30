package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.FileNameHelper;
import com.belugamobile.filemanager.helper.MediaStoreHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by feng on 14-2-10.
 */
public abstract class BelugaActionAsyncTask extends AsyncTask<BelugaFileEntry, BelugaFileEntry, Boolean> {

    private static final String TAG = "BelugaActionAsyncTask";

    protected Context mContext;
    public BelugaActionAsyncTaskCallbackDelegate mCallbackDelegate;
    protected String mFolderPath;
    protected MediaStoreHelper mMediaProviderHelper;

    protected TreeSet<BelugaFileEntry> mOriginalEntries = new TreeSet<BelugaFileEntry>(new BelugaAsyncTaskEntryComparator());

//    protected List<BelugaFileEntry> mFileEntries = new ArrayList<BelugaFileEntry>();

    protected List<BelugaFileEntry> mUnReportedEntries = new ArrayList<BelugaFileEntry>();

    //to increase the copy/paste speed
    protected static final int BUFFER_SIZE = 2048 * 1024;

    protected static final int NEED_UPDATE_TIME = 200;

    private long mStartOperationTime;


    public abstract boolean run();
    public abstract String getProgressDialogTitle(Context context);
    public abstract String getProgressDialogContent(Context context);
    public abstract String getCompleteToastContent(Context context, boolean rst);

    public interface BelugaActionAsyncTaskCallbackDelegate {
        public void onAsyncTaskStarted();
        public void onAsyncTaskProgressUpdated(BelugaFileEntry... progress);
        public void onAsyncTaskCompleted(boolean result);
    }

    public BelugaActionAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate callbackDelegate, final String folder) {
        mContext = context;
        mCallbackDelegate = callbackDelegate;
        mFolderPath = folder;
        mMediaProviderHelper = new MediaStoreHelper(context, this);
    }

    public BelugaActionAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate actionController) {
        mContext = context;
        mCallbackDelegate = actionController;
        mMediaProviderHelper = new MediaStoreHelper(context, this);
    }

    @Override
    protected Boolean doInBackground(BelugaFileEntry... params) {
        mOriginalEntries.addAll(Arrays.asList(params));
//        getAllActionFileEntryList(mFileEntries, params);
        removeChildEntry();
        mStartOperationTime = System.currentTimeMillis();
        return run();
    }

    @Override
    protected void onPreExecute() {
        mCallbackDelegate.onAsyncTaskStarted();
    }

    @Override
    protected void onPostExecute(Boolean rst) {
        mCallbackDelegate.onAsyncTaskCompleted(rst.booleanValue());
    }

    @Override
    protected void onProgressUpdate(BelugaFileEntry... values) {
        mCallbackDelegate.onAsyncTaskProgressUpdated(values);
    }

    protected void publishActionProgress(BelugaFileEntry... entries) {
        final long operationTime = System.currentTimeMillis() - mStartOperationTime;
        if (operationTime > NEED_UPDATE_TIME) {
            if (mUnReportedEntries.size() > 0) {
                mUnReportedEntries.addAll(Arrays.asList(entries));
                super.publishProgress(mUnReportedEntries.toArray(new BelugaFileEntry[mUnReportedEntries.size()]));
                mUnReportedEntries.clear();
            } else {
                super.publishProgress(entries);
            }
            mStartOperationTime = System.currentTimeMillis();
        } else {
            mUnReportedEntries.addAll(Arrays.asList(entries));
        }

    }

    private void removeChildEntry() {
        Iterator<BelugaFileEntry> iterator = mOriginalEntries.iterator();
        String folderPath = null;
        while (iterator.hasNext()) {
            BelugaFileEntry entry = iterator.next();
            if (!TextUtils.isEmpty(folderPath)
                && entry.path.startsWith(folderPath)) {
                    // It should be removed
                iterator.remove();
                continue;
            }
            if (entry.isDirectory) {
                folderPath = entry.path;
                if (!folderPath.endsWith("/")) {
                    folderPath+="/";
                }
            }
        }
    }

    public void executeParallel(BelugaFileEntry... params) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            execute(params);
        } else {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }

    protected void getAllEntriesRecursively(List<BelugaFileEntry> entryList, BelugaFileEntry... entries) {
        if (entries != null) {
            for (BelugaFileEntry entry : entries) {
                if (entry.isDirectory) {
                    getAllEntriesRecursively(entryList, entry.listFiles());
                }
                entryList.add(entry);
            }
        }
        // Sort by name, desc to make sure that child files are always handled before their parent folder
        // Collections.sort(entryList, BelugaSortHelper.getComparator(BelugaSortHelper.SORT_FIELD.IDENTITY, BelugaSortHelper.SORT_ORDER.DESC));
        return;
    }




    protected File checkFileNameAndRename(File conflictFile) {
        File retFile = conflictFile;
        while (true) {
            if (isCancelled()) {
                Log.i(TAG, "checkFileNameAndRename,cancel.");
                return null;
            }
            if (!retFile.exists()) {
                Log.i(TAG, "checkFileNameAndRename,file is not exist.");
                return retFile;
            }
            retFile = FileNameHelper.generateNextNewName(retFile);
            if (retFile == null) {
                Log.i(TAG, "checkFileNameAndRename,retFile is null.");
                return null;
            }
        }
    }

    // Both srcFile and dstFile should be file but not folder
    protected boolean copyFile(byte[] buffer, File srcFile, File dstFile) {
        if ((buffer == null) || (srcFile == null) || (dstFile == null)) {
            Log.i(TAG, "copyFile, invalid parameter.");
            return false;
        }

        FileInputStream in = null;
        FileOutputStream out = null;
        boolean ret = true;
        try {
            if (!srcFile.exists()) {
                Log.i(TAG, "copyFile, src file is not exist.");
                return false;
            }
            File dstParentFile = dstFile.getParentFile();
            if (!dstParentFile.mkdirs() && !dstParentFile.isDirectory()) {
                Log.i(TAG, "copyFile, create possible missing parent folders fail.");
                return false;
            }
            if (!dstFile.createNewFile()) {
                Log.i(TAG, "copyFile, create new file fail.");
                return false;
            }
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(dstFile);

            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                // Copy data from in stream to out stream
                if (isCancelled()) {
                    Log.d(TAG, "copyFile,commit copy file cancelled; " + "break while loop "
                            + "thread id: " + Thread.currentThread().getId());
                    if (!dstFile.delete()) {
                        Log.w(TAG, "copyFile,delete fail in copyFile()");
                    }
                    return false;
                }
                out.write(buffer, 0, len);
            }
        } catch (IOException ioException) {
            Log.e(TAG, "copyFile,io exception!");
            ioException.printStackTrace();
            ret = false;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioException) {
                Log.e(TAG, "copyFile,io exception 2!");
                ioException.printStackTrace();
                ret = false;
            } finally {
                Log.d(TAG, "copyFile,update 100%.");
            }
            // If copy is failed, we should delete half-completed dest file
            if (!ret) {
                dstFile.delete();
            }
        }

        return ret;
    }
}

class BelugaAsyncTaskEntryComparator implements Comparator<BelugaFileEntry> {
    @Override
    public int compare(BelugaFileEntry lhs, BelugaFileEntry rhs) {
        return lhs.getIdentity().compareTo(rhs.getIdentity());
    }
}
