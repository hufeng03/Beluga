package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.text.TextUtils;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.FileNameHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;
import com.belugamobile.filemanager.root.BelugaRootHelper;
import com.belugamobile.filemanager.root.BelugaRootManager;

import org.w3c.dom.Text;

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
public class BelugaCopyPasteAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCopyPasteTask";

    public BelugaActionType mType = BelugaActionType.COPY_PASTE;

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
        List<BelugaFileEntry> failedCopyEntries = new ArrayList<BelugaFileEntry>();
        List<String> failedNewPaths = new ArrayList<String>();
//        List<BelugaFileEntry> originalFailed = new ArrayList<BelugaFileEntry>();
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

        for(BelugaFileEntry entry : mOriginalEntries) {
            if (isCancelled()) {
                mPasteMediaStoreHelper.updateRecords();
                return false;
            }

            String newName = originalNewNameMap.get(entry);

            if (!copyEntry(entry, new File(mFolderPath, newName), buffer, failedCopyEntries, failedNewPaths)) {
                result = false;
            }
        }

        if (!isCancelled() /*&& allFailed.size() > 0*/) {
//            List<BelugaFileEntry> rootCopyEntries = new ArrayList<BelugaFileEntry>();
//            List<String> rootNewPaths = new ArrayList<String>();
//
//            for (BelugaFileEntry entry : originalFailed) {
//                String newName = originalNewNameMap.get(entry);
//                File newFile = new File(mFolderPath, newName);
//                String newPath = newFile.getAbsolutePath();
//                if (newFile.exists()) {
//                    // Some child file failed
//                    String oldPath = entry.path;
//                    if (!oldPath.endsWith("/")) {
//                        oldPath += "/";
//                    }
//                    for (BelugaFileEntry child : allFailed) {
//                        if (child.path.startsWith(oldPath)) {
//                            rootCopyEntries.add(child);
//                            rootNewPaths.add(new File(newPath, child.parentPath.substring(oldPath.length(), child.parentPath.length())).getAbsolutePath());
//                        }
//                    }
//                } else {
//                    rootCopyEntries.add(entry);
//                    rootNewPaths.add(newPath);
//                }
//            }
            BelugaRootManager.getInstance().copyFileAsRoot(failedCopyEntries, failedNewPaths);
            BelugaRootManager.getInstance().waitForIdle();
            int i = 0;
            result = true;
            for (String newPath : failedNewPaths) {
                if (new File(newPath).exists()) {
                    mPasteMediaStoreHelper.addRecord(newPath);
                    if (!new File(newPath).isDirectory()) {
                        BelugaProviderHelper.insertInBelugaDatabase(mContext, newPath);
                    }
                    publishActionProgress(failedCopyEntries.get(i));
                } else {
                    result = false;
                }
                i++;
            }
        }
        mPasteMediaStoreHelper.updateRecords();
        return result;
    }

    private boolean copyEntry(BelugaFileEntry entry, File newFile, byte[] buffer, List<BelugaFileEntry> failedEntries, List<String> failedNewPaths) {
        if (entry.isDirectory) {
            BelugaFileEntry[] children = entry.listFiles();
            // Create a new folder
            if (newFile.mkdirs()) {
                // Copy child files
                for (BelugaFileEntry child : children) {
                    if (isCancelled()) {
                        return false;
                    }
                    copyEntry(child, new File(newFile.getAbsolutePath(), child.name), buffer, failedEntries, failedNewPaths);
                }
                publishActionProgress(entry);
            } else {
                failedEntries.add(entry);
                failedNewPaths.add(newFile.getAbsolutePath());
                return false;
            }
        } else {
            // Copy
            if (copyFile(buffer, entry.getFile(), newFile)) {
                mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                BelugaProviderHelper.insertInBelugaDatabase(mContext, newFile.getAbsolutePath());
                publishActionProgress(entry);
                return true;
            } else {
                failedEntries.add(entry);
                failedNewPaths.add(newFile.getAbsolutePath());
                return false;
            }
        }

        return true;
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
