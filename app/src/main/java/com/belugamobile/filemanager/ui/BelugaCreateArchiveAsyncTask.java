package com.belugamobile.filemanager.ui;

import android.content.Context;
import android.text.TextUtils;

import com.belugamobile.filemanager.R;
import com.belugamobile.filemanager.data.BelugaFileEntry;
import com.belugamobile.filemanager.helper.BelugaProviderHelper;
import com.belugamobile.filemanager.helper.BelugaTimeHelper;
import com.belugamobile.filemanager.helper.MultiMediaStoreHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by feng on 14-2-15.
 */
public class BelugaCreateArchiveAsyncTask extends BelugaActionAsyncTask {

    private static final String TAG = "BelugaCutPasteTask";

    MultiMediaStoreHelper.PasteMediaStoreHelper mPasteMediaStoreHelper;

    public BelugaCreateArchiveAsyncTask(Context context, BelugaActionAsyncTaskCallbackDelegate bac, String folder) {
        super(context, bac, folder);
        mPasteMediaStoreHelper = new MultiMediaStoreHelper.PasteMediaStoreHelper(
                mMediaProviderHelper);
    }

    @Override
    public boolean run() {
        boolean result = archiveFileEntryOneByOne();
        return result;
    }

    @Override
    public String getProgressDialogTitle(Context context) {
        return context.getResources().getString(R.string.progress_create_archive_title);
    }

    @Override
    public String getProgressDialogContent(Context context) {
        return context.getResources().getString(R.string.progress_create_archive_content);
    }

    @Override
    public String getCompleteToastContent(Context context, boolean result) {
        int toast_info_id = 0;

        if (result)
            toast_info_id = R.string.create_archive_success;
        else {
            toast_info_id = R.string.create_archive_failed;
        }
        return toast_info_id == 0 ? "" : context.getResources().getString(toast_info_id);
    }

    private boolean archiveFileEntryOneByOne() {
        boolean result = true;
        byte[] buffer = new byte[BUFFER_SIZE];
        File dstFolder = new File(mFolderPath);
        String zipName = null;
        if (mOriginalEntries.size() == 1) {
            BelugaFileEntry entry = mOriginalEntries.get(0);
            zipName = entry.name;
            if (!TextUtils.isEmpty(entry.extension)) {
                int idx = zipName.lastIndexOf(entry.extension);
                if (idx > 0) {
                    zipName = zipName.substring(0, idx-1);
                }
            }
        } else {
            zipName = BelugaTimeHelper.getTimeString(System.currentTimeMillis()/1000);
        }
        File newZipFile = new File(mFolderPath, zipName+".zip");
        newZipFile = checkFileNameAndRename(newZipFile);

        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(new FileOutputStream(newZipFile));
            result = writeToZip(buffer, zout, "", mOriginalEntries.toArray(new BelugaFileEntry[mOriginalEntries.size()]));
        } catch (FileNotFoundException e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (result) {
            mPasteMediaStoreHelper.addRecord(newZipFile.getAbsolutePath());
            mPasteMediaStoreHelper.updateRecords();
            BelugaProviderHelper.insertInBelugaDatabase(mContext, newZipFile.getAbsolutePath());
        }
        return result;
    }

    private boolean writeToZip(byte[] buffer, ZipOutputStream zout, String currentDir, BelugaFileEntry[] entries) {
        boolean result = true;
        for (BelugaFileEntry entry : entries) {
            if (isCancelled()) {
                return false;
            }
            if (entry.isDirectory) {
                if (!writeToZip(buffer, zout, currentDir + "/" + entry.name, entry.listFiles())) {
                    result = false;
                }
            } else {
                ZipEntry ze = new ZipEntry(currentDir + "/" + entry.name);
                ze.setSize(entry.getSize());
                FileInputStream fin = null;
                try {
                    fin = new FileInputStream(entry.path);
                    zout.putNextEntry(ze);
                    int length;
                    while ((length = fin.read(buffer)) > 0) {
                        if (isCancelled()) {
                            zout.closeEntry();
                            return false;
                        }
                        zout.write(buffer, 0, length);
                    }
                    zout.closeEntry();
                } catch (IOException e ){
                    e.printStackTrace();
                    result = false;
                } finally {
                    if (fin != null) {
                        try {
                            fin.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result;
    }

}
