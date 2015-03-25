package com.belugamobile.filemanager.ui;

import android.content.Context;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;
import com.belugamobile.filemanager.root.BelugaRootManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaCreateFolderAsyncTask extends BelugaActionAsyncTask {

    public BelugaCreateFolderAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(context, bac, folder);
    }

    @Override
    public boolean run() {
        boolean result = createFolder();
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
            toast_info_id = R.string.file_delete_failed;
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

    private boolean createFolder() {
        boolean result = true;

        result = new File(mFolderPath).mkdirs();

        if (!result) {
            BelugaRootManager.getInstance().createFolderAsRoot(mFolderPath);
        }
        return result;
    }

};
