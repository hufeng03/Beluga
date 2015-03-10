package com.hufeng.filemanager.ui;

import android.content.Context;
import android.text.TextUtils;

import com.hufeng.filemanager.R;
import com.hufeng.filemanager.data.BelugaFileEntry;
import com.hufeng.filemanager.data.BelugaZipElementEntry;
import com.hufeng.filemanager.helper.BelugaProviderHelper;
import com.hufeng.filemanager.helper.MultiMediaStoreHelper;
import com.hufeng.filemanager.mount.MountPointManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaExtractArchiveAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCutPasteTask";

    MultiMediaStoreHelper.PasteMediaStoreHelper mPasteMediaStoreHelper;

    public BelugaExtractArchiveAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(context, bac, folder);
        mPasteMediaStoreHelper = new MultiMediaStoreHelper.PasteMediaStoreHelper(
                mMediaProviderHelper);
    }

    @Override
    public boolean run() {
        boolean result = extractFileEntryOneByOne();
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getResources().getString(R.string.progress_extract_archive_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getResources().getString(R.string.progress_extract_archive_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean result) {
        int toast_info_id = 0;

        if (result)
            toast_info_id = R.string.extract_archive_success;
        else {
            toast_info_id = R.string.extract_archive_failed;
        }
        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }

    private boolean extractFileEntryOneByOne() {
        boolean result = true;
        byte[] buffer = new byte[BUFFER_SIZE];
        boolean createTopFolder = false;
        File dstFolder = new File(mFolderPath);
        if (dstFolder.listFiles().length > 0 || mFileEntries.size() > 0) {
            createTopFolder = true;
        }
        for (BelugaFileEntry entry : mFileEntries) {
            if(isCancelled()) {
                return false;
            }
            if (createTopFolder) {
                String name = entry.name;
                if (!TextUtils.isEmpty(entry.extension)) {
                    int idx = entry.name.lastIndexOf(entry.extension);
                    if (idx > 0) {
                        name = entry.name.substring(0, idx - 1);
                    }
                }
                File newFile = new File(mFolderPath, name);
                newFile = checkFileNameAndRename(newFile);
                newFile.mkdirs();
                dstFolder = newFile;
            }

            if (unzip(buffer, new File(entry.path), dstFolder)) {
                publishActionProgress(entry);
            } else {
                result = false;
            }
        }
        mPasteMediaStoreHelper.updateRecords();
        return result;
    }

    private boolean unzip(byte[] buffer, File zipFile, File newFolder) {
        boolean result = true;
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (isCancelled()) {
                    return false;
                }
                if (ze.isDirectory()) continue;
                String name = ze.getName();
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                if (name.endsWith("/")) {
                    name = name.substring(0, name.length()-1);
                }

                File newFile = new File(newFolder, name);
                if (!writeZipElementEntry(buffer, zis, newFile)) {
                    return false;
                }
            }
        }catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    private boolean writeZipElementEntry(byte[] buffer, ZipInputStream zis, File newFile) {
        newFile.getParentFile().mkdirs();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newFile);
            int count;
            while ((count = zis.read(buffer)) != -1) {
                if (isCancelled()) {
                    return false;
                }
                fos.write(buffer, 0, count);
            }
            mPasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
            BelugaProviderHelper.insertInBelugaDatabase(mContext, newFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
