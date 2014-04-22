package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.FileOperationActivity;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;

import java.io.File;

/**
 * Created by feng on 14-2-15.
 */
public class FileDeleteTask extends FileOperationTask {

    public FileDeleteTask(FileOperationActivity act, String[] files) {
        super(act, files);
    }

    @Override
    public boolean run(String[] params) {
        boolean result = FileAction.delete(mOperationFiles, cancel);
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.progress_delete_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.progress_delete_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        int toast_info_id;
        if (rst){
            toast_info_id =  R.string.file_delete_finish;
        }
        else
        {
            if(mOperationFiles.length == 1) {
                String path = mOperationFiles[0];
                File parent = new File(path).getParentFile();
                if (parent == null || !parent.canWrite()) {
                    toast_info_id = R.string.file_delete_single_failed;
                } else {
                    toast_info_id = R.string.file_delete_failed;
                }
            } else {
                toast_info_id = R.string.file_delete_failed;
            }
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

};
