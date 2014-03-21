package com.hufeng.filemanager.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.FileOperationActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by feng on 14-2-10.
 */
public abstract class FileOperationTask extends AsyncTask<String, Integer, Boolean> {

    Boolean cancel = false;
    WeakReference<Activity> mWeakActivity;
    WeakReference<ProgressDialog> mWeakDialog;

    String[] mOperationFiles = null;

    public abstract boolean run(String[] params);
    public abstract String getProgressDialogTitle(Context context);
    public abstract String getProgressDialogContent(Context context);
    public abstract String getCompleteToastContent(Context context, boolean rst);

    private void showProgressDialog(Activity act, final String title, final String content, final Boolean cancel) {
        Field f;
        try {
            f = cancel.getClass().getDeclaredField("value");
            f.setAccessible(true);
            try {
                f.set(cancel, new Boolean("false"));
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PowerManager pm = (PowerManager) FileManager.getAppContext().getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "My Tag");
        if(wakeLock!=null)
            wakeLock.acquire();

        ProgressDialog dialog = null;
        dialog = new ProgressDialog(act);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条
        dialog.setTitle(title);//设置标题
        dialog.setMessage(content);
        dialog.setIndeterminate(false);//设置进度条是否为不明确
        dialog.setCancelable(true);//设置进度条是否可以按退回键取消
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                Field f;
                try {
                    f = cancel.getClass().getDeclaredField("value");
                    f.setAccessible(true);
                    try {
                        f.set(cancel, new Boolean("true"));
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (wakeLock != null) {
                    wakeLock.release();
                }
            }
        });
        dialog.show();
        mWeakDialog = new WeakReference<ProgressDialog>(dialog);
    }

    public FileOperationTask(FileOperationActivity act, final String[] files) {
        mWeakActivity = new WeakReference<Activity>(act);
        mOperationFiles = files;
    }

    @Override
    protected Boolean doInBackground(String[] params) {
        return run(params);
    }

    @Override
    protected void onPreExecute() {
        Activity act = null;
        if (mWeakActivity == null || (act = mWeakActivity.get()) == null) {
            return;
        }
        if (act != null) {
            showProgressDialog(act, getProgressDialogTitle(act), getProgressDialogContent(act), cancel);
        }
    }

    @Override
    protected void onPostExecute(Boolean rst) {
        if (mWeakDialog != null) {
            ProgressDialog dialog = mWeakDialog.get();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
        if (mWeakActivity != null) {
            Activity act = null;
            if (mWeakActivity == null || (act = mWeakActivity.get()) == null) {
                return;
            }
            if (act != null) {
                Toast.makeText(act, getCompleteToastContent(act,rst), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void executeSerial(String... params) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            execute(params);
        } else {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        }
    }
}
