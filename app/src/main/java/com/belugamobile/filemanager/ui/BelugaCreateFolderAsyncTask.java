package com.belugamobile.filemanager.ui;

import android.content.Context;

import com.belugamobile.filemanager.BusProvider;
import com.belugamobile.filemanager.FolderCreateEvent;
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

    public BelugaActionType mType = BelugaActionType.CREATE_FOLDER;

    public BelugaCreateFolderAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(context, bac, folder);
    }

    @Override
    public boolean run() {
        boolean result = createFolder();
        if (result) {
            BusProvider.getInstance().post(new FolderCreateEvent(System.currentTimeMillis(), mFolderPath));
        }
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getString(R.string.progress_create_folder_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getString(R.string.progress_create_folder_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean rst) {
        int toast_info_id;
        if (rst){
            toast_info_id =  R.string.create_folder_success;
        }
        else
        {
            toast_info_id = R.string.create_folder_fail;
        }
        return toast_info_id == 0 ? "" : context.getString(toast_info_id);
    }

    private boolean createFolder() {
        boolean result = new File(mFolderPath).mkdirs();

        if (!result) {
            BelugaRootManager.getInstance().createFolderAsRoot(mFolderPath);
            BelugaRootManager.getInstance().waitForIdle();
            result = new File(mFolderPath).exists() && new File(mFolderPath).isDirectory();
        }
        return result;
    }

};
