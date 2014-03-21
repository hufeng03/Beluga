package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.FileOperationActivity;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;

/**
 * Created by feng on 14-2-15.
 */
public class FileCopyTask extends FileOperationTask{

    public FileCopyTask(FileOperationActivity act, String[] files) {
        super(act, files);
    }

    @Override
    public boolean run(String[] params) {
        int rst = 0;
        String directory = params[0];
        boolean result = FileAction.copy(mOperationFiles, directory, cancel);
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getResources().getString(R.string.progress_paste_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getResources().getString(R.string.progress_paste_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean result) {
        int toast_info_id = 0;

        if (result)
            toast_info_id = R.string.file_copy_finish;
        else
            toast_info_id = R.string.file_copy_failed;

        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }
}
