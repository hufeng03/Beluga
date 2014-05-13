package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.FileManager;
import com.hufeng.filemanager.FileOperationActivity;
import com.hufeng.filemanager.R;
import com.hufeng.filemanager.browser.FileAction;
import com.hufeng.filemanager.storage.StorageManager;
import com.hufeng.filemanager.storage.StorageUtil;

import java.io.File;

/**
 * Created by feng on 14-2-15.
 */
public class FileMoveTask extends FileOperationTask{

    private String mDestinateStorage;

    public FileMoveTask(FileOperationActivity act, String[] files) {
        super(act, files);
    }

    @Override
    public boolean run(String[] params) {
        int rst = 0;
        String directory = params[0];
        mDestinateStorage = StorageManager.getInstance(FileManager.getAppContext()).getStorageForPath(directory);
        boolean result = FileAction.move(mOperationFiles, directory, cancel);
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
            toast_info_id = R.string.file_move_finish;
        else {
            if (mDestinateStorage != null) {
                long available_size = StorageUtil.getAvailaleSize(mDestinateStorage);
                long total_size = 0;
                for (int i = 0; i < mOperationFiles.length; i++) {
                    total_size += new File(mOperationFiles[i]).length();
                }
                if (available_size < total_size) {
                    toast_info_id = R.string.file_copy_full;
                } else {
                    toast_info_id = R.string.file_move_failed;
                }
            } else {
                toast_info_id = R.string.file_move_failed;
            }
        }
        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }
}
