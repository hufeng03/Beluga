package com.belugamobile.filemanager.ui;

import android.content.Context;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;
import com.belugamobile.filemanager.root.BelugaRootManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaDeleteAsyncTask extends BelugaActionAsyncTask {

    MultiMediaStoreHelper.DeleteMediaStoreHelper mDeleteMediaStoreHelper;

    public BelugaDeleteAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac) {
        super(context, bac);
        mDeleteMediaStoreHelper = new MultiMediaStoreHelper.DeleteMediaStoreHelper(
                mMediaProviderHelper);
    }

    @Override
    public boolean run() {
        boolean result = deleteFileEntryOneByOne();
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

    private boolean deleteFileEntryOneByOne() {
        boolean result = true;
        List<BelugaFileEntry> allFailed = new ArrayList<BelugaFileEntry>();
        List<BelugaFileEntry> originalFailed = new ArrayList<BelugaFileEntry>();
        for (BelugaFileEntry entry : mOriginalEntries) {
            if (isCancelled()) {
                mDeleteMediaStoreHelper.updateRecords();
                return false;
            }
            if (!deleteEntry(entry, allFailed)) {
                originalFailed.add(entry);
            }
        }

        if (!isCancelled() && originalFailed.size() > 0) {
            BelugaRootManager.getInstance().deleteAsRoot(originalFailed);
            BelugaRootManager.getInstance().waitForIdle();
            result = true;
            for (BelugaFileEntry entry : allFailed) {
                if (entry.checkExistance()) {
                    result = false;
                } else {
                    mDeleteMediaStoreHelper.addRecord(entry.path);
                    BelugaProviderHelper.deleteInBelugaDatabase(mContext, entry.path);
                    publishActionProgress(entry);
                }
            }
        }
        mDeleteMediaStoreHelper.updateRecords();
        return result;
    }

    private boolean deleteEntry(BelugaFileEntry entry, List<BelugaFileEntry> failed) {
        if (entry.isDirectory) {
            BelugaFileEntry[] children = entry.listFiles();
            if (children != null) {
                for (BelugaFileEntry child : children) {
                    if (isCancelled()) {
                        return false;
                    }
                    deleteEntry(child, failed);
                }
            }
            // delete empty folder
            if (entry.delete()) {
                mDeleteMediaStoreHelper.addRecord(entry.path);
                BelugaProviderHelper.deleteInBelugaDatabase(mContext, entry.path);
                publishActionProgress(entry);
                return true;
            } else {
                failed.add(entry);
                return false;
            }
        } else {
            if (entry.delete()) {
                mDeleteMediaStoreHelper.addRecord(entry.path);
                BelugaProviderHelper.deleteInBelugaDatabase(mContext, entry.path);
                publishActionProgress(entry);
                return true;
            } else {
                failed.add(entry);
                return false;
            }
        }
    }

};
