package com.hufeng.filemanager.ui;

import android.content.Context;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.helper.BelugaProviderHelper;
import com.hufeng.filemanager.helper.MultiMediaStoreHelper;

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
        for (BelugaFileEntry entry : mFileEntries) {
            if (isCancelled()) {
                mDeleteMediaStoreHelper.updateRecords();
                return false;
            }
            if (entry.delete()) {
                mDeleteMediaStoreHelper.addRecord(entry.path);
                BelugaProviderHelper.deleteInBelugaDatabase(mContext, entry.path);
                publishActionProgress(entry);
            } else {
                result = false;
            }
        }
        mDeleteMediaStoreHelper.updateRecords();
        return result;
    }

//    private boolean deleteFileEntry(FileEntry... entries) {
//        boolean result = true;
//        for(FileEntry entry : entries)
//        {
//            if(isCancelled()) {
//                mDeleteMediaStoreHelper.updateRecords();
//                return false;
//            }
//            if(entry.isDirectory) {
//                result = deleteFileEntryAsFolder(entry);
//            } else {
//                if(!entry.delete()) {
//                    result = false;
//                } else {
//                    mDeleteMediaStoreHelper.addRecord(entry.path);
//                    publishProgress(entry);
//                }
//            }
//        }
//        mDeleteMediaStoreHelper.updateRecords();
//        return result;
//    }
//
//    private boolean deleteFileEntryAsFolder(FileEntry entry) {
//        boolean result = true;
//        FileEntry[] children = entry.listFiles(); //取得文件夹里面的子文件
//        if(children == null || children.length == 0){
//            result = entry.delete();
//            if (result)
//                publishProgress(entry);
//        } else {
//            ArrayList<FileEntry> deletedEntries = new ArrayList<FileEntry>();
//            for(FileEntry child : children){
//                if(isCancelled())
//                    return false;
//                if(child.isDirectory){
//                    if(!deleteFileEntryAsFolder(child))
//                        result = false;
//                }else {
//                    if(!child.delete())
//                        result = false;
//                    else
//                        deletedEntries.add(child);
//                }
//            }
//            if (result) {
//                if (!entry.delete()) {
//                    result = false;
//                } else {
//                    deletedEntries.add(entry);
//                }
//            }
//            if (deletedEntries.size() > 0) {
//                publishProgress(deletedEntries.toArray(new FileEntry[deletedEntries.size()]));
//            }
//        }
//        return result;
//    }
};
