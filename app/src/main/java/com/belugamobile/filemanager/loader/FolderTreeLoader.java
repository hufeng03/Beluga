package com.belugamobile.filemanager.loader;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.belugamobile.filemanager.PreferenceKeys;
import com.belugamobile.filemanager.app.InterestingConfigChanges;
import com.belugamobile.filemanager.data.BelugaTreeFolderEntry;
import com.belugamobile.filemanager.helper.BelugaSortHelper;
import com.belugamobile.filemanager.helper.FileCategoryHelper;
import com.belugamobile.filemanager.mount.MountPoint;
import com.belugamobile.filemanager.mount.MountPointManager;
import com.belugamobile.filemanager.utils.FolderUtil;
import com.belugamobile.filemanager.utils.LogUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Feng on 2015-04-22.
 */
public class FolderTreeLoader extends AsyncTaskLoader<List<BelugaTreeFolderEntry>> {

    private static final String TAG = "FolderTreeLoader";

    private Context mContext;

    private String mDevice;
    private String mFolder;
    private boolean mExpand;

    List<BelugaTreeFolderEntry> mFolders;

    // Please make sure that (folder) start with (device)
    public FolderTreeLoader(Context context, String device, String folder, boolean collapse) {
        super(context);
        mContext = context;
        mDevice = device;
        mFolder = folder;
        mExpand = !collapse;
    }

    @Override
    public List<BelugaTreeFolderEntry> loadInBackground() {

        List<MountPoint> mountPoints = MountPointManager.getInstance().getMountPoints();
        if (PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(PreferenceKeys.ROOT_EXPLORER_ENABLE, false)) {
            MountPoint mountPoint = new MountPoint();
            mountPoint.mDescription = "Root explorer (/)";
            mountPoint.mPath = "/";
            mountPoint.mIsMounted = true;
            mountPoint.mIsExternal = false;
            mountPoint.mMaxFileSize = 0;
            Log.d(TAG, "init,description :" + mountPoint.mDescription + ",path : "
                    + mountPoint.mPath + ",isMounted : " + mountPoint.mIsMounted
                    + ",isExternal : " + mountPoint.mIsExternal + ", mMaxFileSize: " + mountPoint.mMaxFileSize);
            mountPoints.add(0, mountPoint);
        }

        List<BelugaTreeFolderEntry> folderEntries = new ArrayList<>();
        for (MountPoint mountPoint : mountPoints) {

            if (!TextUtils.isEmpty(mFolder) && mountPoint.mPath.equals(mDevice)) {
                if (mDevice.equals(mFolder)) {
                    BelugaTreeFolderEntry entry = new BelugaTreeFolderEntry(mountPoint, mExpand);
                    folderEntries.add(entry);
                    if (mExpand) {
                        String[] names = FolderUtil.list(mDevice);
                        for (String name : names) {
                            if (new File(mDevice, name).isDirectory()) {
                                BelugaTreeFolderEntry newEntry = new BelugaTreeFolderEntry(mDevice, name, mDevice, 1, false);
                                folderEntries.add(newEntry);
                            }
                        }
                    }
                } else if (mFolder.startsWith(mDevice)) {
                    BelugaTreeFolderEntry entry = new BelugaTreeFolderEntry(mountPoint, true);
                    folderEntries.add(entry);

                    int depth = entry.depth;
                    String leftPath = mFolder.substring(mDevice.length());
                    List<String> pathSegments = new LinkedList(Arrays.asList(leftPath.split("/")));
                    for (Iterator<String> segmentIterator = pathSegments.iterator(); segmentIterator.hasNext();) {
                        if (TextUtils.isEmpty(segmentIterator.next())) {
                            segmentIterator.remove();
                        }
                    }
                    String path = mDevice;
                    int size = pathSegments.size();
                    int idx = 0;
                    for(String pathSegment : pathSegments) {
                        idx++;
//                        if (!TextUtils.isEmpty(pathSegment)) {
                            String[] names = FolderUtil.list(path);
                            depth++;
                            for (String name:names) {
                                if (new File(path, name).isDirectory()) {
                                    if (name.equals(pathSegment)) {
                                        if (idx < size || mExpand) {
                                            BelugaTreeFolderEntry newEntry = new BelugaTreeFolderEntry(path, name, mDevice, depth, true);
                                            folderEntries.add(newEntry);
                                        } else {
                                            BelugaTreeFolderEntry newEntry = new BelugaTreeFolderEntry(path, name, mDevice, depth, false);
                                            folderEntries.add(newEntry);
                                        }
                                    } else {
                                        BelugaTreeFolderEntry newEntry = new BelugaTreeFolderEntry(path, name, mDevice, depth, false);
                                        folderEntries.add(newEntry);
                                    }
                                }
                            }
                            path = path + "/" + pathSegment;
//                        }
                    }
                    if (mExpand) {
                        depth++;
                        String[] names = FolderUtil.list(path);
                        for (String name : names) {
                            if (new File(path, name).isDirectory()) {
                                BelugaTreeFolderEntry newEntry = new BelugaTreeFolderEntry(path, name, mDevice, depth, false);
                                folderEntries.add(newEntry);
                            }
                        }
                    }
                } else {
                    // Something is not right
                }
            } else {
                BelugaTreeFolderEntry entry = new BelugaTreeFolderEntry(mountPoint, false);
                folderEntries.add(entry);
            }
        }
        Collections.sort(folderEntries, BelugaSortHelper.getComparator(BelugaSortHelper.SORT_FIELD.IDENTITY, BelugaSortHelper.SORT_ORDER.ASC));
        return folderEntries;
    }


    @Override
    public void deliverResult(List<BelugaTreeFolderEntry> data) {
        LogUtil.i(TAG, this.hashCode()+" FileListLoader deliverResult with "+(data==null?0:data.size()));
        if (isReset()) {
            if (data != null) {
                releaseResources(data);
                return;
            }
        }

        List<BelugaTreeFolderEntry> oldFiles = mFolders;
        mFolders = data;

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
        LogUtil.i(TAG, this.hashCode() + " FileListLoader onStartLoading");
        if (mFolders != null ) {
            deliverResult(mFolders);
        }

        // Start watching for changes in the app data.
//        if (mSortObserver == null) {
//            mSortObserver = new SortPreferenceReceiver(this, FileCategoryHelper.CATEGORY_TYPE_UNKNOW);
//        }

        if(takeContentChanged() || mFolders == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }

    @Override
    protected void onReset() {
        LogUtil.i(TAG, this.hashCode() + " FileListLoader onReset");
        onStopLoading();

        if (mFolder != null) {
            releaseResources(mFolders);
            mFolders = null;
        }

        // Stop monitoring for changes.
//        if (mSortObserver != null) {
//            mSortObserver.dismiss(getContext());
//            mSortObserver = null;
//        }
    }

    @Override
    public void onCanceled(List<BelugaTreeFolderEntry> data) {
        LogUtil.i(TAG, this.hashCode() + " FileListLoader onCanceled");
        super.onCanceled(data);
        releaseResources(data);
    }

    @Override
    public void forceLoad() {
        LogUtil.i(TAG, this.hashCode()+" FileListLoader forceLoad");
        super.forceLoad();
    }

    private void releaseResources(List<BelugaTreeFolderEntry> data) {
        // do nothing
    }
}
