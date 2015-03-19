package com.belugamobile.filemanager.ui;

import android.content.Context;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;
import com.belugamobile.filemanager.mount.MountPointManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaCutPasteAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCutPasteTask";

    MultiMediaStoreHelper.PasteMediaStoreHelper mPasteMediaStoreHelper;
    MultiMediaStoreHelper.DeleteMediaStoreHelper mDeleteMediaStoreHelper;

    public BelugaCutPasteAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(context, bac, folder);
        mPasteMediaStoreHelper = new MultiMediaStoreHelper.PasteMediaStoreHelper(
                mMediaProviderHelper);
        mDeleteMediaStoreHelper = new MultiMediaStoreHelper.DeleteMediaStoreHelper(
                mMediaProviderHelper);
    }

    @Override
    public boolean run() {
        boolean result = moveFileEntryOneByOne();
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
            toast_info_id = R.string.file_move_failed;
        }
        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }

    private boolean moveFileEntryOneByOne() {
        boolean result = true;
        byte[] buffer = new byte[BUFFER_SIZE];
        String dstMountPoint = MountPointManager.getInstance().getRealMountPointPath(mFolderPath);
        File dstFolder = new File(mFolderPath);
        for (BelugaFileEntry entry : mFileEntries) {
            if(isCancelled()) {
                return false;
            }
            if(entry.getParentFile().equals(dstFolder)) {
                // No need to move in this case
                publishActionProgress(entry);
                continue;
            }

            String srcMountPoint = MountPointManager.getInstance().getRealMountPointPath(entry.path);
            boolean moveInSameCard = srcMountPoint != null && dstMountPoint != null && dstMountPoint.equals(srcMountPoint);

            File newFile = new File(mFolderPath, entry.name);
            newFile = checkFileNameAndRename(newFile);
            if (newFile == null) {
                result = false;
            } else {
                if (moveInSameCard) {
                    if (mOriginalEntries.contains(entry)) {
                        if (entry.renameTo(newFile)) {
                            if (entry.isDirectory) {
                                //if cut directory, update the files in this directory also.
                                mMediaProviderHelper.updateInMediaStore(
                                        entry.path, newFile.getAbsolutePath());
                                List<BelugaFileEntry> children = new ArrayList<BelugaFileEntry>();
                                getAllActionFileEntryList(children, new BelugaFileEntry(newFile));
                                for (BelugaFileEntry child : children) {
                                    if (child.category > 0) {
                                        String oldChildPath = child.path.replace(newFile.getAbsolutePath(), entry.path);
                                        BelugaProviderHelper.updateInBelugaDatabase(mContext, oldChildPath, child.path);
                                    }
                                }
                            } else {
                                //if cut file, add it to the pasteMediaStoreHelper and deleteMediaStoreHelper
                                mDeleteMediaStoreHelper.addRecord(entry.path);
                                mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                                BelugaProviderHelper.updateInBelugaDatabase(mContext, entry.path, newFile.getAbsolutePath());
                            }
                            publishActionProgress(entry);
                        } else {
                            result = false;
                        }
                    } else {
                        //do nothing, parent folder will handle this
                    }
                } else {
                    if (entry.isDirectory) {
                        if (newFile.mkdirs() || newFile.isDirectory()) {
                            mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                            if (entry.delete()) {
                                mDeleteMediaStoreHelper.addRecord(entry.path);
                                publishActionProgress(entry);
                            } else {
                                result = false;
                            }
                        } else {
                            result = false;
                        }
                    } else {
                        if (copyFile(buffer, new File(entry.path), newFile)) {
                            mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                            if (entry.delete()) {
                                mDeleteMediaStoreHelper.addRecord(entry.path);
                                BelugaProviderHelper.updateInBelugaDatabase(mContext, entry.path, newFile.getAbsolutePath());
                                publishActionProgress(entry);
                            } else {
                                BelugaProviderHelper.insertInBelugaDatabase(mContext, newFile.getAbsolutePath());
                                result = false;
                            }
                        } else {
                            result = false;
                        }
                    }
                }
            }
        }
        mPasteMediaStoreHelper.updateRecords();
        mDeleteMediaStoreHelper.updateRecords();
        return result;
    }


//    //文件复制
//    public boolean moveFileEntry(FileEntry[] entries, String destFolder)
//    {
//        boolean result = true;
//        byte[] buffer = new byte[BUFFER_SIZE];
//        for (FileEntry entry : entries) {
//            if(isCancelled())
//                return false;
//            boolean moveInDiffCard = false;
//            if(entry.parentPath.equals(new File(destFolder).getAbsolutePath())) {
//                continue;
//            }
//            String srcMountPoint = MountPointManager.getInstance().getRealMountPointPath(entry.path);
//            String destMountPoint = MountPointManager.getInstance().getRealMountPointPath(destFolder);
//            if(!TextUtils.isEmpty(srcMountPoint) && !TextUtils.isEmpty(destMountPoint) && !destMountPoint.equals(srcMountPoint)) {
//                //not in same sdcard
//                moveInDiffCard = true;
//            }
//
//            File newFile = new File(destFolder, entry.name);
//            newFile = checkFileNameAndRename(newFile);
//            if (newFile == null) {
//                result = false;
//            } else {
//                if (!moveInDiffCard) {
//                    if (!entry.renameTo(newFile))
//                        result = false;
//                } else {
//                    if (entry.isDirectory) {
//                        if (!copyAndDeleteFileEntryAsFolder(buffer, entry, newFile))
//                            result = false;
//                    } else {
//                        if (!copyFile(buffer, new File(entry.path), newFile))
//                            result = false;
//                        else {
//                            result = entry.delete();
//                            if (result) {
//                                publishProgress(entry);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//
//    //文件夹复制，包括文件夹里面的文件复制
//    private boolean copyAndDeleteFileEntryAsFolder(byte[] buffer, FileEntry srcFolder, File destFolder){
//        if (destFolder.mkdir())
//            return false;
//        ArrayList<FileEntry> movedEntries = new ArrayList<FileEntry>();
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
//                        if (!copyAndDeleteFileEntryAsFolder(buffer, childFile, newChildFile))
//                            result = false;
//                    } else {
//                        if (!copyFile(buffer, new File(childFile.path), newChildFile))
//                            result = false;
//                        else {
//                            if (!childFile.delete()) {
//                                result = false;
//                            } else {
//                                movedEntries.add(childFile);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (result) {
//            if (!srcFolder.delete()) {
//                result = false;
//            } else {
//                movedEntries.add(srcFolder);
//            }
//        }
//        if (movedEntries.size() > 0) {
//            publishProgress(movedEntries.toArray(new FileEntry[movedEntries.size()]));
//        }
//        return result;
//    }
}
