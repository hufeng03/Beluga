package com.hufeng.filemanager.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.hufeng.filemanager.SortPreferenceReceiver;
import com.hufeng.filemanager.data.BelugaZipElementEntry;
import com.hufeng.filemanager.helper.FileCategoryHelper;
import com.hufeng.filemanager.utils.LogUtil;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Feng Hu on 15-03-08.
 * <p/>
 * TODO: Add a class header comment.
 */
public class ZipBrowserLoader extends AsyncTaskLoader<List<BelugaZipElementEntry>> {

    private static final String TAG = "ZipBrowserLoader";

    private String mZipPath;
    private String mParentPath;

    private List<BelugaZipElementEntry> mElements;

    SortPreferenceReceiver mSortObserver;

    public ZipBrowserLoader(Context context, String zipPath, String parentPath) {
        super(context);
        mZipPath = zipPath;
        if (TextUtils.isEmpty(parentPath)) {
            mParentPath = mZipPath;
        } else {
            mParentPath = parentPath;
        }
    }

    @Override
    public List<BelugaZipElementEntry> loadInBackground() {
        HashMap<String, BelugaZipElementEntry> entryMap = new HashMap<String, BelugaZipElementEntry>();
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(mZipPath);
            Enumeration<? extends ZipEntry> elementEntries =  zipFile.entries();
            while (elementEntries.hasMoreElements()) {
                ZipEntry ze = elementEntries.nextElement();
                String name = ze.getName();
                if (name.startsWith("/")) {
                    name = name.substring(1);
                }
                if (name.endsWith("/")) {
                    name = name.substring(0, name.length()-1);
                }

                BelugaZipElementEntry entry = entryMap.get(name);
                if (entry == null) {
                    String path = mZipPath+File.separator+name;
                    entry = new BelugaZipElementEntry(path,
                            ze.isDirectory(),
                            ze.getSize(),
                            ze.getTime()/1000);
//                    if (entry.parentPath.equals(mParentPath)) {
                        entryMap.put(path, entry);
//                    }
                }

                while(true) {
                    int idx = name.lastIndexOf("/");
                    if (idx > 0) {
                        name = name.substring(0, idx);
                        BelugaZipElementEntry existingEntry = entryMap.get(name);
                        if (existingEntry == null) {
                            String path = mZipPath+File.separator+name;
                            entry = new BelugaZipElementEntry(path,
                                    true,
                                    0,
                                    ze.getTime()/1000);
//                            if (entry.parentPath.equals(mParentPath)) {
                                entryMap.put(path, entry);
//                            }
                        } else {
                            if (ze.getTime()/1000 > existingEntry.getTime()) {
                                existingEntry.time = ze.getTime()/1000;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        ZipInputStream zis = null;
//        try {
//            zis = new ZipInputStream(new FileInputStream(new File(mZipPath)));
//            ZipEntry ze;
//            while ((ze = zis.getNextEntry()) != null) {
//                BelugaZipElementEntry entry = new BelugaZipElementEntry(mZipPath, ze);
//                entries.add(entry);
//            }
//        }catch (IOException e) {
//
//        } finally {
//            if (zis != null) {
//                try {
//                    zis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        Iterator<BelugaZipElementEntry> entryIterator = entryMap.values().iterator();
        while (entryIterator.hasNext()) {
            BelugaZipElementEntry entry = entryIterator.next();
            BelugaZipElementEntry parentEntry = entryMap.get(entry.parentPath);
            if (parentEntry != null) {
                if (entry.isDirectory) {
                    parentEntry.childFolderCount++;
                } else {
                    parentEntry.childFileCount++;
                }
            }
        }

        entryIterator = entryMap.values().iterator();
        while (entryIterator.hasNext()) {
            BelugaZipElementEntry entry = entryIterator.next();


            if (!entry.parentPath.equals(mParentPath)) {
                entryIterator.remove();
            }
        }

        return new ArrayList<BelugaZipElementEntry>(entryMap.values());
    }

    @Override
    public void deliverResult(List<BelugaZipElementEntry> data) {
        LogUtil.i(TAG, this.hashCode() + " ZipBrowserLoader deliverResult with " + (data == null ? 0 : data.size()));
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        List<BelugaZipElementEntry> oldFiles = mElements;
        mElements = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldFiles != null && oldFiles != data) {
            releaseResources(oldFiles);
        }
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        LogUtil.i(TAG, this.hashCode() + " ZipBrowserLoader onStartLoading");
        if (mElements != null ) {
            deliverResult(mElements);
        }

        // Start watching for changes in the app data.
        if (mSortObserver == null) {
            mSortObserver = new SortPreferenceReceiver(this, FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
        }

        if(takeContentChanged() || mElements == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        LogUtil.i(TAG, this.hashCode()+" ZipBrowserLoader onStopLoading");
        cancelLoad();
    }

    @Override
    protected void onReset() {
        LogUtil.i(TAG, this.hashCode() + " ZipBrowserLoader onReset");
        onStopLoading();

        if (mElements != null) {
            releaseResources(mElements);
            mElements = null;
        }

        // Stop monitoring for changes.
        if (mSortObserver != null) {
            mSortObserver.dismiss(getContext());
            mSortObserver = null;
        }
    }

    @Override
    public void onCanceled(List<BelugaZipElementEntry> data) {
        LogUtil.i(TAG, this.hashCode()+" ZipBrowserLoader onCanceled");
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    private void releaseResources(List<BelugaZipElementEntry> data) {
        // do nothing
    }
}
