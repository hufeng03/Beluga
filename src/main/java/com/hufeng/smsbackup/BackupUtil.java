package com.hufeng.smsbackup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class BackupUtil {
	
    public static ProgressDialog showProgressDialog(Context context, CharSequence title, CharSequence message,
            boolean indeterminate, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setMax(100);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.setCancelable(cancelable);
        progressDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_SEARCH:
                        return true;
                }
                return false;
            }
        });
        progressDialog.show();

        return progressDialog;
    }

    public static void dismissDialog(Dialog dialog) {
        if (isDialogActive(dialog)) {
            dialog.dismiss();
        }
    }
    
    public static void showProgress(ProgressDialog dialog, int progress)
    {
    	if (isDialogActive(dialog)) 
    	{
    		dialog.setProgress(progress);
    	}
    }
    
    public static boolean isDialogActive(Dialog dialog) {
        return dialog != null && dialog.isShowing();
    }

}
