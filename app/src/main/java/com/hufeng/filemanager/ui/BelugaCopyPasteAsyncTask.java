package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.helper.BelugaProviderHelper;
import com.hufeng.filemanager.helper.MultiMediaStoreHelper;

import java.io.File;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaCopyPasteAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCopyPasteTask";
    MultiMediaStoreHelper.PasteMediaStoreHelper mPasteMediaStoreHelper;

    public BelugaCopyPasteAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(context, bac, folder);
        mPasteMediaStoreHelper = new MultiMediaStoreHelper.PasteMediaStoreHelper(
                mMediaProviderHelper);
    }

    @Override
    public boolean run() {
        boolean result = copyFileEntryOneByOne();
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

        if (result) {
            toast_info_id = R.string.file_copy_finish;
        } else {
            toast_info_id = R.string.file_copy_failed;
        }

        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }


    private boolean copyFileEntryOneByOne() {
        boolean result = true;
        byte[] buffer = new byte[BUFFER_SIZE];
        for(BelugaFileEntry entry : mFileEntries) {
            if(isCancelled()) {
                mPasteMediaStoreHelper.updateRecords();
                return false;
            }
            String name = entry.name;
            File newFile = new File(mFolderPath, name);
            newFile = checkFileNameAndRename(newFile);
            if (newFile == null) {
                result = false;
            } else {
                if (entry.isDirectory) {
                    if (newFile.mkdirs() || newFile.isDirectory()) {
                        mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                        publishActionProgress(entry);
                    } else {
                        result = false;
                    }
                } else {
                    if (copyFile(buffer, new File(entry.path), newFile)) {
                        mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                        BelugaProviderHelper.insertInBelugaDatabase(mContext, newFile.getAbsolutePath());
                        publishActionProgress(entry);
                    } else {
                        result = false;
                    }
                }
            }
        }
        mPasteMediaStoreHelper.updateRecords();
        return result;
    }

//    public boolean copyFileEntry(FileEntry[] entries, String destFolderPath) {
//        boolean result = true;
//        byte[] buffer = new byte[BUFFER_SIZE];
//        for(FileEntry entry : entries) {
//            if(isCancelled())
//                return false;
//            String name = entry.name;
//            File newFile = new File(destFolderPath, name);
//            newFile = checkFileNameAndRename(newFile);
//            if (newFile == null) {
//                result = false;
//            } else {
//                if (entry.isDirectory) {
//                    if (!copyFileEntryAsFolder(buffer, entry, newFile))
//                        result = false;
//                } else {
//                    if (!copyFile(buffer, new File(entry.path), newFile))
//                        result = false;
//                    else
//                        publishProgress(entry);
//                }
//            }
//        }
//        return result;
//    }
//
//    private boolean copyFileEntryAsFolder(byte[] buffer, FileEntry srcFolder, File destFolder){
//        if (destFolder.mkdir())
//            return false;
//        ArrayList<FileEntry> copiedEntries = new ArrayList<FileEntry>();
//        FileEntry[] childrenFile = srcFolder.listFiles();
//        boolean result = true;
//        if(childrenFile != null) {
//            for (FileEntry childFile : childrenFile) {
//                if(isCancelled()) {
//                    return false;
//                }
//                File newChildFile = new File(destFolder.getPath(), childFile.name);
//                newChildFile = checkFileNameAndRename(newChildFile);
//                if (newChildFile == null) {
//                    result = false;
//                } else {
//                    if (childFile.isDirectory) {
//                        if (!copyFileEntryAsFolder(buffer, childFile, newChildFile))
//                            result = false;
//                    } else {
//                        if (!copyFile(buffer, new File(childFile.path), newChildFile))
//                            result = false;
//                        else
//                            copiedEntries.add(childFile);
//                    }
//                }
//            }
//        }
//        copiedEntries.add(srcFolder);
//        if (copiedEntries.size() > 0) {
//            publishProgress(copiedEntries.toArray(new FileEntry[copiedEntries.size()]));
//        }
//        return result;
//    }
//
//
}
