package com.belugamobile.filemanager.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng Hu on 15-03-01.
 * <p/>
 * TODO: Add a class header comment.
 */
public abstract class MultiMediaStoreHelper {
    protected final List<String> mPathList = new ArrayList<String>();
    private static final int NEED_UPDATE = 200;
    protected final MediaStoreHelper mMediaStoreHelper;

    public MultiMediaStoreHelper(MediaStoreHelper mediaStoreHelper) {
        if (mediaStoreHelper == null) {
            throw new IllegalArgumentException("mediaStoreHelper has not been initialized.");
        }
        mMediaStoreHelper = mediaStoreHelper;
    }

    public void addRecord(String path) {
        mPathList.add(path);
        if (mPathList.size() > NEED_UPDATE) {
            updateRecords();
        }
    }

    public void updateRecords() {
        mPathList.clear();
    }

    /**
     * Set dstfolder to scan with folder.
     *
     * @param dstFolder
     */
    public void setDstFolder(String dstFolder) {
        mMediaStoreHelper.setDstFolder(dstFolder);
    }

    public static class PasteMediaStoreHelper extends MultiMediaStoreHelper {
        public PasteMediaStoreHelper(MediaStoreHelper mediaStoreHelper) {
            super(mediaStoreHelper);
        }

        @Override
        public void updateRecords() {
            mMediaStoreHelper.scanPathforMediaStore(mPathList);
            super.updateRecords();
        }
    }

    public static class DeleteMediaStoreHelper extends MultiMediaStoreHelper {
        public DeleteMediaStoreHelper(MediaStoreHelper mediaStoreHelper) {
            super(mediaStoreHelper);
        }

        @Override
        public void updateRecords() {
            mMediaStoreHelper.deleteInMediaStore(mPathList);
            super.updateRecords();
        }
    }

}

