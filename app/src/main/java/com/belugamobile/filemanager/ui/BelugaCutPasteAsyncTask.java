package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.text.TextUtils;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.FileNameHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;
import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.root.BelugaRootManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaCutPasteAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCutPasteTask";

    public BelugaActionType mType = BelugaActionType.CUT_PASTE;

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

    private void updateMediaAndBelugaStore(BelugaFileEntry entry, BelugaFileEntry newEntry) {
        if (entry.isDirectory) {
            //if cut directory, update the files in this directory also.
            mMediaProviderHelper.updateInMediaStore(
                    entry.path, newEntry.path);
            List<BelugaFileEntry> children = new ArrayList<BelugaFileEntry>();
            getAllEntriesRecursively(children, newEntry);
            for (BelugaFileEntry child : children) {
                if (child.category > 0) {
                    String oldChildPath = child.path.replace(newEntry.path, entry.path);
                    BelugaProviderHelper.updateInBelugaDatabase(mContext, oldChildPath, child.path);
                }
            }
        } else {
            //if cut file, add it to the pasteMediaStoreHelper and deleteMediaStoreHelper
            mDeleteMediaStoreHelper.addRecord(entry.path);
            mPasteMediaStoreHelper.addRecord(newEntry.path);
            BelugaProviderHelper.updateInBelugaDatabase(mContext, entry.path, newEntry.path);
        }
        publishActionProgress(entry);
    }

    private boolean moveFileEntryOneByOne() {
        boolean result = true;
        byte[] buffer = new byte[BUFFER_SIZE];
        String dstMountPoint = MountPointManager.getInstance().getRealMountPointPath(mFolderPath);
        List<BelugaFileEntry> failedMoveEntries = new ArrayList<BelugaFileEntry>();
        List<String> failedMoveNewPaths = new ArrayList<String>();
        List<BelugaFileEntry> failedCopyDeleteEntries = new ArrayList<BelugaFileEntry>();
        List<String> failedCopyDeleteNewPaths = new ArrayList<String>();
        List<BelugaFileEntry> failedDeleteEntries = new ArrayList<BelugaFileEntry>();
        List<BelugaFileEntry> failedDeleteFolderEntries = new ArrayList<BelugaFileEntry>();
        List<String> failedDeleteFolderNewPaths = new ArrayList<String>();

        Map<BelugaFileEntry, String> originalNewNameMap = new HashMap<BelugaFileEntry, String>();

        String[] names = new File(mFolderPath).list();
        HashSet<String> existingNames = new HashSet<String>();
        if (names != null && names.length > 0) {
            existingNames.addAll(Arrays.asList(names));
        }
        for (BelugaFileEntry entry : mOriginalEntries) {
            String candidateNewName = entry.name;
            while (TextUtils.isEmpty(candidateNewName) && existingNames.contains(candidateNewName)) {
                candidateNewName = FileNameHelper.generateNextNewName(candidateNewName);
            }
            if (!TextUtils.isEmpty(candidateNewName)) {
                originalNewNameMap.put(entry, candidateNewName);
                existingNames.add(candidateNewName);
            }
        }

        if (isCancelled()) {
            return false;
        }

        for (BelugaFileEntry entry : mOriginalEntries) {
            if (isCancelled()) {
                mPasteMediaStoreHelper.updateRecords();
                mDeleteMediaStoreHelper.updateRecords();
                return false;
            }

            String srcMountPoint = MountPointManager.getInstance().getRealMountPointPath(entry.path);
            boolean moveInSameCard = !TextUtils.isEmpty(srcMountPoint) && !TextUtils.isEmpty(dstMountPoint) && dstMountPoint.equals(srcMountPoint);

            if (!moveInSameCard) {
                if ((TextUtils.isEmpty(srcMountPoint) && MountPointManager.getInstance().isInternalMountPath(dstMountPoint))
                        || (TextUtils.isEmpty(dstMountPoint) && MountPointManager.getInstance().isInternalMountPath(srcMountPoint))) {
                    moveInSameCard = true;
                }
            }

            String newName = originalNewNameMap.get(entry);
            File newFile = new File(mFolderPath, newName);
            String newPath = newFile.getAbsolutePath();
            if (moveInSameCard) {
                if (entry.renameTo(newFile)) {
                    BelugaFileEntry newEntry = new BelugaFileEntry(newFile);
                    updateMediaAndBelugaStore(entry, newEntry);
                } else {
                    result = false;
                    failedMoveEntries.add(entry);
                    failedMoveNewPaths.add(newPath);
                }
            } else {
                if (!copyAndDeleteEntry(entry, newFile, buffer,
                        failedCopyDeleteEntries, failedCopyDeleteNewPaths,
                        failedDeleteEntries,
                        failedDeleteFolderEntries, failedDeleteFolderNewPaths)) {
                    result = false;
                }
            }
        }

        if (!isCancelled() && (failedMoveEntries.size() > 0 || failedCopyDeleteEntries.size() > 0 || failedDeleteEntries.size() > 0)) {
            result = true;
            if (failedMoveEntries.size() > 0) {
                BelugaRootManager.getInstance().moveFileAsRoot(failedMoveEntries, failedMoveNewPaths);
            }
            if (failedCopyDeleteEntries.size() > 0) {
                BelugaRootManager.getInstance().copyDeleteFileAsRoot(failedCopyDeleteEntries, failedCopyDeleteNewPaths);
            }
            if (failedDeleteEntries.size() > 0) {
                BelugaRootManager.getInstance().deleteAsRoot(failedDeleteEntries);
            }
            BelugaRootManager.getInstance().waitForIdle();

            int size = failedMoveEntries.size();
            for (int i=0; i < size; i++) {
                String newPath = failedMoveNewPaths.get(i);
                File newFile  = new File(newPath);
                if (newFile.exists()) {
                    updateMediaAndBelugaStore(failedMoveEntries.get(i), new BelugaFileEntry(newFile));
                } else {
                    result = false;
                }
            }

            size = failedCopyDeleteEntries.size();
            for (int i=0; i < size; i++) {
                String newPath = failedCopyDeleteNewPaths.get(i);
                File newFile  = new File(newPath);
                if (newFile.exists()) {
                    updateMediaAndBelugaStore(failedCopyDeleteEntries.get(i), new BelugaFileEntry(newFile));
                } else {
                    result = false;
                }
            }

            for (BelugaFileEntry entry : failedDeleteEntries) {
                if (!entry.checkExistance()) {
                    mDeleteMediaStoreHelper.addRecord(entry.path);
                    publishActionProgress(entry);
                } else {
                    result = false;
                }
            }

            size = failedCopyDeleteEntries.size();
            List<BelugaFileEntry> finalDeleteEntries = new ArrayList<BelugaFileEntry>();
            for  (int i=0; i < size; i++) {
                String newPath = failedDeleteFolderNewPaths.get(i);
                File newFile = new File(newPath);
                File oldFile = failedCopyDeleteEntries.get(i).getFile();
                int newCount = 0;
                int oldCount = 0;
                if (oldFile.exists()) {
                    String[] children = oldFile.list();
                    if (children != null) oldCount = children.length;
                }

                if (newFile.exists()) {
                    String[] children = newFile.list();
                    if (children != null) newCount = children.length;
                    if (oldCount == newCount) {
                        finalDeleteEntries.add(failedCopyDeleteEntries.get(i));
                    } else {
                        result = false;
                    }
                } else {
                    result = false;
                }
            }

            if (finalDeleteEntries.size() > 0) {
                BelugaRootManager.getInstance().deleteAsRoot(finalDeleteEntries);
                BelugaRootManager.getInstance().waitForIdle();
                for (BelugaFileEntry entry : finalDeleteEntries) {
                    if (!entry.getFile().exists()) {
                        mDeleteMediaStoreHelper.addRecord(entry.path);
                        publishActionProgress(entry);
                    } else {
                        result = false;
                    }
                }
            }
        }
//                    if (entry.isDirectory) {
//                        if (newFile.mkdirs() || newFile.isDirectory()) {
//                            mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
//                            if (entry.delete()) {
//                                mDeleteMediaStoreHelper.addRecord(entry.path);
//                                publishActionProgress(entry);
//                            } else {
//                                result = false;
//                            }
//                        } else {
//                            result = false;
//                        }
//                    } else {
//                        if (copyFile(buffer, new File(entry.path), newFile)) {
//                            mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
//                            if (entry.delete()) {
//                                mDeleteMediaStoreHelper.addRecord(entry.path);
//                                BelugaProviderHelper.updateInBelugaDatabase(mContext, entry.path, newFile.getAbsolutePath());
//                                publishActionProgress(entry);
//                            } else {
//                                BelugaProviderHelper.insertInBelugaDatabase(mContext, newFile.getAbsolutePath());
//                                result = false;
//                            }
//                        } else {
//                            result = false;
//                        }
//                    }


        mPasteMediaStoreHelper.updateRecords();
        mDeleteMediaStoreHelper.updateRecords();
        return result;
    }

    private boolean copyAndDeleteEntry(BelugaFileEntry entry, File newFile, byte[] buffer,
                                       List<BelugaFileEntry> failedCopyDeleteEntries, List<String> failedCopyDeleteNewPaths,
                                       List<BelugaFileEntry> failedDeleteEntries,
                                       List<BelugaFileEntry> failedDeleteFolderEntries, List<String> failedDeleteFolderNewPaths) {
        if (isCancelled()) {
            return false;
        }
        if (entry.isDirectory) {
            BelugaFileEntry[] children = entry.listFiles();
            // Create new folder
            if (newFile.mkdirs()) {
                // Copy child files
                boolean empty = true;
                for (BelugaFileEntry child : children) {
                    if (!copyAndDeleteEntry(child, new File(newFile.getAbsolutePath(), child.name), buffer,
                            failedCopyDeleteEntries, failedCopyDeleteNewPaths,
                            failedDeleteEntries,
                            failedDeleteFolderEntries, failedDeleteFolderNewPaths)) {
                        empty = false;
                    }
                }
                mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                // Delete old folder
                if (empty) {
                    if (!entry.delete()) {
                        failedDeleteEntries.add(entry);
                        return false;
                    } else {
                        mDeleteMediaStoreHelper.addRecord(entry.path);
                        publishActionProgress(entry);
                        return true;
                    }
                } else {
                    failedDeleteFolderEntries.add(entry);
                    failedDeleteFolderNewPaths.add(newFile.getAbsolutePath());
                    return false;
                }
            } else {
                failedCopyDeleteEntries.add(entry);
                failedCopyDeleteNewPaths.add(newFile.getAbsolutePath());
                return false;
            }
        } else {
            // Copy
            if (copyFile(buffer, entry.getFile(), newFile)) {
                mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                if (entry.delete()) {
                    mDeleteMediaStoreHelper.addRecord(entry.path);
                    BelugaProviderHelper.updateInBelugaDatabase(mContext, entry.path, newFile.getAbsolutePath());
                    publishActionProgress(entry);
                    return true;
                } else {
                    BelugaProviderHelper.insertInBelugaDatabase(mContext, newFile.getAbsolutePath());
                    failedDeleteEntries.add(entry);
                    return false;
                }
            } else {
                failedCopyDeleteEntries.add(entry);
                failedCopyDeleteNewPaths.add(newFile.getAbsolutePath());
                return false;
            }
        }
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
