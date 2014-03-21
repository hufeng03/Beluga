package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.FileOperationActivity;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;

/**
 * Created by feng on 14-2-15.
 */
public class FileAddToCloudTask extends FileOperationTask {

    public FileAddToCloudTask(FileOperationActivity act, String[] files) {
        super(act, files);
    }

    @Override
    public boolean run(String[] params) {
        String parent = params[0];
        boolean result = FileAction.add_to_cloud(mOperationFiles, parent, cancel);
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.progress_add_to_cloud_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.progress_add_to_cloud_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        int toast_info_id;
        if(rst) {
            toast_info_id =  R.string.file_add_to_cloud_finish;
        }
        else {
            if(mOperationFiles.length == 1)
                toast_info_id = R.string.file_add_to_cloud_single_failed;
            else
                toast_info_id = R.string.file_add_to_cloud_failed;
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

}
