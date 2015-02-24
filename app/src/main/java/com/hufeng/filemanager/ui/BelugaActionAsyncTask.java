package com.hufeng.filemanager.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.hufeng.filemanager.BelugaActionControllerActivity;
import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.browser.FileEntry;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by feng on 14-2-10.
 */
public abstract class BelugaActionAsyncTask extends AsyncTask<FileEntry, FileEntry, Boolean> {

    public BelugaActionAsyncTaskCallbackDelegate mCallbackDelegate;
    protected String mFolderPath;

    public abstract boolean run(FileEntry[] params);
    public abstract String getProgressDialogTitle(Context context);
    public abstract String getProgressDialogContent(Context context);
    public abstract String getCompleteToastContent(Context context, boolean rst);

    public interface BelugaActionAsyncTaskCallbackDelegate {
        public void onAsyncTaskStarted();
        public void onAsyncTaskProgressUpdated(FileEntry... progress);
        public void onAsyncTaskCompleted(boolean result);
    }

    public BelugaActionAsyncTask(BelugaActionAsyncTaskCallbackDelegate callbackDelegate, final String folder) {
        mCallbackDelegate = callbackDelegate;
        mFolderPath = folder;
    }

    public BelugaActionAsyncTask(BelugaActionAsyncTaskCallbackDelegate actionController) {
        mCallbackDelegate = actionController;
    }

    @Override
    protected Boolean doInBackground(FileEntry[] params) {
        return run(params);
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
    protected void onProgressUpdate(FileEntry... values) {
        mCallbackDelegate.onAsyncTaskProgressUpdated(values);
    }

    public void executeParallel(FileEntry... params) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            execute(params);
        } else {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }
}
