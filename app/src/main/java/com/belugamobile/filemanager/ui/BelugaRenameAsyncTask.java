package com.belugamobile.filemanager.ui;

import android.content.Context;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;
import com.belugamobile.filemanager.root.BelugaRootManager;

import java.io.File;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public class BelugaRenameAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCopyPasteTask";
    MultiMediaStoreHelper.PasteMediaStoreHelper mPasteMediaStoreHelper;
    MultiMediaStoreHelper.DeleteMediaStoreHelper mDeleteMediaStoreHelper;

    public BelugaRenameAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac) {
        super(context, bac);
        mPasteMediaStoreHelper = new MultiMediaStoreHelper.PasteMediaStoreHelper(
                mMediaProviderHelper);
        mDeleteMediaStoreHelper = new MultiMediaStoreHelper.DeleteMediaStoreHelper(
                mMediaProviderHelper);
    }

    private String mNewName;

    public void setNewName(String newName) {
        mNewName = newName;
    }

    @Override
    public boolean run() {
        boolean result = renameFileEntry();
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getResources().getString(R.string.progress_rename_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getResources().getString(R.string.progress_rename_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean result) {
        int toast_info_id = 0;

        if (result) {
            toast_info_id = R.string.file_rename_finish;
        } else {
            toast_info_id = R.string.file_rename_failed;
        }

        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }

    private boolean renameFileEntry() {
        boolean result = true;
        BelugaFileEntry entry = mOriginalEntries.get(0);
        File newFile = new File(entry.parentPath, mNewName);
        newFile = checkFileNameAndRename(newFile);
        if(entry.getFile().renameTo(newFile)) {
            if (entry.isDirectory) {
                mMediaProviderHelper.updateInMediaStore(entry.path, newFile.getAbsolutePath());
                for (BelugaFileEntry oldEntry : mFileEntries) {
                    if (oldEntry.category > 0) {
                        String newEntryPath = oldEntry.path.replace(entry.path, newFile.getAbsolutePath());
                        BelugaProviderHelper.updateInBelugaDatabase(mContext, oldEntry.path, newEntryPath);
                    }
                }
            } else {
                mDeleteMediaStoreHelper.addRecord(entry.path);
                mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                BelugaProviderHelper.updateInBelugaDatabase(mContext, entry.path, newFile.getAbsolutePath());
            }
        } else {
            result = BelugaRootManager.getInstance().renameFileAsRoot(entry.path, newFile.getAbsolutePath());
        }
        mPasteMediaStoreHelper.updateRecords();
        mDeleteMediaStoreHelper.updateRecords();
        return result;
    }

}
